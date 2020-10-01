package com.example.workoutcompanion.model.roomdb

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [(User::class), (StepCounts::class)], version = 1)
abstract class WorkoutCompanionDB: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun stepCountsDao(): StepCountsDao
    companion object {

        var dbInstance: WorkoutCompanionDB? = null
        @Synchronized
        fun get(context: Context): WorkoutCompanionDB {
            if (dbInstance == null) {
                dbInstance =
                    Room.databaseBuilder(context.applicationContext,
                        WorkoutCompanionDB::class.java, "user.db").build()
            }
            return dbInstance!!
        }
    }
}