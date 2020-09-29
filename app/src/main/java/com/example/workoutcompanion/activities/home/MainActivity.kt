package com.example.workoutcompanion.activities.home

import android.Manifest
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEPS
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEP_COUNT_UPDATE
import com.facebook.stetho.Stetho
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val stepsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val steps = intent?.getIntExtra(STEPS, 0).toString()
            tvSteps.text = steps
            Log.d("stepsUI", steps)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // initialize stetho for debugging: reading room db
       // Stetho.initializeWithDefaults(this)

        if (Build.VERSION.SDK_INT >= 29) {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                //ask for permission
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 2);
            }
        }
        bottom_navigation.apply {
            selectedItemId = R.id.home
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@MainActivity, MainActivity::class.java )
            )
        }
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(stepsReceiver, IntentFilter(STEP_COUNT_UPDATE))
        startService(this)
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(stepsReceiver)
        stopService(this)
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