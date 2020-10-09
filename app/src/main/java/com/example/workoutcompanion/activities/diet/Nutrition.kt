package com.example.workoutcompanion.activities.diet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.model.APIcalls
import kotlinx.android.synthetic.main.nutrition_main_page.*
import kotlinx.android.synthetic.main.nutrition_main_page.bottom_navigation

class Nutrition : AppCompatActivity(), View.OnClickListener {

    var URL_To_Call : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nutrition_main_page)


        btn_Gluteineton.setOnClickListener(this)
        btn_Keto.setOnClickListener(this)
        btn_Vegetarian.setOnClickListener(this)
        btn_Lacto_vege.setOnClickListener(this)
        btn_Ovo_vrge.setOnClickListener(this)
        btn_paleo.setOnClickListener(this)
        btn_Primal.setOnClickListener(this)
        btn_whole30.setOnClickListener(this)
        btn_pescatarian.setOnClickListener(this)


        bottom_navigation.apply {
            selectedItemId = R.id.diets
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@Nutrition, Nutrition::class.java )
            )
        }
    }

    //Function that handles the Click on  Cards
    override fun onClick(p0: View?) {
        when(p0){

            btn_Keto  ->      {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Ketogenic&number=10"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                    putExtra("DietType","Ketogenic")
                                }
                                startActivity(intent)
                            }

            btn_Gluteineton -> {URL_To_Call = APIcalls.Diet_endPoint + "&diet=GlutenFree&number=10"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                    putExtra("DietType","Gluten Free")
                                }
                                startActivity(intent)

                            }



            btn_Vegetarian ->  {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Vegetarian&number=10"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                    putExtra("DietType","Vegetarian")
                                }
                                startActivity(intent)
                            }

            btn_Lacto_vege ->  {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Lacto-Vegetarian&number=10"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                    putExtra("DietType","Lacto-Vegetarian")
                                }
                                startActivity(intent)
                            }

            btn_Ovo_vrge ->     {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Ovo-Vegetarian&number=10"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                    putExtra("DietType","Ovo-Vegetarian")
                                }
                                startActivity(intent)
                            }

            btn_paleo  ->       {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Paleo&number=10"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                    putExtra("DietType","Paleo")
                                }
                                startActivity(intent)
                            }

            btn_Primal  ->       {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Primal&number=10"
                                 val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                     putExtra("DietType","Primal")
                                }
                                startActivity(intent)
                            }

            btn_whole30  ->      {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Whole30&number=10"
                                  val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                        putExtra("DietFilter", URL_To_Call)
                                      putExtra("DietType","Whole30")
                                    }
                                    startActivity(intent)
                                }

            btn_pescatarian ->    {URL_To_Call = APIcalls.Diet_endPoint + "&diet=pescartian&number=10"
                                    val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                        putExtra("DietFilter", URL_To_Call)
                                        putExtra("DietType","Pescetarian")
                                    }
                                    startActivity(intent)
                                }


        }
    }
}
