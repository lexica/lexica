package com.serwylo.lexica.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultDao {

    @Insert
    fun insert(result: Result): Long

    @Query("SELECT * FROM Result WHERE gameModeId = :gameModeId AND langCode = :langCode ORDER BY score DESC LIMIT 0, 1")
    fun findHighScore(gameModeId: Long, langCode: String): Result?

    @Query("SELECT * FROM Result WHERE gameModeId = :gameModeId AND langCode = :langCode ORDER BY score DESC LIMIT 0, 10")
    fun findTop10(gameModeId: Long, langCode: String): LiveData<List<Result>>

    @Query("DELETE FROM Result")
    fun deleteAll()

    @Query("DELETE FROM Result WHERE gameModeId = :gameModeId")
    fun deleteByGameMode(gameModeId: Long)

}