package com.example.workoutcompanion.adapters
/*
* Adapter for populating a material pop window with BLE scanned results
*/

import android.graphics.Color
import com.example.workoutcompanion.R
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi

class BleListAdapter(context: Context?) : BaseAdapter() {
    private var bleList = ArrayList<ScanResult>()
    private val inflater: LayoutInflater
            = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return bleList.size
    }

    override fun getItem(pos: Int): Any? {
        return bleList[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(pos: Int, view: View?, parent: ViewGroup?): View {
        val rowView = if (view === null)
            inflater.inflate(R.layout.ble_device_item, parent,false)
        else view

        val tvName = rowView.findViewById<TextView>(R.id.tvName)
        val tvMacAddress = rowView.findViewById<TextView>(R.id.tvAddress)
        val bleItem = bleList[pos]

        tvName.text = bleItem.device?.name ?: ""
        tvMacAddress.text = bleItem.device?.address

        return rowView
    }

    fun setAdapter (scanResult:ScanResult) {
        bleList.add(scanResult)
        notifyDataSetChanged()
    }

    private fun updateTextColor (vararg v: TextView) {
        val tv = listOf(*v)
        tv.forEach { it.setTextColor(Color.GRAY) }
    }
}