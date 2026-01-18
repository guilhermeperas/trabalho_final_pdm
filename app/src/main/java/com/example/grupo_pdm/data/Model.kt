@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@file:UseContextualSerialization(Instant::class)
package com.example.grupo_pdm.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

@Serializable
data class CastMember(
    val personId: Int,
    val character: String
)

@Serializable
data class Category(
    val id: Int,
    val name: String
)
@Serializable
data class Genre(
    val id: Int? = null,
    val name: String,
    val description: String? = null
)
@Serializable
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
@Serializable
data class Person(
    val id: Int? = null,
    val name: String,
    val dateOfBirth: String? = null, // Format: "YYYY-MM-DD"
    val pictures: List<Picture>? = null
)
@Serializable
data class Picture(
    val id: Int? = null,
    val filename: String,
    val mainPicture: Boolean = false,
    val description: String? = null,
    val data: String? = null // Base64 encoded image data
)
