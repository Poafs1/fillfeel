package com.example.fillfeel

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment() {

    private val TAG: String = "DetailsFragment"
    private lateinit var mDatabase: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val bundle = this.arguments
//        var eventId = bundle?.getString("eventId", "")
//        textViewDetail?.setText("Event ID is: "+eventId)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase.child("events").child("id-01")
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot!!.exists()) {
                        val elem = dataSnapshot.getValue(DetailsObject::class.java)
                        val headerTitle: TextView = view!!.findViewById(R.id.detailsHeaderTitle)
                        headerTitle.text = elem?.tag.plus(" Program")

                        val cardImage: CardView = view!!.findViewById(R.id.detailsCard)
                        cardImage.setCardBackgroundColor(Color.parseColor(elem?.paletteImage))

                        val progressBar: ProgressBar = view!!.findViewById(R.id.detailsProgressBar)
                        val divide = elem?.goal?.let { elem.donate?.div(it) }
                        val calProgress = divide?.times(100)
                        if (calProgress != null) {
                            progressBar.setProgress(calProgress.roundToInt())
                        }

                        val currentDonated: TextView = view!!.findViewById(R.id.detailsEventCurrentDonated)
                        if (elem != null) {
                            currentDonated.text = "US$ " + elem.donate.toString()
                        }

                        val eventGoal: TextView = view!!.findViewById(R.id.detailsEventGoal)
                        if (elem != null) {
                            eventGoal.text = "US$ " + elem.goal?.roundToInt().toString()
                        }

                        val currentBuckers: TextView = view!!.findViewById(R.id.detailsBuckers)
                        if (elem != null) {
                            currentBuckers.text = elem.backers.toString()
                        }

//                        val eventStartDate = elem?.timestamps?.let {
//                            Instant.ofEpochSecond(it)
//                                .atZone(ZoneId.systemDefault())
//                                .toLocalDateTime()
//                        }

//                        val dateTime = elem?.period?.let {
//                            Instant.ofEpochSecond(it)
//                                .atZone(ZoneId.systemDefault())
//                                .toLocalDateTime()
//                        }
//                        val currentTime = LocalDateTime.now()
//
//                        val periodDate: TextView = view!!.findViewById(R.id.detailsPeriodDate)
//                        if (dateTime?.isAfter(currentTime)!!) {
//                            if (dateTime.monthValue != currentTime.monthValue) {
//                                val value = dateTime.monthValue - currentTime.monthValue
//                                periodDate.text = value.toString().plus(" Month")
//                            } else {
//                                val value = dateTime.dayOfMonth - currentTime.dayOfMonth
//                                periodDate.text = value.toString().plus(" Day")
//                            }
//                        } else {
//                            periodDate.text = "Closed"
//                        }

//                        val period: Period = Period.between(currentTime.toLocalDate(),
//                            eventStartDate?.toLocalDate()
//                        )
//                        val daysToGo: Int = period.get
//                        Log.d(TAG, period.toString())
                    }
                }
            })
    }

}
