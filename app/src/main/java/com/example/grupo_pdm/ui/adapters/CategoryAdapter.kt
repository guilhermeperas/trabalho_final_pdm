package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.CategoryResponse
import com.example.grupo_pdm.databinding.ItemCategoryBinding

/**
 * Adapter for displaying categories in a horizontal RecyclerView.
 */
class CategoryAdapter(
    onCategoryClick: (CategoryResponse) -> Unit = {}
) : BaseAdapter<CategoryResponse, CategoryAdapter.CategoryViewHolder>(onItemClick = onCategoryClick) {

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryResponse) {
            binding.tvCategoryName.text = category.name

            binding.root.setOnClickListener {
                onItemClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
