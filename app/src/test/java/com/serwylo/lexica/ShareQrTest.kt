package com.serwylo.lexica

import android.net.Uri
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.share.SharedGameData
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareQrTest {

    @Test
    fun parseHumanReadableQr() {

        val uri = Uri.parse("https://lexica.github.io/m/?b=ABCDEFGHIJKLMNOPQRSTUVWXY&l=fr_FR&t=2700&s=l&m=4&mv=20007&v=20007&h=nc")

        val sharedGameData = SharedGameData.parseGame(uri)

        val expectedBoard = listOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y")
        assertEquals(expectedBoard, sharedGameData.board)
        assertEquals(Language.from("fr_FR"), sharedGameData.language)
        assertEquals(45 * 60, sharedGameData.timeLimitInSeconds)
        assertEquals(4, sharedGameData.minWordLength)
        assertEquals(GameMode.SCORE_LETTERS, sharedGameData.scoreType)
        assertEquals("hint_both", sharedGameData.hints)

        val serialized = sharedGameData.serialize()
        println(serialized)
        assertEquals(uri, serialized)

    }

}