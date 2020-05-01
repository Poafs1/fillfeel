package com.example.fillfeel

import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import java.util.*


class MainActivity : AppCompatActivity() {
    private val TAG: String = "MainActivity"
    private lateinit var auth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var gso: GoogleSignInOptions
    private val RC_SIGN_IN: Int = 1
    private lateinit var mCallbackManager: CallbackManager
    private lateinit var method: String

    private lateinit var authenticationPage: View
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View
    private lateinit var mTopToolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase Realtime Database for Offline Mode and keep it refresh in some table
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        val eventsRef = Firebase.database.getReference("events")
        val usersRef = Firebase.database.getReference("users")
        eventsRef.keepSynced(true)
        usersRef.keepSynced(true)

        setContentView(R.layout.activity_main)
        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? =
            fragmentManager.findFragmentById(R.id.root_layout)

        authenticationPage = findViewById(R.id.authentication_page)
        mCallbackManager = CallbackManager.Factory.create()

        // Firebase Authentication Init
        auth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        LoginManager.getInstance().registerCallback(mCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
            }
        })

        // Hidden Toolbar, Bottom Navigation Bar, and Logo in Sign In Page
        handleToolbar()
        handleBottomNavigationBar()
        handleLogo()

        // On Click Listener for Google Sign In Method Button
        val googleButton: Button = findViewById(R.id.google_button)
        googleButton.setOnClickListener{view ->
            method = "google"
            signIn()
        }

        // On Click Listener for Facebook Sign In Method Button
        val facebookButton: Button = findViewById(R.id.facebook_button)
        facebookButton.setOnClickListener{view ->
            method = "facebook"
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"))
            overridePendingTransition(0, 0)
        }

        val nightMode = getResources().configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        handleNighMode(nightMode)
        handleBottomSheetDialog(currentFragment)
    }

    /*
    * Handle Bottom Sheet Dialog
    *
    * It's floating fragment use to commit fragment transaction to:
    * @Account_Fragment
    * @Change_Language_Fragment
    * @Payment_Fragment
    * @Sign_Out
     */
    private fun handleBottomSheetDialog(currentFragment: Fragment?) {
        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetView.accountSetting.setOnClickListener{view ->
            if (!currentFragment?.tag.equals("Account_Fragment")) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, AccountFragment(), "Account_Fragment")
                    .addToBackStack(null).commit();
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetView.changeLanguageSetting.setOnClickListener{view ->
            if (!currentFragment?.tag.equals("Change_Language_Fragment")) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, ChangeLanguageFragment(), "Change_Language_Fragment")
                    .addToBackStack(null).commit();
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetView.paymentSetting.setOnClickListener{view ->
            if (!currentFragment?.tag.equals("Payment_Fragment")) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, PaymentFragment(), "Payment_Fragment")
                    .addToBackStack(null).commit();
                bottomSheetDialog.dismiss()
            }
        }
        bottomSheetView.signoutSetting.setOnClickListener{view ->
            auth.signOut()
            bottomSheetDialog.dismiss()
            startUI()
        }
    }

    /*
    * Handle NightMode for Tracking Device Theme Mode
    *
    * NightMode can change automatically, however, some part of Android Application
    * For example, Device status bar. It has to change the color programmatically
     */
    private fun handleNighMode(nightMode: Int) {
        when(nightMode) {
            // Nigh Mode OFF
            Configuration.UI_MODE_NIGHT_NO -> {
                val view = window.decorView
                view.systemUiVisibility =
                    view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
            // Nigh Mode ON
            Configuration.UI_MODE_NIGHT_YES -> {
                val view = window.decorView
                view.systemUiVisibility =
                    view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
        }
    }

    private fun handleToolbar() {
        mTopToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mTopToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.hide()
    }

    private fun handleBottomNavigationBar() {
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        bottomNavigation.visibility = View.GONE
    }

    private fun handleLogo() {
        val textviewLogo: TextView = findViewById(R.id.textview_logo)
        val shader: Shader = LinearGradient(
            0f, 0f, 0f, textviewLogo.getLineHeight().toFloat(),
            Color.parseColor("#EF6DA0"), Color.parseColor("#EE8E6B"), Shader.TileMode.REPEAT
        )
        textviewLogo.paint.setShader(shader)
    }

    // Update start ui after sign in success
    private fun updateUI(account: FirebaseUser?) {
        val fragment = ExploreFragment()

        authenticationPage.visibility = View.GONE
        supportActionBar?.show()
        bottomNavigation.visibility = View.VISIBLE

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.root_layout, fragment)
            .commit();
    }

    // Update start ui after sign out
    private fun startUI() {
        authenticationPage.visibility = View.VISIBLE
        supportActionBar?.hide()
        bottomNavigation.visibility = View.GONE

        supportFragmentManager.fragments.forEach {fragment ->
            supportFragmentManager.beginTransaction().remove(fragment).commit()
        }
    }

    /*
    * Bottom Navigation Bar Listener for Commit Transaction to Other Fragment
    *
    * @ExploreFragment
    * @HistoryFragment
    * @SavedFragment
    * @ProfileFragment has been removed and change it to settings bottom sheet dialog fragment
     */
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? =
            fragmentManager.findFragmentById(R.id.root_layout)
        when (item.itemId) {
            R.id.bottomNavigationExploreMenuId -> {
                if (!currentFragment?.tag.equals("Explore_Fragment")) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, ExploreFragment(), "Explore_Fragment")
                        .addToBackStack(null).commit();
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottomNavigationHistoryMenuId -> {
                if (!currentFragment?.tag.equals("History_Fragment")) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, HistoryFragment(), "History_Fragment")
                        .addToBackStack(null).commit();
                }
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottomNavigationSavedMenuId -> {
                if (!currentFragment?.tag.equals("Saved_Fragment")) {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.root_layout, SavedFragment(), "Saved_Fragment")
                        .addToBackStack(null).commit();
                }
                return@OnNavigationItemSelectedListener true
            }
            else -> return@OnNavigationItemSelectedListener false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    /*
    * Toolbar Listener for Commit Transaction to Other Fragment
    *
    * @ AddEventFragment
    * @ Bottom Sheet Dialog Fragment
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.getItemId()
        val fragmentManager: FragmentManager = supportFragmentManager
        val currentFragment: Fragment? =
            fragmentManager.findFragmentById(R.id.root_layout)
        if (id == R.id.add_event) {
            if (!currentFragment?.tag.equals("AddEvent_Fragment")) {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, AddEventFragment(), "AddEvent_Fragment")
                    .addToBackStack(null).commit();
            }
            return true
        } else if (id == R.id.settings_button) {
            // open bottom sheet dialog fragment
            bottomSheetDialog.show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        overridePendingTransition(0, 0)
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI(currentUser)
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        if (method != "google") return
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
                }
            }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        if (method != "facebook") return
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    updateUI(null)
                }
            }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }
}
