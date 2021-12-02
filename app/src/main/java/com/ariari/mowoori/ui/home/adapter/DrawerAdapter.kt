package com.ariari.mowoori.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ItemDrawerGroupBinding
import com.ariari.mowoori.ui.home.entity.Group

class DrawerAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Group, DrawerAdapter.GroupViewHolder>(diffUtil) {

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Group>() {
            override fun areItemsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem.groupId == newItem.groupId
            }

            override fun areContentsTheSame(oldItem: Group, newItem: Group): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun itemClick(groupId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        return GroupViewHolder(
            ItemDrawerGroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class GroupViewHolder(private val binding: ItemDrawerGroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                binding.group?.groupId?.let { id ->
                    listener.itemClick(id)
                }
            }
        }

        fun bind(group: Group) {
            binding.group = group
            binding.tvDrawerGroupName.text = group.groupInfo.groupName
            if (group.selected) {
                binding.root.setBackgroundResource(R.drawable.border_sky_blue_fill_16)
            } else {
                binding.root.setBackgroundResource(R.drawable.border_transparent_fill)
            }
            binding.executePendingBindings()
        }
    }
}
