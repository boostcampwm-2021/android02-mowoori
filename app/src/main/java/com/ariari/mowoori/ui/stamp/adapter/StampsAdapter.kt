package com.ariari.mowoori.ui.stamp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ItemStampsBinding
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo

class StampsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Stamp, StampsAdapter.StampViewHolder>(stampsDiffUtil) {

    companion object {
        val stampsDiffUtil = object : DiffUtil.ItemCallback<Stamp>() {
            override fun areItemsTheSame(oldItem: Stamp, newItem: Stamp): Boolean {
                return oldItem.stampId == newItem.stampId
            }

            override fun areContentsTheSame(oldItem: Stamp, newItem: Stamp): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun itemClick(position: Int, imageView: ImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StampViewHolder {
        return StampViewHolder(ItemStampsBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: StampViewHolder, position: Int) {
        holder.bind(getItem(position).stampInfo)
    }

    inner class StampViewHolder(private val binding: ItemStampsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.itemClick(adapterPosition, binding.ivItemStamps)
            }
        }

        fun bind(stampInfo: StampInfo) {
            ViewCompat.setTransitionName(binding.ivItemStamps, stampInfo.pictureUrl)
            binding.tvItemStampsIndex.text = (adapterPosition + 1).toString()
            if (stampInfo.pictureUrl != "") {
                binding.ivItemStamps.setImageResource(R.drawable.ic_launcher_background)
                binding.tvItemStampsIndex.isInvisible = true
            }
        }
    }
}
