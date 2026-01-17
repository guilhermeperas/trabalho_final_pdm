package com.example.grupo_pdm.data

/**
 * Data class for Movie.
 */
data class Movie(
    val id: Int? = null,
    val title: String,
    val synopsis: String? = null,
    val genres: List<Int>? = null, // id genre
    val releaseDate: String? = null, // Format: "YYYY-MM-DD"
    val directorId: Int? = null,
    val cast: List<CastMember>? = null,
    val minimumAge: Int? = null,
    val pictures: List<Picture>? = null,
    val rating: Double? = null
)
