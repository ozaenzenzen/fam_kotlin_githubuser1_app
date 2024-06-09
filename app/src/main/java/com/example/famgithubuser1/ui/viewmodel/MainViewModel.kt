package com.example.famgithubuser1.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.data.response.SearchUserResponseModel
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.ui.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {
    private val _listUser = MutableLiveData<ArrayList<UserModel>>()
    val listUser: LiveData<ArrayList<UserModel>> = _listUser

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    companion object {
        private const val TAG = "MainViewModel"
    }

    init {
        searchUserGithub("\"\"")
    }

    fun searchUserGithub(userName: String) {
        _isLoading.value = true
        val client =
            ApiConfig.getApiService().searchUsername("Bearer ${BuildConfig.API_KEY}", userName)
        client.enqueue(object : Callback<SearchUserResponseModel> {
            override fun onResponse(
                call: Call<SearchUserResponseModel>,
                response: Response<SearchUserResponseModel>
            ) {
                _isLoading.value = false
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    _listUser.value = responseBody.items
                } else {
                    Log.e(TAG, "onFailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SearchUserResponseModel>, t: Throwable) {
                _isLoading.value = false
                Log.e(TAG, "onFailure ${t.message}")
            }
        })
    }
}