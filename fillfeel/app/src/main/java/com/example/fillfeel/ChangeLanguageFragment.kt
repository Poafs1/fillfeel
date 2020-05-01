package com.example.fillfeel

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.facebook.FacebookSdk.getApplicationContext

/**
 * A simple [Fragment] subclass.
 */
class ChangeLanguageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_language, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val radioGroup: RadioGroup = view!!.findViewById(R.id.radioLanguage)
        radioGroup.setOnCheckedChangeListener{radioGroup, optionId ->
            run {
                when (optionId) {
                    R.id.langEN -> {
                        Toast.makeText(getApplicationContext(), "English Language", Toast.LENGTH_SHORT).show();
                    }
                    R.id.langTH -> {
                        Toast.makeText(getApplicationContext(), "Thai Language", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
