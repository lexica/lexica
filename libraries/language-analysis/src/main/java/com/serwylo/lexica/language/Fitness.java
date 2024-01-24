package com.serwylo.lexica.language;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.CharProbGenerator;
import com.serwylo.lexica.lang.Language;

import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Fitness {

    public static final int FITNESS_CALC_BOARDS_TO_GENERATE = 100;
    private final SummaryStatistics stats;

    public static Fitness calc(File trieDir, CharProbGenerator charProbGenerator, Language language) throws IOException {
        return calc(trieDir, charProbGenerator, language, FITNESS_CALC_BOARDS_TO_GENERATE);
    }

    public static Fitness calc(File trieDir, CharProbGenerator charProbGenerator, Language language, int work) throws IOException {
        return new Fitness(generateStats(trieDir, charProbGenerator, language, work));
    }

    private static Map<Language, byte[]> cachedTries = new HashMap<>();

    private static InputStream trieReader(File trieDir, Language language) throws IOException {
        if (!cachedTries.containsKey(language)) {
            byte[] buffer = new byte[1024 * 1024 * 10]; // 10MiB - Needs to be able to fit the largest "words_*.bin file in memory.

            File trieFile = new File(trieDir, language.getTrieFileName());
            InputStream stream = new FileInputStream(trieFile);
            int read = stream.read(buffer);
            byte[] total = new byte[read];
            System.arraycopy(buffer, 0, total, 0, read);
            cachedTries.put(language, total);
        }

        return new ByteArrayInputStream(cachedTries.get(language));
    }

    private static SummaryStatistics generateStats(File trieDir, CharProbGenerator charProbGenerator, Language language, int iterations) throws IOException {
        SummaryStatistics stats = new SummaryStatistics();
        for (int i = 0; i < iterations; i++) {
            Board board = new CharProbGenerator(charProbGenerator).generateFourByFourBoard();
            InputStream stream = trieReader(trieDir, language);
            Trie dict = new StringTrie.Deserializer().deserialize(stream, board, language);
            int numWords = dict.solver(board, new WordFilter.MinLength(3)).size();
            stats.addValue(numWords);
        }
        return stats;
    }

    Fitness(SummaryStatistics stats) {
        this.stats = stats;
    }

    private String cachedStringRepresentation = null;

    public String toString() {
        if (cachedStringRepresentation == null) {
            cachedStringRepresentation = "Min: " + (int) stats.getMin() + ", mean: " + (int) stats.getMean() + ", max: " + (int) stats.getMax() + ", stddev: " + (int) stats.getStandardDeviation();
        }

        return cachedStringRepresentation;
    }
}
