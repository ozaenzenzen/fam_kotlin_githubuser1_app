package com.example.famgithubuser1.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(userLocal: UserLocal)

    @Update
    fun update(userLocal: UserLocal)

    @Delete
    fun delete(userLocal: UserLocal)

    @Query("SELECT * FROM user ORDER BY id ASC")
    fun getAllUsersFavorite(): LiveData<List<UserLocal>>

    @Query("SELECT EXISTS(SELECT * FROM user WHERE id = :id AND is_favorite = 1)")
    fun isUserFavorite(id: String): LiveData<Boolean>
}