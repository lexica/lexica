package com.serwylo.lexica

import com.serwylo.lexica.db.GameMode
import org.junit.Test

import org.junit.Assert.*

class GameModeTest {

    @Test
    fun serializeNonCustom() {
        val mode = GameMode(
                12,
                GameMode.Type.SPRINT,
                null,
                16,
                180,
                3,
                GameMode.SCORE_LETTERS,
                "hint_colour"
        )

        assertEquals(mode, GameMode.deserialize(mode.serialize()))
    }

    @Test
    fun serializeCustom() {
        val mode = GameMode(
                100,
                GameMode.Type.CUSTOM,
                "Custom Game Mode",
                25,
                360,
                5,
                GameMode.SCORE_WORDS,
                "hint_both"
        )

        assertEquals(mode, GameMode.deserialize(mode.serialize()))
    }

}