package com.example.famgithubuser1.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.famgithubuser1.R
import com.example.famgithubuser1.data.factory.ViewModelFactory
import com.example.famgithubuser1.data.response.DetailUserResponseModel
import com.example.famgithubuser1.data.room.UserLocal
import com.example.famgithubuser1.data.service.SettingPreferences
import com.example.famgithubuser1.data.service.dataStore
import com.example.famgithubuser1.databinding.ActivityDetailUserBinding
import com.example.famgithubuser1.ui.adapter.SectionPagerAdapter
import com.example.famgithubuser1.ui.viewmodel.DetailUserViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DetailUserActivity : AppCompatActivity(), View.OnClickListener {
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
    private var isUserFavorite: Boolean = false

    private lateinit var detailUserViewModel: DetailUserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailUserBinding.inflate(layoutInflater)
        usernameProfile = intent.extras?.get(EXTRA_DETAIL) as String
        setContentView(binding.root)

        setViewPager()
        setToolbar(getString(R.string.profile))

        detailUserViewModel = obtainViewModel(this@DetailUserActivity)

        detailUserViewModel.detailUser.observe(this) {
            setDetailUserData(it)
        }

        detailUserViewModel.isUserFav.observe(this) { value: Boolean ->
            isUserFavorite(value)
        }

        detailUserViewModel.isLoading.observe(this) { value ->
            showLoading(value)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    detailUserViewModel.detailUserGithub(
                        usernameProfile ?: ""
                    )
                }
                launch {
                    detailUserViewModel.isUserFavorite3(usernameProfile ?: "")
                }
            }
        }

        binding.fabFavorite.setOnClickListener(this)
    }


    private fun obtainViewModel(activity: AppCompatActivity): DetailUserViewModel {
        var pref = SettingPreferences.getInstance(application.dataStore)
        val factory = ViewModelFactory.getInstance(pref, activity.application)
        return ViewModelProvider(activity, factory).get(DetailUserViewModel::class.java)
    }

    private fun isUserFavorite(favorite: Boolean) {
        if (favorite) {
            isUserFavorite = true
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            isUserFavorite = false
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun setViewPager() {
        val viewPager: ViewPager2 = binding.viewPager
        val tabs: TabLayout = binding.tabs

        viewPager.adapter = SectionPagerAdapter(this, usernameProfile!!)

        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()
    }


    private fun setDetailUserData(detailUserResponseModel: DetailUserResponseModel) {
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
        } else {
            binding.progressBar.visibility = View.GONE
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
//        isFavorite = null
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_favorite -> {
                if (isUserFavorite == true) {
                    userDetail.let {
                        detailUserViewModel.deleteFromFavorite(
                            UserLocal(
                                it?.login.toString(),
                                it?.avatarUrl.toString(),
                                it?.login.toString(),
                                false
                            )
                        )
                    }
                    isUserFavorite(false)
                    Toast.makeText(this, "User deleted from favorite", Toast.LENGTH_SHORT).show()
                } else {
                    userDetail.let {
                        detailUserViewModel.saveAsFavorite(
                            UserLocal(
                                it?.login.toString(),
                                it?.avatarUrl.toString(),
                                it?.login.toString(),
                                true
                            )
                        )
                    }
                    isUserFavorite(true)
                    Toast.makeText(this, "User added to favorite", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}