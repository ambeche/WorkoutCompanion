package com.example.workoutcompanion

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottom_navigation.setOnNavigationItemSelectedListener(BottomNavListener())
    }

    private inner class BottomNavListener: BottomNavigationView.OnNavigationItemSelectedListener{
        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when(item.itemId) {
                R.id.home -> {
                    startActivity(Two::class.java)
                    true
                }
                R.id.diets -> {
                    startActivity(Three::class.java)
                    true
                }
                R.id.chat -> {
                    startActivity(Four::class.java)
                    true
                }
                R.id.routines -> {

                    true
                }
                else -> false
            }
            return true
        }

        fun startActivity (cls: Class<*>) {
            startActivity(Intent(this@MainActivity, cls))
        }

    }

}