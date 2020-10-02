package com.serwylo.lexica.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ResultDao {

    @Insert
    void insert(Result result);

    @Query("SELECT * FROM Result WHERE gameModeId = :gameModeId AND langCode = :langCode ORDER BY score DESC LIMIT 0, 1")
    Result findHighScore(long gameModeId, String langCode);

}
