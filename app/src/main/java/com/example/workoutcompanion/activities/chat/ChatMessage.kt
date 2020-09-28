package com.example.workoutcompanion.activities.chat

import android.graphics.Bitmap

class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}

class ImagChatMessage(val id:String,val bitm: String?,val fromId: String?,val toId: String, val timestamp: Long){
    constructor() : this("", "", "", "", -1)
}