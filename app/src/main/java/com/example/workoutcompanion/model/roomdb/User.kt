package com.example.workoutcompanion.model.roomdb

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) val userId: Long,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    val age: String,
    val weight: String,
    val height: String,
    val gender: String,

) {
    override fun toString(): String = "$firstName $lastName"
}