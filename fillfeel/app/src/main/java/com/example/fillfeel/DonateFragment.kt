package com.example.fillfeel

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.datepicker.DateValidatorPointBackward.before
import com.google.android.material.textfield.TextInputEditText


/**
 * A simple [Fragment] subclass.
 */
class DonateFragment : Fragment() {
    lateinit var eventId: String
    lateinit var title: String

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val donateTitle: TextView = view!!.findViewById(R.id.donateTitle)
        donateTitle.text = title

        val cardnumber: TextInputEditText = view!!.findViewById(R.id.donateCardnumber)
        cardnumber.addTextChangedListener(CreditCardTextFormatter())

        val expdate: TextInputEditText = view!!.findViewById(R.id.donateExpdate)
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
    }

}
