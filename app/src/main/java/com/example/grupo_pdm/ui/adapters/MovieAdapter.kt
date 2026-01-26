package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.Movie
import com.example.grupo_pdm.databinding.ItemMovieRecoBinding

/**
 * Adapter for displaying movies in a horizontal RecyclerView.
 * Used for both "New" and "Trending" sections on the main page.
 */
class MovieAdapter(
    onMovieClick: (Movie) -> Unit = {}
) : BaseAdapter<Movie, MovieAdapter.MovieViewHolder>(onItemClick = onMovieClick) {

    inner class MovieViewHolder(private val binding: ItemMovieRecoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.tvTitle.text = movie.title
            // TODO: Load poster image with Glide/Coil
            // Glide.with(binding.root).load(movie.posterUrl).into(binding.ivPoster)

            binding.root.setOnClickListener {
                onItemClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieRecoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
