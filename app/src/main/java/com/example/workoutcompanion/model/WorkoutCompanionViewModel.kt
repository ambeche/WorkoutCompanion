package com.example.workoutcompanion.model
/*
* Data holder with initialized liveData
*/
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.workoutcompanion.model.roomdb.HeartRate
import com.example.workoutcompanion.model.roomdb.StepCounts
import com.example.workoutcompanion.model.roomdb.User
import com.example.workoutcompanion.model.roomdb.WorkoutCompanionDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutCompanionViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(
        WorkoutCompanionDB.get(application).userDao(),
        WorkoutCompanionDB.get(application).stepCountsDao(),
        WorkoutCompanionDB.get(application).heartRateDao())

    val userData: LiveData<List<User>> = repository.userData
    val userSteps: LiveData<List<StepCounts>> = repository.userSteps
    val heartRate: LiveData<List<HeartRate>> = repository.heartRate

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }

    fun addHearRate(heartRate: HeartRate) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertHrt(heartRate)
        }
    }

    fun deleteHrt() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteHrt()
        }
    }

    fun addStepsToDb(steps: StepCounts) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertSteps(steps)
        }
    }

}