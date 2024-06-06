package com.example.famgithubuser1.ui.view

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.R
import com.example.famgithubuser1.data.response.SearchUserResponseModel
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.databinding.ActivityMainBinding
import com.example.famgithubuser1.ui.adapter.ListUserAdapter
import com.example.famgithubuser1.ui.view.DetailUserActivity.Companion.EXTRA_DETAIL
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

        // Set up the toolbar
        setSupportActionBar(findViewById<Toolbar>(R.id.toolbar))

        // Ensure SearchView is expanded by default
        val searchView = findViewById<SearchView>(R.id.search_view)
        if (searchView != null) {
            searchView.setIconifiedByDefault(false)
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
                                searchUserGithub(it.toString())
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

    private fun searchUserGithub(userName: String) {
        showLoading(true)
        val client =
            ApiConfig.getApiService().searchUsername("Bearer ${BuildConfig.API_KEY}", userName)
        client.enqueue(object : Callback<SearchUserResponseModel> {
            override fun onResponse(
                call: Call<SearchUserResponseModel>,
                response: Response<SearchUserResponseModel>
            ) {
                showLoading(false)
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    setRecycleViewData(responseBody.items)
                } else {
                    Log.e(TAG, "onFailure ${response.message()}")
                }
            }

            override fun onFailure(call: Call<SearchUserResponseModel>, t: Throwable) {
                showLoading(false)
                Log.e(TAG, "onFailure ${t.message}")
            }
        })
    }

    private fun setRecycleViewData(listUserModelData: List<UserModel>) {
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