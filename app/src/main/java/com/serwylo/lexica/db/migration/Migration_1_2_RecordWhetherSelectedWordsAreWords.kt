package com.serwylo.lexica.db.migration

import android.util.Log
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * When first implemented, all "words" were recorded, regardless of whether they were in fact
 * words in the dictionary or not. This wasn't picked up until later, when we actually added
 * the ability to see each recorded word (still during Alpha, so not too worried about clearing
 * this data). It then became immediately obvious that we accidentally recorded all words.
 */
class Migration_1_2_RecordWhetherSelectedWordsAreWords : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        Log.i(TAG, "Removing previously found words from database, because we don't know whether they are actually words or not.")
        database.delete("SelectedWord", null, null)

        Log.i(TAG, "Adding column to SelectedWord to record whether words are indeed words, so future results are recorded correctly.")
        database.execSQL("ALTER TABLE SelectedWord ADD isWord INTEGER NOT NULL DEFAULT 0")
    }

    companion object {
        const val TAG = "MigrateSelectedWords"
    }

}