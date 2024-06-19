package com.example.famgithubuser1.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.data.repository.UserRepository
import com.example.famgithubuser1.data.response.DetailUserResponseModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.data.room.UserLocal
import com.example.famgithubuser1.ui.view.DetailUserActivity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel(application: Application) : ViewModel() {

    private val _detailUser = MutableLiveData<DetailUserResponseModel>()
    val detailUser: LiveData<DetailUserResponseModel> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isUserFav = MutableLiveData<Boolean>()
    val isUserFav: LiveData<Boolean> = _isUserFav

    private val mUserRepository: UserRepository = UserRepository(application)

    companion object {
        private const val TAG = "DetailUserViewModel"
    }

    fun detailUserGithub(userName: String) {
        _isLoading.value = true
        val client =
            ApiConfig.getApiService().getUserDetail("Bearer ${BuildConfig.API_KEY}", userName)
        client.enqueue(object : Callback<DetailUserResponseModel> {
            override fun onResponse(
                call: Call<DetailUserResponseModel>,
                response: Response<DetailUserResponseModel>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    // setDetailUserData(responseBody)
                    _detailUser.value = responseBody!!
                } else {
                    Log.e(TAG, "onFailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponseModel>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure ${t.message}")
            }
        })
    }

    fun saveAsFavorite(user: UserLocal) {
        viewModelScope.launch {
            mUserRepository.insert(user)
        }
    }

    fun deleteFromFavorite(user: UserLocal) {
        viewModelScope.launch {
            mUserRepository.delete(user)
        }
    }

    fun isUserFavorite(id: String): LiveData<Boolean> {
        return mUserRepository.isUserFavorite(id)
    }

    fun isUserFavorite2(id: String): Flow<Boolean> {
        var ans = mUserRepository.isUserFavorite(id).asFlow()
        return ans
    }

    fun isUserFavorite3(id: String) {
        viewModelScope.launch {
            mUserRepository.isUserFavorite(id).asFlow().collect {
                if (it) {
                    _isUserFav.value = it
                } else {
                    _isUserFav.value = it
                }
            }
        }
    }
}