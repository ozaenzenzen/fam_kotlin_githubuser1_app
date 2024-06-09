package com.example.famgithubuser1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.ui.view.FollowersFragment
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FollowersViewModel : ViewModel() {
    private val _listFollowers = MutableLiveData<ArrayList<UserModel>>()
    val listFollowers: LiveData<ArrayList<UserModel>> = _listFollowers

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "FollowersViewModel"
    }

    fun followersUserGithub(userName: String) {
        _isLoading.value = true
        val client =
            ApiConfig.getApiService().getUserFollowers("Bearer ${BuildConfig.API_KEY}", userName)
        client.enqueue(object : Callback<ArrayList<UserModel>> {
            override fun onResponse(
                call: Call<ArrayList<UserModel>>,
                response: Response<ArrayList<UserModel>>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    // setRecycleViewFollowers(responseBody)
                    viewModelScope.launch {
                        _listFollowers.value = responseBody!!
                    }
                } else {
                    Log.e(TAG, "onFailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ArrayList<UserModel>>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure ${t.message}")
            }
        })
    }
}