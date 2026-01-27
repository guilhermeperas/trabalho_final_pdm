package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CommentResponse
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val _movie = MutableStateFlow<ApiResult<MovieResponse>?>(null)
    val movie: StateFlow<ApiResult<MovieResponse>?> = _movie.asStateFlow()

    private val _comments = MutableStateFlow<ApiResult<List<CommentResponse>>?>(null)
    val comments: StateFlow<ApiResult<List<CommentResponse>>?> = _comments.asStateFlow()

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _movie.value = ApiResult.Loading(0)
            MovieServiceClient.getMovieById(movieId).collect { result ->
                _movie.value = result
            }
        }
        // Also load comments
        loadComments(movieId)
    }

    fun loadComments(movieId: Int) {
        viewModelScope.launch {
            MovieServiceClient.getComments(movieId).collect { result ->
                _comments.value = result
            }
        }
    }
}

