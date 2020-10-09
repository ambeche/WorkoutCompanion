package com.example.workoutcompanion.activities.profile


import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.first_fragment_profile.*
import kotlinx.android.synthetic.main.first_fragment_profile.view.*


class FirstFragmentProfile : Fragment() {

    private lateinit var comu:Comunicator
    val imageofUser = MainActivity.currentUser
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.first_fragment_profile, container, false)

        comu = activity as Comunicator

        view.Save_btn.isVisible =false
        view.Cancel_btn.isVisible = false
       view.user_Text.isEnabled = false
        view.Phone_text.isEnabled = false
        view.height_text.isEnabled = false
        view.weight_text.isEnabled = false
        view.gender_text.isEnabled = false
        view.age_text.isEnabled = false


        view.user_Text.text =  MainActivity.currentUser?.username?.toEditable()
        view.Phone_text.text = MainActivity.currentUser?.phone?.toEditable()
        view.height_text.text = MainActivity.currentUser?.height?.toEditable()
        view.weight_text.text = MainActivity.currentUser?.weight?.toEditable()
        view.gender_text.text = MainActivity.currentUser?.gender?.toEditable()
        view.age_text.text = MainActivity.currentUser?.age?.toEditable()
        view.text_Email.text = MainActivity.currentUser?.email

        Picasso.get().load(imageofUser?.profileImag).into(view.image_user)


        return view

    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("UserFromProfile","${imageofUser?.age} .. ${imageofUser?.gender}")



        Save_btn.setOnClickListener {
            if (CheckFieldsProfile()){
                imageofUser?.profileImag?.let { it1 -> comu.update_Userrodatabse(it1) }
                Cancel_btn.isVisible = false
                Update_btn.isVisible = true
                Save_btn.isVisible = false

                view.user_Text.isEnabled = false
                view.Phone_text.isEnabled = false
                view.height_text.isEnabled = false
                view.weight_text.isEnabled = false
                view.gender_text.isEnabled = false
                view.age_text.isEnabled = false
                Toast.makeText(context,"Info Saved",Toast.LENGTH_SHORT).show()
                println(Phone_text.text.toString().length)

            }else if (Phone_text.text.toString().length != 10){
                Toast.makeText(context,"Phone Number should contain 10 digits",Toast.LENGTH_SHORT).show()
            }else {
                Toast.makeText(context,"Please fill-In fields properly",Toast.LENGTH_SHORT).show()
            }


        }

        Cancel_btn.setOnClickListener {

            view.user_Text.isEnabled = false
            view.Phone_text.isEnabled = false
            view.height_text.isEnabled = false
            view.weight_text.isEnabled = false
            view.gender_text.isEnabled = false
            view.age_text.isEnabled = false

              GEtUSerInfoFresh()
            Save_btn.isVisible = false
            Update_btn.isVisible = true
            Cancel_btn.isVisible = false

        }

        Update_btn.setOnClickListener {

            view.user_Text.isEnabled = true
            view.Phone_text.isEnabled = true
            view.height_text.isEnabled = true
            view.weight_text.isEnabled = true
            view.gender_text.isEnabled = true
            view.age_text.isEnabled = true

            Save_btn.isVisible = true
            Cancel_btn.isVisible = true
            Update_btn.isVisible = false

        }
        Sighn_Out_btn.setOnClickListener {
           comu.Sign_out()
        }

    }

    private fun CheckFieldsProfile():Boolean {
        return user_Text.text.isNotEmpty() && Phone_text.text.isNotEmpty() && age_text.text.isNotEmpty() && height_text.text.isNotEmpty() && weight_text.text.isNotEmpty() && gender_text.text.isNotEmpty() && Phone_text.text.toString().length == 10


    }


    //Function that CAncel the saving and reShow default data
    private fun GEtUSerInfoFresh(){
        user_Text.text =  MainActivity.currentUser?.username?.toEditable()
        Phone_text.text = MainActivity.currentUser?.phone?.toEditable()
        height_text.text = MainActivity.currentUser?.height?.toEditable()
        weight_text.text = MainActivity.currentUser?.weight?.toEditable()
        gender_text.text = MainActivity.currentUser?.gender?.toEditable()
        age_text.text = MainActivity.currentUser?.age?.toEditable()
    }
}