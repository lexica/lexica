package com.serwylo.lexica

import com.serwylo.lexica.game.CharProbGenerator
import com.serwylo.lexica.game.FourByFourBoard
import com.serwylo.lexica.lang.EnglishGB
import com.serwylo.lexica.lang.Language
import org.junit.Assert.*
import org.junit.Test


class NextBoardTest {

    private fun getGeneratorForLang(language: Language): CharProbGenerator {
        val stream = javaClass.classLoader.getResourceAsStream(language.letterDistributionFileName)
        return CharProbGenerator(stream, language)
    }


    @Test
    fun invariantHashTest() {
        // four times the same board, but already rotated
        val board1 = FourByFourBoard(arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"))
        val board2 = FourByFourBoard(arrayOf("M", "I", "E", "A", "N", "J", "F", "B", "O", "K", "G", "C", "P", "L", "H", "D"))
        val board3 = FourByFourBoard(arrayOf("P", "O", "N", "M", "L", "K", "J", "I", "H", "G", "F", "E", "D", "C", "B", "A"))
        val board4 = FourByFourBoard(arrayOf("D", "H", "L", "P", "C", "G", "K", "O", "B", "F", "J", "N", "A", "E", "I", "M"))

        val boards = arrayOf(board1, board2, board3, board4)

        for (board in boards) {
            for (i in 0..4) {
                val tmp = FourByFourBoard(board.letters) // we need to create a tmp bc the hash is set in the constructor
                assertEquals(-50360832, tmp.rotationInvariantHash)
                board.rotate()
            }
        }
    }

    @Test
    fun oneIterationTest() {
        val language = EnglishGB()

        val board1 = getGeneratorForLang(language).generateFourByFourBoard(3500)
        val board2 = getGeneratorForLang(language).generateFourByFourBoard(CharProbGenerator.BoardSeed.fromPreviousBoard(board1))

        // TODO: This changes if the word probabilities change, maybe have a some test probabilities
        assertEquals("r,i,c,o,n,a,qu,a,l,t,d,s,e,e,i,r", board1.toString())
        assertEquals("c,n,a,s,s,qu,s,a,a,e,p,l,i,t,a,d", board2.toString())
    }

    @Test
    fun manyIterationTest() {
        val language = EnglishGB()

        var board = getGeneratorForLang(language).generateFourByFourBoard(3500)
        assertEquals("r,i,c,o,n,a,qu,a,l,t,d,s,e,e,i,r", board.toString())

        for (i in 0..500) {
            board = getGeneratorForLang(language).generateFourByFourBoard(CharProbGenerator.BoardSeed.fromPreviousBoard(board))
        }

        assertEquals("v,t,s,p,d,p,a,n,s,o,i,i,l,r,a,o", board.toString())
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