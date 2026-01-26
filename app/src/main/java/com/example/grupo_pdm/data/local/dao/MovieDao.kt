package com.example.grupo_pdm.data.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.grupo_pdm.data.local.entity.MovieCast
import com.example.grupo_pdm.data.local.entity.MovieEntity
import com.example.grupo_pdm.data.local.entity.MovieFull
import com.example.grupo_pdm.data.local.entity.MovieGenre
import com.example.grupo_pdm.data.local.entity.MoviePicture
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    // Insere/atualiza um filme.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovie(movie: MovieEntity)

    // Insere/atualiza as ligações filme <-> género.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMovieGenres(refs: List<MovieGenre>)

    // Insere/atualiza o elenco (movieId + personId é a chave).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCast(cast: List<MovieCast>)

    // Insere/atualiza as pictures do filme.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPictures(pictures: List<MoviePicture>)

    // Apaga todos os géneros associados a 1 filme
    @Query("DELETE FROM movie_genres WHERE movieId = :movieId")
    suspend fun clearMovieGenres(movieId: Int)

    @Query("DELETE FROM movie_cast WHERE movieId = :movieId")
    suspend fun clearMovieCast(movieId: Int)

    @Query("DELETE FROM movie_pictures WHERE movieId = :movieId")
    suspend fun clearMoviePictures(movieId: Int)

    // Guarda um filme completo numa transação:
    // 1) guarda o filme
    // 2) limpa relações antigas (genres/cast/pictures)
    // 3) insere relações novas
    @Transaction
    suspend fun saveMovieFull(
        movie: MovieEntity,
        genreIds: List<Int>,
        cast: List<MovieCast>,
        pictures: List<MoviePicture>
    ) {
        upsertMovie(movie)

        clearMovieGenres(movie.id)
        clearMovieCast(movie.id)
        clearMoviePictures(movie.id)

        upsertMovieGenres(genreIds.map { gid -> MovieGenre(movieId = movie.id, genreId = gid) })
        upsertCast(cast)
        upsertPictures(pictures)
    }
    // Pesquisa filmes por título (para a lista de resultados).
    @Query("SELECT * FROM movies WHERE title LIKE '%' || :q || '%' ORDER BY title")
    fun searchByTitle(q: String): Flow<List<MovieEntity>>

    // Observa um filme “completo” (filme + géneros + cast + pictures).
    @Transaction
    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun observeMovieFull(id: Int): Flow<MovieFull?>

    // Busca 1 filme (sem relações).
    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieById(id: Int): MovieEntity?
}