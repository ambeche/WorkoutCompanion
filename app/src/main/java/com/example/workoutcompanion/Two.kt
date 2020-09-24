package com.example.workoutcompanion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class Two : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two)
        title = "two"
        bottom_navigation.apply {
            selectedItemId = R.id.diets
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@Two, Two::class.java ))
        }
    }
}