package com.example.grupo_pdm.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.databinding.ItemMovieRecoBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Adapter for displaying movies in a horizontal RecyclerView.
 * Used for both "New" and "Trending" sections on the main page.
 */
class MovieAdapter(
    private val scope: CoroutineScope? = null,
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
            binding.ivPoster.setImageResource(android.R.drawable.ic_menu_report_image)
            val pictureId = movie.mainPicture?.id
            if (pictureId != null) {

                // “Tag” para evitar bug comum de RecyclerView:
                // se o utilizador faz scroll rápido, a mesma ImageView é reutilizada,
                // e uma coroutine antiga pode acabar por colocar a imagem errada.
                // Com tagValue, só aplicamos a imagem se ainda for o mesmo item.
                val tagValue = "${movie.id}:$pictureId"
                binding.ivPoster.tag = tagValue

                // Se não houver scope (null), não carrega imagem (fica placeholder).
                scope?.launch {

                    // 1) Chamada de rede (IO)
                    val bytes = withContext(Dispatchers.IO) {
                        MovieServiceClient.getMoviePictureBytes(movie.id, pictureId)
                    }

                    // 2) Se ainda for o mesmo item (tag igual) e vier bytes, decodifica
                    if (bytes != null && binding.ivPoster.tag == tagValue) {

                        // decodeByteArray pode ser pesado -> Dispatchers.Default
                        val bmp = withContext(Dispatchers.Default) {
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }

                        // 3) Confirma tag outra vez (segurança extra) antes de aplicar
                        if (binding.ivPoster.tag == tagValue) {
                            binding.ivPoster.setImageBitmap(bmp)
                        }
                    }
                }
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
