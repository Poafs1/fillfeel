package com.example.fillfeel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.facebook.FacebookSdk.getApplicationContext
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions

/**
 * A simple [Fragment] subclass.
 */
class ChangeLanguageFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    val TAG = "ChangeLanguageFragment"

    lateinit var englishThaiTranslator: FirebaseTranslator
    lateinit var thaiEnglishTranslator: FirebaseTranslator

    lateinit var langEN: RadioButton
    lateinit var langTH: RadioButton
    lateinit var detailsHeaderTitle: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_language, container, false)
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
//                view.typeface = R.font.sukhumvit_bold
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun languageChange(lang: String) {
        if (lang == "en") {
            translateToEn(detailsHeaderTitle)
        } else {
            translateToTh(detailsHeaderTitle)
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


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initTranslation()

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        langEN = view!!.findViewById(R.id.langEN)
        langTH = view!!.findViewById(R.id.langTH)
        detailsHeaderTitle = view!!.findViewById(R.id.detailsHeaderTitle)

        mDatabase.child("users").child(user.uid).child("lang")
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        languageChange(dataSnapshot.value.toString())
                        if (dataSnapshot.value.toString() == "en") {
                            langEN.isChecked = true
                        } else {
                            langTH.isChecked = true
                        }
                    }
                }
            })

        val radioGroup: RadioGroup = view!!.findViewById(R.id.radioLanguage)
        radioGroup.setOnCheckedChangeListener{radioGroup, optionId ->
            run {
                when (optionId) {
                    R.id.langEN -> {
                        val childUpdates: MutableMap<String, Any> = mutableMapOf()
                        childUpdates.put("/users/"+user.uid+"/lang", "en")
                        mDatabase.updateChildren(childUpdates)
                        Snackbar.make(radioGroup, "Change to English language", Snackbar.LENGTH_LONG).show();
                    }
                    R.id.langTH -> {
                        val childUpdates: MutableMap<String, Any> = mutableMapOf()
                        childUpdates.put("/users/"+user.uid+"/lang", "th")
                        mDatabase.updateChildren(childUpdates)
                        Snackbar.make(radioGroup, "Change to Thai language", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
