package com.example.workoutcompanion.activities.diet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.MainActivity
import com.example.workoutcompanion.model.APIcalls
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nutrition_main_page.*
import kotlinx.android.synthetic.main.nutrition_main_page.bottom_navigation

class Nutrition : AppCompatActivity(), View.OnClickListener {

    var URL_To_Call : String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nutrition_main_page)
       // getSupportActionBar()?.hide()

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

    override fun onClick(p0: View?) {
        when(p0){

            btn_Keto  ->      {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Ketogenic&number=60"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                }
                                startActivity(intent)
                            }

            btn_Gluteineton -> {URL_To_Call = APIcalls.Diet_endPoint + "&diet=GlutenFree&number=10"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                }
                                startActivity(intent)

                            }



            btn_Vegetarian ->  {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Vegetarian&number=60"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                }
                                startActivity(intent)
                            }

            btn_Lacto_vege ->  {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Lacto-Vegetarian&number=60"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                }
                                startActivity(intent)
                            }

            btn_Ovo_vrge ->     {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Ovo-Vegetarian&number=60"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                }
                                startActivity(intent)
                            }

            btn_paleo  ->       {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Paleo&number=60"
                                val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                }
                                startActivity(intent)
                            }

            btn_Primal  ->       {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Primal&number=60"
                                 val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                    putExtra("DietFilter", URL_To_Call)
                                }
                                startActivity(intent)
                            }

            btn_whole30  ->      {URL_To_Call = APIcalls.Diet_endPoint + "&diet=Whole30&number=60"
                                  val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                        putExtra("DietFilter", URL_To_Call)
                                    }
                                    startActivity(intent)
                                }

            btn_pescatarian ->    {URL_To_Call = APIcalls.Diet_endPoint + "&diet=pescartian&number=60"
                                    val intent = Intent(this, ResultOfDiet1::class.java).apply {
                                        putExtra("DietFilter", URL_To_Call)
                                    }
                                    startActivity(intent)
                                }


        }
    }
}
