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
import com.ariari.mowoori.util.BindingAdapters.bindImageUrl
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
        holder.bind(getItem(position))
    }

    inner class StampViewHolder(private val binding: ItemStampsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                listener.itemClick(adapterPosition, binding.ivItemStamps)
            }
        }

        fun bind(stamp: Stamp) {
            ViewCompat.setTransitionName(binding.ivItemStamps, stamp.stampId)
            Timber.d(adapterPosition.toString())
            binding.tvItemStampsIndex.text = (adapterPosition + 1).toString()
            when (stamp.stampInfo.pictureUrl) {
                "empty" -> {
                    // 빈 스탬프
                    with(binding) {
                        ivItemStamps.setImageResource(R.drawable.border_sky_blue_line_oval)
                        tvItemStampsIndex.isVisible = true
                        containerItemStamps.isClickable = false
                    }
                }
                "" -> {
                    // picture url이 없는 스탬프
                    with(binding) {
                        tvItemStampsIndex.isInvisible = true
                        containerItemStamps.isClickable = true
                    }
                    Glide.with(binding.ivItemStamps)
                        .load(R.drawable.ic_stamp)
                        .circleCrop()
                        .into(binding.ivItemStamps)
                }
                else -> {
                    // picture url이 있는 스탬프
                    LogUtil.log("adapter url", stamp.stampInfo.pictureUrl)

                    with(binding) {
                        tvItemStampsIndex.isInvisible = true
                        containerItemStamps.isClickable = true
                        ivItemStamps.bindImageUrl(stamp.stampInfo.pictureUrl, true)

//                      Glide.with(binding.ivItemStamps)
//                        .load(stampInfo.pictureUrl)
//                        .circleCrop()
//                        .into(binding.ivItemStamps)
                    }
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
