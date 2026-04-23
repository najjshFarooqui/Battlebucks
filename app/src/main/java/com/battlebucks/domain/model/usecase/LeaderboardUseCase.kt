package com.battlebucks.domain.model.usecase

import com.battlebucks.domain.model.LeaderboardItem
import com.battlebucks.domain.model.Player
import com.battlebucks.domain.model.ScoreEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class LeaderboardUseCase @Inject constructor() {

    fun process(
        initialScores: Map<String, Int>,
        events: Flow<ScoreEvent>
    ): Flow<List<LeaderboardItem>> {

        // 🔹 Initial sorted list
        val initialPlayers = initialScores.map { (id, score) ->
            Player(id, id, score)
        }.sortedByDescending { it.score }

        return events
            .scan(initialPlayers) { currentPlayers, event ->

                val mutableList = currentPlayers.toMutableList()

                // 🔥 Remove old player
                val index = mutableList.indexOfFirst { it.id == event.playerId }
                if (index != -1) {
                    mutableList.removeAt(index)
                }

                // 🔥 Create updated player
                val updatedPlayer = Player(
                    id = event.playerId,
                    name = event.playerId,
                    score = event.newScore
                )

                // 🔥 Binary search insert (DESC order)
                val insertIndex = mutableList.binarySearch {
                    event.newScore.compareTo(it.score)
                }.let { if (it < 0) -it - 1 else it }

                mutableList.add(insertIndex, updatedPlayer)

                mutableList
            }
            .map { sortedPlayers ->

                // 🔹 Previous ranks
                val previousRanks = sortedPlayers
                    .mapIndexed { index, player -> player.id to (index + 1) }
                    .toMap()

                buildLeaderboard(sortedPlayers, previousRanks)
            }
            .flowOn(Dispatchers.Default) // 🔥 run on background
    }

    // 🔹 Ranking logic (NO sorting here)
    private fun buildLeaderboard(
        sortedPlayers: List<Player>,
        previousRanks: Map<String, Int>
    ): List<LeaderboardItem> {

        var previousScore: Int? = null
        var currentRank = 0

        return sortedPlayers.mapIndexed { index, player ->

            if (player.score != previousScore) {
                currentRank = index + 1
            }

            val newRank = currentRank
            val oldRank = previousRanks[player.id] ?: newRank

            previousScore = player.score

            LeaderboardItem(
                playerId = player.id,
                name = player.name,
                score = player.score,
                rank = newRank,
                rankChange = oldRank - newRank
            )
        }
    }
}