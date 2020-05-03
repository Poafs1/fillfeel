package com.example.fillfeel

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslator
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_explore.*
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment : Fragment() {
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private val TAG: String = "ExploreFragment"

    lateinit var englishThaiTranslator: FirebaseTranslator
    lateinit var thaiEnglishTranslator: FirebaseTranslator

    private lateinit var bottomNavigation: BottomNavigationView

    private lateinit var highlightObj: MutableList<ExploreObject>
    private lateinit var childObj: MutableList<ExploreObject>
    private lateinit var elderObj: MutableList<ExploreObject>
    private lateinit var patientObj: MutableList<ExploreObject>
    private lateinit var animalObj: MutableList<ExploreObject>
    private lateinit var environmentObj: MutableList<ExploreObject>

    lateinit var hightlightTitle: TextView
    lateinit var childTitle: TextView
    lateinit var eldererTitle: TextView
    lateinit var patientTitle: TextView
    lateinit var animalTitle: TextView
    lateinit var environmentTitle: TextView

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation = activity!!.findViewById(R.id.bottom_navigation)
        bottomNavigation.menu.getItem(0).isChecked = true

        hightlightTitle = view!!.findViewById(R.id.highlight_principle)
        childTitle = view!!.findViewById(R.id.child_principle)
        eldererTitle = view!!.findViewById(R.id.elder_principle)
        patientTitle = view!!.findViewById(R.id.patient_principle)
        animalTitle = view!!.findViewById(R.id.animal_principle)
        environmentTitle = view!!.findViewById(R.id.env_principle)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
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

    fun languageChange(lang: String) {
        if (lang == "en") {
            translateToEn(hightlightTitle)
            translateToEn(childTitle)
            translateToEn(eldererTitle)
            translateToEn(patientTitle)
            translateToEn(animalTitle)
            translateToEn(environmentTitle)
        } else {
            translateToTh(hightlightTitle)
            translateToTh(childTitle)
            translateToTh(eldererTitle)
            translateToTh(patientTitle)
            translateToTh(animalTitle)
            translateToTh(environmentTitle)
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

        mDatabase.child("events").orderByChild("rate").limitToLast(5)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    highlightObj = mutableListOf()
                    if (dataSnapshot!!.exists()) {
                        for(e in dataSnapshot.children) {
                            val elem = e.getValue(ExploreObject::class.java)
                            if (elem != null) {
                                elem.id = e.getKey()
                            }
                            highlightObj.add(elem!!)
                        }
                        highlightObj.reverse()
                        val highlightAdapter = CustomAdapter(highlightObj)
                        val context: Context = context ?: return
                        if (context != null && rvlist != null) {
                            rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            rvlist.setHasFixedSize(true)
                            rvlist.adapter = highlightAdapter
                        }
                    }
                }
            })

        mDatabase.child("events").orderByChild("tag").equalTo("Children and youth")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    childObj = mutableListOf()
                    if (dataSnapshot!!.exists()) {
                        for(e in dataSnapshot.children) {
                            val elem = e.getValue(ExploreObject::class.java)
                            if (elem != null) {
                                elem.id = e.getKey()
                            }
                            childObj.add(elem!!)
                        }
                        val childAdapter = miniAdapter(childObj)
                        val context: Context = context ?: return
                        if (context != null) {
                            child_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            child_rvlist.setHasFixedSize(true)
                            child_rvlist.adapter = childAdapter
                        }
                    }
                }
            })

        mDatabase.child("events").orderByChild("tag").equalTo("Elderer")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    elderObj = mutableListOf()
                    if (dataSnapshot!!.exists()) {
                        for(e in dataSnapshot.children) {
                            val elem = e.getValue(ExploreObject::class.java)
                            if (elem != null) {
                                elem.id = e.getKey()
                            }
                            elderObj.add(elem!!)
                        }
                        val elderAdapter = miniAdapter(elderObj)
                        val context: Context = context ?: return
                        if (context != null) {
                            elder_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            elder_rvlist.setHasFixedSize(true)
                            elder_rvlist.adapter = elderAdapter
                        }
                    }
                }
            })

        mDatabase.child("events").orderByChild("tag").equalTo("Patient and disabled")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    patientObj = mutableListOf()
                    if (dataSnapshot!!.exists()) {
                        for(e in dataSnapshot.children) {
                            val elem = e.getValue(ExploreObject::class.java)
                            if (elem != null) {
                                elem.id = e.getKey()
                            }
                            patientObj.add(elem!!)
                        }
                        val patientAdapter = miniAdapter(patientObj)
                        val context: Context = context ?: return
                        if (context != null) {
                            patient_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            patient_rvlist.setHasFixedSize(true)
                            patient_rvlist.adapter = patientAdapter
                        }
                    }
                }
            })

        mDatabase.child("events").orderByChild("tag").equalTo("Animal")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    animalObj = mutableListOf()
                    if (dataSnapshot!!.exists()) {
                        for(e in dataSnapshot.children) {
                            val elem = e.getValue(ExploreObject::class.java)
                            if (elem != null) {
                                elem.id = e.getKey()
                            }
                            animalObj.add(elem!!)
                        }
                        val animalAdapter = miniAdapter(animalObj)
                        val context: Context = context ?: return
                        if (context != null) {
                            animal_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            animal_rvlist.setHasFixedSize(true)
                            animal_rvlist.adapter = animalAdapter
                        }
                    }
                }
            })

        mDatabase.child("events").orderByChild("tag").equalTo("Environment")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e(TAG, databaseError.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    environmentObj = mutableListOf()
                    if (dataSnapshot!!.exists()) {
                        for(e in dataSnapshot.children) {
                            val elem = e.getValue(ExploreObject::class.java)
                            if (elem != null) {
                                elem.id = e.getKey()
                            }
                            environmentObj.add(elem!!)
                        }
                        val enAdapter = miniAdapter(environmentObj)
                        val context: Context = context ?: return
                        if (context != null) {
                            env_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                            env_rvlist.setHasFixedSize(true)
                            env_rvlist.adapter = enAdapter
                        }
                    }
                }
            })
    }
}
