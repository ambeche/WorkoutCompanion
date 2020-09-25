package com.example.workoutcompanion.activities.routines

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import kotlinx.android.synthetic.main.activity_main.*

class RoutinesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routines)
        title = getString(R.string.activity_title_routines)
        bottom_navigation.apply {
            selectedItemId = R.id.routines
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@RoutinesActivity, RoutinesActivity::class.java )
            )
        }
    }
}