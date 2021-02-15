package com.serwylo.lexica.db

import android.content.Context
import androidx.lifecycle.LiveData
import com.serwylo.lexica.lang.Language

class SelectedWordRepository(private val selectedWordDao: SelectedWordDao) {

    /**
     * Convenience constructor. The primary constructor is the one which allows for proper dependency
     * injection and easier unit tests, etc.
     */
    constructor(context: Context): this(
            Database.get(context).selectedWordDao(),
    )

    fun findAllWordsByLanguage(language: Language): LiveData<List<SelectedWord>> {
        return selectedWordDao.findAllWordsByLanguage(language.name)
    }

}