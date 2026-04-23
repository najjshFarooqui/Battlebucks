package com.battlebucks.domain.model

data class Player(
    val id: String,
    val name: String,
    val score: Int = 0
)