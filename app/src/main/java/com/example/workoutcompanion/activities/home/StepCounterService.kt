package com.example.workoutcompanion.activities.home

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.workoutcompanion.R
import java.lang.Exception

class StepCounterService : Service(), SensorEventListener {
    private var todayCount = 0f
    private var previousSteps = 0f
    private var currentSteps = 1
    private var rawSteps: Float? = null
    private var sensorManager: SensorManager? = null
    lateinit var sCounter: Sensor
    lateinit var sharedPref: SharedPreferences

     companion object{
        const val PREF = "com.example.workoutcompanion.STEP_COUNTER"
        const val PREVIOUS_STEPS = "com.example.workoutcompanion.PREVIOUS_STEPS"
        const val CHANNEL_ID = "step counter service"
        const val SERVICE_ID = 1
        const val UI_UPDATE = "Current Steps"
    }
    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!!

        sharedPref = getSharedPreferences(PREF, Context.MODE_PRIVATE)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER))  {
            sensorManager?.registerListener(this, sCounter, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("yessssss", sCounter.name)

        } else  toast(getString(R.string.no_sensor))
        // starts foreground service when onStartCommand is called
        startForeground(SERVICE_ID, createNotification())

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            sCounter.also { sensorManager?.unregisterListener(this ) }
        }catch (e:Exception) {
            e.message?.let { Log.d("StepCounter", it) }
        }
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == sCounter) {
            rawSteps = event.values[0]
            currentSteps = rawSteps?.toInt()?.minus(previousSteps.toInt())!!

            LocalBroadcastManager.getInstance(this).sendBroadcast(Intent().apply {
                action = UI_UPDATE
                putExtra("steps", "$currentSteps")
            })
            Log.d("rawSteps", rawSteps.toString())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, getString(R.string.channel_name),
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = getString(R.string.channel_description)
            }
            ( getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).apply {
                createNotificationChannel(channel)
            }
        }
    }

    private fun createNotification() : Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID).apply {
            val intent = Intent(this@StepCounterService, MainActivity::class.java)
                .apply {  Intent.FLAG_ACTIVITY_CLEAR_TASK  }
            setSmallIcon(R.drawable.icon_service_notice)
            setContentText(getString(R.string.notice_title))
            setContentText(getString(R.string.notice_text))
            priority = NotificationCompat.PRIORITY_LOW
            setContentIntent(
                PendingIntent.getActivity(
                    this@StepCounterService, 0, intent, 0 )
            )
            setAutoCancel(true)
        }.build()
    }

    private fun toast(text: String) {
        Toast.makeText(
            this, text, Toast.LENGTH_SHORT).show()
    }
}
