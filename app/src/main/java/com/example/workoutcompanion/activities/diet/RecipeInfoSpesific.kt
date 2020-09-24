package com.example.workoutcompanion.activities.diet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutcompanion.R
import com.example.workoutcompanion.adapters.AdapterForDiet1
import com.example.workoutcompanion.interfaces.DietComunicator
import com.example.workoutcompanion.model.APIcalls
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.recipe_specific.*
import kotlinx.android.synthetic.main.testingresult1.*
import okhttp3.*
import java.io.IOException

class RecipeInfoSpesific:AppCompatActivity() ,DietComunicator{

    var url :String? = null
    var url_image : String? = null
    var amo : String? = null
    val fragment1 = FirstFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_specific)


        val Specific_Meal__ID = getIntent().getStringExtra("specific_info_from_ResultDiet1")?.toInt()
        Log.d("IDDd", Specific_Meal__ID.toString())

        url = APIcalls.REcipe_Info_(Specific_Meal__ID)
        url_image = getIntent().getStringExtra("img")

        text1.text = url_image

        fetchJsonFromApiSpec()

    }

    private fun fetchJsonFromApiSpec() {

        val reques = url?.let { Request.Builder().url(it).build() }
        val client = OkHttpClient()
        if (reques != null) {
            client.newCall(reques).enqueue(object: Callback {

                override fun onFailure(call: Call, e: IOException) {

                    println("Failed to do request!!!")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response?.body?.string()
                    println("kkkkkkkkkkkk"+body)

                    val gson = GsonBuilder().create()

                    val api_diet_3   = gson.fromJson(body,ContentofSpesific::class.java)

                    Log.d("response_fetch","${api_diet_3}")
                    text1.text = api_diet_3.bad[2].amount
                    amo = api_diet_3.bad[2].amount




                }

            })
        }

    }

    override fun PassData(Sth: String) {
        val bundle = Bundle()
        bundle.putString("message",Sth)

       //val fragment2 = SecondFragment()
        val fragment1 = FirstFragment()
        fragment1.arguments = bundle
        this.supportFragmentManager.beginTransaction().replace(R.id.flfragment, fragment1).addToBackStack(null).commit()

    }

    fun Goo():String?{
        return amo
    }

}

//class ResultFromSpesificApi(val nutrition:List<ContentofSpesific>)

class ContentofSpesific(val calories:String,val  bad:List<DeepInfo>)

class DeepInfo(val title : String,val amount:String, val percentOfDailyNee: Float)

class Specs(val title:String,val amount:Double,val unit:String, val percentOfDailyNeeds: String )

class ResultFromSpesificApi {

    data class REsult(
        val nutrition : data
    )

    data class data (
        val nutrients : data2
    )
    data class data2(
        val title: String, val amount:Double,val unit : String, val percentOfDailyNeeds: Double


    )

}