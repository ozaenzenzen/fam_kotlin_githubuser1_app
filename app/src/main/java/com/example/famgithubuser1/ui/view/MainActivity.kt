package com.example.famgithubuser1.ui.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.R
import com.example.famgithubuser1.data.factory.ViewModelFactory
import com.example.famgithubuser1.data.response.SearchUserResponseModel
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.data.service.SettingPreferences
import com.example.famgithubuser1.data.service.dataStore
import com.example.famgithubuser1.databinding.ActivityMainBinding
import com.example.famgithubuser1.ui.adapter.ListUserAdapter
import com.example.famgithubuser1.ui.view.DetailUserActivity.Companion.EXTRA_DETAIL
import com.example.famgithubuser1.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var pref = SettingPreferences.getInstance(application.dataStore)

        val mainViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pref, application)
        ).get(MainViewModel::class.java)
        mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
            if (isDarkModeActive) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Set up the toolbar
        setSupportActionBar(binding.appBarMain.toolbar)

        mainViewModel.listUser.observe(this) { data ->
            setRecycleViewData(data)
        }

        // Ensure SearchView is expanded by default
        val searchView = findViewById<SearchView>(R.id.search_view)
        if (searchView != null) {
            searchView.setIconifiedByDefault(false)
        }

        mainViewModel.isLoading.observe(this) { value ->
            showLoading(value)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    searchView.queryHint = "Github Username"
                    searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                        override fun onQueryTextSubmit(query: String?): Boolean {
                            query?.let {
//                                print("Submitted $it")
//                                Log.d("Tag", "Submitted $it")
                                mainViewModel.searchUserGithub(it.toString())
                            }
                            return true
                        }

                        override fun onQueryTextChange(newText: String?): Boolean {
                            if (newText.isNullOrEmpty()) {
                                // Clear button was clicked
                                println("Clear button clicked")
                                // Perform any action you want when the clear button is clicked
                            }
                            return false
                        }
                    })
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                Intent(this@MainActivity, FavoriteActivity::class.java).also {
                    startActivity(it)
                }
            }

            R.id.settings -> {
                Intent(this@MainActivity, SettingsActivity::class.java).also {
                    startActivity(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setRecycleViewData(listUserModelData: ArrayList<UserModel>) {
        val listUserAdapter = ListUserAdapter()
        listUserAdapter.submitList(listUserModelData)
        binding.home.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = listUserAdapter
            setHasFixedSize(true)
        }
        listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
            override fun onItemClicked(user: UserModel) {
                goToDetailUser(user)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.home.recyclerView.visibility = View.GONE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.home.recyclerView.visibility = View.VISIBLE
        }
    }

    private fun errorAction() {
        Toast.makeText(this@MainActivity, "An Error is Occurred", Toast.LENGTH_SHORT).show()
    }

    private fun goToDetailUser(user: UserModel) {
        Intent(this@MainActivity, DetailUserActivity::class.java).apply {
            putExtra(EXTRA_DETAIL, user.login)
        }.also {
            startActivity(it)
        }
    }
}