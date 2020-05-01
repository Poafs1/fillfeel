package com.example.fillfeel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomnavigation.BottomNavigationView

class HistoryFragment : Fragment() {

    private lateinit var bottomNavigation: BottomNavigationView
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
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        historyToExplore = view!!.findViewById(R.id.historyToExplore)
        historyToExplore.setOnClickListener{view ->
            val activity = view.context as AppCompatActivity
            val fragment = ExploreFragment()

            activity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.root_layout, fragment).addToBackStack(null).commit();
        }
    }

}
