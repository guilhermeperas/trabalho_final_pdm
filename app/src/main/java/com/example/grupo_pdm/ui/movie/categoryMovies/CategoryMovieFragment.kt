package com.example.grupo_pdm.ui.movie.categoryMovies

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import bindTopBarNavigation
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentCategoryMovieScreenBinding
import com.example.grupo_pdm.ui.adapters.MovieAdapter
import kotlinx.coroutines.launch

class CategoryMovieFragment : Fragment(R.layout.fragment_category_movie_screen) {
    
    private val args: CategoryMovieFragmentArgs by navArgs()
    private var _binding: FragmentCategoryMovieScreenBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: CategoryMovieViewModel by viewModels()

    private val adapter = MovieAdapter { movie ->
        findNavController().navigate(
            CategoryMovieFragmentDirections.actionCategoryMovieFragmentToMovieDetailFragment(movie.id)
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCategoryMovieScreenBinding.bind(view)
        bindTopBarNavigation(binding.topBar)


        binding.rvMovies.adapter = adapter
        binding.tvCategoryTitle.text = args.categoryName
        
        viewModel.loadMovies(args.categoryName)
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movies.collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is ApiResult.Success -> {
                        binding.progressBar.visibility = View.GONE
                        if (result.data.isEmpty()) {
                            binding.rvMovies.visibility = View.GONE
                            binding.tvEmptyState.visibility = View.VISIBLE
                        } else {
                            binding.rvMovies.visibility = View.VISIBLE
                            binding.tvEmptyState.visibility = View.GONE
                            adapter.submitList(result.data)
                        }
                    }
                    is ApiResult.Failure -> {
                        binding.progressBar.visibility = View.GONE
                        Log.e("erro", "Error: ${result.error.detail}")
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
