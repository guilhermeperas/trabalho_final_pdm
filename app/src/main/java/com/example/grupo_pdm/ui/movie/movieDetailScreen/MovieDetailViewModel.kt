package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CreateRatingRequest
import com.example.grupo_pdm.data.RatingResponse
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val _movie = MutableStateFlow<ApiResult<MovieResponse>?>(null)
    val movie: StateFlow<ApiResult<MovieResponse>?> = _movie.asStateFlow()

    private val _ratings = MutableStateFlow<ApiResult<List<RatingResponse>>?>(null)
    val ratings: StateFlow<ApiResult<List<RatingResponse>>?> = _ratings.asStateFlow()

    private val _hasReviewed = MutableStateFlow(false)
    val hasReviewed: StateFlow<Boolean> = _hasReviewed.asStateFlow()

    private val _submitResult = MutableStateFlow<ApiResult<Unit>?>(null)
    val submitResult: StateFlow<ApiResult<Unit>?> = _submitResult.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentMovieId: Int = 0
    private val prefs = app.getSharedPreferences("prefs", Context.MODE_PRIVATE)

    fun loadMovie(movieId: Int) {
        currentMovieId = movieId

        viewModelScope.launch {
            _movie.value = ApiResult.Loading(0)
            MovieServiceClient.getMovieById(movieId).collect { result ->
                _movie.value = result
                if (result is ApiResult.Success) {
                    _isFavorite.value = result.data.favorite == true
                }
            }
        }

    }
    fun toggleFavorite() {
        if (currentMovieId == 0) return
        val newValue = !_isFavorite.value
        
        viewModelScope.launch {
            _isFavorite.value = newValue
            
            val result = MovieServiceClient.markAsFavorite(currentMovieId, newValue)
            if (result is ApiResult.Failure) {
                // Revert on failure
                _isFavorite.value = !newValue
                android.util.Log.e("MovieDetailViewModel", "Failed to update favorite: ${result.error.detail}")
            }
        }
    }

    fun loadRatings(movieId: Int) {
        viewModelScope.launch {
            MovieServiceClient.getRatings(movieId).collect { result ->
                _ratings.value = result
                if (result is ApiResult.Success) {
                    val userId = prefs.getInt("userId", -1)
                    val hasReviewedFromApi = result.data.any { it.author == userId }
                    if (hasReviewedFromApi) {
                        _hasReviewed.value = true
                    }
                }
            }
        }
    }

    fun submitRating(score: Int, comment: String?) {
        if (currentMovieId == 0) return
        if (_hasReviewed.value) return // Prevent submission if already reviewed
        
        viewModelScope.launch {
            _submitResult.value = ApiResult.Loading(0)
            val request = CreateRatingRequest(score = score, comment = comment?.takeIf { it.isNotBlank() })
            val result = MovieServiceClient.submitRating(currentMovieId, request)
            _submitResult.value = result
            if (result is ApiResult.Success) {
                _hasReviewed.value = true
                loadRatings(currentMovieId)
            }
        }
    }

    fun resetSubmitResult() {
        _submitResult.value = null
    }
}
