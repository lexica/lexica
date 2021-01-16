package com.serwylo.lexica.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameModeDao {

    @Insert
    fun insert(gameMode: GameMode): Long

    @Insert
    fun insert(gameModes: List<GameMode>)

    @Delete
    fun delete(gameMode: GameMode)

    @Query("SELECT * FROM GameMode")
    fun getAllGameModes(): LiveData<List<GameMode>>

    /**
     * Used for testing as LiveData is tricky to use under test.
     */
    @Query("SELECT * FROM GameMode")
    fun getAllGameModesSynchronous(): List<GameMode>

    @Query("SELECT * FROM GameMode ORDER BY gameModeId LIMIT 0, 1")
    fun getFirst(): GameMode?

    @Query("SELECT * FROM GameMode WHERE gameModeId = :id")
    fun getById(id: Long): GameMode?

}