@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@file:UseContextualSerialization(Instant::class)
package com.example.grupo_pdm.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi

// CASTMEMBER
@Serializable
data class CastMemberResponse(
    val personId: Int,
    val character: String
)

@Serializable
data class CreateCastMemberRequest(
    val personId: Int,
    val character: String
)

// CATEGORY
@Serializable
data class CategoryResponse(
    val id: Int,
    val name: String
)

@Serializable
data class CreateCategoryRequest(
    val name: String
)

// GENRE
@Serializable
data class GenreResponse(
    val id: Int,
    val name: String,
    val description: String? = null
)

@Serializable
data class CreateGenreRequest(
    val name: String,
    val description: String? = null
)

// MOVIE
@Serializable
data class MovieResponse(
    val id: Int,
    val title: String,
    val synopsis: String? = null,
    val genres: List<Int>? = null, // id genre
    val releaseDate: String? = null, // Format: "YYYY-MM-DD"
    val directorId: Int? = null,
    val cast: List<CastMemberResponse>? = null,
    val minimumAge: Int? = null,
    val pictures: List<PictureResponse>? = null,
    val rating: Double? = null
)

@Serializable
data class CreateMovieRequest(
    val title: String,
    val synopsis: String? = null,
    val genres: List<Int>? = null,
    val releaseDate: String? = null,
    val directorId: Int? = null,
    val cast: List<CreateCastMemberRequest>? = null,
    val minimumAge: Int? = null,
    val pictures: List<CreatePictureRequest>? = null
)

// PERSON
@Serializable
data class PersonResponse(
    val id: Int,
    val name: String,
    val dateOfBirth: String? = null, // Format: "YYYY-MM-DD"
    val pictures: List<PictureResponse>? = null
)

@Serializable
data class CreatePersonRequest(
    val name: String,
    val dateOfBirth: String? = null,
    val pictures: List<CreatePictureRequest>? = null
)

// PICTURE
@Serializable
data class PictureResponse(
    val id: Int,
    val filename: String,
    val data: String,
)

@Serializable
data class CreatePictureRequest(
    val filename: String,
    val data: String
)



@Serializable
data class RegisterUserRequest(
    val username: String,
    val password: String,
    val dateOfBirth: String? = null,
    val picture: CreatePictureRequest? = null
)

@Serializable
data class LoginResponse(

    val id: Int,
    val username: String,
    val role: String,
    val description: String? = null
)
@Serializable
data class ProblemDetails(
    val type: String,
    val title: String,
    val status: Int,
    val detail: String,
    val instance: String? = null
)
class InstantSerializer : KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: Instant
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): Instant {
        val dateTime8601 = decoder.decodeString()
        return Instant.parse(dateTime8601)
    }
}
