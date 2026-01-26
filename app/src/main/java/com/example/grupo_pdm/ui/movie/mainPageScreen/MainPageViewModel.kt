@file:OptIn(ExperimentalUuidApi::class)

package com.example.grupo_pdm.ui.movie.mainPageScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlin.uuid.ExperimentalUuidApi



import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CategoryResponse
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.PersonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainPageViewModel(app: Application) : AndroidViewModel(app) {

    private val _categories = MutableStateFlow<ApiResult<List<CategoryResponse>>?>(null)
    val categories: StateFlow<ApiResult<List<CategoryResponse>>?> = _categories.asStateFlow()

    private val _newMovies = MutableStateFlow<ApiResult<List<MovieResponse>>?>(null)
    val newMovies: StateFlow<ApiResult<List<MovieResponse>>?> = _newMovies.asStateFlow()

    private val _trendingMovies = MutableStateFlow<ApiResult<List<MovieResponse>>?>(null)
    val trendingMovies: StateFlow<ApiResult<List<MovieResponse>>?> = _trendingMovies.asStateFlow()

    private val _actors = MutableStateFlow<ApiResult<List<PersonResponse>>?>(null)
    val actors: StateFlow<ApiResult<List<PersonResponse>>?> = _actors.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            launch {
                //MovieServiceClient.getCategories().collect { _categories.value = it }
            }
            launch {
                //MovieServiceClient.getNewMovies().collect { _newMovies.value = it }
            }
            launch {
                //MovieServiceClient.getTrendingMovies().collect { _trendingMovies.value = it }
            }
            launch {
                MovieServiceClient.getActors().collect { _actors.value = it }
            }
        }
    }
}
