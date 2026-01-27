package com.example.grupo_pdm.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.MovieResponse
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
    // scope opcional:
    // - Se for passado (ex.: viewLifecycleOwner.lifecycleScope), o adapter consegue carregar imagens async.
    // - Se for null, o adapter mostra só placeholder (útil para não obrigar a passar scope em todo o lado).
    private val scope: CoroutineScope? = null,
    onMovieClick: (MovieResponse) -> Unit = {}
) : BaseAdapter<MovieResponse, MovieAdapter.MovieViewHolder>(onItemClick = onMovieClick) {

    /**
     * ViewHolder da lista HORIZONTAL (recomendações).
     * Usa o layout item_movie_reco.xml (via ItemMovieRecoBinding).
     */
    inner class MovieViewHolder(private val binding: ItemMovieRecoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: MovieResponse) {

            binding.tvTitle.text = movie.title

            // --- Poster: começa sempre com placeholder ---
            // Importante para evitar que a RecyclerView mostre imagens “antigas” quando recicla Views.
            binding.ivPoster.setImageResource(android.R.drawable.ic_menu_report_image)

            // --- Poster: carregar a imagem real se existir mainPicture ---
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

                    val bytes = withContext(Dispatchers.IO) {
                        MovieServiceClient.getMoviePictureBytes(movie.id, pictureId)
                    }
                    if (bytes != null && binding.ivPoster.tag == tagValue) {

                        // decodeByteArray pode ser pesado -> Dispatchers.Default
                        val bmp = withContext(Dispatchers.Default) {
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }
                        if (binding.ivPoster.tag == tagValue) {
                            binding.ivPoster.setImageBitmap(bmp)
                        }
                    }
                }
            }

            // --- Clique no item ---
            // onItemClick vem do BaseAdapter (passado no construtor).
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

