package com.example.appusagetracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.appusagetracker.databinding.ItemAppUsageBinding

class UsageAdapter(private val items: List<AppUsageInfo>) :
    RecyclerView.Adapter<UsageAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemAppUsageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppUsageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.appIcon.setImageDrawable(item.icon)
        holder.binding.appName.text = item.appName
        holder.binding.appDuration.text = item.formattedDuration()
    }

    override fun getItemCount(): Int = items.size
}
