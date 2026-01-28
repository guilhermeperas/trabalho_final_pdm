@file:OptIn(ExperimentalUuidApi::class, ExperimentalTime::class)
@file:UseContextualSerialization(Instant::class)
package com.example.grupo_pdm.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
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
    val name: String? = null,
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

// DIRECTOR (embedded in Movie)
@Serializable
data class DirectorResponse(
    val personId: Int,
    val name: String,
    val picture: PictureResponse? = null
)

/**
 * Custom serializer for genres that handles both:
 * - List of strings: ["Horror", "Drama"] (from movie list endpoint)
 * - List of objects: [{"id": 1, "name": "Horror"}] (from movie detail endpoint)
 */
object GenreListSerializer : KSerializer<List<GenreResponse>?> {
    override val descriptor: SerialDescriptor = 
        kotlinx.serialization.builtins.ListSerializer(kotlinx.serialization.json.JsonElement.serializer()).descriptor

    override fun serialize(encoder: Encoder, value: List<GenreResponse>?) {
        if (value == null) {
            encoder.encodeNull()
        } else {
            val listSerializer = kotlinx.serialization.builtins.ListSerializer(GenreResponse.serializer())
            encoder.encodeSerializableValue(listSerializer, value)
        }
    }

    override fun deserialize(decoder: Decoder): List<GenreResponse>? {
        val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
            ?: return null
        val element = jsonDecoder.decodeJsonElement()
        if (element is kotlinx.serialization.json.JsonNull) return null
        if (element !is kotlinx.serialization.json.JsonArray) return null
        
        return element.map { item ->
            when (item) {
                is kotlinx.serialization.json.JsonPrimitive -> {
                    // It's a string like "Horror"
                    GenreResponse(id = 0, name = item.content)
                }
                is kotlinx.serialization.json.JsonObject -> {
                    // It's a full object
                    kotlinx.serialization.json.Json.decodeFromJsonElement(GenreResponse.serializer(), item)
                }
                else -> GenreResponse(id = 0, name = "Unknown")
            }
        }
    }
}

// MOVIE
@Serializable
data class MovieResponse(
    val id: Int,
    val title: String,
    val synopsis: String? = null,
    @Serializable(with = GenreListSerializer::class)
    val genres: List<GenreResponse>? = null,
    val releaseDate: String? = null,
    val director: DirectorResponse? = null,
    val cast: List<CastMemberResponse>? = null,
    val minimumAge: Int? = null,
    val pictures: List<PictureResponse>? = null,
    val favorite: Boolean? = null  // From API - true if user has favorited this movie
)

@Serializable
data class RatingSummary(
    val average: Double? = null,
    val buckets: List<RatingBucket>? = null
)

@Serializable
data class RatingBucket(
    val rating: Int,
    val count: Int
)

object RatingPolymorphicSerializer : KSerializer<RatingSummary?> {
    override val descriptor: SerialDescriptor = 
        kotlinx.serialization.descriptors.buildClassSerialDescriptor("RatingSummary")

    override fun serialize(encoder: Encoder, value: RatingSummary?) {
        // We always serialize back to Object just to be safe, or we could handle it.
        // For read-only app, serialization might not be critical, but we should support it.
        if (value == null) {
            encoder.encodeNull()
            return
        }
        val output = encoder as kotlinx.serialization.json.JsonEncoder
        output.encodeSerializableValue(RatingSummary.serializer(), value)
    }

    override fun deserialize(decoder: Decoder): RatingSummary? {
        val input = decoder as? kotlinx.serialization.json.JsonDecoder ?: return null
        val element = input.decodeJsonElement()
        
        if (element is kotlinx.serialization.json.JsonNull) return null
        
        return if (element is kotlinx.serialization.json.JsonPrimitive) {
            // It's a number like 4.5 or 4
            try {
                RatingSummary(average = element.content.toDoubleOrNull())
            } catch (e: Exception) {
                null
            }
        } else if (element is kotlinx.serialization.json.JsonObject) {
            // It's the full object { "average": 3, ... }
            input.json.decodeFromJsonElement(RatingSummary.serializer(), element)
        } else {
            null
        }
    }
}

@Serializable
data class MovieResponse2(
    val id: Int,
    val title: String,
    val synopsis: String? = null,
    @Serializable(with = GenreListSerializer::class)
    val genres: List<GenreResponse>? = null,
    val director: DirectorShort? = null,
    @SerialName("mainPicture")
    private val _mainPicture: PictureResponse? = null,
    val releaseDate: String? = null, // Format: "YYYY-MM-DD"
    val favorite: Boolean = false,
    @Serializable(with = RatingPolymorphicSerializer::class)
    val rating: RatingSummary? = null, // Changed from Double to Object
    val cast: List<CastMemberResponse>? = null,
    val minimumAge: Int? = null,
    val pictures: List<PictureResponse>? = null
) {
    val mainPicture: PictureResponse?
        get() = _mainPicture ?: pictures?.firstOrNull { it.mainPicture == true } ?: pictures?.firstOrNull()
}

@Serializable
data class DirectorShort(
    val personId: Int,
    val name: String,
    val picture: PictureShort? = null
)
@Serializable
data class PictureShort(
    val id: Int,
    val mainPicture: Boolean = false,
    val filename: String? = null,
    val contentType: String? = null,
    val description: String? = null
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

@Serializable
data class UpdateMovieRequest(
    val id: Int,
    val title: String,
    val synopsis: String? = null,
    val genres: List<Int>? = null,
    val releaseDate: String? = null,
    val directorId: Int? = null,
    val minimumAge: Int? = null
)


// PERSON
@Serializable
data class PersonResponse(
    val id: Int,
    val name: String,
    val dateOfBirth: String? = null, // Format: "YYYY-MM-DD"
    val picture: PictureResponse? = null
)
@Serializable
data class PersonDetailResponse(
    val id: Int,
    val name: String,
    val dateOfBirth: String? = null, // Format: "YYYY-MM-DD"
    val pictures: List<PictureResponse>? = null,
    val directedMovies: List<DirectedMovie>? = null,
    val roles: List<PersonRole>? = null
)

@Serializable
data class DirectedMovie(
    val id: Int,
    val title: String,
    val releaseDate: String? = null,
    val picture: PictureResponse? = null
)

@Serializable
data class PersonRole(
    val movieId: Int,
    val title: String,
    val releaseDate: String? = null,
    val character: String
)

@Serializable
data class CreatePersonRequest(
    val name: String,
    val dateOfBirth: String? = null,
    val pictures: List<CreatePictureRequest>? = null
)

@Serializable
data class UpdatePersonRequest(
    val id: Int,
    val name: String,
    val dateOfBirth: String? = null
)

// PICTURE
@Serializable
data class PictureResponse(
    val id: Int,
    val mainPicture: Boolean? = null,
    val filename: String? = null,
    val contentType: String? = null,
    val description: String? = null,
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

// RATING (from /movies/{id}/ratings endpoint)
@Serializable
data class RatingResponse(
    val score: Int,           // 0-5 rating
    val comment: String? = null,
    val author: Int           // user_id
)

@Serializable
data class CreateRatingRequest(
    val score: Int,
    val comment: String? = null
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
