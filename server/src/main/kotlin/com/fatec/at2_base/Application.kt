package com.fatec.at2_base

import com.fatec.at2_base.games.Game
import com.fatec.at2_base.games.NewGameRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicInteger

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            },
        )
    }

    val idSeq = AtomicInteger(3)
    val games = mutableListOf(
        Game(id = 1, title = "Hollow Knight", platform = "PC", genre = "Metroidvania", year = 2017),
        Game(id = 2, title = "The Legend of Zelda: Breath of the Wild", platform = "Switch", genre = "Aventura", year = 2017),
        Game(id = 3, title = "Stardew Valley", platform = "PC", genre = "Simulação", year = 2016),
    )

    routing {
        get("/") {
            call.respondText("Ktor: ${Greeting().greet()}")
        }

        route("/games") {
            get {
                call.respond(games)
            }

            post {
                val req = call.receive<NewGameRequest>()

                val created = Game(
                    id = idSeq.incrementAndGet(),
                    title = req.title.trim(),
                    platform = req.platform.trim(),
                    genre = req.genre.trim(),
                    year = req.year,
                )
                games.add(created)
                call.respond(HttpStatusCode.Created, created)
            }
        }
    }
}