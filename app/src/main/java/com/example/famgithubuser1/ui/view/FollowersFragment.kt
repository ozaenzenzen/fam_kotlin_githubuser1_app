package com.example.famgithubuser1.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.databinding.FragmentFollowersBinding
import com.example.famgithubuser1.ui.adapter.ListUserAdapter
import com.example.famgithubuser1.ui.viewmodel.FollowersViewModel
import com.example.famgithubuser1.ui.viewmodel.MainViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FollowersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowersFragment : Fragment() {
    private lateinit var binding: FragmentFollowersBinding

    private val followersViewModel: FollowersViewModel by viewModels()

    companion object {
        const val ARGS_USERNAME = "username"
        private const val TAG = "FollowersFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString(ARGS_USERNAME) ?: ""
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                followersViewModel.listFollowers.observe(viewLifecycleOwner) {
                    setRecycleViewFollowers(it)
                }
                followersViewModel.isLoading.observe(viewLifecycleOwner) { value ->
                    showLoading(value)
                }
            }
            launch {

                followersViewModel.followersUserGithub(username ?: "")
            }
        }
    }

    fun setRecycleViewFollowers(listFollowersResponseModel: ArrayList<UserModel>) {
        if (listFollowersResponseModel.size > 0) {
            val listUserAdapter = ListUserAdapter()
            listUserAdapter.submitList(listFollowersResponseModel)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = listUserAdapter
                setHasFixedSize(true)
            }
            listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
                override fun onItemClicked(user: UserModel) {
                    goToDetailUser(user)
                }
            })
        }
    }

    private fun goToDetailUser(user: UserModel) {
        Intent(activity, DetailUserActivity::class.java).apply {
            putExtra(DetailUserActivity.EXTRA_DETAIL, user.login)
        }.also {
            startActivity(it)
        }
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
}