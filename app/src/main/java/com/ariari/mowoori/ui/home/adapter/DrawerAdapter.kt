package com.ariari.mowoori.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ItemDrawerGroupBinding
import com.ariari.mowoori.databinding.ItemDrawerHeaderBinding
import com.ariari.mowoori.ui.home.entity.Group
import com.ariari.mowoori.ui.home.entity.GroupInfo

class DrawerAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun itemClick(groupId: String)
    }

    companion object {
        private const val HEADER = 0
        private const val GROUP = 1
    }

    var groups = listOf<Group>()

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            else -> GROUP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> HeaderViewHolder(
                ItemDrawerHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> GroupViewHolder(
                ItemDrawerGroupBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> Unit // nothing to bind
            else -> (holder as GroupViewHolder).bind(groups[position - 1])
        }
    }

    override fun getItemCount(): Int = groups.size + 1

    inner class HeaderViewHolder(binding: ItemDrawerHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvDrawerHeaderAdd.setOnClickListener {
                it.findNavController().navigate(R.id.action_homeFragment_to_inviteCheckFragment)
            }
        }
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
