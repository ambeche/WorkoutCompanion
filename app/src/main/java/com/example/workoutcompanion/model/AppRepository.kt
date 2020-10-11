package com.example.workoutcompanion.model
/*
* Central repository for app data
*/

import androidx.lifecycle.LiveData
import com.example.workoutcompanion.model.roomdb.*

class AppRepository(private val userDao: UserDao,
                    private val stepsDao: StepCountsDao, private val heartRateDao: HeartRateDao) {
    val userData: LiveData<List<User>> = userDao.getAllUsers()
    val userSteps: LiveData<List<StepCounts>> = stepsDao.getAll()
    val heartRate: LiveData<List<HeartRate>> = heartRateDao.getAllHrt()

    fun insertUser(user: User) = userDao.insert(user)
    fun insertSteps(steps: StepCounts) = stepsDao.insert(steps)
    fun insertHrt (heartRate: HeartRate) = heartRateDao.insert(heartRate)
    fun deleteHrt() = heartRateDao.deleteAll()


}
