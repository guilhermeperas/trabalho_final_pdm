package com.example.grupo_pdm.ui.movie.allReviews

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.grupo_pdm.R
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.databinding.FragmentAllReviewsBinding
import com.example.grupo_pdm.ui.adapters.RatingAdapter
import kotlinx.coroutines.launch

class AllReviewsFragment : Fragment(R.layout.fragment_all_reviews) {
    private var _binding: FragmentAllReviewsBinding? = null
    private val binding get() = _binding!!
    
    private val args: AllReviewsFragmentArgs by navArgs()
    private val viewModel: AllReviewsViewModel by viewModels()
    private val ratingAdapter = RatingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllReviewsBinding.bind(view)

        binding.rvReviews.adapter = ratingAdapter
        
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        viewModel.loadRatings(args.movieId)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ratings.collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        val reviews = result.data
                        ratingAdapter.submitList(reviews)
                        binding.tvEmptyState.isVisible = reviews.isEmpty()
                        binding.rvReviews.isVisible = reviews.isNotEmpty()
                    }
                    is ApiResult.Failure -> {
                        android.util.Log.e("AllReviewsFragment", "Error loading reviews: ${result.error.detail}")
                        binding.tvEmptyState.isVisible = true
                        binding.tvEmptyState.text = "Failed to load reviews"
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
