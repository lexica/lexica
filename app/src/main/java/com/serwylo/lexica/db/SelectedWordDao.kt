package com.serwylo.lexica.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SelectedWordDao {

    @Insert
    fun insert(selectedWords: List<SelectedWord>)

    @Query("DELETE FROM SelectedWord WHERE resultId IN (SELECT resultId FROM Result WHERE gameModeId = :gameModeId)")
    fun deleteByGameMode(gameModeId: Long)

}