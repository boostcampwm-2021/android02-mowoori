package com.ariari.mowoori.ui.missions.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.databinding.ItemMissionsBinding
import com.ariari.mowoori.ui.missions.entity.Mission

class MissionsAdapter : ListAdapter<Mission, MissionsAdapter.MissionsViewHolder>(missionsDiffUtil) {
    class MissionsViewHolder(private val binding: ItemMissionsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(mission: Mission) {
            binding.missionInfo = mission.missionInfo
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissionsViewHolder =
        MissionsViewHolder(
            ItemMissionsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: MissionsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val missionsDiffUtil = object : DiffUtil.ItemCallback<Mission>() {
            override fun areItemsTheSame(oldItem: Mission, newItem: Mission): Boolean =
                oldItem.missionId == newItem.missionId

            override fun areContentsTheSame(oldItem: Mission, newItem: Mission): Boolean =
                oldItem == newItem

        }
    }
}
