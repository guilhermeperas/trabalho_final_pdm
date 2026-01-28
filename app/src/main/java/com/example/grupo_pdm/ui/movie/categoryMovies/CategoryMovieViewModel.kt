package com.example.grupo_pdm.ui.movie.categoryMovies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CategoryMovieViewModel(app: Application) : AndroidViewModel(app) {

    private val _movies = MutableStateFlow<ApiResult<List<MovieResponse2>>?>(null)
    val movies: StateFlow<ApiResult<List<MovieResponse2>>?> = _movies.asStateFlow()

    fun loadMovies(categoryName: String) {
        viewModelScope.launch {
            _movies.value = ApiResult.Loading(0)
            MovieServiceClient.getMoviesByCategory(categoryName).collect { result ->
                _movies.value = result
            }
        }
    }
}