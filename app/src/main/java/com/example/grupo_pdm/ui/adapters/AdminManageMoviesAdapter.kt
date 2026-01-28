package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.databinding.ItemAdminManageBinding

class AdminManageMoviesAdapter(
    private val onEdit: (MovieResponse) -> Unit,
    private val onDelete: (MovieResponse) -> Unit
) : ListAdapter<MovieResponse, AdminManageMoviesAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<MovieResponse>() {
        override fun areItemsTheSame(a: MovieResponse, b: MovieResponse) =
            a.id == b.id

        override fun areContentsTheSame(a: MovieResponse, b: MovieResponse) =
            a == b
    }
) {

    inner class ViewHolder(
        private val binding: ItemAdminManageBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieResponse) {
            binding.txtItemName.text = movie.title

            binding.btnEdit.setOnClickListener {
                onEdit(movie)
            }

            binding.btnDelete.setOnClickListener {
                onDelete(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            ItemAdminManageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}