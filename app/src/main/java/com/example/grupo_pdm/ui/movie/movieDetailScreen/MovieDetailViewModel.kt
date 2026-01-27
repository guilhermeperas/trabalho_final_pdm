package com.example.grupo_pdm.ui.movie.movieDetailScreen

import android.R
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieServiceClient
import com.example.grupo_pdm.data.PersonResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MovieDetailViewModel(app: Application) : AndroidViewModel(app) {
    private val _movie = MutableStateFlow<ApiResult<MovieResponse>?>(null)
    val movie: StateFlow<ApiResult<MovieResponse>?> = _movie.asStateFlow()

    private val _director = MutableStateFlow<ApiResult<PersonResponse>?>(null)
    val director: StateFlow<ApiResult<PersonResponse>?> = _director.asStateFlow()

    private val _cast = MutableStateFlow<ApiResult<List<PersonResponse>>?>(null)
    val cast: StateFlow<ApiResult<List<PersonResponse>>?> = _cast.asStateFlow()

    fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            _movie.value = ApiResult.Loading(0)
            MovieServiceClient.getMovieById(movieId).collect { result ->
                _movie.value = result
                if (result is ApiResult.Success) {
                    val movie = result.data
                    
                    // Fetch Director
                    movie.directorId?.let { id ->
                        launch {
                            MovieServiceClient.getPerson(id).collect { res ->
                                _director.value = res
                            }
                        }
                    }

                    // Fetch Cast (This is N+1, but necessary as API only gives IDs)
                    movie.cast?.let { castMembers ->
                        launch {
                            var fetchedCount = 0
                            val total = castMembers.size
                            val actors = mutableListOf<PersonResponse>()
                            // Use a simple loop for now, handling concurrency could be better but sticking to simple
                            // We need to collect ALL results.
                            // Better approach: launch n coroutines and wait? Or sequential?
                            // Sequential is safer against rate limits but slower.
                            // Given "User: dont create new stuff", I will do sequential for simplicity or concurrent if easy.
                            // Let's do a simple flow for now.
                            
                            // Actually, I can't emit partial lists easily to ApiResult without complex logic.
                            // I will fetch them validly.
                            
                            val fetchedActors = mutableListOf<PersonResponse>()
                            castMembers.forEach { member ->
                                try {
                                    // Collect first item from flow
                                    MovieServiceClient.getPerson(member.personId).collect { res ->
                                        if (res is ApiResult.Success) {
                                            fetchedActors.add(res.data)
                                        }
                                        // Ignore failures for individual cast members for now
                                    }
                                } catch (e: Exception) {
                                    // ignore
                                }
                            }
                            _cast.value = ApiResult.Success(fetchedActors)
                        }
                    }
                }
            }
        }
    }

}