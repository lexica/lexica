package com.serwylo.lexica

import com.serwylo.lexica.game.CharProbGenerator
import com.serwylo.lexica.game.FourByFourBoard
import com.serwylo.lexica.lang.EnglishGB
import org.junit.Assert.*
import org.junit.Test
import java.io.File


class NextBoardTest {

    private fun getGeneratorForLang(language: EnglishGB): CharProbGenerator {
        val stream = javaClass.classLoader.getResourceAsStream(language.letterDistributionFileName)
        return CharProbGenerator(stream, language)
    }

    @Test
    fun oneIterationTest() {
        val language = EnglishGB()

        val board1 = getGeneratorForLang(language).generateFourByFourBoard(3500)
        val board2 = getGeneratorForLang(language).generateFourByFourBoard(board1)

        // TODO: This changes if the word probabilities change, maybe have a some test probabilities
        assertEquals("r,i,c,o,n,a,qu,a,l,t,d,s,e,e,i,r", board1.toString())
        assertEquals("p,s,e,i,e,d,e,m,i,e,n,t,p,t,a,a", board2.toString())
    }

    @Test
    fun manyIterationTest() {
        val language = EnglishGB()

        var board = getGeneratorForLang(language).generateFourByFourBoard(3500)
        assertEquals("r,i,c,o,n,a,qu,a,l,t,d,s,e,e,i,r", board.toString())

        for (i in 0..500) {
            board =  getGeneratorForLang(language).generateFourByFourBoard(board)
        }

        assertEquals("m,s,o,a,s,s,i,e,a,t,b,d,r,n,o,p", board.toString())
    }


    @Test
    fun testSeededBoards() {
        val language = EnglishGB()

        // the generators are altered after using them. might not be the most intuitive thing
        val board1 = getGeneratorForLang(language).generateFourByFourBoard(3500)
        val board2 = getGeneratorForLang(language).generateFourByFourBoard(3500)

        assertEquals(board1.toString(), board2.toString())

        val board3 = getGeneratorForLang(language).generateFiveByFiveBoard(3500)
        val board4 = getGeneratorForLang(language).generateFiveByFiveBoard(3500)

        assertEquals(board3.toString(), board4.toString())

        val board5 = getGeneratorForLang(language).generateSixBySixBoard(3500)
        val board6 = getGeneratorForLang(language).generateSixBySixBoard(3500)

        assertEquals(board5.toString(), board6.toString())


    }
}