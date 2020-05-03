package com.example.fillfeel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
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
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.example.fillfeel.AccountFragment.AppConstants.REQUEST_IMAGE_CAPTURE
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.image_sheet_layout.view.*
import java.io.ByteArrayOutputStream
import java.util.*


class AccountFragment : Fragment() {
    private lateinit var mStorageRef: StorageReference
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    var fileUri: Uri? = null
    val TAG: String = "AccountFragment"

    lateinit var englishThaiTranslator: FirebaseTranslator
    lateinit var thaiEnglishTranslator: FirebaseTranslator

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
    lateinit var saveButton: AppCompatButton
    lateinit var genderAdapter: ArrayAdapter<String?>
    lateinit var items: List<String>
    lateinit var photoButton: TextView
    lateinit var display: ShapeableImageView

    lateinit var accountHeaderTitle: TextView
    lateinit var accountChangePhoto: TextView


    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView: View

    object AppConstants {
        val GALLERY_REQUEST_CODE: Int = 2
        val REQUEST_IMAGE_CAPTURE: Int = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    fun translateToEn(view: TextView) {
        val text = view.text.toString()
        thaiEnglishTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToTh(view: TextView) {
        val text = view.text.toString()
        englishThaiTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToEnLayout(view: TextInputLayout) {
        val text = view.hint.toString()
        thaiEnglishTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.hint = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToThLayout(view: TextInputLayout) {
        val text = view.hint.toString()
        englishThaiTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.hint = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToEnButton(view: AppCompatButton) {
        val text = view.text.toString()
        thaiEnglishTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToThButton(view: AppCompatButton) {
        val text = view.text.toString()
        englishThaiTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun languageChange(lang: String) {
        if (lang == "en") {
            translateToEn(accountHeaderTitle)
            translateToEn(accountChangePhoto)
            translateToEnLayout(accountFNameLayout)
            translateToEnLayout(accountLNameLayout)
            translateToEnLayout(accountBdLayout)
            translateToEnLayout(accountGenderLayout)
            translateToEnLayout(accountPhoneLayout)
            translateToEnLayout(accountPhoneLayout)
            translateToEnButton(saveButton)
        } else {
            translateToTh(accountHeaderTitle)
            translateToTh(accountChangePhoto)
            translateToThLayout(accountFNameLayout)
            translateToThLayout(accountLNameLayout)
            translateToThLayout(accountBdLayout)
            translateToThLayout(accountGenderLayout)
            translateToThLayout(accountPhoneLayout)
            translateToThButton(saveButton)
        }
        return
    }

    fun initTranslation() {
        val options1 = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.TH)
            .build()

        val options2 = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.TH)
            .setTargetLanguage(FirebaseTranslateLanguage.EN)
            .build()

        englishThaiTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options1)
        englishThaiTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }

        thaiEnglishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options2)
        thaiEnglishTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
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

    fun handleBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialog)
        bottomSheetView = layoutInflater.inflate(R.layout.image_sheet_layout, null)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetView.takeAPhotoSettings.setOnClickListener{view ->
            // Take a photo
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                val pm = getActivity()?.packageManager
                takePictureIntent.resolveActivity(pm!!)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }

            bottomSheetDialog.dismiss()
        }
        bottomSheetView.selectPhotoFromGallerySettings.setOnClickListener{view ->
            // Select photo from gallery
            val intent: Intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")

            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)

            startActivityForResult(intent, AppConstants.GALLERY_REQUEST_CODE)

            bottomSheetDialog.dismiss()
        }
    }

    fun resizeBitmap(source: Bitmap, width: Int, height: Int): Bitmap? {
        var source = source
        if (source.height == height && source.width == width) return source
        val maxLength = Math.min(width, height)
        return try {
            source = source.copy(source.config, true)
            if (source.height <= source.width) {
                if (source.height <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio =
                    source.width.toDouble() / source.height.toDouble()
                val targetWidth = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, targetWidth, maxLength, false)
            } else {
                if (source.width <= maxLength) { // if image already smaller than the required height
                    return source
                }
                val aspectRatio =
                    source.height.toDouble() / source.width.toDouble()
                val targetHeight = (maxLength * aspectRatio).toInt()
                Bitmap.createScaledBitmap(source, maxLength, targetHeight, false)
            }
        } catch (e: Exception) {
            source
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppConstants.GALLERY_REQUEST_CODE) {
                val imageUri: Uri? = data?.data
                val inputStream = context?.contentResolver?.openInputStream(imageUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val bitmapResize = resizeBitmap(bitmap, 150, 150)
                uploadImage(bitmapResize!!)
            }
            else if (requestCode == AppConstants.REQUEST_IMAGE_CAPTURE) {
                val bitmap = data?.extras?.get("data") as Bitmap
                uploadImage(bitmap)
            }
        }
    }

    fun uploadImage(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uuid = UUID.randomUUID();
        val imgId = uuid.toString()

        val uploadTask = mStorageRef
            .child("display")
            .child(imgId)
            .putBytes(data)
        uploadTask.addOnFailureListener {
            Log.e(TAG, "Upload Image Failure")
        }.addOnSuccessListener {taskSnapshot ->
            Log.e(TAG, "Upload Image Success")
        }

        val urlTask = uploadTask.continueWithTask {task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            mStorageRef.child("display/"+imgId).downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val url = task.result
                val defaultColor: Int = Color.WHITE
                val palette: Palette = Palette.from(bitmap).generate()
                val swatch = palette.vibrantSwatch
                val rgb = swatch!!.rgb
                var hex = Integer.toHexString(rgb.and(0xffffff))
                if (hex.length < 6) {
                    hex = "0" + hex;
                }
                hex = "#" + hex;

                val childUpdates: MutableMap<String, Any> = mutableMapOf()
                childUpdates.put("displayImg", url.toString())
                childUpdates.put("paletteImage", hex)

                mDatabase
                    .child("users")
                    .child(user.uid)
                    .updateChildren(childUpdates)
            } else {
                Log.e(TAG, "Upload Image Failure")
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initTranslation()

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mStorageRef = FirebaseStorage.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        accountHeaderTitle = view!!.findViewById(R.id.accountHeaderTitle)
        accountChangePhoto = view!!.findViewById(R.id.accountChangePhoto)
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
        saveButton = view!!.findViewById(R.id.saveButton)
        photoButton = view!!.findViewById(R.id.accountChangePhoto)
        display = view!!.findViewById(R.id.accountDisplayImage)

        items = listOf("Male", "Female", "Unspecified")
        genderAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        accountGender.setAdapter(genderAdapter)

        mDatabase
            .child("users")
            .child(user.uid).child("lang")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        languageChange(dataSnapshot.value.toString())
                    }
                }
            })

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
                        val getDisplayImg = elem?.displayImg
                        var getPalette = elem?.paletteImage

                        if (isEmpty(getPalette)) {
                            display.setBackgroundColor(Color.parseColor(getPalette))
                        }
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
                        if (isEmpty(getDisplayImg)) {
                            Picasso.get()
                                .load(getDisplayImg)
                                .resize(150, 150)
                                .centerCrop()
                                .into(display)
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
