package com.example.workoutcompanion.model.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "steps")
data class StepCounts (
    @PrimaryKey val date: String,
    val owner: String,
    val value: Float,

    ) {
    override fun toString(): String = "$date $value"
}