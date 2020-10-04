package com.example.workoutcompanion

import android.content.Context
import android.widget.Toast

class AppHelperFunctions {
    fun toast(context: Context, text: String) {
        Toast.makeText(
            context, text, Toast.LENGTH_SHORT).show()
    }
}
