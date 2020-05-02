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
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.threeten.bp.Instant

class DonateFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    val TAG: String = "DonateFragment"

    lateinit var eventId: String
    lateinit var title: String

    lateinit var donateTitle: TextView
    lateinit var donateFNameLayout: TextInputLayout
    lateinit var donateFName: TextInputEditText
    lateinit var donateEmailLayout: TextInputLayout
    lateinit var donateEmail: TextInputEditText
    lateinit var donatePhoneLayout: TextInputLayout
    lateinit var donatePhone: TextInputEditText
    lateinit var donateAmountLayout: TextInputLayout
    lateinit var donateAmount: TextInputEditText

    lateinit var cardNumberLayout: TextInputLayout
    lateinit var cardNumber: TextInputEditText
    lateinit var cardHolderLayout: TextInputLayout
    lateinit var cardHolder: TextInputEditText
    lateinit var expDateLayout: TextInputLayout
    lateinit var expDate: TextInputEditText
    lateinit var cvvLayout: TextInputLayout
    lateinit var cvv: TextInputEditText
    lateinit var visualCredit: LinearLayout
    lateinit var visualCardNumber: TextView
    lateinit var visualExpDate: TextView
    lateinit var saveButton: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_donate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        eventId = bundle?.getString("eventId", "").toString()
        title = bundle?.getString("title", "").toString()
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

    private fun validateIsEmpty(input: TextInputEditText, layout: TextInputLayout): Boolean {
        val text = input?.text.toString().trim()
        if (text.isNullOrEmpty()) {
            layout?.error = "This field is required"
            return false
        }
        return true
    }

    private fun validateIsEmptyAndLength(input: TextInputEditText, layout: TextInputLayout, length: Int): Boolean {
        val text = input?.text.toString().trim()
        if (text.isNullOrEmpty()) {
            layout?.error = "This field is required"
            return false
        }
        if (text.length != length) {
            layout?.error = "Incorrect format"
            return false
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        donateTitle = view!!.findViewById(R.id.donateTitle)
        donateFNameLayout = view!!.findViewById(R.id.donateFNameLayout)
        donateFName = view!!.findViewById(R.id.donateFName)
        donateEmailLayout = view!!.findViewById(R.id.donateEmailLayout)
        donateEmail = view!!.findViewById(R.id.donateEmail)
        donatePhoneLayout = view!!.findViewById(R.id.donatePhoneLayout)
        donatePhone = view!!.findViewById(R.id.donatePhone)
        donateAmountLayout = view!!.findViewById(R.id.donateAmountLayout)
        donateAmount = view!!.findViewById(R.id.donateAmount)
        cardNumberLayout = view!!.findViewById(R.id.donateCardNumberLayout)
        cardNumber = view!!.findViewById(R.id.donateCardNumber)
        cardHolderLayout = view!!.findViewById(R.id.donateCardHolderLayout)
        cardHolder = view!!.findViewById(R.id.donateCardHolder)
        expDateLayout = view!!.findViewById(R.id.donateExpDateLayout)
        expDate = view!!.findViewById(R.id.donateExpDate)
        cvvLayout = view!!.findViewById(R.id.donateCvvLayout)
        cvv = view!!.findViewById(R.id.donateCvv)
        visualCredit = view!!.findViewById(R.id.donateVisualCredit)
        visualCardNumber = view!!.findViewById(R.id.donateVisualCardNumber)
        visualExpDate = view!!.findViewById(R.id.donateVisualExpDate)
        saveButton = view!!.findViewById(R.id.saveDonateTextField)

        donateTitle.text = title

        cardNumber?.addTextChangedListener(CreditCardTextFormatter())
        expDate?.addTextChangedListener(CreditCardDateTextFormatter())

        if (user.email != null) {
            donateEmailLayout.setHintAnimationEnabled(false)
            donateEmail.setText(user.email)
        }

        mDatabase.child("users").child(user.uid)
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val elem = dataSnapshot.getValue(DonateObject::class.java)
                        val getFName = elem?.firstName
                        val getLName = elem?.lastName
                        var fullName = ""
                        val getPhone = elem?.phone
                        val getCardNumber = elem?.cardNumber
                        val getCardHolder = elem?.cardHolder
                        val getCardExpiry = elem?.expiryDate

                        if (isEmpty(getFName)) {
                            fullName += getFName
                        }

                        if (!fullName.isNullOrEmpty()) {
                            fullName += " "
                        }

                        if (isEmpty(getLName)) {
                            fullName += getLName
                        }

                        if (isEmpty(fullName)) {
                            donateFNameLayout.setHintAnimationEnabled(false);
                            donateFName.setText(fullName)
                        }

                        if (isEmpty(getPhone)) {
                            donatePhoneLayout.setHintAnimationEnabled(false);
                            donatePhone.setText(getPhone)
                        }

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

        saveButton.setOnClickListener { view ->
            val isFName = validateIsEmpty(donateFName, donateFNameLayout)
            val isEmail = validateIsEmpty(donateEmail, donateEmailLayout)
            val isPhone = validateIsEmptyAndLength(donatePhone, donatePhoneLayout, 10)
            val isAmount = validateIsEmpty(donateAmount, donateAmountLayout)
            val isCardNumber = validateIsEmptyAndLength(cardNumber, cardNumberLayout, 19)
            val isCardHolder = validateIsEmpty(cardHolder, cardHolderLayout)
            val isExpiryDate = validateIsEmpty(expDate, expDateLayout)
            val isCvv = validateIsEmpty(cvv, cvvLayout)

            hideKeyboard(getActivity())

            if (isFName && isEmail && isPhone && isAmount && isCardNumber && isCardHolder && isExpiryDate && isCvv) {

                donateFNameLayout.error = null
                donateEmailLayout.error = null
                donatePhoneLayout.error = null
                donateAmountLayout.error = null
                cardNumberLayout.error = null
                cardHolderLayout.error = null
                expDateLayout.error = null
                cvvLayout.error = null

                val amount = donateAmount.text.toString().toDouble()

                val instant = Instant.now()

                mDatabase.child("events").child(eventId).runTransaction(object : Transaction.Handler {
                    override fun doTransaction(mutableData: MutableData): Transaction.Result {
                        val p = mutableData.getValue(EventObject::class.java)
                            ?: return Transaction.success(mutableData)
                        if (p == null) {
                            return Transaction.success(mutableData)
                        }
                        p.backers = p.backers?.plus(1)
                        p.donate = p.donate?.plus(amount)
                        mutableData.value = p
                        return Transaction.success(mutableData)
                    }

                    override fun onComplete(
                        databaseError: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        // Transaction completed
                        if (databaseError != null) {
                            Log.d(TAG, "postTransaction:onComplete:" + databaseError!!)
                        }
                    }
                })

                // Insert usersDonation into events id
                val eventUpdate: HashMap<String, Any> = HashMap()
                eventUpdate.put("uid", user.uid)
                eventUpdate.put("amount", amount)
                eventUpdate.put("timestamp", instant.epochSecond)

                mDatabase
                    .child("events")
                    .child(eventId)
                    .child("usersDonation").push().setValue(eventUpdate)

                // Insert historyEvent into users
                val userUpdate: HashMap<String, Any> = HashMap()

                mDatabase
                    .child("users")
                    .child(user.uid)
                    .child("historyEvent").push().setValue(userUpdate)
            }
        }
    }

}
