package com.example.workoutcompanion.activities.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.chat.RegisterActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.first_fragment_profile.view.*

class ProfileActivity :AppCompatActivity(),Comunicator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val fragment1 = First_Fragment_Profile()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_fmain,fragment1)
            .commit()

    }

    override fun TransToSettings(){
        val fragment2 = Second_Fragment_Settings()

        this.supportFragmentManager.beginTransaction().replace(R.id.fragment_fmain, fragment2).addToBackStack(null).commit()
    }

    override fun Sign_out() {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

    }

}