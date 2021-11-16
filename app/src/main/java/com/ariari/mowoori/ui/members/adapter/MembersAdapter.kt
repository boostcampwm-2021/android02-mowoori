package com.ariari.mowoori.ui.members.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.databinding.ItemMembersBinding
import com.ariari.mowoori.ui.register.entity.User

class MembersAdapter : ListAdapter<User, MembersAdapter.MembersViewHolder>(membersDiffUtil) {
    class MembersViewHolder(private val binding: ItemMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.userInfo = user.userInfo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MembersViewHolder =
        MembersViewHolder(
            ItemMembersBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: MembersViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        private val membersDiffUtil = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
                oldItem == newItem

        }
    }
}
