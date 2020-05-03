package com.example.fillfeel

import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.image_sheet_layout.view.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.io.ByteArrayOutputStream
import java.util.*


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
    lateinit var addEventDetailsLayout: TextInputLayout
    lateinit var addEventDetails: TextInputEditText
    lateinit var addEventOverviewLayout: TextInputLayout
    lateinit var addEventOverview: TextInputEditText
    lateinit var addEventPlanLayout: TextInputLayout
    lateinit var addEventPlan: TextInputEditText
    lateinit var addEventAccountNumberLayout: TextInputLayout
    lateinit var addEventAccountNumber: TextInputEditText
    lateinit var saveButton: AppCompatButton
    lateinit var items: List<String>
    lateinit var builder: MaterialDatePicker.Builder<Long>
    lateinit var picker: MaterialDatePicker<Long>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_event, container, false)
    }

    private fun hideKeyboard(activity: Activity?) {
        val inputManager: InputMethodManager? =
            activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as?
                    InputMethodManager
        // check if no view has focus:
        val v = activity?.currentFocus ?: return
        inputManager?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    private fun validateIsEmpty(input: TextInputEditText, layout: TextInputLayout): Boolean {
        val text = input?.text.toString().trim()
        if (text.isNullOrEmpty()) {
            layout?.error = "This field is required"
            return false
        }
        return true
    }

    private fun validateIsEmptyProgram(input: AppCompatAutoCompleteTextView, layout: TextInputLayout): Boolean {
        val text = input?.text.toString().trim()
        if (text.isNullOrEmpty()) {
            layout?.error = "This field is required"
            return false
        }
        return true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

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
        addEventDetailsLayout = view!!.findViewById(R.id.addEventDetailsLayout)
        addEventDetails = view!!.findViewById(R.id.addEventDetails)
        addEventOverviewLayout = view!!.findViewById(R.id.addEventOverviewLayout)
        addEventOverview = view!!.findViewById(R.id.addEventOverview)
        addEventPlanLayout = view!!.findViewById(R.id.addEventPlanLayout)
        addEventPlan = view!!.findViewById(R.id.addEventPlan)
        addEventAccountNumberLayout = view!!.findViewById(R.id.addEventAccountNumberLayout)
        addEventAccountNumber = view!!.findViewById(R.id.addEventAccountNumber)
        saveButton = view!!.findViewById(R.id.saveAddEventTextField)

        builder = MaterialDatePicker.Builder.datePicker()
        picker = builder.build()

        items = listOf("Children and Youth", "Elderer", "Patient and Disabled", "Animal", "Environment")
        typeOfProgramAdapter = ArrayAdapter(requireContext(), R.layout.list_item, items)
        addEventTypeOfProgram.setAdapter(typeOfProgramAdapter)

        addEventPeriod.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val c: Calendar = Calendar.getInstance()
                val mYear = c.get(Calendar.YEAR)
                val mMonth = c.get(Calendar.MONTH)
                val mDay = c.get(Calendar.DAY_OF_MONTH)

                val datePickerDialog = context?.let {
                    DatePickerDialog(
                        it,
                        OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                            addEventPeriod.setText(
                                        dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year
                                    )
                        }, mYear, mMonth, mDay
                    )
                }

                datePickerDialog?.show()
            }
        })

        saveButton.setOnClickListener { view ->
            val isFoundation = validateIsEmpty(addEventFoundation, addEventFoundationLayout)
            val isProjectName = validateIsEmpty(addEventProjectName, addEventProjectNameLayout)
            val isPeriod = validateIsEmpty(addEventPeriod, addEventPeriodLayout)
            val isGoal = validateIsEmpty(addEventGoal, addEventGoalLayout)
            val isTypeOfProgram = validateIsEmptyProgram(addEventTypeOfProgram, addEventTypeOfProgramLayout)
            val isDetails = validateIsEmpty(addEventDetails, addEventDetailsLayout)
            val isAccountNumber = validateIsEmpty(addEventAccountNumber, addEventAccountNumberLayout)

            hideKeyboard(getActivity())

            if (isFoundation && isProjectName && isPeriod && isGoal && isGoal && isTypeOfProgram && isDetails && isAccountNumber) {
                addEventFoundationLayout.error = null
                addEventProjectNameLayout.error = null
                addEventPeriodLayout.error = null
                addEventGoalLayout.error = null
                addEventTypeOfProgramLayout.error = null
                addEventDetailsLayout.error = null
                addEventAccountNumberLayout.error = null

                val instant = Instant.now()

                // save to database
                val eventUpdate: HashMap<String, Any> = HashMap()
                eventUpdate.put("account", addEventAccountNumber.text.toString())
                eventUpdate.put("backers", 0)
                eventUpdate.put("details", addEventDetails.text.toString())
                eventUpdate.put("donate", 0)
                eventUpdate.put("goal", addEventGoal.text.toString())
                eventUpdate.put("overview", addEventOverview.text.toString())
                eventUpdate.put("plan", addEventPlan.text.toString())
                eventUpdate.put("rate", 4.6)
                eventUpdate.put("status", false)
                eventUpdate.put("tag", addEventTypeOfProgram.text.toString())
                eventUpdate.put("title", addEventProjectName.text.toString())
                eventUpdate.put("foundation", addEventFoundation.text.toString())
                eventUpdate.put("timestamp", instant.epochSecond)

                val getPeriod = addEventPeriod.text.toString()
                val changeFormat = getPeriod.split("/")
                val newFormat = changeFormat[2] + "-0" + changeFormat[1] + "-0" + changeFormat[0] + " 00:00:00"

                val localDateTime = LocalDateTime.parse(newFormat, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    .atZone(ZoneId.of("America/Los_Angeles"));
                val time = localDateTime.toInstant().toEpochMilli()

                eventUpdate.put("period", time)

                mDatabase.child("events").push().setValue(eventUpdate)

                Snackbar.make(view, "Thank you for create new program", Snackbar.LENGTH_SHORT).show();
                val activity = view.context as AppCompatActivity
                val fragment = ExploreFragment()

                activity.getSupportFragmentManager()
                    .beginTransaction().replace(R.id.root_layout, fragment).addToBackStack(null).commit();
            }
        }
    }

}
