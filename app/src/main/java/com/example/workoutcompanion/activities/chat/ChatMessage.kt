package com.example.workoutcompanion.activities.chat


//Class to structure messages as follows
class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}


//Class to structure Image messages as follows
class ImagChatMessage(val id:String,val bitm: String?,val fromId: String?,val toId: String, val timestamp: Long){
    constructor() : this("", "", "", "", -1)
}