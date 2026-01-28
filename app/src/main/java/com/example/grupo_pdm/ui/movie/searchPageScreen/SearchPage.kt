package com.example.grupo_pdm.ui.movie.searchPageScreen

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import bindTopBarNavigation
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentSearchPageBinding
import com.example.grupo_pdm.ui.adapters.MovieAdapter
import com.example.grupo_pdm.ui.adapters.MovieResultAdapter
import kotlinx.coroutines.launch

class SearchPage : Fragment(R.layout.fragment_search_page) {
    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    // Argumentos vindos do Navigation (Safe Args).
    private val args: SearchPageArgs by navArgs()

    // ViewModel do ecra. Guarda o estado e faz chamadas a API.
    private val vm: SearchPageViewModel by viewModels()

    // Guarda o ID do filme principal para remover das recomendacoes.
    private var currentMovieId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchPageBinding.bind(view)

        // Views de estado
        val progress = binding.progress
        val tvMessage = binding.tvMessage

        // TopBar (Home / Search / User)
        val topBar = binding.topBar
        bindTopBarNavigation(topBar)

        // RecyclerView das Recomendacoes (horizontal)
        val recoAdapter = MovieAdapter(
            onMovieClick = { movie ->
                findNavController().navigate(
                    SearchPageDirections.actionSearchPageToMovieDetailFragment(movie.id)
                )
            }
        )
        binding.rvRecommendations.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvRecommendations.adapter = recoAdapter

        // RecyclerView dos Resultados (vertical)
        val resultsAdapter = MovieResultAdapter(
            onMovieClick = { movie ->
                findNavController().navigate(
                    SearchPageDirections.actionSearchPageToMovieDetailFragment(movie.id)
                )
            }
        )
        binding.rvResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResults.adapter = resultsAdapter

        // 1) Pesquisa inicial vinda da navegacao
        val initialQuery = args.query.trim()
        topBar.setSearchText(initialQuery)

        if (initialQuery.isNotBlank()) {
            vm.loadMovies(initialQuery)
        } else {
            progress.isVisible = false
            tvMessage.text = getString(R.string.msg_no_results)
            tvMessage.isVisible = true
            binding.rvResults.isVisible = false
            resultsAdapter.submitList(emptyList())
            recoAdapter.submitList(emptyList())
            currentMovieId = null
        }

        // 2) Nova pesquisa feita no proprio SearchPage
        topBar.setOnSearchListener { text ->
            val q = text.trim()
            if (q.isNotBlank()) {
                vm.loadMovies(q)
            } else {
                progress.isVisible = false
                tvMessage.text = getString(R.string.msg_no_results)
                tvMessage.isVisible = true
                binding.rvResults.isVisible = false
                resultsAdapter.submitList(emptyList())
                recoAdapter.submitList(emptyList())
                currentMovieId = null
            }
        }

        // 3) Observar flows do ViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // RESULTADOS
                launch {
                    vm.movies.collect { result ->
                        when (result) {
                            is ApiResult.Loading -> {
                                progress.isVisible = true
                                tvMessage.isVisible = false
                                binding.rvResults.isVisible = false
                            }
                            is ApiResult.Success -> {
                                progress.isVisible = false
                                val list = result.data
                                if (list.isEmpty()) {
                                    tvMessage.text = getString(R.string.msg_no_results)
                                    tvMessage.isVisible = true
                                    binding.rvResults.isVisible = false
                                    resultsAdapter.submitList(emptyList())
                                    recoAdapter.submitList(emptyList())
                                    currentMovieId = null
                                } else {
                                    tvMessage.isVisible = false
                                    binding.rvResults.isVisible = true
                                    resultsAdapter.submitList(list)
                                    val firstMovie = list.firstOrNull()
                                    currentMovieId = firstMovie?.id
                                    val genre = firstMovie?.genres?.firstOrNull()
                                    if (genre != null) {
                                        vm.loadRecommendationsByGenre(genre)
                                    } else {
                                        recoAdapter.submitList(emptyList())
                                    }
                                }
                            }
                            is ApiResult.Failure -> {
                                progress.isVisible = false
                                tvMessage.text = getString(R.string.msg_error_loading)
                                tvMessage.isVisible = true
                                binding.rvResults.isVisible = false
                                resultsAdapter.submitList(emptyList())
                                recoAdapter.submitList(emptyList())
                                currentMovieId = null
                            }
                            null -> Unit
                        }
                    }
                }

                // RECOMENDACOES
                launch {
                    vm.recommendations.collect { rec ->
                        when (rec) {
                            is ApiResult.Success -> {
                                val filtered = rec.data.filter { it.id != currentMovieId }
                                recoAdapter.submitList(filtered)
                            }
                            is ApiResult.Failure -> {
                                recoAdapter.submitList(emptyList())
                            }
                            is ApiResult.Loading -> Unit
                            null -> Unit
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
