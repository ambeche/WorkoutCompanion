package com.example.workoutcompanion.activities.diet

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.interfaces.DietComunicator
import com.example.workoutcompanion.model.APIcalls
import com.google.gson.GsonBuilder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.recipe_specific.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class RecipeInfoSpesific:AppCompatActivity() ,DietComunicator{

    var url :String? = null
    var url_image : String? = null
    var amo : String? = null
    var url_For_Summary:String? = null

    var isfront = true
    lateinit var front_anim :AnimatorSet
    lateinit var back_anim : AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_specific)

        this.getSupportActionBar()?.hide();


        val scale:Float = applicationContext.resources.displayMetrics.density
        card_front.cameraDistance = 8000 * scale
        card_back.cameraDistance = 8000 * scale


        front_anim = AnimatorInflater.loadAnimator(applicationContext,R.animator.front_animation) as AnimatorSet
        back_anim = AnimatorInflater.loadAnimator(applicationContext,R.animator.back_animation) as AnimatorSet


        flip_button.setOnClickListener {
            if(isfront){
                front_anim.setTarget(card_front)
                back_anim.setTarget(card_back)
                front_anim.start()
                back_anim.start()
                isfront = false
            }else {
                front_anim.setTarget(card_back)
                back_anim.setTarget(card_front)
                back_anim.start()
                front_anim.start()
                isfront = true
            }
        }



        val Specific_Meal__ID = getIntent().getStringExtra("specific_info_from_ResultDiet1")?.toInt()
        Log.d("IDDd", Specific_Meal__ID.toString())

        url = APIcalls.REcipe_Info_(Specific_Meal__ID)
        url_For_Summary = APIcalls.Summarize_Recipes(Specific_Meal__ID)
        url_image = getIntent().getStringExtra("img")

        Picasso.get().load(url_image).into(card_front)

        card_back.text = url_image

        GlobalScope.launch(Dispatchers.Main) {
            async { fetchJsonFromApiSpec() }
            async {fetchSummary()}

        }


        bottom_nav.setOnNavigationItemSelectedListener(BottomNavListener
            (this, RecipeInfoSpesific::class.java ))

        fetchJsonFromApiSpec()


    }

    //Function that fetches Summary of The Meal
    private fun fetchSummary() {

        val reques = url_For_Summary?.let { Request.Builder().url(it).build() }
        val client = OkHttpClient()
        if (reques != null) {
            client.newCall(reques).enqueue(object: Callback {

                override fun onFailure(call: Call, e: IOException) {

                    println("Failed to do request!!!")
                }

                override fun onResponse(call: Call, response: Response) {
                    val body = response.body?.string()
                    println("kkkkkkkkkkkk"+body)

                    val gson = GsonBuilder().create()

                    val api_Summary   = gson.fromJson(body,ContentofSummary::class.java)

                    Log.d("response_fetch","${api_Summary}")
                    card_back.text = api_Summary.summary




                }

            })
        }
    }

    //Function that Fetches the Nutrition Values of Meal
    private fun fetchJsonFromApiSpec() {

        val reques = url?.let { Request.Builder().url(it).build() }
        val client = OkHttpClient()
        if (reques != null) {
            client.newCall(reques).enqueue(object: Callback {

                override fun onFailure(call: Call, e: IOException) {

                    println("Failed to do request!!!")
                }

                @SuppressLint("SetTextI18n")
                override fun onResponse(call: Call, response: Response) {
                    val body = response?.body?.string()
                    println("kkkkkkkkkkkk"+body)

                    val gson = GsonBuilder().create()

                    val api_diet_3   = gson.fromJson(body,ContentofSpesific::class.java)

                    Log.d("response_fetch","${api_diet_3}")
                    amo = api_diet_3.bad[2].amount

                    runOnUiThread {
                        textView_Fat.text = api_diet_3.bad[1].amount
                        Fat_per.text = api_diet_3.bad[1].percentOfDailyNeeds.toString()
                        textView_Carbohydrates.text = api_diet_3.bad[3].amount
                        Calrb_per.text = api_diet_3.bad[3].percentOfDailyNeeds.toString()
                        textView_Protein.text = api_diet_3.good[0].amount
                        Prot_per.text = api_diet_3.good[0].percentOfDailyNeeds.toString()
                        TextView_Calorie.text = api_diet_3.calories + "Kcal"
                    }





                }

            })
        }

    }

    override fun PassData(sth: String) {
        val bundle = Bundle()
        bundle.putString("message",sth)


        //al fragment1 = FirstFragment()
       // fragment1.arguments = bundle


    }



}



class ContentofSpesific(val calories:String,val  bad:List<DeepInfoBad>, val good:List<DeepInfoGood>)

class DeepInfoBad(val title : String,val amount:String, val percentOfDailyNeeds: Float)

class DeepInfoGood(val title : String,val amount:String, val percentOfDailyNeeds: Float)

class ContentofSummary(val summary:String)