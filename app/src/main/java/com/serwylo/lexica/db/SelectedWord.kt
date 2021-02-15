package com.serwylo.lexica.db

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Record each word that is found for every game played.
 */
@Parcelize
@Entity
data class SelectedWord(
        @PrimaryKey(autoGenerate = true)
        val foundWordId: Long = 0,
        val resultId: Long,
        val word: String,
        val points: Int,
        val isWord: Boolean,
) : Parcelable