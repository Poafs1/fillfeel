package com.example.fillfeel

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    private lateinit var bottomNavigation: BottomNavigationView
    lateinit var historyToExplore: AppCompatButton

    private val TAG: String = "HistoryFragment"
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    lateinit var visualCard: FrameLayout
    lateinit var visualSVG: FrameLayout

    private lateinit var HistoryObj: MutableList<HistoryObject>

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

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        visualCard = view!!.findViewById(R.id.havehistory)
        visualSVG = view!!.findViewById(R.id.nohistory)

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

        historyToExplore = view!!.findViewById(R.id.historyToExplore)
        historyToExplore.setOnClickListener{view ->
            val activity = view.context as AppCompatActivity
            val fragment = ExploreFragment()

            activity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.root_layout, fragment).addToBackStack(null).commit();
        }
    }

}
