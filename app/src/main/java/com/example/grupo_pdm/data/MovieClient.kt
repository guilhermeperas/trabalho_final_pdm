package com.example.grupo_pdm.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlin.time.Instant

val customSerializersModule = SerializersModule {
    contextual(InstantSerializer())
}

val jsonConfiguration = Json {
    serializersModule = customSerializersModule
    isLenient = true
    ignoreUnknownKeys = true
}

val httpClient = HttpClient(Android) {

    install(ContentNegotiation) {
        json(
            jsonConfiguration
        )
    }

    defaultRequest {
        contentType(ContentType.Application.Json)
        url {
            protocol = URLProtocol.HTTP
            host = "10.0.2.2"
            port = 8080
        }
    }
}

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
