package com.ariari.mowoori.ui.stamp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.ItemStampsBinding
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo
import com.ariari.mowoori.util.LogUtil
import com.bumptech.glide.Glide
import timber.log.Timber

class StampsAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Stamp, StampsAdapter.StampViewHolder>(stampsDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StampViewHolder {
        return StampViewHolder(
            ItemStampsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
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
            Timber.d(adapterPosition.toString())
            binding.tvItemStampsIndex.text = (adapterPosition + 1).toString()
            // TODO: 이런 로직은 뷰모델에서 해야하는데, 어댑터에 대한 뷰모델을 만들어아 할까?
            when {
                stampInfo.pictureUrl.contains("default") -> {
                    // TODO: 기본 이미지 적용
                    Glide.with(binding.ivItemStamps)
                        .load(R.drawable.ic_launcher_background)
                        .circleCrop()
                        .into(binding.ivItemStamps)

                    binding.tvItemStampsIndex.isInvisible = true
                    binding.containerItemStamps.isClickable = true
                }
                stampInfo.pictureUrl != "" -> {
                    LogUtil.log("adapter url", stampInfo.pictureUrl)

                    Glide.with(binding.ivItemStamps)
                        .load(stampInfo.pictureUrl)
                        .circleCrop()
                        .into(binding.ivItemStamps)
                    binding.tvItemStampsIndex.isInvisible = true
                    binding.containerItemStamps.isClickable = true
                }
                else -> {
                    binding.ivItemStamps.setImageResource(R.drawable.border_sky_blue_line_oval)
                    binding.tvItemStampsIndex.isVisible = true
                    binding.containerItemStamps.isClickable = false
                }
            }
        }
    }

    interface OnItemClickListener {
        fun itemClick(position: Int, imageView: ImageView)
    }

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
}
