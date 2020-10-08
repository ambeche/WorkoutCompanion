package com.example.workoutcompanion.model.roomdb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface StepCountsDao {

    @Query("SELECT * FROM steps")
    fun getAll() : LiveData<List<StepCounts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(steps: StepCounts): Long

    @Query("UPDATE steps SET value = :newSteps WHERE date = :date")
    fun updateSteps(date: String, newSteps: Float): Int
}

@Dao
interface HeartRateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(heartRate: HeartRate): Long

    @Query("SELECT * FROM hrt")
    fun getAllHrt() : LiveData<List<HeartRate>>

    @Query("DELETE FROM hrt")
    fun deleteAll ()
}


