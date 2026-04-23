package com.battlebucks.domain.model

data class ScoreEvent(
    val playerId: String,
    val newScore: Int
)