package com.calculator.app

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.calculator.app.databinding.ItemHistoryBinding

/**
 * RecyclerView adapter for displaying calculation history entries.
 */
class HistoryAdapter(
    private var items: List<HistoryEntry>
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = items[position]
        holder.binding.tvHistoryExpression.text = entry.expression
        holder.binding.tvHistoryResult.text = entry.result
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(newItems: List<HistoryEntry>) {
        items = newItems
        notifyDataSetChanged()
    }
}
