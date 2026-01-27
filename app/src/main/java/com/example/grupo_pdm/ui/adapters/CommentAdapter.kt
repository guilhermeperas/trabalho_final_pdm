package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.RatingResponse
import com.example.grupo_pdm.databinding.ItemCommentBinding

/**
 * Adapter for displaying user ratings with author, comment, and score.
 */
class RatingAdapter : ListAdapter<RatingResponse, RatingAdapter.RatingViewHolder>(RatingDiffCallback()) {

    inner class RatingViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(rating: RatingResponse) {
            binding.tvUser.text = "User #${rating.author}"
            binding.tvComment.text = rating.comment?.takeIf { it.isNotBlank() } ?: "No comment"
            binding.tvDate.text = "" // No date in API response
            binding.tvRating.text = "${rating.score}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RatingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RatingDiffCallback : DiffUtil.ItemCallback<RatingResponse>() {
        override fun areItemsTheSame(oldItem: RatingResponse, newItem: RatingResponse): Boolean {
            return oldItem.author == newItem.author // Use author as unique id
        }

        override fun areContentsTheSame(oldItem: RatingResponse, newItem: RatingResponse): Boolean {
            return oldItem == newItem
        }
    }
}

