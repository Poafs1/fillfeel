package com.example.fillfeel

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.annotation.IdRes
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_details.*
import kotlinx.android.synthetic.main.fragment_explore.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var bottomNavigation: BottomNavigationView

    fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

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

        //Create Data Array
        val highlightData :MutableList<ExploreObject> = ArrayList<ExploreObject>()
        val childData : MutableList<ExploreObject> = ArrayList<ExploreObject>()
        val elderData : MutableList<ExploreObject> = ArrayList<ExploreObject>()
        val patientData : MutableList<ExploreObject> = ArrayList<ExploreObject>()
        val animalData : MutableList<ExploreObject> = ArrayList<ExploreObject>()
        val envData : MutableList<ExploreObject> = ArrayList<ExploreObject>()

        //read json data
        val jsonFileString = getJsonDataFromAsset(requireContext(), "exploreModel.json")
        val gson = Gson()
        val exploreDataType = object : TypeToken<List<ExploreObject>>() {}.type
        val exploreObj: List<ExploreObject> = gson.fromJson(jsonFileString, exploreDataType)

        //create object & filled filter data
        val highlightsObj = exploreObj.sortedWith(compareByDescending({it.rate}))
        val childObj = exploreObj.filter({it.tag == "Children and youth"})
        val elderObj = exploreObj.filter({it.tag == "Elderer"})
        val patientObj = exploreObj.filter({it.tag == "Patient and disabled"})
        val animalObj = exploreObj.filter({it.tag == "Animal"})
        val envObj = exploreObj.filter({it.tag == "Environment"})

        //loop for adding object to the list
        //in highlight will show only 5 items, others shows all
        for (item in 1..5) {
            highlightData.add(highlightsObj[item - 1])
        }
        for (item in 1..childObj.size) {
            childData.add(childObj[item - 1])
        }
        for (item in 1..elderObj.size) {
            elderData.add(elderObj[item - 1])
        }
        for (item in 1..patientObj.size) {
            patientData.add(patientObj[item - 1])
        }
        for (item in 1..animalObj.size) {
            animalData.add(animalObj[item - 1])
        }
        for (item in 1..envObj.size) {
            envData.add(envObj[item - 1])
        }

        //send data to adapter
        //CustomAdapter is big card_view
        //miniAdapter is small card_view
        val highlightAdapter = CustomAdapter(highlightData)
        val childAdapter = miniAdapter(childData)
        val elderAdapter = miniAdapter(elderData)
        val patientAdapter = miniAdapter(patientData)
        val animalAdapter = miniAdapter(animalData)
        val envAdapter = miniAdapter(envData)

        //highlight
        rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvlist.setHasFixedSize(true)
        rvlist.adapter = highlightAdapter
        //child
        child_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        child_rvlist.setHasFixedSize(true)
        child_rvlist.adapter = childAdapter
        //elder
        elder_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        elder_rvlist.setHasFixedSize(true)
        elder_rvlist.adapter = elderAdapter
        //patient
        patient_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        patient_rvlist.setHasFixedSize(true)
        patient_rvlist.adapter = patientAdapter
        //animal
        animal_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        animal_rvlist.setHasFixedSize(true)
        animal_rvlist.adapter = animalAdapter
        //env
        env_rvlist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        env_rvlist.setHasFixedSize(true)
        env_rvlist.adapter = envAdapter

    }
}
