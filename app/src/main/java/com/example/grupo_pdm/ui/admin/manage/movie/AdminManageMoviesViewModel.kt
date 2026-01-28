package com.example.grupo_pdm.ui.admin.manage.movie
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminManageMoviesViewModel(app: Application) : AndroidViewModel(app) {

    private val _movies =
        MutableStateFlow<ApiResult<List<MovieEntity>>?>(null)
    val movies: StateFlow<ApiResult<List<MovieEntity>>?> =
        _movies.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            // TODO: integrar API quando endpoint de filmes estiver disponível
            }
        }
    }