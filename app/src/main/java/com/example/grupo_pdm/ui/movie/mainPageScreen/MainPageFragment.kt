package com.example.grupo_pdm.ui.movie.mainPageScreen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentMainPageBinding
import com.example.grupo_pdm.ui.adapters.ActorHomeAdapter
import com.example.grupo_pdm.ui.adapters.CategoryAdapter

import com.example.grupo_pdm.ui.adapters.MovieAdapter
import kotlinx.coroutines.launch

class MainPage : Fragment(R.layout.fragment_main_page) {
    private var _binding: FragmentMainPageBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MainPageViewModel by viewModels()

    private val categoriesAdapter = CategoryAdapter { category ->
        findNavController().navigate(
            MainPageDirections.actionMainPageToCategoryMovieFragment(category.name)
        )
    }
    private val newMovieAdapter = MovieAdapter { movie ->
        findNavController().navigate(
            MainPageDirections.actionMainPageToMovieDetailFragment(movie.id)
        )
    }
    private val trendingMovieAdapter = MovieAdapter { movie ->
        findNavController().navigate(
            MainPageDirections.actionMainPageToMovieDetailFragment(movie.id)
        )

    }
    private val actorAdapter = ActorHomeAdapter { person ->
        findNavController().navigate(
            MainPageDirections.actionMainPageToPeopleDetailFragment(person.id)
        )

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMainPageBinding.bind(view)

        setupRecyclerViews()
        observeData()
    }
    
    private fun setupRecyclerViews() {
        binding.rvCategories.adapter = categoriesAdapter
        binding.rvNewMovies.adapter = newMovieAdapter
        binding.rvTrendingMovies.adapter = trendingMovieAdapter
        binding.rvActors.adapter = actorAdapter
    }
    
    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.actors.collect { result ->
                 when (result) {
                    is ApiResult.Success -> actorAdapter.submitList(result.data)
                    is ApiResult.Failure -> Toast.makeText(requireContext(), "Failed to load actors: ${result.error.detail}", Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { result ->
                when (result) {
                    is ApiResult.Success -> categoriesAdapter.submitList(result.data)
                    is ApiResult.Failure -> Toast.makeText(requireContext(), "Failed to load categories: ${result.error.detail}", Toast.LENGTH_SHORT).show()
                    else -> {}
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
