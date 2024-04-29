package com.example.tictactoe.Database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.tictactoe.Win
import java.sql.Timestamp

@Entity(tableName = "game_results")
data class GameResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val winner:  Win?,
    val timestamp: String
)