package com.example.workoutcompanion.activities.home
/*
* Receiver is registered in the MainActivity
* used by the StepCounterService for updating the UI
*/

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

open class StepsReceiver() : BroadcastReceiver() {
    var steps = 0
    override fun onReceive(context: Context, intent: Intent) {
        steps = intent.getIntExtra(StepCounterService.STEPS, 0)

        Log.d("stepsUI", steps.toString())
    }
}
