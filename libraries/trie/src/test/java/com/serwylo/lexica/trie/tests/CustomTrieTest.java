package com.serwylo.lexica.trie.tests;

import com.serwylo.lexica.lang.EnglishGB;
import com.serwylo.lexica.lang.EnglishUS;
import com.serwylo.lexica.lang.FrenchNoDiacritics;
import com.serwylo.lexica.lang.Persian;
import com.serwylo.lexica.trie.util.LetterFrequency;

import net.healeys.trie.Deserializer;
import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.fail;

public class CustomTrieTest extends TrieTest {

    private static final String[] GB_WORDS = new String[]{"queen", "MONARCH", "UnitedKingdom", "Commonwealth",};

    private static final String[] US_WORDS = new String[]{"president", "REPUBLIC", "America",};

    private static final String[] BOTH_DIALECTS = new String[]{"quit", "aqua", "a", "alibi", "LongerWordThanA"};

    @Test
    public void testAdding() {
        StringTrie usTrie = new StringTrie(new EnglishUS());
        StringTrie gbTrie = new StringTrie(new EnglishGB());

        addWords(usTrie, US_WORDS);
        addWords(usTrie, BOTH_DIALECTS);

        addWords(gbTrie, GB_WORDS);
        addWords(gbTrie, BOTH_DIALECTS);

        assertEverythingAboutTrie(usTrie, gbTrie, new StringTrie.Deserializer());
    }

    @Test
    public void testSuffixes() {
        String[] englishWords = {
                "without", // No "q" or "qu" to deal with.
                "queen", // "qu" present, but only at the beginning.
        };

        String[] frenchWords = {
                "bonjour", // No "q" or "qu" to deal with.
                "que", // As with English and others, "qu" only at the beginning.
                "jusqu", // Unlike English, this ends with "qu" which caused some issues with the trie handling initially.
        };

        StringTrie englishTrie = new StringTrie(new EnglishGB());
        StringTrie frenchTrie = new StringTrie(new FrenchNoDiacritics());

        addWords(englishTrie, englishWords);
        addWords(frenchTrie, frenchWords);

        assertTrieMatches("English qu sufix works as expected", englishTrie, englishWords, new EnglishGB());
        assertTrieMatches("French qu sufix works as expected, including at end of word", frenchTrie, frenchWords, new FrenchNoDiacritics());
    }

    @Test
    public void testPersian() throws IOException {
        String[] words = new String[]{"آیی", "اغوایم", "وزشها", "وزیدنیمان",};

        LetterFrequency letters = new LetterFrequency(new Persian());
        StringTrie trie = new StringTrie(new Persian());

        for (String word : words) {
            trie.addWord(word);
            letters.addWord(word);
        }

        assertTrieMatches("Before desrializing", trie, words, new Persian());

        byte[] serialized = serialize(trie);

        Trie deserializedAll = new StringTrie.Deserializer().deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(letters, new Persian()), new Persian());
        assertTrieMatches("After deserializing all words", deserializedAll, words, new Persian());

        String[] subsetOfLetters = new String[]{"آ", "م", "ا", "و", "غ", "ی"};
        String[] subsetWords = new String[]{"آیی", "اغوایم",};

        Trie deserializedSubsetWords = new StringTrie.Deserializer().deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(subsetOfLetters), new Persian());
        assertTrieMatches("After desrializing only a subset of words from the letters آ م ا و غ ی", deserializedSubsetWords, subsetWords, new Persian());
    }

    private static String[] join(String[] one, String[] two) {
        String[] joined = new String[one.length + two.length];
        System.arraycopy(one, 0, joined, 0, one.length);
        System.arraycopy(two, 0, joined, one.length, two.length);
        return joined;
    }

    // "aeinqt" => "a" (all), "quit" (all), "aqua" (all), "queen" (gb)
    // "abcehilmnor" => "america" (us), "monarch" (gb), "a" (all), "alibi" (all)
    private static <T extends Trie> void assertEverythingAboutTrie(T usTrie, T gbTrie, Deserializer<T> deserializer) {
        try {
            assertTrieMatches("Before desrializing US", usTrie, join(US_WORDS, BOTH_DIALECTS), new EnglishUS());
            assertTrieMatches("Before desrializing GB", gbTrie, join(GB_WORDS, BOTH_DIALECTS), new EnglishGB());

            byte[] serializedUs = serialize(usTrie);
            byte[] serializedGb = serialize(gbTrie);

            Trie deserializedAllUs = deserializer.deserialize(new ByteArrayInputStream(serializedUs), new CanTransitionMap(), new EnglishUS());
            Trie deserializedAllGb = deserializer.deserialize(new ByteArrayInputStream(serializedGb), new CanTransitionMap(), new EnglishGB());
            assertTrieMatches("After deserializing all US words", deserializedAllUs, join(US_WORDS, BOTH_DIALECTS), new EnglishUS());
            assertTrieMatches("After deserializing all GB words", deserializedAllGb, join(GB_WORDS, BOTH_DIALECTS), new EnglishGB());

            String[] aeinqt = new String[]{"a", "e", "i", "n", "qu", "t"};
            String[] aeinqtUsWords = new String[]{};
            String[] aeinqtGbWords = new String[]{"queen"};
            String[] aeinqtBothWords = new String[]{"a", "quit", "aqua"};

            Trie deserializedAeinqtUs = deserializer.deserialize(new ByteArrayInputStream(serializedUs), new CanTransitionMap(aeinqt), new EnglishUS());
            Trie deserializedAeinqtGb = deserializer.deserialize(new ByteArrayInputStream(serializedGb), new CanTransitionMap(aeinqt), new EnglishGB());
            assertTrieMatches("After desrializing only a subset of words from the letters AEINQT in US", deserializedAeinqtUs, join(aeinqtUsWords, aeinqtBothWords), new EnglishUS());
            assertTrieMatches("After desrializing only a subset of words from the letters AEINQT in GB", deserializedAeinqtGb, join(aeinqtGbWords, aeinqtBothWords), new EnglishGB());

            String[] abcehilmnor = new String[]{"a", "b", "c", "e", "h", "i", "l", "m", "n", "o", "r"};
            String[] abcehilmnorUsWords = new String[]{"america"};
            String[] abcehilmnorGbWords = new String[]{"monarch"};
            String[] abcehilmnorBothWords = new String[]{"a", "alibi"};

            Trie deserializedAbcehilmnorUs = deserializer.deserialize(new ByteArrayInputStream(serializedUs), new CanTransitionMap(abcehilmnor), new EnglishUS());
            Trie deserializedAbcehilmnorGb = deserializer.deserialize(new ByteArrayInputStream(serializedGb), new CanTransitionMap(abcehilmnor), new EnglishGB());
            assertTrieMatches("After desrializing only a subset of words from the letters ABCEHILMNOR in US", deserializedAbcehilmnorUs, join(abcehilmnorUsWords, abcehilmnorBothWords), new EnglishUS());
            assertTrieMatches("After desrializing only a subset of words from the letters ABCEHILMNOR in GB", deserializedAbcehilmnorGb, join(abcehilmnorGbWords, abcehilmnorBothWords), new EnglishGB());
        } catch (IOException e) {
            fail("Error while deserializing trie: " + e.getMessage());
        }
    }

}
