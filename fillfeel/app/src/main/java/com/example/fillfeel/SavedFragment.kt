package com.example.fillfeel

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class SavedFragment : Fragment() {

    private lateinit var bottomNavigation: BottomNavigationView
    lateinit var savedToExplore: AppCompatButton

    private val TAG: String = "SavedFragment"
    private lateinit var mDatabase: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_saved, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bottomNavigation = activity!!.findViewById(R.id.bottom_navigation)
        bottomNavigation.menu.getItem(2).isChecked = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mDatabase = FirebaseDatabase.getInstance().getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        mDatabase.child("users").child(user.uid).child("savedEvent")
            .addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e(TAG, p0.message)
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        // Has saved
                    } else {
                        // Visible SVG Image
                    }
                }
            })

        savedToExplore = view!!.findViewById(R.id.savedToExplore)
        savedToExplore.setOnClickListener{view ->
            val activity = view.context as AppCompatActivity
            val fragment = ExploreFragment()

            activity.getSupportFragmentManager()
                .beginTransaction().replace(R.id.root_layout, fragment).addToBackStack(null).commit();
        }
    }

}
