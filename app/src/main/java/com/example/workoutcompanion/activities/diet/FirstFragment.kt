package com.example.workoutcompanion.activities.diet

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.workoutcompanion.R
import com.example.workoutcompanion.interfaces.DietComunicator
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.fragment_first.view.*
import kotlinx.android.synthetic.main.recipe_specific.*

class FirstFragment : Fragment() {
     var edited:String? = ""
    private lateinit var comu:DietComunicator

    lateinit var ACTIVITY: RecipeInfoSpesific

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view =  inflater.inflate(R.layout.fragment_first, container, false)
        comu = activity as DietComunicator
        edited = arguments?.getString("message")

        view.textView.text = edited
        println(edited)
        //ACTIVITY = context as RecipeInfoSpesific

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ACTIVITY = context as RecipeInfoSpesific
    }





    }

