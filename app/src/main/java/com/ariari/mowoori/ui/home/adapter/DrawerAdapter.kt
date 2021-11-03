package com.ariari.mowoori.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ItemDrawerGroupBinding
import com.ariari.mowoori.databinding.ItemDrawerHeaderBinding

class DrawerAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun itemClick()
    }

    companion object {
        private const val HEADER = 0
        private const val GROUP = 1
    }

    private val groups = listOf("정직한 코박쥐들", "멍청한 원숭이들", "화목한 캥거루들")

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> HEADER
            else -> GROUP
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HEADER -> HeaderViewHolder(ItemDrawerHeaderBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
            else -> GroupViewHolder(ItemDrawerGroupBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HeaderViewHolder -> {

            }
            else -> {
                (holder as GroupViewHolder).bind(groups[position - 1])
            }
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
                listener.itemClick()
            }
        }

        fun bind(groupName: String) {
            binding.tvDrawerGroupName.text = groupName
        }
    }
}
