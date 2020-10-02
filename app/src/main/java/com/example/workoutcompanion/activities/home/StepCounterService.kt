package com.example.workoutcompanion.activities.home

import android.app.*
import android.content.Context
import android.content.Intent
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
import androidx.core.app.NotificationCompat
import com.example.workoutcompanion.R
import java.text.DateFormat
import java.util.*
import kotlin.math.roundToInt

class StepCounterService : Service(), SensorEventListener {
    val todayDate: String = DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date())
    val dateForDistance = "$todayDate-km"
    private var stepsBeforeReset = 0f
    private var previousSteps = 0f
    private var currentSteps = 0
    private var rawSteps: Float = 0f
    private var sensorManager: SensorManager? = null
    private var sCounter: Sensor? = null
    private lateinit var sharedPref: SharedPreferences

    companion object{
        const val PREF = "com.example.workoutcompanion.activities.STEP_COUNTER"
        const val PREVIOUS_STEPS = "com.example.workoutcompanion.activities.PREVIOUS_STEPS"
        const val CHANNEL_ID = "com.example.workoutcompanion.activities.STEP_COUNTER_SERVICE"
        const val SERVICE_ID = 1
        const val STEP_COUNT_UPDATE = "com.example.workoutcompanion.activities.ACTION_STEPS"
        const val STEPS = "com.example.workoutcompanion.activities.MESSAGE_STEPS"
        const val DISTANCE_METER = "com.example.workoutcompanion.activities.DISTANCE_METER"
    }

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sCounter = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        sharedPref = getSharedPreferences(PREF, Context.MODE_PRIVATE)

        if (packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER))  {
            sensorManager?.registerListener(this, sCounter, SensorManager.SENSOR_DELAY_NORMAL)
            sCounter?.name?.let { Log.d("PERMITTED", it) }

        } else  toast(getString(R.string.no_sensor))
        // starts foreground service when onStartCommand is called
        startForeground(SERVICE_ID, createNotification())
        Log.d("offset", rawSteps.toString())

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == sCounter) {
           // val todaySteps = sharedPref.getFloat(todayDate, 0f)
            updateAndResetCount(event.values[0])
            rawSteps = event.values[0]
            currentSteps =( rawSteps.toInt() + stepsBeforeReset.toInt()).minus(previousSteps.toInt())
            saveSteps()
            Log.d("$todayDate rawSteps", rawSteps.toString())
            sendBroadcast(Intent().apply {
                action = STEP_COUNT_UPDATE
                putExtra(STEPS, currentSteps)
                putExtra(DISTANCE_METER, calculateDistanceFromSteps())
            })
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("${sensor?.name}", "$accuracy")
    }

    private fun saveSteps() {
        val previousRawSteps =rawSteps
        sharedPref.edit().putFloat(todayDate, currentSteps.toFloat()).apply()
        sharedPref.edit().putFloat(PREVIOUS_STEPS, previousRawSteps).apply()
        sharedPref.edit().putString(dateForDistance, calculateDistanceFromSteps().toString()).apply()
    }

    private fun calculateDistanceFromSteps(): Double{
        val aveStepLength = 0.6858
        return ((aveStepLength * currentSteps) / 1000 * 100.0).roundToInt() /100.0
    }

    private fun updateAndResetCount (rawValue: Float) {
        val todaySteps = sharedPref.getFloat(todayDate, 0f)
        if (rawValue == 0f && todaySteps > 0f) {
            stepsBeforeReset += todaySteps.toInt()
        }else if (rawValue > 0f && todaySteps == 0f){
            previousSteps = sharedPref.getFloat(PREVIOUS_STEPS, 0f)
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            sCounter.also { sensorManager?.unregisterListener(this ) }
        }catch (e:Exception) {
            e.message?.let { Log.d("StepCounter", it) }
        }
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
            setContentText(getString(R.string.notice_text, currentSteps))
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

