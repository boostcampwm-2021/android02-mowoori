package com.ariari.mowoori.ui.stamp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ariari.mowoori.databinding.ItemStampsBinding
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.ui.stamp.entity.StampInfo

class StampsAdapter : RecyclerView.Adapter<StampsAdapter.StampViewHolder>() {

    private val stampList = listOf(Stamp("1", StampInfo("123", "hello", "2021")),
        Stamp("1", StampInfo("123", "hello", "2021")),
        Stamp("1", StampInfo("123", "hello", "2021")),
        Stamp("1", StampInfo("123", "hello", "2021")),
        Stamp("1", StampInfo("123", "hello", "2021")),
        Stamp("1", StampInfo("123", "hello", "2021")),
        Stamp("1", StampInfo("123", "hello", "2021")))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StampViewHolder {
        return StampViewHolder(ItemStampsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: StampViewHolder, position: Int) {
        holder.bind(stampList[position])
    }

    override fun getItemCount(): Int = stampList.size

    class StampViewHolder(private val binding: ItemStampsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stamp: Stamp) {
            binding.tvItemStamps.text = stamp.stampId
        }
    }
}
