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

    fun getAllUsersFavorite(): LiveData<List<UserLocal>> = mUsersDao.getAllUsersFavorite()

    fun isUserFavorite(id: String): LiveData<Boolean> = mUsersDao.isUserFavorite(id)

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