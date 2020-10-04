package com.example.workoutcompanion.activities.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.chat.RegisterActivity
import com.example.workoutcompanion.activities.home.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.first_fragment_profile.*
import kotlinx.android.synthetic.main.first_fragment_profile.view.*
import kotlinx.android.synthetic.main.row_for_diet1.view.*

class First_Fragment_Profile : Fragment() {

    private lateinit var comu:Comunicator
    val imageofUser = MainActivity.currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.first_fragment_profile, container, false)

        comu = activity as Comunicator

        view.user_Text.text = MainActivity.currentUser?.username
        view.Phone_text.text = MainActivity.currentUser?.phone
        view.height_text.text = MainActivity.currentUser?.height
        view.weight_text.text = MainActivity.currentUser?.weight
        view.gender_text.text = MainActivity.currentUser?.gender
        view.age_text.text = MainActivity.currentUser?.age

        Picasso.get().load(imageofUser?.profileImag).into(view.image_user)

        view.Settings_btn.setOnClickListener {
            comu.TransToSettings()
        }

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("User1111","${imageofUser?.age} .. ${imageofUser?.gender}")

        Save_btn.setOnClickListener {

              SAveDataToFireBase()
        }

        Cancel_btn.setOnClickListener {

              CancelSaving()
        }

        Update_btn.setOnClickListener {

            UpdateDataFromFireBase()

        }
        Sighn_Out_btn.setOnClickListener {
           comu.Sign_out()
        }



    }




    private fun UpdateDataFromFireBase() {

    }

    private fun CancelSaving() {

    }

    private fun SAveDataToFireBase() {

    }
}