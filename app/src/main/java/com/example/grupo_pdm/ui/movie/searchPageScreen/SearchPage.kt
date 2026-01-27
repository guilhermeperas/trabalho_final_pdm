package com.example.grupo_pdm.ui.movie.searchPageScreen

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.ui.adapters.MovieAdapter
import com.example.grupo_pdm.ui.adapters.MovieResultAdapter
import com.example.grupo_pdm.ui.components.TopBarView
import kotlinx.coroutines.launch


class SearchPage : Fragment(R.layout.fragment_search_page) {
    // Argumentos vindos do Navigation (Safe Args).
    // Aqui estamos a receber, por exemplo, a "query" inicial da pesquisa.
    private val args: SearchPageArgs by navArgs()
    // ViewModel do ecrã. Guarda o estado (resultados e recomendações) e faz chamadas à API.
    private val vm: SearchPageViewModel by viewModels()
    // Guarda o ID do “filme principal” (normalmente o 1º resultado),
    // para depois remover esse filme da lista de recomendações (não recomendar o próprio filme).
    private var currentMovieId: Int? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Views de estado ---
        val progress = view.findViewById<ProgressBar>(R.id.progress)
        val tvMessage = view.findViewById<TextView>(R.id.tvMessage)

        // --- TopBar reutilizável (Home / Search / User) ---
        val topBar = view.findViewById<TopBarView>(R.id.topBar)

        // --- RecyclerView das Recomendações (horizontal) ---
        val rvReco = view.findViewById<RecyclerView>(R.id.rvRecommendations)

        // Adapter das recomendações (horizontal).
        // Recebe lifecycleScope porque dentro do adapter você está a carregar imagens (async).
        val recoAdapter = MovieAdapter(viewLifecycleOwner.lifecycleScope)
        rvReco.adapter = recoAdapter

        // --- RecyclerView dos Resultados (vertical) ---
        val rvResults = view.findViewById<RecyclerView>(R.id.rvResults)

        // Adapter dos resultados (vertical).
        // Também recebe lifecycleScope pelo mesmo motivo (carregar imagens / operações async).
        val resultsAdapter = MovieResultAdapter(viewLifecycleOwner.lifecycleScope)
        rvResults.adapter = resultsAdapter

        // 1) pesquisa inicial vinda da navegação
        val initialQuery = args.query.trim()

        // Meter o texto da pesquisa dentro do TopBar
        topBar.setSearchText(initialQuery)

        if (initialQuery.isNotBlank()) {
            // Dispara a pesquisa inicia
            vm.loadMovies(initialQuery)
        } else {
            // Se a query vier vazia, mostramos “sem resultados” e limpamos as listas.
            progress.isVisible = false
            tvMessage.text = getString(R.string.msg_no_results)
            tvMessage.isVisible = true
            rvResults.isVisible = false
            resultsAdapter.submitList(emptyList())

            recoAdapter.submitList(emptyList())
            currentMovieId = null
        }

        // 2) nova pesquisa feita no próprio SearchPage (sem navegar)
        topBar.setOnSearchListener { text ->
            val q = text.trim()
            if (q.isNotBlank()) {
                // Faz nova pesquisa sem sair do ecrã
                vm.loadMovies(q)
            } else {
                // Pesquisa vazia -> mostrar mensagem e limpar listas
                progress.isVisible = false
                tvMessage.text = getString(R.string.msg_no_results)
                tvMessage.isVisible = true
                rvResults.isVisible = false
                resultsAdapter.submitList(emptyList())

                recoAdapter.submitList(emptyList())
                currentMovieId = null
            }
        }

        // 3) observar resultados + recomendações
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Collect 1: resultados
                launch {
                    vm.movies.collect { result ->
                        when (result) {
                            // Estado “a carregar”
                            is ApiResult.Loading -> {
                                progress.isVisible = true
                                tvMessage.isVisible = false
                                rvResults.isVisible = false
                            }
                            // Estado “sucesso”
                            is ApiResult.Success -> {
                                progress.isVisible = false

                                val list = result.data
                                if (list.isEmpty()) {
                                    // Sucesso mas sem resultados
                                    tvMessage.text = getString(R.string.msg_no_results)
                                    tvMessage.isVisible = true
                                    rvResults.isVisible = false
                                    resultsAdapter.submitList(emptyList())
                                    // Se não há resultados, também não há recomendações
                                    recoAdapter.submitList(emptyList())
                                    currentMovieId = null
                                } else {
                                    // Temos resultados
                                    tvMessage.isVisible = false
                                    rvResults.isVisible = true
                                    resultsAdapter.submitList(list)

                                    // 1) Apanhamos o “primeiro filme” como base para recomendações
                                    val firstMovie = list.firstOrNull()
                                    currentMovieId = firstMovie?.id

                                    // 2) Pegamos no 1º género do 1º filme
                                    //    e pedimos recomendações desse género
                                    val genre = firstMovie?.genres?.firstOrNull()
                                    if (!genre.isNullOrBlank()) {
                                        vm.loadRecommendationsByGenre(genre)
                                    } else {
                                        // Se não há género, não dá para recomendar por género.
                                        recoAdapter.submitList(emptyList())
                                    }
                                }
                            }
                            // Estado “falha” (erro de rede / API)
                            is ApiResult.Failure -> {
                                progress.isVisible = false
                                tvMessage.text = getString(R.string.msg_error_loading)
                                tvMessage.isVisible = true
                                rvResults.isVisible = false
                                resultsAdapter.submitList(emptyList())

                                recoAdapter.submitList(emptyList())
                                currentMovieId = null
                            }

                            null -> Unit
                        }
                    }
                }

                // Collect 2: recomendações
                launch {
                    vm.recommendations.collect { rec ->
                        when (rec) {

                            // Sucesso: preencher a lista horizontal
                            is ApiResult.Success -> {

                                // Remove o “filme principal” das recomendações,
                                // para não recomendar o mesmo filme que o utilizador pesquisou.
                                val filtered = rec.data.filter { it.id != currentMovieId }

                                recoAdapter.submitList(filtered)
                            }

                            // Falha: limpar recomendações
                            is ApiResult.Failure -> {
                                recoAdapter.submitList(emptyList())
                            }

                            // Loading: opcional (poderia mostrar shimmer / progress específico)
                            is ApiResult.Loading -> { /* opcional */
                            }

                            // Estado inicial
                            null -> Unit
                        }
                    }
                }
            }
        }
    }
}
