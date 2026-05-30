package com.example.simon_pse.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGame(game: GameEntity)

    @Query("SELECT * FROM games_table ORDER BY timestamp DESC")
    fun getAllGames(): Flow<List<GameEntity>>
}