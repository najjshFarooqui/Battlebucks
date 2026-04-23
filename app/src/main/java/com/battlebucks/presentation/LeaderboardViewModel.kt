package com.battlebucks.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battlebucks.data.engine.ScoreGenerator
import com.battlebucks.domain.model.LeaderboardItem
import com.battlebucks.domain.model.usecase.LeaderboardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    generator: ScoreGenerator,
    useCase: LeaderboardUseCase
) : ViewModel() {

    private val scoreEvents = generator.start()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            replay = 0
        )

    val leaderboard: StateFlow<List<LeaderboardItem>> =
        useCase.process(
            initialScores = generator.initialScores(),
            events = scoreEvents
        ).stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val lastUpdatedPlayerId: StateFlow<String?> =
        scoreEvents
            .map { it.playerId }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
}
