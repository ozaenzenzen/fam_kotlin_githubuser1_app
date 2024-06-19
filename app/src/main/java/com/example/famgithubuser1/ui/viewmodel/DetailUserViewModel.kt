package com.example.famgithubuser1.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.data.repository.UserRepository
import com.example.famgithubuser1.data.response.DetailUserResponseModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.ui.view.DetailUserActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserViewModel(application: Application) : ViewModel() {

    private val _detailUser = MutableLiveData<DetailUserResponseModel>()
    val detailUser: LiveData<DetailUserResponseModel> = _detailUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

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

    fun getUserFavorite(id: String) {
        mUserRepository.getUserFavorite(id)
    }
}