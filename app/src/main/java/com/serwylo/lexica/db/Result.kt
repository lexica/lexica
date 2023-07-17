package com.serwylo.lexica.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Each game that is played ends up recording a score in the database.
 * The score is recorded by this entity, on a per-game-mode-per-language basis (because different
 * languages may tend towards higher or lower scores due to dictionary differences).
 */
@Parcelize
@Entity
data class Result(
    @PrimaryKey(autoGenerate = true)
    val resultId: Long = 0,
    val gameModeId: Long,
    val langCode: String,
    val score: Long,
    val maxScore: Long,
    val numWords: Int = 0,
    val maxNumWords: Int = 0,
) : Parcelable