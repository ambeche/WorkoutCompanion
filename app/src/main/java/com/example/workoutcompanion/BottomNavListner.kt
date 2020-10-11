package com.example.workoutcompanion
/*
* bottom navigation listener
*/
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.example.workoutcompanion.activities.chat.LatestMessageActivity
import com.example.workoutcompanion.activities.diet.Nutrition
import com.example.workoutcompanion.activities.home.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class BottomNavListener (private val context:Context, private val activity: Class<*>)
    : BottomNavigationView.OnNavigationItemSelectedListener{
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.home -> startActivity(MainActivity::class.java)
            R.id.diets -> startActivity(Nutrition::class.java)
            R.id.chat -> startActivity(LatestMessageActivity::class.java)
        }
        return false
    }

    private fun startActivity (cls: Class<*>) {
        if (cls != activity) {
            val intent = Intent(context, cls)
            context.startActivity(intent)
        }
    }

}