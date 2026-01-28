package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.grupo_pdm.data.DirectedMovie
import com.example.grupo_pdm.databinding.ItemMovieRecoBinding

/**
 * Adapter for displaying movies directed by a person.
 * Reuses item_movie_reco.xml but hides the favorite button.
 */
class DirectedMovieAdapter(
    private val onMovieClick: (DirectedMovie) -> Unit = {}
) : BaseAdapter<DirectedMovie, DirectedMovieAdapter.DirectedMovieViewHolder>(onItemClick = onMovieClick) {

    inner class DirectedMovieViewHolder(private val binding: ItemMovieRecoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: DirectedMovie) {
            binding.tvTitle.text = movie.title
            binding.ivFavorite.visibility = View.GONE // Hide favorite button

            val pictureId = movie.picture?.id
            if (pictureId != null) {
                // TODO: Update base URL to be dynamic or constant
                binding.ivPoster.load("http://10.0.2.2:8080/movies/${movie.id}/pictures/$pictureId") {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.ivPoster.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            binding.root.setOnClickListener {
                onItemClick(movie)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectedMovieViewHolder {
        val binding = ItemMovieRecoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DirectedMovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DirectedMovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
