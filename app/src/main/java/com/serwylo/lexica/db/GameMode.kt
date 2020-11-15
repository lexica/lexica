package com.serwylo.lexica.db

import android.os.Parcelable
import androidx.room.PrimaryKey
import android.os.Parcel
import androidx.room.Entity
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity
data class GameMode (

        @PrimaryKey(autoGenerate = true)
        var gameModeId: Long = 0,

        val label: String,

        val description: String,

        /**
         * 16, 25, 36.
         */
        val boardSize: Int,

        val timeLimitSeconds: Int,

        /**
         * Between 3 and 9.
         */
        val minWordLength: Int,

        val scoreType: String,

        val hintMode: String,

        val isCustom: Boolean,

) : Parcelable {

    fun hintModeCount(): Boolean {
        return hintMode == "tile_count" || hintMode == "hint_both"
    }

    fun hintModeColor(): Boolean {
        return hintMode == "hint_colour" || hintMode == "hint_both"
    }

    override fun toString(): String {
        return "Game Mode [id: $gameModeId, label: $label]"
    }

    companion object {
        const val SCORE_TYPE = "scoreType"
        const val SCORE_WORDS = "W"
        const val SCORE_LETTERS = "L"
    }

}