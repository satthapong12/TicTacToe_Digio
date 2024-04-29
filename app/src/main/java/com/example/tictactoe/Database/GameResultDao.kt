package com.example.tictactoe.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameResultDao {
    @Insert
    suspend fun insertGameResult(gameResult: GameResult)

    @Query("SELECT * FROM game_results ORDER BY id DESC")
    suspend fun getAllGameResults(): List<GameResult>


}