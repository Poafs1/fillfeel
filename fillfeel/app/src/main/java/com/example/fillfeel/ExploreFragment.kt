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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_explore.*
import java.io.IOException

/**
 * A simple [Fragment] subclass.
 */
class ExploreFragment : Fragment() {

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_explore, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val data :MutableList<ExploreObject> = ArrayList<ExploreObject>()

        val jsonFileString = getJsonDataFromAsset(requireContext(), "exploreModel.json")
        val gson = Gson()
        val exploreDataType = object : TypeToken<List<ExploreObject>>() {}.type
        val exploreObj: List<ExploreObject> = gson.fromJson(jsonFileString, exploreDataType)
        val highlightsObj = exploreObj.sortedWith(compareByDescending({ it.rate }))

        for (item in 1..5) {
            data.add(highlightsObj[item - 1])
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val adapter = CustomAdapter(data)

        rvlist.layoutManager = layoutManager
        rvlist.setHasFixedSize(true)
        rvlist.adapter = adapter
    }
}
