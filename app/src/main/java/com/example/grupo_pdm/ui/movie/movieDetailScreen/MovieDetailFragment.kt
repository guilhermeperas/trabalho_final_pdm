package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentCategoryMovieScreenBinding
import com.example.grupo_pdm.databinding.FragmentMainPageBinding
import com.example.grupo_pdm.databinding.FragmentMovieDetailBinding
import com.example.grupo_pdm.ui.movie.categoryMovies.CategoryMovieFragmentArgs
import com.example.grupo_pdm.ui.movie.mainPageScreen.MainPageViewModel
import kotlinx.coroutines.launch
import kotlin.getValue

class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val args: MovieDetailFragmentArgs by navArgs()

    private val viewModel: MovieDetailViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMovieDetailBinding.bind(view)

        viewModel.loadMovie(args.movieId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movie.collect { result ->
                if (result == null) return@collect
                when (result) {
                    is ApiResult.Loading -> {
                        // show loading
                    }
                    is ApiResult.Success -> {
                        val movie = result.data
                        binding.tvTitle.text = movie.title
                        binding.tvOverview.text = movie.synopsis ?: "No synopsis available."
                        
                        val date = movie.releaseDate ?: "Unknown Date"
                        val rating = movie.rating?.toString() ?: "N/A"
                        val genres = movie.genres?.joinToString(", ") ?: "Unknown Genre"
                        
                        binding.tvMeta.text = "$date • Rating: $rating • $genres"

                        // TODO: Handle Cast (needs fetching per personId or expanded API)
                        // TODO: Handle Poster (pictures list)
                    }
                    is ApiResult.Failure -> {
                        android.widget.Toast.makeText(requireContext(), "Error: ${result.error.detail}", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}