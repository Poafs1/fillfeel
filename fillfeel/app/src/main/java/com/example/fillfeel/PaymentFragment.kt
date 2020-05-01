package com.example.fillfeel

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


/**
 * A simple [Fragment] subclass.
 */
class PaymentFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    lateinit var cardnumber: TextInputEditText;
    lateinit var cardholder: TextInputEditText;
    lateinit var expdate: TextInputEditText;
    lateinit var cvv: TextInputEditText;
    val TAG: String = "Debug"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()

        cardnumber = view!!.findViewById(R.id.cardnumber)
        cardnumber.addTextChangedListener(CreditCardTextFormatter())

        expdate = view!!.findViewById(R.id.expdate)
        expdate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s!!.isNotEmpty() && (s.length % 3) == 0) {
                    val c = s[s.length-1]
                    if (c == '/') {
                        s.delete(s.length-1, s.length)
                    }
                }
                if (s.isNotEmpty() && (s.length % 3) == 0) {
                    val c = s[s.length-1]
                    if (Character.isDigit(c) && TextUtils.split(s.toString(), "/").size <= 2) {
                        s.insert(s.length-1, "/")
                    }
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {}
        })

        cardholder = view!!.findViewById(R.id.cardholder)
        cvv = view!!.findViewById(R.id.cvv)

        val savePayment: AppCompatButton = view!!.findViewById(R.id.savePaymentTextField)
        savePayment.setOnClickListener { view ->
            val postValues: HashMap<String, Any> = HashMap()
            postValues["cardNumber"] = cardnumber.text.toString()
            postValues["cardHolder"] = cardholder.text.toString()
            postValues["expiryDate"] = expdate.text.toString()

            mDatabase.child("users")
                .child("klua")
                .push()
                .setValue("helloworld")
        }
    }
}
