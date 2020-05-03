package com.example.fillfeel

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
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
import org.w3c.dom.Text

class PaymentFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    val TAG: String = "PaymentFragment"

    lateinit var englishThaiTranslator: FirebaseTranslator
    lateinit var thaiEnglishTranslator: FirebaseTranslator

    lateinit var cardNumberLayout: TextInputLayout
    lateinit var cardNumber: TextInputEditText
    lateinit var cardHolderLayout: TextInputLayout
    lateinit var cardHolder: TextInputEditText
    lateinit var expDateLayout: TextInputLayout
    lateinit var expDate: TextInputEditText
    lateinit var cvvLayout: TextInputLayout
    lateinit var cvv: TextInputEditText
    lateinit var saveButton: AppCompatButton
    lateinit var visualCredit: LinearLayout
    lateinit var visualCardNumber: TextView
    lateinit var visualExpDate: TextView

    lateinit var detailsHeaderTitle: TextView
    lateinit var paymentCardNumberLayout: TextInputLayout
    lateinit var paymentCardHolderLayout: TextInputLayout
    lateinit var paymentExpDateLayout: TextInputLayout
    lateinit var savePaymentTextField: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
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
            translateToEn(detailsHeaderTitle)
            translateToEnLayout(paymentCardNumberLayout)
            translateToEnLayout(paymentCardHolderLayout)
            translateToEnLayout(paymentExpDateLayout)
            translateToEnButton(savePaymentTextField)
        } else {
            translateToTh(detailsHeaderTitle)
            translateToThLayout(paymentCardNumberLayout)
            translateToThLayout(paymentCardHolderLayout)
            translateToThLayout(paymentExpDateLayout)
            translateToThButton(savePaymentTextField)
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

    private fun validateCardNumber() : Boolean {
        val text = cardNumber?.text.toString().trim()
        if (text.isNullOrEmpty()) {
            cardNumberLayout?.error = "This field is required"
            return false
        }
        if (text.length != 19) {
            cardNumberLayout?.error = "Incorrect format"
            return false
        }
        return true
    }

    private fun validateIsEmpty(input: TextInputEditText, layout: TextInputLayout): Boolean {
        val text = input?.text.toString().trim()
        if (text.isNullOrEmpty()) {
            layout?.error = "This field is required"
            return false
        }
        return true
    }

    private fun isEmpty(input: String?) : Boolean {
        val text = input?.trim()
        if (text.isNullOrEmpty()) {
            return false
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        cardNumberLayout = view!!.findViewById(R.id.paymentCardNumberLayout)
        cardNumber = view!!.findViewById(R.id.paymentCardNumber)
        cardHolderLayout = view!!.findViewById(R.id.paymentCardHolderLayout)
        cardHolder = view!!.findViewById(R.id.paymentCardHolder)
        expDateLayout = view!!.findViewById(R.id.paymentExpDateLayout)
        expDate = view!!.findViewById(R.id.paymentExpDate)
        cvvLayout = view!!.findViewById(R.id.paymentCvvLayout)
        cvv = view!!.findViewById(R.id.paymentCvv)
        saveButton = view!!.findViewById(R.id.savePaymentTextField)
        visualCredit = view!!.findViewById(R.id.visualCredit)
        visualCardNumber = view!!.findViewById(R.id.visualCardNumber)
        visualExpDate = view!!.findViewById(R.id.visualExpDate)

        detailsHeaderTitle = view!!.findViewById(R.id.detailsHeaderTitle)
        paymentCardNumberLayout = view!!.findViewById(R.id.paymentCardNumberLayout)
        paymentCardHolderLayout = view!!.findViewById(R.id.paymentCardHolderLayout)
        paymentExpDateLayout = view!!.findViewById(R.id.paymentExpDateLayout)
        savePaymentTextField = view!!.findViewById(R.id.savePaymentTextField)

        initTranslation()

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

        mDatabase.child("users").child(user.uid)
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val elem = dataSnapshot.getValue(PaymentObject::class.java)
                        val getCardNumber = elem?.cardNumber
                        val getCardHolder = elem?.cardHolder
                        val getCardExpiry = elem?.expiryDate

                        if (isEmpty(getCardNumber) && isEmpty(getCardExpiry)) {
                            visualCardNumber.text = "•••• •••• •••• " + getCardNumber?.takeLast(4)
                            visualExpDate.text = getCardExpiry
                            visualCredit.visibility = View.VISIBLE
                        }

                        if (isEmpty(getCardNumber)) {
                            cardNumberLayout.setHintAnimationEnabled(false);
                            cardNumber.setText(getCardNumber)
                        }
                        if (isEmpty(getCardHolder)) {
                            cardHolderLayout.setHintAnimationEnabled(false);
                            cardHolder.setText(getCardHolder)
                        }
                        if (isEmpty(getCardExpiry)) {
                            expDateLayout.setHintAnimationEnabled(false);
                            expDate.setText(getCardExpiry)
                        }
                    }
                }
            })

        cardNumber?.addTextChangedListener(CreditCardTextFormatter())
        expDate?.addTextChangedListener(CreditCardDateTextFormatter())

        saveButton?.setOnClickListener { view ->
            val isCardNumber = validateCardNumber()
            val isCardHolder = validateIsEmpty(cardHolder, cardHolderLayout)
            val isExpiryDate = validateIsEmpty(expDate, expDateLayout)
            val isCvv = validateIsEmpty(cvv, cvvLayout)

            if (isCardNumber && isCardHolder && isExpiryDate && isCvv) {
                hideKeyboard(getActivity())

                cardNumberLayout.error = null
                cardHolderLayout.error = null
                expDateLayout.error = null
                cvvLayout.error = null

                val childUpdates: MutableMap<String, Any> = mutableMapOf()
                childUpdates.put("/users/"+user.uid+"/cardNumber", cardNumber.text.toString())
                childUpdates.put("/users/"+user.uid+"/cardHolder", cardHolder.text.toString())
                childUpdates.put("/users/"+user.uid+"/expiryDate", expDate.text.toString())

                mDatabase.updateChildren(childUpdates)

                Snackbar.make(view, "Update Payment", Snackbar.LENGTH_LONG).show();
            } else {
                hideKeyboard(getActivity())
            }
        }
    }
}
