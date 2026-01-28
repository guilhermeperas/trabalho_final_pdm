package com.example.grupo_pdm.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import com.example.grupo_pdm.databinding.ItemAdminOptionBinding
import android.view.LayoutInflater

class AdminHomeAdapter(
    private val items: List<String>,
    private val onItemClick: (Int) -> Unit
) : RecyclerView.Adapter<AdminHomeAdapter.ViewHolder>() {

    inner class ViewHolder(
        private val binding: ItemAdminOptionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(text: String, position: Int) {
            binding.txtOptionTitle.text = text
            binding.root.setOnClickListener {
                onItemClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminOptionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int = items.size
}