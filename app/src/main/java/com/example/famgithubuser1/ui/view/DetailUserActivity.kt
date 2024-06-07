package com.example.famgithubuser1.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.R
import com.example.famgithubuser1.data.response.DetailUserResponseModel
import com.example.famgithubuser1.data.response.SearchUserResponseModel
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.databinding.ActivityDetailUserBinding
import com.example.famgithubuser1.databinding.ActivityMainBinding
import com.example.famgithubuser1.ui.adapter.TabListFollAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailUserActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "DetailUserActivity"
        const val EXTRA_DETAIL = "extra_detail"
        private val TAB_TITLES = intArrayOf(
            R.string.followers,
            R.string.following
        )
    }

    private lateinit var binding: ActivityDetailUserBinding

    private var usernameProfile: String? = null
    private var profileUrl: String? = null
    private var userDetail: DetailUserResponseModel? = null
    private var isFavorite: Boolean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        usernameProfile = intent.extras?.get(EXTRA_DETAIL) as String
        setContentView(binding.root)

        setViewPager()
        setToolbar(getString(R.string.profile))

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
//                    if (binding.progressBar.visibility == View.VISIBLE) detailUserGithub(
//                        usernameProfile ?: ""
//                    )
                    detailUserGithub(
                        usernameProfile ?: ""
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setViewPager() {
        val viewPager: ViewPager2 = binding.viewPager
        val tabs: TabLayout = binding.tabs

        viewPager.adapter = TabListFollAdapter(this, usernameProfile!!)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }

    private fun detailUserGithub(userName: String) {
        showLoading(true)
        val client =
            ApiConfig.getApiService().getUserDetail("Bearer ${BuildConfig.API_KEY}", userName)
        client.enqueue(object : Callback<DetailUserResponseModel> {
            override fun onResponse(
                call: Call<DetailUserResponseModel>,
                response: Response<DetailUserResponseModel>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    setDetailUserData(responseBody)
                } else {
                    Log.e(TAG, "onFailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DetailUserResponseModel>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure ${t.message}")
            }
        })
    }

    fun setDetailUserData(detailUserResponseModel: DetailUserResponseModel) {
        userDetail = detailUserResponseModel
        binding.apply {
            tvName.text = if (userDetail?.name == null) "" else userDetail?.name.toString()
            tvUsername.text = userDetail?.login.toString()
            tvRepositories.text = userDetail?.publicRepos.toString()
            tvFollowing.text = userDetail?.following.toString()
            tvFollowers.text = userDetail?.followers.toString()
        }
        Glide
            .with(this)
            .load(userDetail?.avatarUrl.toString())
            .placeholder(R.drawable.profile_placeholder)
            .into(binding.userImageDetail);
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
//            binding.home.recyclerView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
//            binding.home.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun setToolbar(title: String) {
        setSupportActionBar(binding.appBarMain.toolbar)
//        binding.appBarMain.toolbar.isTitleEnabled = false
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }
    }

    override fun onDestroy() {
//        binding = null
        usernameProfile = null
        profileUrl = null
        isFavorite = null
        super.onDestroy()
    }
}