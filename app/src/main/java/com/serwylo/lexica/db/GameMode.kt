package com.serwylo.lexica.db

import android.content.Context
import android.os.Parcelable
import androidx.room.PrimaryKey
import androidx.room.Entity
import com.serwylo.lexica.R
import kotlinx.android.parcel.Parcelize
import kotlin.math.sqrt


@Parcelize
@Entity
data class GameMode (

        @PrimaryKey(autoGenerate = true)
        var gameModeId: Long = 0,

        val type: Type,

        val customLabel: String? = null,

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

) : Parcelable {

    enum class Type {
        SPRINT,
        MARATHON,
        BEGINNER,
        LETTER_POINTS,
        CUSTOM,
        LEGACY,
    }

    fun label(context: Context): String {
        return when (type) {
            Type.SPRINT -> context.getString(R.string.game_mode_sprint)
            Type.MARATHON -> context.getString(R.string.game_mode_marathon)
            Type.BEGINNER -> context.getString(R.string.game_mode_beginner)
            Type.LETTER_POINTS -> context.getString(R.string.game_mode_letter_points)

            Type.LEGACY -> {
                val duration = context.resources.getQuantityString(R.plurals.num_minutes, timeLimitSeconds / 60, timeLimitSeconds / 60)
                val boardWidth = sqrt(boardSize.toDouble()).toInt()
                val size = "${boardWidth}x${boardWidth}"
                val score = if (SCORE_LETTERS == scoreType) context.getString(R.string.letter_points) else context.getString(R.string.word_length)
                return "$duration / $size / $score"
            }

            // Don't bother internationalizing, because there should always be a custom label
            // when the type is custom, though Kotlin doesn't know this.
            Type.CUSTOM -> customLabel ?: "Custom"
        }
    }

    fun description(context: Context): String {
        return when (type) {
            Type.SPRINT -> context.getString(R.string.game_mode_sprint_description)
            Type.MARATHON -> context.getString(R.string.game_mode_marathon_description)
            Type.BEGINNER -> context.getString(R.string.game_mode_beginner_description)
            Type.LETTER_POINTS -> context.getString(R.string.game_mode_letter_points_description)
            Type.CUSTOM -> context.getString(R.string.game_mode_custom_description)
            Type.LEGACY -> context.getString(R.string.game_mode_legacy_description)
        }
    }

    fun hintModeCount(): Boolean {
        return hintMode == "tile_count" || hintMode == "hint_both"
    }

    fun hintModeColor(): Boolean {
        return hintMode == "hint_colour" || hintMode == "hint_both"
    }

    fun serialize(): String {
        return StringBuilder()
                .appendLine(gameModeId)
                .appendLine(type.name)
                .appendLine(customLabel ?: "")
                .appendLine(boardSize)
                .appendLine(timeLimitSeconds)
                .appendLine(minWordLength)
                .appendLine(scoreType)
                .appendLine(hintMode)
                .toString()
    }

    override fun toString(): String {
        return "Game Mode [id: $gameModeId, type: $type]"
    }

    companion object {
        const val SCORE_WORDS = "W"
        const val SCORE_LETTERS = "L"

        @JvmStatic fun deserialize(modeString: String): GameMode {
            val parts = modeString.split("\n")

            return GameMode(
                    gameModeId = parts[0].toLong(),
                    type = Type.valueOf(parts[1]),
                    customLabel = if (parts[2] == "") null else parts[2],
                    boardSize = parts[3].toInt(),
                    timeLimitSeconds = parts[4].toInt(),
                    minWordLength = parts[5].toInt(),
                    scoreType = parts[6],
                    hintMode = parts[7],
            )
        }

    }

}