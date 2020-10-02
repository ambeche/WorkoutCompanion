package com.example.workoutcompanion.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.diet.ContentofResulDietclass
import com.example.workoutcompanion.activities.diet.ResultfromDietclass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.row_for_diet1.view.*
import kotlinx.android.synthetic.main.videos_row.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URI
import java.net.URL


class AdapterForDiet1(val context:Context,val resultdiet1: ResultfromDietclass,val clickListener: (ContentofResulDietclass) -> Unit): RecyclerView.Adapter<CustomViewHolderdiet1>() {

    //var imgUrl : URL? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderdiet1 {
        val layoutInflator = LayoutInflater.from(parent?.context)
        val cellRow = layoutInflator.inflate(R.layout.row_for_diet1,parent,false)
        return CustomViewHolderdiet1(cellRow)


    }

    override fun getItemCount(): Int {
        return resultdiet1.results.count()

    }
    override fun onBindViewHolder(holder: CustomViewHolderdiet1, position: Int) {
        val resulti = resultdiet1.results[position]

        Picasso.get().load(resulti.image).into(holder.itemView.tourImage)
        holder?.itemView.tourImage.resume()

        holder?.view?.title.text = resulti.title
        holder?.view?.setOnClickListener { clickListener(resulti) }


    }

}

class Adapterempty(): RecyclerView.Adapter<CustomViewHolderEmpty>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolderEmpty {
       val layoutinflator = LayoutInflater.from(parent?.context)
        val cell = layoutinflator.inflate(R.layout.row_for_diet1,parent,false)
        return CustomViewHolderEmpty(cell)
    }


    override fun getItemCount(): Int {
       return 4
    }


    override fun onBindViewHolder(holder: CustomViewHolderEmpty, position: Int) {

    }

}

class CustomViewHolderdiet1(val view : View): RecyclerView.ViewHolder(view) {

}

class CustomViewHolderEmpty(val v : View) : RecyclerView.ViewHolder(v){

}