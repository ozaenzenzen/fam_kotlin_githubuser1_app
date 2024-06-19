package com.example.famgithubuser1.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.famgithubuser1.R
import com.example.famgithubuser1.data.room.UserLocal
import com.example.famgithubuser1.databinding.UserItemCardBinding

class ListUserFavoriteAdapter :
    ListAdapter<UserLocal, ListUserFavoriteAdapter.MyViewHolder>(ListUserFavoriteAdapter.DIFF_CALLBACK) {
    class MyViewHolder(val binding: UserItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(itemUser: UserLocal) {
//            binding.cardIdUsername.text = "${itemUser.login}"
//        }
    }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListUserFavoriteAdapter.MyViewHolder {
        val binding =
            UserItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListUserFavoriteAdapter.MyViewHolder, position: Int) {
        val itemUser = getItem(position)
//        holder.bind(itemUser)
        holder.binding.apply {
            holder.binding.cardIdUsername.text = "${itemUser.login}"
            Glide
                .with(holder.itemView.context)
                .load(itemUser.avatarUrl.toString())
                .placeholder(R.drawable.profile_placeholder)
                .into(holder.binding.cardImageProfile);
        }

        holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(itemUser) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserLocal>() {
            override fun areItemsTheSame(oldItem: UserLocal, newItem: UserLocal): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UserLocal, newItem: UserLocal): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: UserLocal)
    }
}