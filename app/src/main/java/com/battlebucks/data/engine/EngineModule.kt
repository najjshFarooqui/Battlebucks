package com.battlebucks.data.engine


import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object EngineModule {

    @Provides
    @Singleton
    fun provideScoreGenerator(): ScoreGenerator {
        return ScoreGenerator(
            players = GamePlayers.defaultPlayers,
            seed = 12345
        )
    }
}
