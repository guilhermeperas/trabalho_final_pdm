package com.example.grupo_pdm.ui.movie.mainPageScreen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import bindTopBarNavigation
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.databinding.FragmentMainPageBinding
import com.example.grupo_pdm.ui.adapters.ActorHomeAdapter
import com.example.grupo_pdm.ui.adapters.CategoryAdapter
import com.example.grupo_pdm.ui.adapters.MovieAdapter
import com.example.grupo_pdm.ui.components.TopBarView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import kotlinx.coroutines.launch

class MainPage : Fragment(R.layout.fragment_main_page) {
    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainPageViewModel by viewModels()
    
    private var currentRandomMovie: MovieResponse2? = null

    private val categoriesAdapter = CategoryAdapter { category ->
        findNavController().navigate(
            MainPageDirections.actionMainPageToCategoryMovieFragment(category.name)
        )
    }
    
    private val newMovieAdapter = MovieAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                MainPageDirections.actionMainPageToMovieDetailFragment(movie.id)
            )
        },
        onFavoriteClick = { movie ->
            viewModel.toggleFavorite(movie.id)
        }
    )
    
    private val trendingMovieAdapter = MovieAdapter(
        onMovieClick = { movie ->
            findNavController().navigate(
                MainPageDirections.actionMainPageToMovieDetailFragment(movie.id)
            )
        },
        onFavoriteClick = { movie ->
            viewModel.toggleFavorite(movie.id)
        }
    )
    
    private val actorAdapter = ActorHomeAdapter { person ->
        findNavController().navigate(
            MainPageDirections.actionMainPageToPeopleDetailFragment(person.id)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainPageBinding.bind(view)
        val topBar = view.findViewById<TopBarView>(R.id.topBar)
        bindTopBarNavigation(topBar)

        setupRecyclerViews()
        setupRandomMovieClick()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh favorites when returning from detail screen
        viewModel.loadFavorites()
    }
    
    private fun setupRecyclerViews() {
        binding.rvCategories.adapter = categoriesAdapter
        binding.rvNewMovies.adapter = newMovieAdapter
        binding.rvTrendingMovies.adapter = trendingMovieAdapter
        binding.rvActors.adapter = actorAdapter
    }
    
    private fun setupRandomMovieClick() {
        binding.cardRandomMovie.setOnClickListener {
            currentRandomMovie?.let { movie ->
                findNavController().navigate(
                    MainPageDirections.actionMainPageToMovieDetailFragment(movie.id)
                )
            }
        }
    }
    
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actors.collect { result ->
                 when (result) {
                    is ApiResult.Success -> actorAdapter.submitList(result.data)
                    is ApiResult.Failure -> {
                        android.util.Log.e("MainPageFragment", "Failed to load actors: ${result.error}")
                    }
                    else -> {}
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { result ->
                when (result) {
                    is ApiResult.Success -> categoriesAdapter.submitList(result.data)
                    is ApiResult.Failure -> {
                        android.util.Log.e("MainPageFragment", "Failed to load categories: ${result.error}")
                    }
                    else -> {}
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newMovies.collect { result ->
                when (result) {
                    is ApiResult.Success -> newMovieAdapter.submitList(result.data)
                    is ApiResult.Failure -> {
                        android.util.Log.e("MainPageFragment", "Failed to load new movies: ${result.error}")
                    }
                    else -> {}
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.trendingMovies.collect { result ->
                when (result) {
                    is ApiResult.Success -> trendingMovieAdapter.submitList(result.data)
                    is ApiResult.Failure -> {
                        android.util.Log.e("MainPageFragment", "Failed to load trending movies: ${result.error}")
                    }
                    else -> {}
                }
            }
        }
        // Observe random movie
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.randomMovie.collect { movie ->
                if (movie != null) {
                    currentRandomMovie = movie
                    val pictureId = movie.mainPicture?.id
                    if (pictureId != null) {
                        binding.ivRandomMovie.load("http://10.0.2.2:8080/movies/${movie.id}/pictures/$pictureId") {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_gallery)
                        }
                    } else {
                        binding.ivRandomMovie.setImageResource(android.R.drawable.ic_menu_gallery)
                    }
                }
            }
        }
        // Observe favorite IDs and update adapters
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.favoriteIds.collect { favoriteIds ->
                newMovieAdapter.updateFavorites(favoriteIds)
                trendingMovieAdapter.updateFavorites(favoriteIds)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
