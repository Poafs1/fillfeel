package com.example.fillfeel

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.view.*
import kotlinx.android.synthetic.main.image_sheet_layout.view.*

class AccountFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var fileUri: Uri? = null
    val TAG: String = "AccountFragment"

    lateinit var accountFNameLayout: TextInputLayout
    lateinit var accountFName: TextInputEditText
    lateinit var accountLNameLayout: TextInputLayout
    lateinit var accountLName: TextInputEditText
    lateinit var accountBdLayout: TextInputLayout
    lateinit var accountBd: TextInputEditText
    lateinit var accountGenderLayout: TextInputLayout
    lateinit var accountGender: AppCompatAutoCompleteTextView
    lateinit var accountPhoneLayout: TextInputLayout
    lateinit var accountPhone: TextInputEditText
    lateinit var changePhoto: TextView
    lateinit var saveButton: AppCompatButton
    lateinit var genderAdapter: ArrayAdapter<String?>
    lateinit var items: List<String>
    lateinit var photoButton: TextView

    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View

//    object AppConstants {
//        val TAKE_PHOTO_REQUEST: Int = 2
//        val PICK_PHOTO_REQUEST: Int = 1
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    private fun hideKeyboard(activity: Activity?) {
        val inputManager: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as?
                    InputMethodManager
        // check if no view has focus:
        val v = activity?.currentFocus ?: return
        inputManager?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun isEmpty(input: String?) : Boolean {
        val text = input?.trim()
        if (text.isNullOrEmpty()) {
            return false
        }
        return true
    }

//    //pick img from gallery
//    private fun pickImg() {
//        val pickImgIntent = Intent(Intent.ACTION_PICK,
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(pickImgIntent, AppConstants.PICK_PHOTO_REQUEST)
//    }
//
//    //launch camera to take img via intent
//    private fun launchCamera() {
////        val values = ContentValues(1)
////        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
////        fileUri = contentResolver
////            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
////        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
////        if (intent.resolveActivity(packageManager) != null) {
////            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
////            intent.addFlags(
////                Intent.FLAG_GRANT_READ_URI_PERMISSION
////                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
////            )
////            startActivityForResult(intent, AppConstants.TAKE_PHOTO_REQUEST)
////        }
//
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            .also { takePictureIntent ->
//                takePictureIntent.resolveActivity(packageManager)?.also {
//                    startActivityForResult(takePictureIntent, AppConstants.TAKE_PHOTO_REQUEST)
//                }
//            }
//    }

    fun handleBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        bottomSheetView = layoutInflater.inflate(R.layout.image_sheet_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetView.takeAPhotoSettings.setOnClickListener{view ->
            // Take a photo
        }
        bottomSheetView.selectPhotoFromGallerySettings.setOnClickListener{view ->
            // Select photo from gallery
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        accountFNameLayout = view!!.findViewById(R.id.accountFNameLayout)
        accountFName = view!!.findViewById(R.id.accountFName)
        accountLNameLayout = view!!.findViewById(R.id.accountLNameLayout)
        accountLName = view!!.findViewById(R.id.accountLName)
        accountBdLayout = view!!.findViewById(R.id.accountBdLayout)
        accountBd = view!!.findViewById(R.id.accountBd)
        accountGenderLayout = view!!.findViewById(R.id.accountGenderLayout)
        accountGender = view!!.findViewById(R.id.accountGender)
        accountPhoneLayout = view!!.findViewById(R.id.accountPhoneLayout)
        accountPhone = view!!.findViewById(R.id.accountPhone)
        saveButton = view!!.findViewById(R.id.saveAccountTextField)
        photoButton = view!!.findViewById(R.id.accountChangePhoto)

        items = listOf("Male", "Female", "Unspecified")
        genderAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        accountGender.setAdapter(genderAdapter)

        accountBd.addTextChangedListener(BdTextFormatter())

        mDatabase.child("users").child(user.uid)
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val elem = dataSnapshot.getValue(AccountObject::class.java)
                        val getFName = elem?.firstName
                        val getLName = elem?.lastName
                        val getBd = elem?.dateOfBirth
                        val getGender = elem?.gender
                        val getPhone = elem?.phone

                        if (isEmpty(getFName)) {
                            accountFNameLayout.setHintAnimationEnabled(false);
                            accountFName.setText(getFName)
                        }
                        if (isEmpty(getLName)) {
                            accountLNameLayout.setHintAnimationEnabled(false);
                            accountLName.setText(getLName)
                        }
                        if (isEmpty(getBd)) {
                            accountBdLayout.setHintAnimationEnabled(false);
                            accountBd.setText(getBd)
                        }
                        if (isEmpty(getGender)) {
                            accountGenderLayout.setHintAnimationEnabled(false);
                            val context: Context = context ?: return
                            if (context != null) {
                                accountGender.setText(getGender)
                                genderAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
                                accountGender.setAdapter(genderAdapter)
                            }
                        }
                        if (isEmpty(getPhone)) {
                            accountPhoneLayout.setHintAnimationEnabled(false);
                            accountPhone.setText(getPhone)
                        }
                    }
                }
            })

        handleBottomSheetDialog()

        photoButton.setOnClickListener {view ->
            hideKeyboard(getActivity())
            bottomSheetDialog.show()
        }

        saveButton?.setOnClickListener {view ->
            hideKeyboard(getActivity())

            accountFNameLayout.error = null
            accountLNameLayout.error = null
            accountBdLayout.error = null
            accountGenderLayout.error = null
            accountPhoneLayout.error = null

            val childUpdates: MutableMap<String, Any> = mutableMapOf()
            childUpdates.put("/users/"+user.uid+"/firstName", accountFName.text.toString())
            childUpdates.put("/users/"+user.uid+"/lastName", accountLName.text.toString())
            childUpdates.put("/users/"+user.uid+"/dateOfBirth", accountBd.text.toString())
            childUpdates.put("/users/"+user.uid+"/gender", accountGender.text.toString())
            childUpdates.put("/users/"+user.uid+"/phone", accountPhone.text.toString())

            mDatabase.updateChildren(childUpdates)

            Snackbar.make(view,"Update Account", Snackbar.LENGTH_LONG).show();
        }
    }

}
