package com.serwylo.lexica.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SelectedWordDao {

    @Insert
    void insert(List<SelectedWord> selectedWords);

}
