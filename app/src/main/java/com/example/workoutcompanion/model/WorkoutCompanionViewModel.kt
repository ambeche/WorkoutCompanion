package com.example.workoutcompanion.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.workoutcompanion.model.roomdb.StepCounts
import com.example.workoutcompanion.model.roomdb.User
import com.example.workoutcompanion.model.roomdb.WorkoutCompanionDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutCompanionViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(
        WorkoutCompanionDB.get(application).userDao(),
        WorkoutCompanionDB.get(application).stepCountsDao() )

    val userData: LiveData<List<User>> = repository.userData
    val userSteps: LiveData<List<StepCounts>> = repository.userSteps

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }

    fun addStepsToDb(steps: StepCounts) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSteps(steps)
        }
    }

    fun updateSteps(date: String, steps: Float) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.updateSteps(date, steps)
        }
    }
}