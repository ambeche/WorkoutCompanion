package com.example.workoutcompanion.activities.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.UI_UPDATE
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var stepsReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // initialize stetho for debugging: reading room db
       // Stetho.initializeWithDefaults(this)

        bottom_navigation.apply {
            selectedItemId = R.id.home
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@MainActivity, MainActivity::class.java )
            )
        }
        startService(this)

        stepsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val steps = intent?.getStringExtra("steps")
                tvSteps.text = steps
                if (steps != null) {
                    Log.d("stepsUI", steps)
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(stepsReceiver, IntentFilter("Counter"))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(stepsReceiver)
        //stopService(this)
    }

    private fun startService (context: Context) {
        val startIntent = Intent(context, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
            context.startForegroundService(startIntent)

        }else context.startService(startIntent)
    }

    private fun stopService(context: Context) {
        context.stopService(Intent(context, StepCounterService::class.java))
    }
}