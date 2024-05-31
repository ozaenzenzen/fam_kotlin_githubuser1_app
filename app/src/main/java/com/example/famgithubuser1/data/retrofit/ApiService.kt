package com.example.famgithubuser1.data.retrofit

import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.data.response.DetailUserResponseModel
import com.example.famgithubuser1.data.response.ListFollowersResponseModel
import com.example.famgithubuser1.data.response.ListFollowingResponseModel
import com.example.famgithubuser1.data.response.SearchUserResponseModel
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
//    @FormUrlEncoded
//    @Headers("Authorization: Bearer ${BuildConfig.API_KEY}")
    @GET("search/users")
    fun searchUsername(
        @Header("Authorization") token: String,
        @Query("q") q: String
    ): Call<SearchUserResponseModel>

    @GET("users/{username}")
    suspend fun getUserDetail(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): DetailUserResponseModel

    @GET("users/{username}/followers")
    suspend fun getUserFollowers(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): ArrayList<ListFollowersResponseModel>

    @GET("users/{username}/following")
    suspend fun getUserFollowing(
        @Header("Authorization") token: String,
        @Path("username") username: String
    ): ArrayList<ListFollowingResponseModel>
}