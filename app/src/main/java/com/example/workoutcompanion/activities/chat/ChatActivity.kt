package com.example.workoutcompanion.activities.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.workoutcompanion.BottomNavListener
import com.example.workoutcompanion.R
import com.example.workoutcompanion.activities.home.MainActivity
import kotlinx.android.synthetic.main.activity_main.*

class ChatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        title = getString(R.string.activity_title_chat)

        bottom_navigation.apply {
            selectedItemId = R.id.chat
            setOnNavigationItemSelectedListener(
                BottomNavListener(this@ChatActivity, ChatActivity::class.java )
            )
        }
    }
}