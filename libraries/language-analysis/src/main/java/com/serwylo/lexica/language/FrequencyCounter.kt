package com.serwylo.lexica.language

import com.serwylo.lexica.game.CharProbGenerator
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.language.GeneticAlgorithm.Fitness
import java.io.File

object FrequencyCounter {

    fun run(trieDir: File, dictDir: File, outputDir: File, language: Language) {

        val charsInDict = mutableMapOf<Char, MutableList<Int>>()

        val dictionaryFile = File(dictDir, language.dictionaryFileName)
        val words = dictionaryFile.readLines().filterNot { it.matches("^(\\s*|#.*)$".toRegex()) }

        words.forEach { word ->

            val charsInWord = word.toCharArray()
            val charCountsInWord = mutableMapOf<Char, Int>()
            charsInWord.onEach {  c ->
                val countForCharInWord = charCountsInWord[c] ?: 0
                charCountsInWord[c] = countForCharInWord + 1
            }

            charCountsInWord.onEach { (c, charCountInWord) ->
                val charCountInDict = charsInDict[c] ?: mutableListOf()
                for (i in 0 until charCountInWord) {
                    if (charCountInDict.size <= i) {
                        charCountInDict.add(1)
                    } else {
                        charCountInDict[i] ++
                    }
                }
                charsInDict[c] = charCountInDict
            }
        }

        val probsString = renderProbs(charsInDict)
        val charProbGenerator = createCharProbGenerator(probsString, language)
        val fitness = GeneticAlgorithm.Fitness.calc(trieDir, charProbGenerator, language)

        println("#")
        println("# These numbers represent the frequency of each character in the dictionary.")
        println("#")
        println("# When a letter is found in a word:")
        println("#  - If once, the 1st probability is incremented by 1.")
        println("#  - If twice, the 1st probability is incremented by 2, and the 2nd probability by 1, etc.")
        println("#")
        println("# When generating ${GeneticAlgorithm.FITNESS_CALC_BOARDS_TO_GENERATE} 4x4 boards, the following number of words were observed:")
        println("#   $fitness")
        println("#")
        println("")
        println(probsString)
        println("")
    }

    private fun renderProbs(charsInDict: Map<Char, List<Int>>): String {
        return charsInDict
            .toSortedMap(Comparator { a, b -> a - b })
            .map { (letter, counts) -> "$letter ${counts.joinToString(" ")}" }
            .joinToString("\n")
    }

    private fun createCharProbGenerator(probs: String, language: Language): CharProbGenerator {
        return CharProbGenerator(probs.byteInputStream(), language)
    }

}