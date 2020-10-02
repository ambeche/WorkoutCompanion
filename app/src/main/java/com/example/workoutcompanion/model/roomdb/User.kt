package com.example.workoutcompanion.model.roomdb

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "users")
data class User (
    @PrimaryKey val email: String,
    val username: String,
    val phone: String,
    val age: String,
    val weight: String,
    val height: String,
    val gender: String,

) {
    override fun toString(): String = "$username $email"
}