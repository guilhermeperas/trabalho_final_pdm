package com.example.grupo_pdm.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.client.call.body
import android.util.Base64
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

val customSerializersModule = SerializersModule {
    contextual(InstantSerializer())
}

val jsonConfiguration = Json {
    serializersModule = customSerializersModule
    isLenient = true
    ignoreUnknownKeys = true
}

private var authHeader: String? = null

fun setCredentials(username: String, password: String) {
    val authString = "$username:$password"
    val encodedAuthString = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
    authHeader = "Basic $encodedAuthString"
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
        authHeader?.let {
            header("Authorization", it)
        }
    }
}
sealed interface ApiResult<out T> {
    // object Loading : ApiResult<Nothing>
    data class Loading(val progress: Int): ApiResult<Nothing>
    data class Success<T>(val data: T) : ApiResult<T>
    data class Failure(val error: ProblemDetails) : ApiResult<Nothing>
}








object MovieServiceClient {

    private val lock = Any()
    private var credentials: Credentials? = null


    fun setCredentials(username: String, password: String) = synchronized(lock) {
        assert(username.isNotBlank()) { "username is blank" }
        assert(password.isNotBlank()) { "password is blank" }
        this.credentials = Credentials(username, password)
    }


    fun getCredentials() = synchronized(lock) {
        credentials
    }

    private val client by lazy {
        HttpClient(Android) {

            install(ContentNegotiation) {
                json(
                    Json {
                        serializersModule = customSerializersModule
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                contentType(ContentType.Application.Json)

                getCredentials()?.run {
                    basicAuth(username, password)
                }

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2"
                    port = 8080
                }
            }
        }
    }


    fun getMovies(): Flow<ApiResult<List<MovieResponse>>> = flow {


        repeat(10) {
            delay(100)
            emit(ApiResult.Loading((it + 1) * 10))
        }

        try {
            val response = client.get("/movies")
            if (response.status.isSuccess()) {
                emit(ApiResult.Success(response.body()))
            } else {
                emit(ApiResult.Failure(response.body()))
            }
        } catch (e: Exception) {
            TODO("Not yet implemented")
        }
    }
    data class Credentials(val username: String, val password: String)
}