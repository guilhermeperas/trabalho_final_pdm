package com.example.grupo_pdm.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.databinding.ItemMovieResultBinding

class MovieResultAdapter(
    private val onMovieClick: (MovieResponse2) -> Unit = {

    }
) : RecyclerView.Adapter<MovieResultAdapter.VH>() {

    // Lista interna de itens que o adapter está a mostrar.
    // O RecyclerView vai pedir “quantos” (getItemCount) e vai bindar (onBindViewHolder).
    private val items = mutableListOf<MovieResponse2>()

    /**
     * Atualiza a lista mostrada no RecyclerView.
     * Estratégia simples: limpa tudo e mete tudo de novo (notifyDataSetChanged()).
     * (Mais tarde pode ser melhorado com DiffUtil para performance).
     */
    fun submitList(newItems: List<MovieResponse2>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
    /**
     * ViewHolder: representa 1 linha/célula na lista vertical (item_movie_result.xml).
     * O binding dá acesso direto às views (tvTitle, tvMeta, ivPoster, etc.).
     */
    inner class VH(private val binding: ItemMovieResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        /**
         * Preenche o layout do item com os dados do filme.
         * Este método é chamado sempre que o RecyclerView precisa “desenhar”/reutilizar um item.
         */

        fun bind(movie: MovieResponse2) {

            // --- Título ---
            binding.tvTitle.text = movie.title
            // --- Meta: ano + géneros ---
            // Ex.: "1999 • Horror/Romance"
            val year = movie.releaseDate?.take(4) ?: ""                 // pega só no ano (YYYY)
            val genres = movie.genres?.joinToString(" ") { it.name } ?: ""
            binding.tvMeta.text = listOf(year, genres)                  // cria lista [ano, generos]
                .joinToString(" • ")                                    // junta com " • "

            // --- Poster ---
            // Se existir mainPicture, usar Coil direto com URL
            val picId = movie.mainPicture?.id
            if (picId != null) {
                val url = "http://10.0.2.2:8080/movies/${movie.id}/pictures/$picId"
                binding.ivPoster.load(url) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.ivPoster.setImageResource(android.R.drawable.ic_menu_report_image)
            }

            // --- Clique no item ---
            // Quando o utilizador toca no item, chamamos o callback.
            binding.root.setOnClickListener { onMovieClick(movie) }
        }
    }
    /**
     * Cria (infla) um novo ViewHolder quando o RecyclerView precisa de um item novo.
     */

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemMovieResultBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VH(binding)
    }
    /**
     * Liga (bind) o item da posição "position" ao ViewHolder.
     * Aqui chamamos holder.bind(...) com o MovieResponse correspondente.
     */
    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    /**
     * Diz ao RecyclerView quantos itens existem na lista atual.
     */
    override fun getItemCount(): Int = items.size
}
