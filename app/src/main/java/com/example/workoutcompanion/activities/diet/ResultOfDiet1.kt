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
import com.example.workoutcompanion.R
import com.example.workoutcompanion.adapters.AdapterForDiet1
import com.example.workoutcompanion.interfaces.DietComunicator
import com.example.workoutcompanion.model.CenterZoomLayout
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.testingresult1.*
import okhttp3.*
import java.io.IOException

class ResultOfDiet1 : AppCompatActivity() {

    var url:String? = null

    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.testingresult1)



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
        url = My_Url

        println(My_Url)

        fetchJsonfromApiDiet()


    }

    private fun fetchJsonfromApiDiet() {

        val reques = url?.let { Request.Builder().url(it).build() }
        val client = OkHttpClient()
        if (reques != null) {
            client.newCall(reques).enqueue(object: Callback {

                override fun onFailure(call: Call, e: IOException) {

                    println("Failed to do request!!!")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response?.body?.string()
                    println("this is the bodyyy"+body)

                    val gson = GsonBuilder().create()

                     val api_diet_2    = gson.fromJson(body,ResultfromDietclass::class.java)

                Log.d("response_fetch","${api_diet_2}")

                   runOnUiThread {
                        recycler_test.adapter = AdapterForDiet1(applicationContext,api_diet_2){

                            val intent = Intent(this@ResultOfDiet1,RecipeInfoSpesific::class.java).apply {
                                putExtra("specific_info_from_ResultDiet1","${it.id}")
                                putExtra("img","${it.image}")
                            }
                            startActivity(intent)

                        }
                    }
                }

            })
        }
    }

}

class ResultfromDietclass(val results : List<ContentofResulDietclass>)

class ContentofResulDietclass(val id:Int, val image: String,val title:String, val calories:Int)