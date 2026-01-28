package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import bindTopBarNavigation
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentMovieDetailBinding
import com.example.grupo_pdm.ui.adapters.CastAdapter
import com.example.grupo_pdm.ui.adapters.GenreAdapter
import com.example.grupo_pdm.ui.adapters.RatingAdapter
import kotlinx.coroutines.launch
import com.example.grupo_pdm.ui.components.TopBarView
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder

class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {
    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!
    private val args: MovieDetailFragmentArgs by navArgs()

    private val viewModel: MovieDetailViewModel by viewModels()

    private val genreAdapter = GenreAdapter { genre ->
        findNavController().navigate(
            MovieDetailFragmentDirections.actionMovieDetailFragmentToCategoryMovieFragment(genre.name)
        )
    }
    private val castAdapter = CastAdapter { cast ->
        findNavController().navigate(
            MovieDetailFragmentDirections.actionMovieDetailFragmentToPeopleDetailFragment(cast.personId)
        )
    }
    private val ratingAdapter = RatingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMovieDetailBinding.bind(view)
        bindTopBarNavigation(binding.topBar)

        // Setup horizontal layout managers
        binding.rvGenres.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            requireContext(),
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvGenres.adapter = genreAdapter

        binding.rvCast.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            requireContext(),
            androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL,
            false
        )
        binding.rvCast.adapter = castAdapter
        binding.rvComments.adapter = ratingAdapter

        viewModel.loadMovie(args.movieId)

        // Submit review button
        binding.btnSubmitReview.setOnClickListener {
            val score = binding.userRatingBar.rating.toInt()
            if (score == 0) {
                Toast.makeText(requireContext(), getString(R.string.msg_select_rating), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val comment = binding.etComment.text.toString()
            viewModel.submitRating(score, comment)
        }

        // View all reviews button
        binding.btnViewAllReviews.setOnClickListener {
            findNavController().navigate(
                MovieDetailFragmentDirections.actionMovieDetailFragmentToAllReviewsFragment(args.movieId)
            )
        }

        // Favorite button
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite()
        }

        // Observe favorite state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isFavorite.collect { isFavorite ->
                if (isFavorite) {
                    binding.btnFavorite.setImageResource(R.drawable.ic_heart_filled_24)
                } else {
                    binding.btnFavorite.setImageResource(R.drawable.ic_heart_outline_24)
                }
            }
        }

        // Observe movie data
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movie.collect { result ->
                if (result == null) return@collect
                when (result) {
                    is ApiResult.Loading -> { /* show loading */ }
                    is ApiResult.Success -> {
                        val movie = result.data

                        // Movie Poster
                        val pictureId = movie.mainPicture?.id
                        if (pictureId != null) {
                            binding.ivMoviePoster.load("http://10.0.2.2:8080/movies/${movie.id}/pictures/$pictureId") {
                                crossfade(true)
                                placeholder(android.R.drawable.ic_menu_gallery)
                            }
                        } else {
                            binding.ivMoviePoster.setImageResource(android.R.drawable.ic_menu_gallery)
                        }

                        // Title
                        binding.tvTitle.text = movie.title

                        // Synopsis
                        binding.tvOverview.text = movie.synopsis ?: getString(R.string.text_no_synopsis)

                        // Meta: Date • Age
                        val date = movie.releaseDate ?: getString(R.string.text_unknown)
                        val age = movie.minimumAge?.let { "$it+" } ?: getString(R.string.text_all_ages)
                        binding.tvMeta.text = "$date • Age: $age"



                        // Genres
                        movie.genres?.let { genreAdapter.submitList(it) }

                        // Rating from Movie Object directly
                        val ratingVal = movie.rating?.average ?: 0.0
                        binding.ratingBar.rating = ratingVal.toFloat()
                        binding.tvRating.text = if (ratingVal > 0.0) String.format("%.1f", ratingVal) else "N/A"

                        // Director
                        movie.director?.let { director ->
                            binding.tvDirector.text = director.name
                            binding.directorContainer.setOnClickListener {
                                findNavController().navigate(
                                    MovieDetailFragmentDirections.actionMovieDetailFragmentToPeopleDetailFragment(director.personId)
                                )
                            }
                            
                            val dirPicId = director.picture?.id
                            if (dirPicId != null) {
                                binding.ivDirectorPhoto.load("http://10.0.2.2:8080/people/${director.personId}/picture/$dirPicId") {
                                    crossfade(true)
                                    placeholder(R.drawable.ic_user_24)
                                }
                            } else {
                                binding.ivDirectorPhoto.setImageResource(R.drawable.ic_user_24)
                            }
                        }

                        movie.cast?.let { castAdapter.submitList(it) }
                    }
                    is ApiResult.Failure -> {
                        android.util.Log.e("MovieDetailFragment", "Error loading movie: ${result.error.title} - ${result.error.detail} (Status: ${result.error.status})")
                    }
                }
            }
        }
        
        // Observe ratings/comments
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ratings.collect { result ->
                android.util.Log.d("MovieDetailDebug", "Ratings flow emission: $result")
                when (result) {
                    is ApiResult.Success -> {
                        val ratings = result.data
                        android.util.Log.d("MovieDetailDebug", "Ratings loaded: ${ratings.size}")
                        // Toast.makeText(requireContext(), "Ratings: ${ratings.size}", Toast.LENGTH_SHORT).show()
                        
                        if (ratings.isNotEmpty()) {
                            val avg = ratings.map { it.score }.average()
                            android.util.Log.d("MovieDetailDebug", "Calculated Average: $avg")
                            
                            binding.ratingBar.rating = avg.toFloat()
                            binding.tvRating.text = String.format("%.1f", avg)
                        } else {
                            android.util.Log.d("MovieDetailDebug", "Ratings empty, setting 0")
                            binding.ratingBar.rating = 0f
                            binding.tvRating.text = "N/A"
                        }
                        // Hide reviews list as requested
                        binding.rvComments.visibility = View.GONE
                        binding.btnViewAllReviews.visibility = View.GONE
                    }
                    is ApiResult.Failure -> {
                        android.util.Log.e("MovieDetailDebug", "Error loading ratings: ${result.error.detail}")
                        // Toast.makeText(requireContext(), "Ratings Err: ${result.error.detail}", Toast.LENGTH_SHORT).show()
                    }
                    is ApiResult.Loading -> {
                        android.util.Log.d("MovieDetailDebug", "Ratings Loading...")
                    }
                    else -> {}
                }
            }
        }

        // Observe hasReviewed state
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.hasReviewed.collect { hasReviewed ->
                binding.reviewInputContainer.isVisible = !hasReviewed
                binding.tvAlreadyReviewed.isVisible = hasReviewed
            }
        }

        // Observe submit result
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.submitResult.collect { result ->
                when (result) {
                    is ApiResult.Loading -> {
                        binding.btnSubmitReview.isEnabled = false
                        binding.btnSubmitReview.text = getString(R.string.btn_submitting)
                    }
                    is ApiResult.Success -> {
                        binding.btnSubmitReview.isEnabled = true
                        binding.btnSubmitReview.text = getString(R.string.btn_submit_review)
                        Toast.makeText(requireContext(), getString(R.string.msg_review_submitted), Toast.LENGTH_SHORT).show()
                        binding.etComment.setText("")
                        binding.userRatingBar.rating = 0f
                        viewModel.resetSubmitResult()
                    }
                    is ApiResult.Failure -> {
                        binding.btnSubmitReview.isEnabled = true
                        binding.btnSubmitReview.text = getString(R.string.btn_submit_review)
                        android.util.Log.e("MovieDetailFragment", "Error submitting review: ${result.error.detail}")
                        Toast.makeText(requireContext(), getString(R.string.msg_review_failed), Toast.LENGTH_SHORT).show()
                        viewModel.resetSubmitResult()
                    }
                    null -> {}
                }
            }
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
