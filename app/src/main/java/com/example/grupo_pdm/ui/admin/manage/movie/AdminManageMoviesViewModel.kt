package com.example.grupo_pdm.ui.admin.manage.movie
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CreateMovieRequest
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminManageMoviesViewModel(app: Application) : AndroidViewModel(app) {

    private val _movies =
        MutableStateFlow<ApiResult<List<MovieResponse2>>?>(null)
    val movies = _movies.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            MovieServiceClient
                .getAllMovies()
                .collect {
                    _movies.value = it
                }
        }
    }

    fun createMovie(request: CreateMovieRequest) {
        viewModelScope.launch {
            MovieServiceClient
                .createMovie(request)
                .collect {
                    loadMovies()
                }
        }
    }
}
