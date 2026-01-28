package com.example.grupo_pdm.ui.movie.allReviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.RatingResponse
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AllReviewsViewModel : ViewModel() {
    private val _ratings = MutableStateFlow<ApiResult<List<RatingResponse>>?>(null)
    val ratings: StateFlow<ApiResult<List<RatingResponse>>?> = _ratings.asStateFlow()

    fun loadRatings(movieId: Int) {
        viewModelScope.launch {
            MovieServiceClient.getRatings(movieId).collect { result ->
                _ratings.value = result
            }
        }
    }
}
