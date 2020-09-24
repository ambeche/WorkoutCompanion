package com.example.workoutcompanion.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.workoutcompanion.model.roomdb.User
import com.example.workoutcompanion.model.roomdb.WorkoutCompanionDB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutCompanionViewModel(application: Application): AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(
        WorkoutCompanionDB.get(application).userDao() )

    val userData: LiveData<List<User>> = repository.userData

    fun addUser(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertUser(user)
        }
    }
}