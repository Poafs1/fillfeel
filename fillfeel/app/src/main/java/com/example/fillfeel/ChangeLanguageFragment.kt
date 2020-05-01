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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

/**
 * A simple [Fragment] subclass.
 */
class ChangeLanguageFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_language, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

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
