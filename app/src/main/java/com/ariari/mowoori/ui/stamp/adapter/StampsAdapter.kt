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
import com.bumptech.glide.Glide

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
            // TODO: 이런 로직은 뷰모델에서 해야하는데, 어댑터에 대한 뷰모델을 만들어아 할까?
            when {
                stampInfo.pictureUrl.contains("default") -> {
                    // TODO: 기본 이미지 적용
                    Glide.with(binding.ivItemStamps)
                        .load(R.drawable.ic_launcher_background)
                        .circleCrop()
                        .into(binding.ivItemStamps)

//                    binding.ivItemStamps.setImageResource(R.drawable.ic_launcher_background)
//                    binding.ivItemStamps.clipToOutline = true
                    binding.tvItemStampsIndex.isInvisible = true
                }
                stampInfo.pictureUrl != "" -> {
                    // TODO: 글라이드
                }
                else -> {
                    // TODO: 번호만 표시
                }
            }
        }
    }
}
