package com.example.fillfeel

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.xml.datatype.DatatypeConstants.DAYS
import kotlin.math.roundToInt


/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment() {

    private val TAG: String = "DetailsFragment"
    private lateinit var mDatabase: DatabaseReference
    private lateinit var eventId: String
    private lateinit var eventImg: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = this.arguments
        eventId = bundle?.getString("eventId", "").toString()
        eventImg = bundle?.getString("eventImg", "").toString()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bookmark: ImageView = view!!.findViewById(R.id.detailsBk)
        bookmark.setColorFilter(Color.parseColor("#B3B3B3"))

        bookmark.setOnClickListener{view ->
            Log.d(TAG, view.backgroundTintMode.toString())
        }

        mDatabase = FirebaseDatabase.getInstance().getReference()
        mDatabase.child("events").child(eventId)
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                @SuppressLint("SetTextI18n")
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot!!.exists()) {
                        val elem = dataSnapshot.getValue(DetailsObject::class.java)
                        val titleImage: ImageView = view!!.findViewById(R.id.detailsCardImg)

                        Picasso.get().load(eventImg).into(titleImage)

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

                        val createDate = elem?.timestamps?.let {
                            Instant.ofEpochSecond(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                        }
                        val endDate = elem?.period?.let {
                            Instant.ofEpochSecond(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                        }
                        val nowDate = LocalDateTime.now()

                        val periodDate: TextView = view!!.findViewById(R.id.detailsPeriodDate)
                        val daysToGo: TextView = view!!.findViewById(R.id.detailsDaysToGo)

                        if (endDate?.isAfter(nowDate)!!) {
                            if (createDate != null) {
                                if (endDate != null) {
                                    periodDate.text = (endDate.monthValue - createDate.monthValue).toString() + " months"
                                }
                            }
                        } else {
                            periodDate.text = "Closed"
                        }

                        val daysBetween: Long = ChronoUnit.DAYS.between(nowDate, endDate)
                        daysToGo.text = daysBetween.toString()

                        val detailsTitle: TextView = view!!.findViewById(R.id.detailsTitle)
                        detailsTitle.text = elem.title

                        val detailsEventDetail: TextView = view!!.findViewById(R.id.detailsEventDetail)
                        detailsEventDetail.text = elem.details

                        val detailsOverview: TextView = view!!.findViewById(R.id.detailsOverview)
                        detailsOverview.text = elem.overview

                        val detailsPlan: TextView = view!!.findViewById(R.id.detailsPlan)
                        detailsPlan.text = elem.plan
                    }
                }
            })
    }

}
