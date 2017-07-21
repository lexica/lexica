package com.serwylo.lexica;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.CharProbGenerator;
import com.serwylo.lexica.lang.Catalan;
import com.serwylo.lexica.lang.DeGerman;
import com.serwylo.lexica.lang.Dutch;
import com.serwylo.lexica.lang.Italian;
import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.lang.Persian;
import com.serwylo.lexica.lang.Spanish;
import com.serwylo.lexica.lang.UkEnglish;
import com.serwylo.lexica.lang.UsEnglish;
import com.serwylo.lexica.trie.util.LetterFrequency;

import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class LanguageExperiments {

    @Test
    public void stats() throws IOException {
        calcStats(new CharProbGeneratorGenerator.OnlySingle());
        System.out.println("");
        calcStats(new CharProbGeneratorGenerator.TotalCharCount());
    }

    private void calcStats(CharProbGeneratorGenerator generatorGenerator) throws IOException {
        gatherLanguageStats(generatorGenerator, new UsEnglish());
        gatherLanguageStats(generatorGenerator, new UkEnglish());
        gatherLanguageStats(generatorGenerator, new DeGerman());
        gatherLanguageStats(generatorGenerator, new Spanish());
        gatherLanguageStats(generatorGenerator, new Catalan());
        gatherLanguageStats(generatorGenerator, new Dutch());
        gatherLanguageStats(generatorGenerator, new Persian());
        gatherLanguageStats(generatorGenerator, new Italian());
    }

    private static void gatherLanguageStats(CharProbGeneratorGenerator generatorGenerator, Language language) throws IOException {
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 0; i < 1000; i ++) {
            CharProbGenerator prob = generatorGenerator.createCharProp(language);
            Board board = prob.generateFourByFourBoard();
            try {
                InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream(language.getTrieFileName());
                Trie dict = new StringTrie.Deserializer().deserialize(stream, board, language);
                int numWords = dict.solver(board, new WordFilter.MinLength(3)).size();
                stats.addValue(numWords);
            } catch(IOException ignored) { }
        }

        System.out.println(
                "Chars: " + generatorGenerator.getName() + ", " +
                        "Language: " + language.getName() + " " +
                        "[stddev: " + (int)stats.getStandardDeviation() + "] " +
                        "[min: " + (int)stats.getMin() + ", avg: " + (int)stats.getMean() + ", max: " + (int)stats.getMax() + "]");
    }

    abstract static class CharProbGeneratorGenerator {
        abstract String getName();
        abstract String fromLetters(LetterFrequency letters);

        CharProbGenerator createCharProp(Language language) throws IOException {
            InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream(language.getDictionaryFileName());
            BufferedReader br = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
            LetterFrequency letters = new LetterFrequency(language);

            String line;
            while((line = br.readLine()) != null) {
                String word = line.toLowerCase(language.getLocale());
                letters.addWord(word);
            }

            return new CharProbGenerator(new ByteArrayInputStream(fromLetters(letters).getBytes("UTF-8")));
        }

        static class OnlySingle extends CharProbGeneratorGenerator {

            @Override
            public String getName() {
                return "Count single character occurrences";
            }

            @Override
            String fromLetters(LetterFrequency letters) {
                return letters.toSingleLetterCountString();
            }

            @Override
            public CharProbGenerator createCharProp(Language language) {
                InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream(language.getLetterDistributionFileName());
                return new CharProbGenerator(stream);
            }
        }

        static class TotalCharCount extends CharProbGeneratorGenerator{

            @Override
            public String getName() {
                return "   Count all character occurrences";
            }

            @Override
            String fromLetters(LetterFrequency letters) {
                return letters.toTotalLetterCountString();
            }

            @Override
            public CharProbGenerator createCharProp(Language language) {
                InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream(language.getLetterDistributionFileName());
                return new CharProbGenerator(stream);
            }
        }
    }
}
