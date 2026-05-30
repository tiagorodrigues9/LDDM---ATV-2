package com.fatec.at2_base.games

import kotlinx.serialization.Serializable

@Serializable
data class Game(
    val id: Int,
    val title: String,
    val platform: String,
    val genre: String,
    val year: Int,
)

@Serializable
data class NewGameRequest(
    val title: String,
    val platform: String,
    val genre: String,
    val year: Int,
)

