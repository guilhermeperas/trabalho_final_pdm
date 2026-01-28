package com.example.grupo_pdm.ui.movie.searchPageScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.grupo_pdm.data.ApiResult
import com.example.grupo_pdm.data.GenreResponse
import com.example.grupo_pdm.data.MovieResponse
import com.example.grupo_pdm.data.MovieResponse2
import com.example.grupo_pdm.data.MovieServiceClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchPageViewModel(app: Application) : AndroidViewModel(app) {

    // ============================================================
    // RESULTADOS da pesquisa (lista vertical)
    // ============================================================

    // State interno (mutável) usado pelo ViewModel para atualizar o estado.
    // Guarda: Loading / Success(data) / Failure(error) ou null (estado inicial).
    private val _movies = MutableStateFlow<ApiResult<List<MovieResponse2>>?>(null)

    // State exposto para o Fragment (somente leitura).
    // O Fragment faz collect deste flow para atualizar a UI.
    val movies: StateFlow<ApiResult<List<MovieResponse2>>?> = _movies.asStateFlow()

    /**
     * Faz pesquisa de filmes por título (partial match, conforme API).
     * - Lança uma coroutine no viewModelScope (vive enquanto o ViewModel existir).
     * - Coleta o Flow vindo do MovieServiceClient e vai colocando o resultado no _movies.
     */
    fun loadMovies(moviesName: String) {
        viewModelScope.launch {
            MovieServiceClient.getMoviesByTitle(moviesName).collect { result ->
                _movies.value = result
            }
        }
    }


    // ============================================================
    // RECOMENDAÇÕES (lista horizontal)
    // ============================================================

    // State interno das recomendações.
    private val _recommendations = MutableStateFlow<ApiResult<List<MovieResponse2>>?>(null)

    // State exposto para o Fragment (somente leitura).
    val recommendations: StateFlow<ApiResult<List<MovieResponse2>>?> = _recommendations.asStateFlow()

    /**
     * Carrega recomendações com base num género.
     * Ideia: depois de obter resultados, pega no 1º género do 1º filme
     * e busca ~10 filmes desse género para mostrar na lista horizontal.
     */
    fun loadRecommendationsByGenre(genre: GenreResponse) {
        viewModelScope.launch {
            MovieServiceClient.getMoviesByGenre(genre.name , count = 10).collect { result ->
                _recommendations.value = result
            }
        }
    }
}
