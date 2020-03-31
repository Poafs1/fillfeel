package com.example.fillfeel

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.bottomNavigationExploreMenuId -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, ExploreFragment())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottomNavigationHistoryMenuId -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, HistoryFragment())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottomNavigationSavedMenuId -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, SavedFragment())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            R.id.bottomNavigationProfileMenuId -> {
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.root_layout, ProfileFragment())
                    .commit()
                return@OnNavigationItemSelectedListener true
            }
            else -> return@OnNavigationItemSelectedListener false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        if(savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.root_layout, ExploreFragment())
                .commit()
        }
    }
}
