package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
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

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _movie.value = ApiResult.Loading(0)
            MovieServiceClient.getMovieById(movieId).collect { result ->
                _movie.value = result
            }
        }
        loadRatings(movieId)
    }

    fun loadRatings(movieId: Int) {
        viewModelScope.launch {
            MovieServiceClient.getRatings(movieId).collect { result ->
                _ratings.value = result
            }
        }
    }
}


