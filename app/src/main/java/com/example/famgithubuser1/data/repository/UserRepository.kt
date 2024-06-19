package com.example.famgithubuser1.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.example.famgithubuser1.data.room.UserDao
import com.example.famgithubuser1.data.room.UserLocal
import com.example.famgithubuser1.data.room.UserRoomDatabase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUsersDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = UserRoomDatabase.getDatabase(application)
        mUsersDao = db.userDao()
    }

    fun getAllUsers(): LiveData<List<UserLocal>> = mUsersDao.getAllUsers()

    fun getUserFavorite(id: String): LiveData<Boolean> = mUsersDao.getUserFavorite(id)

    fun insert(userLocal: UserLocal) {
        executorService.execute { mUsersDao.insert(userLocal) }
    }

    fun delete(userLocal: UserLocal) {
        executorService.execute { mUsersDao.delete(userLocal) }
    }

    fun update(userLocal: UserLocal) {
        executorService.execute { mUsersDao.update(userLocal) }
    }

}