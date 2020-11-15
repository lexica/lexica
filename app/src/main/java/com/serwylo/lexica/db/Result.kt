package com.serwylo.lexica.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

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