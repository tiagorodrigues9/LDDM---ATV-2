package com.fatec.at2_base.games

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

class GameApi(
    private val baseUrl: String,
    private val client: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                },
            )
        }
    },
) {
    suspend fun listGames(): List<Game> =
        client.get("$baseUrl/games").body()

    suspend fun createGame(request: NewGameRequest): Game =
        client.post("$baseUrl/games") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
}

