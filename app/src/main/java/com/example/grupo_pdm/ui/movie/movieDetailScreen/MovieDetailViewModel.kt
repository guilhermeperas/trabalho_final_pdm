package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CreateRatingRequest
import com.example.grupo_pdm.data.RatingResponse
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the Movie Detail screen.
 * Handles loading movie details, managing favorite status, and submitting ratings.
 *
 * Requirements:
 * - Req. 2 (Interface): Provides state for UI.
 * - Req. 4 (Data): Retrieves data from Web Service / Local Prefs.
 * - Req. 5 (Async): Uses Coroutines for network calls.
 *
 * @param app The application context.
 */
class MovieDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val _movie = MutableStateFlow<ApiResult<MovieResponse2>?>(null)
    val movie: StateFlow<ApiResult<MovieResponse2>?> = _movie.asStateFlow()

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

    /**
     * Loads movie details by ID.
     * Also checks local SharedPreferences to see if the user has already rated this movie.
     *
     * @param movieId The ID of the movie to fetch.
     */
    fun loadMovie(movieId: Int) {
        currentMovieId = movieId
        
        val userId = prefs.getInt("userId", -1)
        val hasRatedLocally = prefs.getBoolean("rated_${userId}_${movieId}", false)
        if (hasRatedLocally) {
            _hasReviewed.value = true
        } else {
            _hasReviewed.value = false
        }

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

    /**
     * Toggles the favorite status of the current movie.
     * Updates the UI immediately (optimistic) and reverts if the API call fails.
     */
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
        // error 500
    }

    /**
     * Submits a user rating for the movie.
     * Prevents double submission via local flag.
     *
     * @param score The rating score (1-5).
     * @param comment Optional comment text.
     */
    fun submitRating(score: Int, comment: String?) {
        if (currentMovieId == 0) return
        if (_hasReviewed.value) return // Prevent submission if already reviewed
        
        viewModelScope.launch {
            _submitResult.value = ApiResult.Loading(0)
            val request = CreateRatingRequest(score = score, comment = comment?.takeIf { it.isNotBlank() })
            val result = MovieServiceClient.submitRating(currentMovieId, request)
            _submitResult.value = result
            if (result is ApiResult.Success) {
                // Save local state
                val userId = prefs.getInt("userId", -1)
                prefs.edit().putBoolean("rated_${userId}_${currentMovieId}", true).apply()
                
                _hasReviewed.value = true
                loadMovie(currentMovieId) // Refresh to get new average
            }
        }
    }

    fun resetSubmitResult() {
        _submitResult.value = null
    }
}
