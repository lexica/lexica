package com.serwylo.lexica.api;

import com.serwylo.lexica.game.CharProbGenerator
import com.serwylo.lexica.lang.EnglishUS
import com.serwylo.lexica.lang.Language
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {

    if (args.size != 3) {
        println("Usage: ApiGenerator path-to-dict-dir path-to-letters-dir path-to-output-dir")
        exitProcess(1)
    }

    val dictDir = File(args[0])
    val lettersDir = File(args[1])
    val outputDir = File(args[2])

    if (!File(dictDir, EnglishUS().dictionaryFileName).exists()) {
        println("Error: Incorrect path-to-dict-dir: $dictDir")
        exitProcess(1)
    }

    if (!File(lettersDir, EnglishUS().letterDistributionFileName).exists()) {
        println("Error: Incorrect path-to-letters-dir: $lettersDir")
        exitProcess(1)
    }

    if (!outputDir.exists() && !outputDir.mkdirs()) {
        println("Error: Could not make output directory: ${outputDir.absolutePath}")
        exitProcess(1)
    }

    val languageMetadata: Map<Language, LanguageMetadata> =
        Language.allLanguages.values.associateWith { language ->
            val charProbs = CharProbGenerator(File(lettersDir, language.letterDistributionFileName).inputStream(), language)

            LanguageMetadata(
                language.name,
                language.locale.toLanguageTag(),
                language.isBeta,
                language.definitionUrl,
                letterProbabilities = readLetters(language, lettersDir),
                letterScores = charProbs.alphabet.associateWith { letter ->
                    language.getPointsForLetter(language.applyMandatorySuffix(letter))
                }
            )
        }

    val paths = mutableListOf<String>()

    paths.add(outputSummaryMetadata(outputDir, languageMetadata))
    paths.add(outputLanguagesList(outputDir, languageMetadata))

    Language.allLanguages.values.onEach { language ->
        paths.addAll(outputLanguageMetadata(language, languageMetadata[language]!!, dictDir, lettersDir, outputDir))
    }

    outputIndex(outputDir, paths)
}

fun outputIndex(outputDir: File, paths: List<String>) {
    val html = """
        <html>
            <head>
                <title>Lexica API</title>
            </head>
            <body>
                <ul>
                    ${paths.joinToString("\n") { """<li><a href="./$it">$it</a></li>""" }}
                </ul>
            </body>
        </html>
    """.trimIndent()

    File(outputDir, "index.html").writeText(html)
}

fun outputSummaryMetadata(outputDir: File, languageMetadata: Map<Language, LanguageMetadata>): String {
    println("Writing metadata.json for ${languageMetadata.size} languages")
    val metadataJson = Json.encodeToString(languageMetadata.values.toList())
    return writeApiFile(metadataJson, File("metadata.json"), outputDir)
}

fun outputLanguagesList(outputDir: File, languageMetadata: Map<Language, LanguageMetadata>): String {
    println("Writing languages.json for ${languageMetadata.size} languages")
    val json = Json.encodeToString(Language.allLanguages.values.map { it.name })
    return writeApiFile(json, File("languages.json"), outputDir)
}

fun readLetters(language: Language, lettersDir: File): Map<String, List<Int>> {
    val lettersFile = File(lettersDir, language.letterDistributionFileName)
    val distribution = CharProbGenerator(lettersFile.inputStream(), language)

    return distribution.distribution
}

fun outputLanguageMetadata(language: Language, metadata: LanguageMetadata, dictDir: File, lettersDir: File, outputDir: File): List<String> {
    val paths = mutableListOf<String>()

    println("Writing data for ${language.name}")

    val path = File("language/${language.name}")

    val metadataJson = Json.encodeToString(metadata)
    paths.add(writeApiFile(metadataJson, File(path, "metadata.json"), outputDir))

    val probabilitiesJson = Json.encodeToString(readLetters(language, lettersDir))
    paths.add(writeApiFile(probabilitiesJson, File(path, "probabilities.json"), outputDir))

    val lettersFile = File(lettersDir, language.letterDistributionFileName)
    paths.add(writeApiFile(lettersFile.readText(), File(path, "probabilities.txt"), outputDir))

    val dictionaryFile = File(dictDir, language.dictionaryFileName)
    val dictionaryWords = dictionaryFile.readLines()
    val dictionaryJson = Json.encodeToString(dictionaryWords)
    paths.add(writeApiFile(dictionaryJson, File(path, "dictionary.json"), outputDir))
    paths.add(writeApiFile(dictionaryFile.readText(), File(path, "dictionary.txt"), outputDir))

    // TODO: Output the trie in a JSON format also.

    return paths
}

fun writeApiFile(contents: String, filename: File, outputDir: File): String {
    val path = "api/v1/${filename.path}"

    val file = File(outputDir, path)

    println("Writing file to: $file")

    file.parentFile.mkdirs()
    file.writeText(contents)

    return path
}

@Serializable
data class LanguageMetadata(
    val name: String,
    val locale: String,
    val isBeta: Boolean,
    val definitionUrl: String,
    val letterProbabilities: Map<String, List<Int>>,
    val letterScores: Map<String, Int>,
)
