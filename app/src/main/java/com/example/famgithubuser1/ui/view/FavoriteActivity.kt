package com.example.famgithubuser1.ui.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.famgithubuser1.data.factory.ViewModelFactory
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.room.UserLocal
import com.example.famgithubuser1.data.service.SettingPreferences
import com.example.famgithubuser1.data.service.dataStore
import com.example.famgithubuser1.databinding.ActivityFavoriteBinding
import com.example.famgithubuser1.ui.adapter.ListUserFavoriteAdapter
import com.example.famgithubuser1.ui.viewmodel.FavoriteViewModel
import kotlinx.coroutines.launch

class FavoriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavoriteBinding

    companion object {
        const val EXTRA_USER = "extra_user"
        const val ALERT_DIALOG_CLOSE = 10
        const val ALERT_DIALOG_DELETE = 20
    }

    private var isEdit = false
    private var userLocal: UserLocal? = null

    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbar("Favorite List")

        var pref = SettingPreferences.getInstance(application.dataStore)

        favoriteViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref, application)
        ).get(FavoriteViewModel::class.java)

        favoriteViewModel.getAllUsersFavorite().observe(this) { listUser ->
            setRecycleViewData(listUser)
        }
    }

    private fun setRecycleViewData(listUserFavData: List<UserLocal>) {
        val listUserFavoriteAdapter = ListUserFavoriteAdapter()
        listUserFavoriteAdapter.submitList(listUserFavData)
        binding.rvFavorite.apply {
            layoutManager = LinearLayoutManager(this@FavoriteActivity)
            adapter = listUserFavoriteAdapter
            setHasFixedSize(true)
        }
        listUserFavoriteAdapter.setOnItemClickCallback(object : ListUserFavoriteAdapter.OnItemClickCallback {
            override fun onItemClicked(user: UserLocal) {
                goToDetailUser(user)
            }
        })
    }

    private fun goToDetailUser(user: UserLocal) {
        Intent(this@FavoriteActivity, DetailUserActivity::class.java).apply {
            putExtra(DetailUserActivity.EXTRA_DETAIL, user.login)
        }.also {
            startActivity(it)
        }
    }

    private fun setToolbar(title: String) {
        setSupportActionBar(binding.appBarMain.toolbar)
        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            this.title = title
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}