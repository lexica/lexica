package com.serwylo.lexica.language

import org.junit.Assert
import org.junit.Test

class FrequencyCounterTest {

    @Test
    fun countCharsInWord() {
        val acacia = FrequencyCounter.countCharsInWord("acacia")
        Assert.assertEquals(3, acacia.size)
        Assert.assertEquals(3, acacia['a'])
        Assert.assertEquals(2, acacia['c'])
        Assert.assertEquals(1, acacia['i'])
    }

    @Test
    fun countCharsInDict() {
        val counts = FrequencyCounter.countCharsInDict(
            listOf(
                "acacia",
                "aac",
                "fruit",
                "zoom",
                "room",
                "a",
            )
        )

        val expected = """
            a 3 2 1
            c 2 1
            f 1
            i 2
            m 2
            o 2 2
            r 2
            t 1
            u 1
            z 1
        """.trimIndent()

        Assert.assertEquals(expected, FrequencyCounter.renderProbs(counts))
    }

}