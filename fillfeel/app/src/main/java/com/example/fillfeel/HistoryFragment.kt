package com.example.fillfeel

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val TAG: String = "HistoryFragment"

    lateinit var englishThaiTranslator: FirebaseTranslator
    lateinit var thaiEnglishTranslator: FirebaseTranslator

    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var HistoryObj: MutableList<HistoryObject>

    lateinit var visualCard: FrameLayout
    lateinit var visualSVG: FrameLayout

    lateinit var noHistoryTitle: TextView
    lateinit var haveHistoryTitle: TextView
    lateinit var history_detail: TextView
    lateinit var historyToExplore: AppCompatButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation = activity!!.findViewById(R.id.bottom_navigation)
        bottomNavigation.menu.getItem(1).isChecked = true

        noHistoryTitle = view!!.findViewById(R.id.nohistory_principle)
        haveHistoryTitle = view!!.findViewById(R.id.havehistory_principle)
        history_detail = view!!.findViewById(R.id.history_detail)
        historyToExplore = view!!.findViewById(R.id.historyToExplore)
    }

    fun translateToEn(view: TextView) {
        val text = view.text.toString()
        thaiEnglishTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToTh(view: TextView) {
        val text = view.text.toString()
        englishThaiTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToEnButton(view: AppCompatButton) {
        val text = view.text.toString()
        thaiEnglishTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun translateToThButton(view: AppCompatButton) {
        val text = view.text.toString()
        englishThaiTranslator.translate(text)
            .addOnSuccessListener { translatedText ->
                view.text = translatedText
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    fun languageChange(lang: String) {
        if (lang == "en") {
            translateToEn(noHistoryTitle)
            translateToEn(haveHistoryTitle)
            translateToEn(history_detail)
            translateToEnButton(historyToExplore)
        } else {
            translateToTh(noHistoryTitle)
            translateToTh(haveHistoryTitle)
            translateToTh(history_detail)
            translateToThButton(historyToExplore)
        }
        return
    }

    fun initTranslation() {
        val options1 = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.EN)
            .setTargetLanguage(FirebaseTranslateLanguage.TH)
            .build()

        val options2 = FirebaseTranslatorOptions.Builder()
            .setSourceLanguage(FirebaseTranslateLanguage.TH)
            .setTargetLanguage(FirebaseTranslateLanguage.EN)
            .build()

        englishThaiTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options1)
        englishThaiTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }

        thaiEnglishTranslator = FirebaseNaturalLanguage.getInstance().getTranslator(options2)
        thaiEnglishTranslator.downloadModelIfNeeded()
            .addOnSuccessListener {
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, exception.toString())
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initTranslation()

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        visualCard = view!!.findViewById(R.id.havehistory)
        visualSVG = view!!.findViewById(R.id.nohistory)

        mDatabase
            .child("users")
            .child(user.uid).child("lang")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        languageChange(dataSnapshot.value.toString())
                    }
                }
            })

        mDatabase.child("users").child(user.uid).child("historyEvent")
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Has saved
                        visualSVG.visibility = View.GONE
                        visualCard.visibility = View.VISIBLE
                        HistoryObj = mutableListOf()
                        if (dataSnapshot!!.exists()) {
                            for(e in dataSnapshot.children) {
                                val elem = e.getValue(HistoryObject::class.java)
                                HistoryObj.add(elem!!)
                            }
                            HistoryObj.reverse()
                            val historyAdapter = HistoryAdapter(HistoryObj)
                            val context: Context = context ?: return
                            if (context != null && history_rvlist!= null) {
                                history_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                                history_rvlist.setHasFixedSize(true)
                                history_rvlist.adapter = historyAdapter
                            }
                        }
                    } else {
                        // Visible SVG Image
                        visualSVG.visibility = View.VISIBLE
                        visualCard.visibility = View.INVISIBLE
                    }
                }
            })

        historyToExplore.setOnClickListener{view ->
            val activity = view.context as AppCompatActivity
            val fragment = ExploreFragment()

            activity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.root_layout, fragment).addToBackStack(null).commit();
        }
    }

}
