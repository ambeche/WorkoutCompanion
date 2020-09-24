package com.example.workoutcompanion.activities.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.view.View
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.diet.Nutrition
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.apply {
            selectedItemId = R.id.home
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@MainActivity, MainActivity::class.java )
            )
        }

    }
}