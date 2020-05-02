package com.example.fillfeel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference

/**
 * A simple [Fragment] subclass.
 */
class AddEventFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    val TAG: String = "AddEventFragment"

    lateinit var addEventFoundationLayout: TextInputLayout
    lateinit var addEventFoundation: TextInputEditText
    lateinit var addEventProjectNameLayout: TextInputLayout
    lateinit var addEventProjectName: TextInputEditText
    lateinit var addEventPeriodLayout: TextInputLayout
    lateinit var addEventPeriod: TextInputEditText
    lateinit var addEventGoalLayout: TextInputLayout
    lateinit var addEventGoal: TextInputEditText
    lateinit var addEventTypeOfProgramLayout: TextInputLayout
    lateinit var addEventTypeOfProgram: AppCompatAutoCompleteTextView
    lateinit var typeOfProgramAdapter: ArrayAdapter<String?>
    lateinit var items: List<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        addEventFoundationLayout = view!!.findViewById(R.id.addEventFoundationLayout)
        addEventFoundation = view!!.findViewById(R.id.addEventFoundation)
        addEventProjectNameLayout = view!!.findViewById(R.id.addEventProjectNameLayout)
        addEventProjectName = view!!.findViewById(R.id.addEventProjectName)
        addEventPeriodLayout = view!!.findViewById(R.id.addEventPeriodLayout)
        addEventPeriod = view!!.findViewById(R.id.addEventPeriod)
        addEventGoalLayout = view!!.findViewById(R.id.addEventGoalLayout)
        addEventGoal = view!!.findViewById(R.id.addEventGoal)
        addEventTypeOfProgramLayout = view!!.findViewById(R.id.addEventTypeOfProgramLayout)
        addEventTypeOfProgram = view!!.findViewById(R.id.addEventTypeOfProgram)

        items = listOf("Children and Youth", "Elderer", "Patient and Disabled", "Animal", "Environment")
        typeOfProgramAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        addEventTypeOfProgram.setAdapter(typeOfProgramAdapter)
    }

}
