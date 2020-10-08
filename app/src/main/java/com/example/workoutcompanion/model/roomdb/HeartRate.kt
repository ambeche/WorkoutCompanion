package com.example.workoutcompanion.model.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hrt")
data class HeartRate (
    @PrimaryKey (autoGenerate = true) val hrt: Long,
    val bpm: Float?,

    ) {
    override fun toString(): String = " $bpm"
}