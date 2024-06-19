package com.example.famgithubuser1.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.famgithubuser1.data.repository.UserRepository
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.room.UserLocal
import kotlinx.coroutines.launch

class FavoriteViewModel(application: Application) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    //    private val _favorites = MutableStateFlow(listOf<UserLocal>())
    //    val favorite = _favorites.asStateFlow()
    private val _favorites = MutableLiveData<UserLocal>()
    val favorite: LiveData<UserLocal> = _favorites

    private val _listUser = MutableLiveData<ArrayList<UserLocal>>()
    val listUser: LiveData<ArrayList<UserLocal>> = _listUser

//    init {
//        getAllUsersFavorite()
//    }

    fun insert(userLocal: UserLocal) {
        mUserRepository.insert(userLocal)
    }

    fun update(userLocal: UserLocal) {
        mUserRepository.update(userLocal)
    }

    fun delete(userLocal: UserLocal) {
        mUserRepository.delete(userLocal)
    }

    fun getAllUsersFavorite(): LiveData<List<UserLocal>> {
//        viewModelScope.launch {
//            mUserRepository.getAllUsersFavorite()
//        }
        return mUserRepository.getAllUsersFavorite()
    }

    fun isUserFavorite(id: String) {
        mUserRepository.isUserFavorite(id)
    }

}