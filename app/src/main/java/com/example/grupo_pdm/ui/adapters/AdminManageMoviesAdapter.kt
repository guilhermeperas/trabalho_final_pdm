package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.databinding.ItemAdminManageMovieBinding

class AdminManageMoviesAdapter(
    private val onEdit: (MovieResponse2) -> Unit,
    private val onDelete: (MovieResponse2) -> Unit
) : ListAdapter<MovieResponse2, AdminManageMoviesAdapter.ViewHolder>(
    object : DiffUtil.ItemCallback<MovieResponse2>() {

        override fun areItemsTheSame(a: MovieResponse2, b: MovieResponse2) =
            a.id == b.id

        override fun areContentsTheSame(a: MovieResponse2, b: MovieResponse2) =
            a == b
    }
) {

    inner class ViewHolder(
        private val binding: ItemAdminManageMovieBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieResponse2) {
            binding.txtTitle.text = movie.title

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
            ItemAdminManageMovieBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}