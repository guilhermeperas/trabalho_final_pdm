package com.example.grupo_pdm.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val synopsis: String,
    val releaseDate: String,
    val directorId: Int,
    val minimumAge: Int = 0,
    val favorite: Boolean = false,
    val rating: Double? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

@Entity( tableName = "movie_genres",
    primaryKeys = ["movieId", "genreId"]
)
data class MovieGenre(
    val movieId: Int,
    val genreId: Int
)
@Entity(
    tableName = "movie_cast",
    primaryKeys = ["movieId", "personId"]
)
data class MovieCast(
    val movieId: Int,
    val personId: Int,
    val character: String
)

@Entity(tableName = "movie_pictures")
data class MoviePicture(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val movieId: Int,

    val filename: String,
    val isMain: Boolean,
    val description: String? = null,

    val dataBase64: String? = null
)

data class MovieFull(
    @Embedded val movie: MovieEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieGenre::class,
            parentColumn = "movieId",
            entityColumn = "genreId"
        )
    )
    val genres: List<GenreEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "movieId"
    )
    val cast: List<MovieCast>,

    @Relation(
        parentColumn = "id",
        entityColumn = "movieId"
    )
    val pictures: List<MoviePicture>
)