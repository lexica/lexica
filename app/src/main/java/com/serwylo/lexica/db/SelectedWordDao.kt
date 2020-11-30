package com.serwylo.lexica.db

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface SelectedWordDao {

    @Insert
    fun insert(selectedWords: List<SelectedWord>)

}