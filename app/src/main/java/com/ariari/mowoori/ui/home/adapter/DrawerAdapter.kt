package com.ariari.mowoori.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ItemDrawerGroupBinding
import com.ariari.mowoori.databinding.ItemDrawerHeaderBinding
import com.ariari.mowoori.ui.home.entity.GroupInfo

class DrawerAdapter(private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface OnItemClickListener {
        fun itemClick(position: Int)
    }

    companion object {
        private const val HEADER = 0
        private const val GROUP = 1
    }

    private val groups = listOf(
        GroupInfo("정직한 코박쥐들"),
        GroupInfo("못생긴 원숭이들"),
        GroupInfo("고약한 거북이들"),
        GroupInfo("귀여운 개구리들"),
        GroupInfo("행복한 캥거루들")
    )

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
                // nothing to bind
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
                listener.itemClick(adapterPosition)
            }
        }

        fun bind(groupInfo: GroupInfo) {
            binding.tvDrawerGroupName.text = groupInfo.groupName
        }
    }

    fun getHeaderLayoutView(recyclerView: RecyclerView): View {
        return ItemDrawerHeaderBinding.inflate(LayoutInflater.from(recyclerView.context),
            recyclerView,
            false).root
    }
}
