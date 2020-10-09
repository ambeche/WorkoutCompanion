package com.example.workoutcompanion.activities.diet

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.adapters.AdapterForDiet1
import com.example.workoutcompanion.model.APIcalls
import com.example.workoutcompanion.model.CenterZoomLayout
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.testingresult1.*
import okhttp3.*
import java.io.IOException

class ResultOfDiet1 : AppCompatActivity() {

    var url:String? = null
    var type_of_diet:String? = null

     var searchedMale:String? = null
     var searchedmincal :String? =null
     var searchedmaxCalo:String? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testingresult1)


        search_Meal_btn.setOnClickListener {
            searchedMale = edit_Meal.text.toString()
            searchedmincal =  edit_Min.text.toString()
            searchedmaxCalo = edit_Max.text.toString()
            checkSearch()
        }


        bottom_navigation.setOnNavigationItemSelectedListener(BottomNavListener
            (this, ResultOfDiet1::class.java ))


        //this is for Animating the Recycler view
        val layoutManager = CenterZoomLayout(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true

        recycler_test.layoutManager = layoutManager

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycler_test)
        recycler_test.isNestedScrollingEnabled = false

        recycler_test.layoutManager = LinearLayoutManager(this, LinearLayout.HORIZONTAL,false)


        val My_Url = getIntent().getStringExtra("DietFilter")
        val dietType = getIntent().getStringExtra("DietType")
        type_of_diet = dietType
        url = My_Url

        println(My_Url)

        fetchJsonfromApiDiet()


    }

    //Function that Executes the search for meal
    private fun checkSearch() {
        if (searchedMale?.isNotEmpty()!! && searchedmincal?.isNotEmpty()!! && searchedmaxCalo?.isNotEmpty()!! && searchedmincal!!.toInt() < searchedmaxCalo!!.toInt() && searchedmincal!!.count() <= 3 && searchedmaxCalo!!.count() <= 4){
            url = APIcalls.SearchByCal(searchedMale,searchedmincal,searchedmaxCalo,type_of_diet)
                try{
                    fetchJsonfromApiDiet()
                }catch (e:Exception){
                    Log.d("Error1","${e}")
                }

        }else if ( searchedmincal!!.isNotEmpty() && searchedmaxCalo!!.isNotEmpty() && searchedmincal!!.toInt() > searchedmaxCalo!!.toInt()) {
            Toast.makeText(this,"GOOOOOOOOO",Toast.LENGTH_SHORT).show()
            Log.d("MinMaxVAlues","the Max calories should be higher than Min calories")
        }else {
            Toast.makeText(this,"Please fill in the required fields properly",Toast.LENGTH_SHORT).show()
        }

    }

    //Function That fetches Data from Api and populates the  Recycler view
    private fun fetchJsonfromApiDiet() {
        Log.d("URL","${url}")

        val reques = url?.let { Request.Builder().url(it).build() }
        val client = OkHttpClient()
        if (reques != null) {
            client.newCall(reques).enqueue(object: Callback {

                override fun onFailure(call: Call, e: IOException) {

                    println("Failed to do request!!!")
                    println(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    println("this is the bodyyy"+body)

                    val gson = GsonBuilder().create()

                     val api_diet_2    = gson.fromJson(body,ResultfromDietclass::class.java)

                Log.d("response_fetch","${api_diet_2}   ${api_diet_2.totalResults}")


                    if (api_diet_2.totalResults != 0){

                        runOnUiThread {

                            recycler_test.adapter = AdapterForDiet1(applicationContext,api_diet_2){

                                val intent = Intent(this@ResultOfDiet1,RecipeInfoSpesific::class.java).apply {
                                    putExtra("specific_info_from_ResultDiet1","${it.id}")
                                    putExtra("img", it.image)
                                }
                                startActivity(intent)

                            }
                        }


                    }else {
                        runOnUiThread {
                            Toast.makeText(this@ResultOfDiet1,"Sorry no result found!",Toast.LENGTH_SHORT).show()
                        }

                    }




                }

            })
        }
    }

}


class ResultfromDietclass(val results : List<ContentofResulDietclass>, val totalResults:Int)

class ContentofResulDietclass(val id:Int, val image: String,val title:String, val calories:Int)