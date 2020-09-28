package com.serwylo.lexica.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameModeDao {

    @Insert
    void insert(GameMode gameMode);

    @Delete
    void delete(GameMode gameMode);

    @Query("SELECT * FROM GameMode")
    LiveData<List<GameMode>> getAllGameModes();

    @Query("SELECT * FROM GameMode ORDER BY gameModeId LIMIT 0, 1")
    GameMode getFirst();

    @Query("SELECT * FROM GameMode WHERE gameModeId = :id")
    GameMode getById(long id);

}
