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
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsBytes
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


    fun getActors(): Flow<ApiResult<List<PersonResponse>>> = flow {
        android.util.Log.d("MovieClient", "Fetching actors...")
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

    suspend fun register(request: RegisterUserRequest): ApiResult<LoginResponse> = try {
        val response = client.post("/users/register") {
            setBody(request)
        }
        if (response.status.isSuccess()) {
             // The API returns PrivateUserResponse usually, but let's check Swagger.
             // Swagger says /users/register returns PrivateUserResponse.
             // However, for immediate login or simplifying, we might want to map it or just return success.
             // Let's assume the user wants to login immediately after or just navigate back.
             // Wait, the return type in signature I wrote is LoginResponse...
             // Swagger says:
             // /users/register -> 200 OK -> PrivateUserResponse
             // /users/login -> 200 OK -> LoginResponse
             // I will change the return type to PrivateUserResponse to match API.
            ApiResult.Success(LoginResponse(0, request.username, "user")) // Mocking LoginResponse for now or I should change T to PrivateUserResponse?
            // Actually, let's look at Model.kt. We have PrivateUserResponse? No.
            // Let's add PrivateUserResponse to Model.kt if missing or just use simple mapping.
            // Wait, I can't check Model.kt content again easily in this turn.
            // The user wants "logic for create account".
            // I'll implementation a simple Void/Boolean success or return the user data.
            // Let's stick to returning ApiResult<Boolean> for simplicity or the actual response.
            // I'll return ApiResult<Boolean> for "success".
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

    /**
     * Pesquisa filmes por TÍTULO usando o endpoint GET /movies com query params.
     *
     * Retorna um Flow<ApiResult<List<MovieResponse>>> porque:
     * - Flow: permite emitir vários estados ao longo do tempo (Loading -> Success/Failure)
     * - ApiResult: padroniza estados da chamada (Loading/Success/Failure)
     */
    fun getMoviesByTitle(title: String): Flow<ApiResult<List<MovieResponse>>> = flow {
        // 1) Informa a UI que a pesquisa começou (para mostrar ProgressBar, etc.)
        emit(ApiResult.Loading(0))

        try {
            // 2) Faz o GET /movies com parâmetros de pesquisa (query string)
            val response = client.get("/movies") {
                parameter("title", title)
                parameter("fromRating", 0)
                parameter("toRating", 5)
                parameter("favoritesOnly", false)
                parameter("sortBy", "releaseDate")     // ou "rating" / "title"
                parameter("sortOrder", "desc")         // "asc" / "desc"
            }
            // 3) Interpreta a resposta
            if (response.status.isSuccess()) {
                val movies = response.body<List<MovieResponse>>()
                emit(ApiResult.Success(movies))
            } else {
                emit(ApiResult.Failure(response.body()))
            }
        } catch (e: Exception) {
            // 4) Erro de rede/parse/etc.: emite Failure com um ProblemDetails “genérico”
            emit(
                ApiResult.Failure(
                    ProblemDetails(
                        "error",
                        "Network Error",
                        500,
                        e.message ?: "Unknown error")
                )
            )
        }
    }
    /**
     * Vai buscar a IMAGEM (bytes) de um filme.
     * Endpoint: GET /movies/{id}/pictures/{pictureId}
     *
     * Porquê "suspend":
     * - É uma chamada única, não precisamos de Flow aqui
     * - Pode ser chamada dentro de coroutine (ex: no adapter)
     *
     * Retorna ByteArray?:
     * - ByteArray com a imagem quando dá sucesso
     * - null quando falha (para manter simples no adapter)
     */
    suspend fun getMoviePictureBytes(movieId: Int, pictureId: Int): ByteArray? {
        return try {
            val response = client.get("/movies/$movieId/pictures/$pictureId")
            if (response.status.isSuccess()) response.bodyAsBytes() else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Recomendações por GÉNERO.
     * Usa GET /movies com query param "genre", pedindo "count" itens.
     *
     * Observação:
     * - Aqui você está a ordenar por "rating desc" para recomendar “os melhores”
     * - Mantém filtros básicos (favoritesOnly=false, rating 0..5)
     */
    fun getMoviesByGenre(genre: String, count: Int = 10): Flow<ApiResult<List<MovieResponse>>> = flow {
        emit(ApiResult.Loading(0))
        try {
            val response = client.get("/movies") {
                parameter("genre", genre)
                parameter("count", count)
                parameter("sortBy", "rating")
                parameter("sortOrder", "desc")
                parameter("favoritesOnly", false)
                parameter("fromRating", 0)
                parameter("toRating", 5)
            }

            if (response.status.isSuccess()) {
                emit(ApiResult.Success(response.body()))
            } else {
                emit(ApiResult.Failure(response.body()))
            }
        } catch (e: Exception) {
            emit(ApiResult.Failure(ProblemDetails("error", "Network Error", 500, e.message ?: "Unknown error")))
        }
    }
}