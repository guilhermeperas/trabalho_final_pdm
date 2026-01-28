@file:OptIn(ExperimentalUuidApi::class)

package com.example.grupo_pdm.ui.movie.mainPageScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlin.uuid.ExperimentalUuidApi


import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.CategoryResponse
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.PersonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainPageViewModel(app: Application) : AndroidViewModel(app) {

    private val _categories = MutableStateFlow<ApiResult<List<CategoryResponse>>?>(null)
    val categories: StateFlow<ApiResult<List<CategoryResponse>>?> = _categories.asStateFlow()

    private val _newMovies = MutableStateFlow<ApiResult<List<MovieResponse2>>?>(null)
    val newMovies: StateFlow<ApiResult<List<MovieResponse2>>?> = _newMovies.asStateFlow()

    private val _trendingMovies = MutableStateFlow<ApiResult<List<MovieResponse2>>?>(null)
    val trendingMovies: StateFlow<ApiResult<List<MovieResponse2>>?> = _trendingMovies.asStateFlow()

    private val _actors = MutableStateFlow<ApiResult<List<PersonResponse>>?>(null)
    val actors: StateFlow<ApiResult<List<PersonResponse>>?> = _actors.asStateFlow()

    private val _randomMovie = MutableStateFlow<MovieResponse2?>(null)
    val randomMovie: StateFlow<MovieResponse2?> = _randomMovie.asStateFlow()

    private val _favoriteIds = MutableStateFlow<Set<Int>>(emptySet())
    val favoriteIds: StateFlow<Set<Int>> = _favoriteIds.asStateFlow()

    init {
        loadData()
        loadFavorites()
    }

    fun loadData() {
        viewModelScope.launch {
            launch {
                MovieServiceClient.getCategories().collect { _categories.value = it }
            }
            launch {
                MovieServiceClient.getMoviesSortedBy("releaseDate").collect { result ->
                    if (result is ApiResult.Success) {
                        _randomMovie.value = result.data.randomOrNull()
                        _newMovies.value = result
                        updateFavoriteIdsFromMovies(result.data)
                    }
                }
            }
            launch {
                MovieServiceClient.getMoviesSortedBy("rating").collect { result ->
                    _trendingMovies.value = result
                    if (result is ApiResult.Success) {
                        updateFavoriteIdsFromMovies(result.data)
                    }
                }
            }
            launch {
                MovieServiceClient.getActors().collect { _actors.value = it }
            }
        }
    }

    private fun updateFavoriteIdsFromMovies(movies: List<MovieResponse2>) {
        val currentFavorites = _favoriteIds.value.toMutableSet()
        for (movie in movies) {
            if (movie.favorite) {
                currentFavorites.add(movie.id)
            } else {
                currentFavorites.remove(movie.id)
            }
        }
        _favoriteIds.value = currentFavorites
    }

    fun loadFavorites() {
        // No-op or reload movies to get fresh favorite status if needed
        // For now, relies on loadData() to populate initial state
    }

    fun toggleFavorite(movieId: Int) {
        val currentlyFavorite = _favoriteIds.value.contains(movieId)
        val newValue = !currentlyFavorite
        
        viewModelScope.launch {
            // Optimistically update UI
            val updatedFavorites = _favoriteIds.value.toMutableSet()
            if (newValue) {
                updatedFavorites.add(movieId)
            } else {
                updatedFavorites.remove(movieId)
            }
            _favoriteIds.value = updatedFavorites
            
            // Call API
            val result = MovieServiceClient.markAsFavorite(movieId, newValue)
            if (result is ApiResult.Failure) {
                // Revert on failure
                val revertedFavorites = _favoriteIds.value.toMutableSet()
                if (newValue) {
                    revertedFavorites.remove(movieId)
                } else {
                    revertedFavorites.add(movieId)
                }
                _favoriteIds.value = revertedFavorites
                android.util.Log.e("MainPageViewModel", "Failed to update favorite: ${result.error.detail}")
            }
        }
    }
}
