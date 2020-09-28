package com.example.workoutcompanion.activities.home

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.workoutcompanion.R
import com.example.workoutcompanion.model.WorkoutCompanionViewModel
import com.example.workoutcompanion.model.roomdb.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_register_user.*
import kotlinx.android.synthetic.main.fragment_register_user.view.*

class RegisterUserFragment : Fragment() {
    companion object{
        fun fragIntance(): RegisterUserFragment {
            return RegisterUserFragment()
        }
    }
    private lateinit var userViewModel: WorkoutCompanionViewModel
    //private lateinit var activityListener: OnLoadFragment

   /* override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnLoadFragment) {
            activityListener = context
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userViewModel = ViewModelProvider(this).get(WorkoutCompanionViewModel::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragLayout = inflater.inflate(R.layout.fragment_register_user,container,false)
        val gender = resources.getStringArray(R.array.gender)
        val dropDownMenuAdapter = ArrayAdapter(requireContext(), R.layout.list_item, gender)
        (fragLayout.evGender.editText as? AutoCompleteTextView)?.setAdapter(dropDownMenuAdapter)

        fragLayout.btnRegister.setOnClickListener(ClickListener())
        userViewModel.userData.observe(viewLifecycleOwner, {
            if (it.isNotEmpty()){
                Log.d("user", it[0].firstName)
            }
        })

        return fragLayout
    }

    /*private fun addUserToDB () {
        if (isNotEmptyTV(evFirstName, evLastName, evAge, evGender,
                evEmail, evWeight, evPhone, evPassWord, evHeight)){

            userViewModel.addUser(
                User(0, getInput(evFirstName), getInput(evLastName), getInput(evPhone),
                    getInput(evEmail), getInput(evAge), getInput(evWeight), getInput(evHeight),
                    getInput(evGender), getInput(evPassWord)
                )
            )

            clearTxt(evFirstName, evLastName, evAge, evGender,
                evEmail, evWeight, evPhone, evPassWord, evHeight
            )

        }else toastNotice(R.string.failed_to_create)
    }*/


    inner class ClickListener : View.OnClickListener{
        override fun onClick(v: View?) {
            when(v?.id) {
                //R.id.btnRegister -> addUserToDB()
            }
        }
    }
    private fun getInput(v: TextInputLayout) = v.editText?.text.toString()

    private fun clearTxt(vararg v: TextInputLayout) {
        val ev = listOf(*v)
        ev.forEach { it.editText?.text?.clear() }
    }

    private fun isNotEmptyTV(vararg v: TextInputLayout): Boolean {
        val ev = listOf(*v)
        return ev.run {
            this.none { inputTxt -> inputTxt.editText!!.text.isEmpty() }
        }
    }

    private fun toastNotice(notice: Int){
        makeText(requireContext(), getString(notice),
            Toast.LENGTH_SHORT).show()
    }
}