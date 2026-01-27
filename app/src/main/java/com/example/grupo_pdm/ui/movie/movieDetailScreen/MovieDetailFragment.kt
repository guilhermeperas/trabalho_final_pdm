package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentMovieDetailBinding
import com.example.grupo_pdm.ui.adapters.CastAdapter
import com.example.grupo_pdm.ui.adapters.GenreAdapter
import com.example.grupo_pdm.ui.adapters.ImageAdapter
import kotlinx.coroutines.launch

class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val args: MovieDetailFragmentArgs by navArgs()

    private val viewModel: MovieDetailViewModel by viewModels()

    private val genreAdapter = GenreAdapter { /* genre click */ }
    private val castAdapter = CastAdapter { cast ->
        // Navigate to person detail when cast is clicked
        findNavController().navigate(
            MovieDetailFragmentDirections.actionMovieDetailFragmentToPeopleDetailFragment(cast.personId)
        )
    }
    private val galleryAdapter = ImageAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMovieDetailBinding.bind(view)

        // Setup adapters
        binding.rvGenres.adapter = genreAdapter
        binding.rvCast.adapter = castAdapter
        binding.rvGallery.adapter = galleryAdapter

        viewModel.loadMovie(args.movieId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movie.collect { result ->
                if (result == null) return@collect
                when (result) {
                    is ApiResult.Loading -> { /* show loading */ }
                    is ApiResult.Success -> {
                        val movie = result.data

                        // Title
                        binding.tvTitle.text = movie.title

                        // Synopsis
                        binding.tvOverview.text = movie.synopsis ?: "No synopsis available."

                        // Meta: Date • Age
                        val date = movie.releaseDate ?: "Unknown"
                        val age = movie.minimumAge?.let { "$it+" } ?: "All ages"
                        binding.tvMeta.text = "$date • Age: $age"

                        // Rating with stars
                        movie.rating?.let { rating ->
                            binding.ratingBar.rating = (rating / 2).toFloat() // Convert 0-10 to 0-5 stars
                            binding.tvRating.text = "$rating/10"
                        } ?: run {
                            binding.ratingBar.rating = 0f
                            binding.tvRating.text = "No rating"
                        }

                        // Genres
                        movie.genres?.let { genreAdapter.submitList(it) }

                        // Director
                        movie.director?.let { director ->
                            binding.tvDirector.text = director.name
                            binding.directorContainer.setOnClickListener {
                                findNavController().navigate(
                                    MovieDetailFragmentDirections.actionMovieDetailFragmentToPeopleDetailFragment(director.personId)
                                )
                            }
                        }

                        // Gallery pictures
                        movie.pictures?.let { galleryAdapter.submitList(it) }

                        // Cast
                        movie.cast?.let { castAdapter.submitList(it) }
                    }
                    is ApiResult.Failure -> {
                        android.widget.Toast.makeText(requireContext(), "Error: ${result.error.detail}", android.widget.Toast.LENGTH_SHORT).show()
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