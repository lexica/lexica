package com.serwylo.lexica

import android.net.Uri
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.share.SharedGameData
import org.junit.Assert.assertEquals
import org.junit.Test

class ShareQrTest {

    @Test
    fun generateQr() {

    }

    @Test
    fun parseHumanReadableQr() {
        val qr = """
            lexica://share/
            A B C D E
            F G H I J
            K L M N O
            P Q R S T
            U V W X Y

            Language: fr_FR
            Time: 45m
            Score: Letter
            Min Word Length: 4
            Hints: Colour + Number
        """.trimIndent()

        // val uri = Uri.parse(qr)
        val uri = Uri.parse("lexica://share/ABCDEFGHIJKLMNOPQRSTUVWXY?l=fr_FR&t=2700&s=l&m=4&h=nc")

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