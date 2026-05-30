package com.example.simon_pse.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games_table")
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sequence: String,
    val errorIndex: Int,
    val longestStreak: Int,
    val timestamp: Long = System.currentTimeMillis()
)