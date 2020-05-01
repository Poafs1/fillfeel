package com.example.fillfeel

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val items = listOf("Male", "Female", "Unspecified")
        val genderField: AppCompatAutoCompleteTextView = view!!.findViewById(R.id.gender)
        val adapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        (genderField as? AutoCompleteTextView)?.setAdapter(adapter)

        val bdField: TextInputEditText = view!!.findViewById(R.id.bd)
        bdField.addTextChangedListener(object : TextWatcher {
            var sb : StringBuilder = StringBuilder("")
            var _ignore = false
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(_ignore){
                    _ignore = false
                    return
                }
                sb.clear()
                sb.append(if(s!!.length > 10){ s.subSequence(0,10) }else{ s })
                if(sb.lastIndex == 2){
                    if(sb[2] != '/'){
                        sb.insert(2,"/")
                    }
                } else if(sb.lastIndex == 5){
                    if(sb[5] != '/'){
                        sb.insert(5,"/")
                    }
                }
                _ignore = true
                bdField.setText(sb.toString())
                bdField.setSelection(sb.length)
        }})
    }

}
