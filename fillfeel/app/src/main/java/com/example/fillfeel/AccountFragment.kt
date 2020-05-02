package com.example.fillfeel

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

/**
 * A simple [Fragment] subclass.
 */
class AccountFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
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
    lateinit var saveButton: AppCompatButton
    lateinit var genderAdapter: ArrayAdapter<String?>
    lateinit var items: List<String>

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
