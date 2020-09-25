package com.example.workoutcompanion.model

import com.example.workoutcompanion.model.roomdb.User
import com.example.workoutcompanion.model.roomdb.UserDao

class AppRepository(private val userDao: UserDao) {
    val userData = userDao.getAllUsers()

    fun insertUser(user: User) = userDao.insert(user)

}
