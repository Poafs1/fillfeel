package com.example.fillfeel

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.temporal.ChronoUnit
import javax.xml.datatype.DatatypeConstants.DAYS
import kotlin.math.roundToInt
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class DetailsFragment : Fragment() {

    private val TAG: String = "DetailsFragment"
    private lateinit var mDatabase: DatabaseReference
    private lateinit var eventId: String
    private lateinit var eventImg: String
    private lateinit var title: String

    lateinit var savedCurrent: String
    lateinit var savedGoal: String
    lateinit var savedBacker: String
    var savedEndDate by Delegates.notNull<Long>()

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    lateinit var detailsSaved: ImageView
    var isSaved: Boolean = false

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

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        val colorQuoteWhite = getResources().getString(R.color.colorQuoteWhite);
        val colorPrimaryPink = getResources().getString(R.color.colorPrimaryPink);

        val detailsSaved: ImageView = view!!.findViewById(R.id.detailsSaved)
        detailsSaved.setColorFilter(Color.parseColor(colorQuoteWhite))

        mDatabase.child("users").child(user.uid).child("savedEvent").child(eventId)
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        detailsSaved.setColorFilter(Color.parseColor(colorPrimaryPink))
                        isSaved = true
                    } else {
                        detailsSaved.setColorFilter(Color.parseColor(colorQuoteWhite))
                        isSaved = false
                    }
                }
            })

        //saveButton
        detailsSaved.setOnClickListener{view ->
            if (isSaved) {
                // remove from savedEvent
                mDatabase
                    .child("users")
                    .child(user.uid)
                    .child("savedEvent")
                    .child(eventId).setValue(null)
//                Snackbar.make(view, "Saved event has been remove", Snackbar.LENGTH_LONG).show();
            } else {
                // push into savedEvent
                val childUpdates: MutableMap<String, Any> = mutableMapOf()
                childUpdates.put(eventId+"/current", savedCurrent)
                childUpdates.put(eventId+"/goal", savedGoal)
                childUpdates.put(eventId+"/backer", savedBacker)
                childUpdates.put(eventId+"/endDate", savedEndDate)
//                childUpdates.put(eventId+"/timestamp", System.currentTimeMillis())
                mDatabase
                    .child("users")
                    .child(user.uid)
                    .child("savedEvent")
                    .updateChildren(childUpdates)

//                Snackbar.make(view, "Event has been saved", Snackbar.LENGTH_LONG).show();
            }
        }

        val joinButton: AppCompatButton = view!!.findViewById(R.id.joinButton)
        joinButton.setOnClickListener{view ->
            val activity = view.context as AppCompatActivity
            val fragment = DonateFragment()

            val bundle = Bundle()
            bundle.putString("eventId", eventId)
            bundle.putString("title", title)
            fragment.setArguments(bundle)

            activity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.root_layout, fragment).addToBackStack(null).commit();
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
                            savedCurrent = elem.donate.toString()
                        }

                        val eventGoal: TextView = view!!.findViewById(R.id.detailsEventGoal)
                        if (elem != null) {
                            eventGoal.text = "goal: US$ " + elem.goal?.roundToInt().toString()
                            savedGoal = elem.goal?.roundToInt().toString()
                        }

                        val currentBackers: TextView = view!!.findViewById(R.id.detailsBackers)
                        if (elem != null) {
                            currentBackers.text = elem.backers.toString()
                            savedBacker = elem.backers.toString()
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
                        savedEndDate = elem?.period!!

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
                        detailsTitle.text = elem?.title
                        title = elem?.title.toString()

                        val detailsEventDetail: TextView = view!!.findViewById(R.id.detailsEventDetail)
                        detailsEventDetail.text = elem?.details

                        val detailsOverview: TextView = view!!.findViewById(R.id.detailsOverview)
                        detailsOverview.text = elem?.overview

                        val detailsPlan: TextView = view!!.findViewById(R.id.detailsPlan)
                        detailsPlan.text = elem?.plan
                    }
                }
            })
    }

}
