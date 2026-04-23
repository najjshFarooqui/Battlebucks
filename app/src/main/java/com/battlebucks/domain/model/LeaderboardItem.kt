package com.battlebucks.domain.model

data class LeaderboardItem(
    val playerId: String,
    val name: String,
    val score: Int,
    val rank: Int,
    val rankChange: Int = 0
)