package com.example.workoutcompanion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.home -> {

                    true
                }
                R.id.diets -> {

                    true
                }
                R.id.chat -> {

                    true
                }
                R.id.routines -> {

                    true
                }
                else -> false
            }
        }
    }
}