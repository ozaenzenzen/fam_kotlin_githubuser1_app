package com.example.famgithubuser1.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.famgithubuser1.ui.view.FollowersFragment
import com.example.famgithubuser1.ui.view.FollowingFragment

class TabListFollAdapter(activity: AppCompatActivity, private val username: String) :
    FragmentStateAdapter(activity) {

    companion object {
        const val ARGS_USERNAME = "username"
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FollowersFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARGS_USERNAME, username)
                    }
                }
            }

            else -> {
                FollowingFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARGS_USERNAME, username)
                    }
                }
            }
        }
    }
}