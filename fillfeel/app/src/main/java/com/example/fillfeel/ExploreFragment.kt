package com.example.fillfeel

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_explore.*
import java.io.IOException


/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavigation: BottomNavigationView
    private val TAG: String = "ExploreFragment"
    private lateinit var mDatabase: DatabaseReference

    private lateinit var highlightObj: MutableList<ExploreObject>
    private lateinit var childObj: MutableList<ExploreObject>
    private lateinit var elderObj: MutableList<ExploreObject>
    private lateinit var patientObj: MutableList<ExploreObject>
    private lateinit var animalObj: MutableList<ExploreObject>
    private lateinit var environmentObj: MutableList<ExploreObject>

    lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        auth = FirebaseAuth.getInstance()
//        val currentUser = auth.currentUser
//        Log.d("ExploreFragment", currentUser?.email)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation = activity!!.findViewById(R.id.bottom_navigation)
        bottomNavigation.menu.getItem(0).isChecked = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
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
                        rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        rvlist.setHasFixedSize(true)
                        rvlist.adapter = highlightAdapter
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
                        child_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        child_rvlist.setHasFixedSize(true)
                        child_rvlist.adapter = childAdapter
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
                        elder_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        elder_rvlist.setHasFixedSize(true)
                        elder_rvlist.adapter = elderAdapter
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
                        patient_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        patient_rvlist.setHasFixedSize(true)
                        patient_rvlist.adapter = patientAdapter
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
                        animal_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        animal_rvlist.setHasFixedSize(true)
                        animal_rvlist.adapter = animalAdapter
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
                        env_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
                        env_rvlist.setHasFixedSize(true)
                        env_rvlist.adapter = enAdapter
                    }
                }
            })
    }
}
