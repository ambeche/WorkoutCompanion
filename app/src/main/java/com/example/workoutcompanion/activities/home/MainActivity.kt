package com.example.workoutcompanion.activities.home
/*
* Home screen
* Request system permissions
* initialize parameters
*/

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workoutcompanion.AppHelperFunctions
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.MusicActivity
import com.example.workoutcompanion.activities.chat.RegisterActivity
import com.example.workoutcompanion.activities.chat.User
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.DISTANCE_METER
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.PREF
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEPS
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEP_COUNT_UPDATE
import com.example.workoutcompanion.activities.profile.ProfileActivity
import com.example.workoutcompanion.interfaces.OnLoadFragment
import com.example.workoutcompanion.model.WorkoutCompanionViewModel
import com.example.workoutcompanion.model.roomdb.StepCounts
import com.facebook.stetho.Stetho
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnLoadFragment {
    private lateinit var appViewModel: WorkoutCompanionViewModel
    private lateinit var sharedPref: SharedPreferences
    private val stepService = StepCounterService()
    private var isRunning = false

    companion object {
        var currentUser: User? = null
        const val IS_RUNNING = "isRunning"
    }

    // receives steps broadcast from StepCounter service
    private val stepsReceiver: StepsReceiver = object : StepsReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            isRunning =  intent.getBooleanExtra(IS_RUNNING, true)
            val steps = intent.getIntExtra(STEPS, 0).toString()
            val stepsFromPref = sharedPref.getFloat(stepService.todayDate, 0f)
            tvSteps.text = stepsFromPref.toInt().toString()
            // steps progress bar set up
            progressBar.apply {
                setProgressWithAnimation(steps.toFloat(), 2000)
                startAngle = 180f
            }
            Log.d("stepsUI", steps)

            val distance = intent.getDoubleExtra(DISTANCE_METER, 0.0).toString()
            tvDistance.text = getString(R.string.distance_km, distance)

            val acc = sharedPref.getString(stepService.dateForAcc, "0")
            tvAcceleration.text = getString(R.string.walking_acc, acc)

            val calories = sharedPref.getFloat(stepService.dateForCalories, 0f)
            tvCalories.text = getString(R.string.calories_cal, calories.toString())

            // persists the day's count to db and stops counter service
            if (currentUser !== null) {
                val owner = currentUser!!.email
                val stepsToDB = StepCounts (stepService.todayDate, owner,
                    stepsFromPref, calories)

                // persists step counts and calories to room DB
                appViewModel.addStepsToDb(stepsToDB)
            }

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

        //Fetches the current log user
        fetchCurrentUser()

        //Checks if the user is log in or not
        verifyuserIsLogrdIn()


        // set bottom navigation bar
        bottom_navigation.apply {
            selectedItemId = R.id.home
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@MainActivity, MainActivity::class.java )
            )
        }
        handleActionBarClicks() // action bar navigation handler
        topAppBar?.apply {
            setLogo(R.drawable.icon_logo)

        }

        // loads charts on home screen
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragments_container_home, StepsBarChartFragment.newInstance(),
                getString(R.string.tag_barchart))
            .commit()

        tvCalories.setOnClickListener(OnclickListener())


        appViewModel = ViewModelProvider(this).get(WorkoutCompanionViewModel::class.java)
        sharedPref = getSharedPreferences(PREF, Context.MODE_PRIVATE)

        tvSteps.apply {
            if (text == getText(R.string.number_of_steps))
            // verify and loads step counts from preferences
                text = sharedPref.getFloat(stepService.todayDate, 0f).toInt().toString()
            setOnClickListener(OnclickListener())
        }
        // loads values from shared preference when steps service is not started
        updateCountInfo(tvDistance, stepService.dateForDistance, R.string.distance_km)
        updateCountInfo(tvCalories, stepService.dateForCalories, R.string.calories_cal)
        val acc = sharedPref.getString(stepService.dateForAcc, "0")
        tvAcceleration.text = getString(R.string.walking_acc, acc)
    }

    override fun onStart() {
        super.onStart()
        registerReceiver(stepsReceiver, IntentFilter(STEP_COUNT_UPDATE))
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(stepsReceiver)
    }
    // foreground or background service started based on the SDK version
    private fun startService (context: Context) {

       if (!isRunning) {
           val startIntent = Intent(context, StepCounterService::class.java)
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  {
               context.startForegroundService(startIntent)
           }else context.startService(startIntent)

           AppHelperFunctions().toast(this, getString(R.string.counter_started))
       }else AppHelperFunctions().toast(this, getString(R.string.counter_started_already))
    }

    private fun stopService(context: Context) {
        if (isRunning){
            context.stopService(Intent(context, StepCounterService::class.java))
            AppHelperFunctions().toast(this, getString(R.string.counter_stoped))
            isRunning = false
        }
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
        // saves user's weight in shard pref for calories calculation
        if (currentUser !== null && currentUser?.weight !== "") {
            val userWeight = currentUser!!.weight
            sharedPref.edit().putString(StepCounterService.WEIGHT, userWeight).apply()
        }
    }

    private fun verifyuserIsLogrdIn() {
        val uid =  FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun handleActionBarClicks() {
       topAppBar.setOnMenuItemClickListener { menuItem ->
           when (menuItem.itemId) {
               R.id.menu_profile -> {
                   val intent = Intent(this, ProfileActivity::class.java)
                   startActivity(intent)
                   true
               }
               R.id.music_btn -> {
                   val intent = Intent(this, MusicActivity::class.java)
                   startActivity(intent)
                   true
               }
               R.id.start_walk -> {startService(this@MainActivity)
                   true
               }
               R.id.stop_walk -> {
                   stopService(this@MainActivity)
                   true
               }
               R.id.heartRate -> {
                   loadFragment(BLEHeartRateFragment.newInstance(), R.string.tag_hrt)
                   true
               }
               else -> false
           }

       }
   }
    private inner class  OnclickListener : View.OnClickListener {
        override fun onClick(viewId: View?) {
            when (viewId?.id) {
                R.id.tvCalories -> {
                    if (currentUser == null || currentUser?.weight == "") {
                        AppHelperFunctions().toast(this@MainActivity,
                            getString(R.string.set_weight))
                    } else loadFragment(
                        CaloriesPieChartFragment.newInstance(), R.string.pieChart)
                }

                R.id.tvSteps -> loadFragment(
                    StepsBarChartFragment.newInstance(), R.string.tag_barchart)
            }
        }

    }
    private fun loadFragment ( frag: Fragment, txt: Int ) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragments_container_home, frag,
                getString(txt))
            .commit()
    }

    private fun updateCountInfo(tv: TextView, txt:String, string: Int){
        if (tv.text == getText(string))
            tv.text = getString(string, sharedPref.getFloat(txt, 0.0f).toString())
    }

    override fun onLoadFragment() {
        loadFragment(
            StepsBarChartFragment.newInstance(), R.string.tag_barchart)
    }

}