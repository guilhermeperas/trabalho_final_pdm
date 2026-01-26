package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.GenreResponse
import com.example.grupo_pdm.databinding.ItemCategoryBinding

/**
 * Adapter for displaying genres in a horizontal RecyclerView.
 */
class GenreAdapter(
    onGenreClick: (GenreResponse) -> Unit = {}
) : BaseAdapter<GenreResponse, GenreAdapter.GenreViewHolder>(onItemClick = onGenreClick) {

    inner class GenreViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(genre: GenreResponse) {
            binding.tvCategoryName.text = genre.name

            binding.root.setOnClickListener {
                onItemClick(genre)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return GenreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
