package com.example.workoutcompanion.activities.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.chat.RegisterActivity
import com.example.workoutcompanion.activities.chat.User
import com.example.workoutcompanion.activities.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.first_fragment_profile.*

class ProfileActivity :AppCompatActivity(),Comunicator {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val fragment1 = FirstFragmentProfile()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_fmain,fragment1)
            .commit()

    }

    override fun TransToSettings() {

    }


    //Function for sign-in out from application
    override fun Sign_out() {

            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

    }

    //Function for updating user data to firebase
     override fun update_Userrodatabse(ProfileImgUrl:String) {
        val uid = FirebaseAuth.getInstance().uid
        val ref =  FirebaseDatabase.getInstance().getReference("/users/${uid}")
        val user = uid?.let { User(it, user_Text.text.toString(), ProfileImgUrl, MainActivity.currentUser?.email!!,
            age_text.text.toString(), gender_text.text.toString(), weight_text.text.toString(), height_text.text.toString(),
            Phone_text.text.toString() ) }


        ref.setValue(user).addOnSuccessListener {
            MainActivity.currentUser?.username =  user_Text.text.toString()
            MainActivity.currentUser?.age = age_text.text.toString()
            MainActivity.currentUser?.gender = gender_text.text.toString()
            MainActivity.currentUser?.height = height_text.text.toString()
            MainActivity.currentUser?.weight = weight_text.text.toString()
            MainActivity.currentUser?.phone  = Phone_text.text.toString()
            Log.d("Update","Finally the user to firebase")


        }.addOnFailureListener {
            Log.d("Update","Failed to update to database : ${it.message}")
        }
    }

}