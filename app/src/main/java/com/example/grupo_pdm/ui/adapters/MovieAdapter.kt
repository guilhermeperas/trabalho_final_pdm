package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.databinding.ItemMovieRecoBinding


/**
 * Adapter for displaying movies in a horizontal RecyclerView.
 * Used for both "New" and "Trending" sections on the main page.
 */
class MovieAdapter(
    private val onMovieClick: (MovieResponse2) -> Unit = {},
    private val onFavoriteClick: (MovieResponse2) -> Unit = {}
) : BaseAdapter<MovieResponse2, MovieAdapter.MovieViewHolder>(onItemClick = onMovieClick) {

    private var favoriteIds: Set<Int> = emptySet()

    fun updateFavorites(ids: Set<Int>) {
        favoriteIds = ids
        notifyDataSetChanged()
    }

    inner class MovieViewHolder(private val binding: ItemMovieRecoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieResponse2) {
            binding.tvTitle.text = movie.title

            // --- Poster: começa sempre com placeholder ---
            // Importante para evitar que a RecyclerView mostre imagens “antigas” quando recicla Views.
            val pictureId = movie.mainPicture?.id
            if (pictureId != null) {
                 binding.ivPoster.load("http://10.0.2.2:8080/movies/${movie.id}/pictures/$pictureId") {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.ivPoster.setImageResource(android.R.drawable.ic_menu_report_image)
            }


            // Show favorite status
            val isFavorite = favoriteIds.contains(movie.id)
            binding.ivFavorite.setImageResource(
                if (isFavorite) R.drawable.ic_heart_filled_24 else R.drawable.ic_heart_outline_24
            )

            // Card click -> navigate to detail
            binding.root.setOnClickListener {
                onMovieClick(movie)
            }

            // Heart click -> toggle favorite (doesn't navigate)
            binding.ivFavorite.setOnClickListener {
                onFavoriteClick(movie)
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
