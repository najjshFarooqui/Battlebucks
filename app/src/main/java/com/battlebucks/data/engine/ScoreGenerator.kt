package com.battlebucks.data.engine

import com.battlebucks.domain.model.ScoreEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random




class ScoreGenerator(
    private val players: List<String>,
    private val seed: Int,
    private val minDelayMs: Long = 500L,
    private val maxDelayMs: Long = 2000L,
    private val minIncrement: Int = 10,
    private val maxIncrement: Int = 100,
    private val minInitialScore: Int = 500,
    private val maxInitialScore: Int = 3000,
) {

    private val random = Random(seed)

    private val scores: MutableMap<String, Int> = players
        .associateWith { random.nextInt(minInitialScore, maxInitialScore + 1) }
        .toMutableMap()

    fun initialScores(): Map<String, Int> = scores.toMap()

    fun start(): Flow<ScoreEvent> = flow {
        while (true) {
            val delayMs = random.nextLong(minDelayMs, maxDelayMs + 1)
            delay(delayMs)

            val player = players[random.nextInt(players.size)]
            val increment = random.nextInt(minIncrement, maxIncrement + 1)

            val newScore = (scores[player] ?: 0) + increment
            scores[player] = newScore

            emit(
                ScoreEvent(
                    playerId = player,
                    newScore = newScore
                )
            )
        }
    }
}
