package com.example.famgithubuser1.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.famgithubuser1.BuildConfig
import com.example.famgithubuser1.R
import com.example.famgithubuser1.data.response.UserModel
import com.example.famgithubuser1.data.retrofit.ApiConfig
import com.example.famgithubuser1.databinding.FragmentFollowersBinding
import com.example.famgithubuser1.databinding.FragmentFollowingBinding
import com.example.famgithubuser1.ui.adapter.ListUserAdapter
import com.example.famgithubuser1.ui.viewmodel.DetailUserViewModel
import com.example.famgithubuser1.ui.viewmodel.FollowingViewModel
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
 * Use the [FollowingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FollowingFragment : Fragment() {
    private lateinit var binding: FragmentFollowingBinding

    companion object {
        const val ARGS_USERNAME = "username"
        private const val TAG = "FollowingFragment"
    }

    private lateinit var followingViewModel: FollowingViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFollowingBinding.inflate(layoutInflater)
        followingViewModel = ViewModelProvider(
            requireActivity()
        ).get(FollowingViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val username = arguments?.getString(ARGS_USERNAME) ?: ""
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            launch {
                followingViewModel.listFollowing.observe(viewLifecycleOwner) {
                    setRecycleViewFollowing(it)
                }
                followingViewModel.isLoading.observe(viewLifecycleOwner) { value ->
                    showLoading(value)
                }
            }
            launch {
                followingViewModel.followingUserGithub(username ?: "")
            }
        }
    }


    fun setRecycleViewFollowing(listFollowingResponseModel: ArrayList<UserModel>) {
        if (listFollowingResponseModel.size > 0) {
            val listUserAdapter = ListUserAdapter()
            listUserAdapter.submitList(listFollowingResponseModel)

//            val linearLayoutManager = LinearLayoutManager(activity)
//            val listUserAdapter = ListUserAdapter(listFollowingResponseModel)

            binding.recyclerView.apply {
                 layoutManager = LinearLayoutManager(activity)
//                layoutManager = linearLayoutManager
                adapter = listUserAdapter
                setHasFixedSize(true)
            }
            listUserAdapter.setOnItemClickCallback(object : ListUserAdapter.OnItemClickCallback {
                override fun onItemClicked(user: UserModel) {
                    goToDetailUser(user)
                }
            })
        } else {
            binding.tvStatus.visibility = View.VISIBLE
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