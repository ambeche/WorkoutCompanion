package com.example.workoutcompanion.activities.home

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock.EXTRA_MESSAGE
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.chat.NewMessageActivity
import com.example.workoutcompanion.activities.chat.RegisterActivity
import com.example.workoutcompanion.activities.chat.User
import com.example.workoutcompanion.activities.diet.Nutrition
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEPS
import com.example.workoutcompanion.activities.home.StepCounterService.Companion.STEP_COUNT_UPDATE
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        var currentUser: User? = null
    }
    private val stepsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val steps = intent?.getIntExtra(STEPS, 0).toString()
            //tvSteps.text = steps
            Log.d("stepsUI", steps)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // request permission to use step counter
        if (Build.VERSION.SDK_INT >= 29) {
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                //ask for permission
                requestPermissions(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 2);
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
}