package com.example.grupo_pdm.ui.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.databinding.ItemMovieResultBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieResultAdapter(
    // scope vindo do Fragment (viewLifecycleOwner.lifecycleScope).
    // Usamos este scope para lançar coroutines e carregar imagens de forma assíncrona,
    // sem bloquear a UI (main thread)
    private val scope: CoroutineScope,
    // Callback chamado quando o utilizador clica num filme da lista.
    // O Fragment decide o que fazer (ex.: navegar para detalhes).
    private val onMovieClick: (MovieResponse) -> Unit = {}
) : RecyclerView.Adapter<MovieResultAdapter.VH>() {

    // Lista interna de itens que o adapter está a mostrar.
    // O RecyclerView vai pedir “quantos” (getItemCount) e vai bindar (onBindViewHolder).
    private val items = mutableListOf<MovieResponse>()

    /**
     * Atualiza a lista mostrada no RecyclerView.
     * Estratégia simples: limpa tudo e mete tudo de novo (notifyDataSetChanged()).
     * (Mais tarde pode ser melhorado com DiffUtil para performance).
     */
    fun submitList(newItems: List<MovieResponse>) {
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

        fun bind(movie: MovieResponse) {

            // --- Título ---
            binding.tvTitle.text = movie.title

            // --- Meta: ano + géneros ---
            // Ex.: "1999 • Horror/Romance"
            val year = movie.releaseDate?.take(4) ?: ""                 // pega só no ano (YYYY)
            val genres = movie.genres.joinToString("/")                 // junta géneros com "/"
            binding.tvMeta.text = listOf(year, genres)                  // cria lista [ano, generos]
                .filter { it.isNotBlank() }                             // remove vazios
                .joinToString(" • ")                                    // junta com " • "

            // --- Poster ---
            // 1) Mete placeholder primeiro para evitar mostrar “imagem antiga” quando recicla.
            binding.ivPoster.setImageResource(android.R.drawable.ic_menu_report_image)

            // 2) Se existir mainPicture, tenta buscar os bytes da imagem via API e converter para Bitmap.
            val picId = movie.mainPicture?.id
            if (picId != null) {

                // Lança coroutine para fazer a chamada de rede fora da thread principal.
                scope.launch {

                    // Chamada ao client (Ktor) para obter os bytes da imagem.
                    val bytes = MovieServiceClient.getMoviePictureBytes(movie.id, picId)

                    // Se vier bytes, converte para bitmap e coloca na ImageView.
                    if (bytes != null) {
                        // decodeByteArray pode ser pesado, por isso usamos Dispatchers.Default
                        val bmp = withContext(Dispatchers.Default) {
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        }
                        binding.ivPoster.setImageBitmap(bmp)
                    }
                }
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
