package com.example.workoutcompanion.activities.home

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.chat.NewMessageActivity
import com.example.workoutcompanion.activities.chat.RegisterActivity
import com.example.workoutcompanion.activities.chat.User
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.DISTANCE_METER
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.PREF
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEPS
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEP_COUNT_UPDATE
import com.example.workoutcompanion.model.WorkoutCompanionViewModel
import com.example.workoutcompanion.model.roomdb.StepCounts
import com.facebook.stetho.Stetho
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var appViewModel: WorkoutCompanionViewModel
    private lateinit var sharedPref: SharedPreferences
    private val stepService = StepCounterService()
    companion object {
        var currentUser: User? = null
    }
    // receives steps broadcast from StepCounter service
    private val stepsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val steps = intent?.getIntExtra(STEPS, 0).toString()
            val stepsFromPref = sharedPref.getFloat(stepService.todayDate, 0f).toString()
            tvSteps.text = stepsFromPref
            // steps progress bar set up
            progressBar.apply {
                setProgressWithAnimation(steps.toFloat(), 2000)
                startAngle = 180f
            }
            Log.d("stepsUI", steps)

            val distance = intent?.getDoubleExtra(DISTANCE_METER, 0.0).toString()
            tvDistance.text = getString(R.string.distance_km, distance)

            // persists the day's count to db and stops counter service
            val owner = "tamanji.ambe@gmail.com"
            val stepsToDB = StepCounts (stepService.todayDate, owner,
                sharedPref.getFloat(stepService.todayDate, 0f))
            appViewModel.addStepsToDb(stepsToDB)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Stetho.initializeWithDefaults(this)
        // request permission to use step counter
        if (Build.VERSION.SDK_INT >= 29) {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 2)
            }
        }

        fetchCurrentUser()
        verifyuserIsLogrdIn()

        bottom_navigation.apply {
            selectedItemId = R.id.home
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@MainActivity, MainActivity::class.java )
            )
        }
        appViewModel = ViewModelProvider(this).get(WorkoutCompanionViewModel::class.java)

        sharedPref = getSharedPreferences(PREF, Context.MODE_PRIVATE)
        tvSteps.apply {
            if (text == getText(R.string.number_of_steps))
            // verify and loads step counts from preferences
                text = sharedPref.getFloat(stepService.todayDate, 0f).toInt().toString()

            setOnLongClickListener {
                stopService(this@MainActivity)
                true
            }
        }
        updateCountInfo(tvDistance, stepService.dateForDistance, R.string.distance_km)


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
    }
    // foreground or background service started based on the SDK version
    private fun startService (context: Context) {
        val startIntent = Intent(context, StepCounterService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
            context.startForegroundService(startIntent)

        }else context.startService(startIntent)
    }

    private fun stopService(context: Context) {
        context.stopService(Intent(context, StepCounterService::class.java))
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser?.username}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    private fun verifyuserIsLogrdIn() {
        val uid =  FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(this, NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override  fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    private fun updateCountInfo(tv: TextView, txt:String, string: Int){
        if (tv.text == getText(string))
            tv.text = getString(string, sharedPref.getString(txt,"0"))
    }
}