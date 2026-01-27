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
import io.ktor.client.request.post
import io.ktor.client.request.setBody
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

                getCredentials()?.let { (username, password) ->
                    val authString = "$username:$password"
                    val encodedAuthString = Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)
                    header("Authorization", "Basic $encodedAuthString")
                }

                url {
                    protocol = URLProtocol.HTTP
                    host = "10.0.2.2"
                    port = 8080
                }
            }
        }
    }


    fun getActors(): Flow<ApiResult<List<PersonResponse>>> = flow {
        try {
            val response = client.get("/people")
            if (response.status.isSuccess()) {
                val actors = response.body<List<PersonResponse>>()
                android.util.Log.d("MovieClient", "Actors fetched: ${actors.size}")
                emit(ApiResult.Success(actors))
            } else {
                android.util.Log.e("MovieClient", "Failed to fetch actors: ${response.status}")
                emit(ApiResult.Failure(response.body()))
            }
        } catch (e: Exception) {
             android.util.Log.e("MovieClient", "Exception fetching actors", e)
             emit(ApiResult.Failure(ProblemDetails("error", "Network Error", 500, e.message ?: "Unknown error")))
        }
    }
    fun getCategories(): Flow<ApiResult<List<CategoryResponse>>> = flow {

        try {
            val response = client.get("/genres");
            if (response.status.isSuccess()) {
                val categories = response.body<List<CategoryResponse>>()
                emit(ApiResult.Success(categories))
            } else {
                emit(ApiResult.Failure(response.body()))
            }
        } catch (e: Exception) {
            android.util.Log.e("MovieClient", "Exception fetching categories", e)
            emit(ApiResult.Failure(ProblemDetails("error", "Network Error", 500, e.message ?: "Unknown error")))

        }
    }
    fun getPerson(id: Int): Flow<ApiResult<PersonResponse>> = flow {
        try {
            val response = client.get("/people/$id")
            if (response.status.isSuccess()) {
                val person = response.body<PersonResponse>()
                android.util.Log.d("MovieClient", "Person fetched: ${person.name}")
                emit(ApiResult.Success(person))
            } else {
                android.util.Log.e("MovieClient", "Failed to fetch person $id: ${response.status}")
                emit(ApiResult.Failure(response.body()))
            }
        } catch (e: Exception) {
             android.util.Log.e("MovieClient", "Exception fetching person $id", e)
             emit(ApiResult.Failure(ProblemDetails("error", "Network Error", 500, e.message ?: "Unknown error")))
        }
    }

    fun getMoviesByCategory(categoryName: String): Flow<ApiResult<List<MovieResponse>>> = flow {
         try {
            val response = client.get("/movies?&genre=$categoryName")
            if (response.status.isSuccess()) {
                val movies = response.body<List<MovieResponse>>()
                emit(ApiResult.Success(movies))
            } else {
                val errorDetails = try {
                    response.body<ProblemDetails>()
                } catch (e: Exception) {
                     android.util.Log.e("MovieClient", "Failed to parse error body", e)
                    ProblemDetails(
                        type = "error",
                        title = "Api Error",
                        status = response.status.value,
                        detail = "Request failed with status ${response.status}"
                    )
                }
                emit(ApiResult.Failure(errorDetails))
            }
        } catch (e: Exception) {
             android.util.Log.e("MovieClient", "Exception fetching cat movies", e)
             emit(ApiResult.Failure(ProblemDetails("error", "Network Error", 500, e.message ?: "Unknown error")))
        }
    }

    suspend fun register(request: RegisterUserRequest): ApiResult<LoginResponse> = try {
        val response = client.post("/users/register") {
            setBody(request)
        }
        if (response.status.isSuccess()) {
            ApiResult.Success(LoginResponse(0, request.username, "user")) // Mocking LoginResponse for now or I should change T to PrivateUserResponse?
            ApiResult.Success(LoginResponse(0, request.username, "user")) // Placeholder, effectively just "Success"
        } else {
            ApiResult.Failure(response.body())
        }

    } catch (e: Exception) {
        ApiResult.Failure(ProblemDetails("error", "Network Error", 500, e.message ?: "Unknown error"))
    }

    suspend fun login(username: String, password: String): ApiResult<LoginResponse> = try {
        android.util.Log.d("MovieClient", "Attempting login for user: $username")
        val response = client.get("/users/login") {
            basicAuth(username, password)
        }
        if (response.status.isSuccess()) {
            val loginResult: LoginResponse = response.body()
            android.util.Log.d("MovieClient", "Login success: ${loginResult.id}")
            setCredentials(username, password)
            ApiResult.Success(loginResult)
        } else {
            android.util.Log.e("MovieClient", "Login failed: ${response.status}")
            ApiResult.Failure(response.body())
        }
    } catch (e: Exception) {
        android.util.Log.e("MovieClient", "Exception during login", e)
        ApiResult.Failure(ProblemDetails("error", "Network Error", 500, e.message ?: "Unknown error"))
    }


    data class Credentials(val username: String, val password: String)
}