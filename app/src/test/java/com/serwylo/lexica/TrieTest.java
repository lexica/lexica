package com.serwylo.lexica;

import net.healeys.trie.Trie;

import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class TrieTest {

    private static final String[] NOT_WORDS = new String[]{"NotAWord", "DefinitelyNotAWord", "WellThisIsEmbarrassing", "Bleh", "Sneh"};

    static void assertTrieMatches(String message, Trie trie, String[] words, String[] notWords) {
        String log = message + ": ";
        for (String word : words) {
            Assert.assertTrue(log + word + " should be a word", trie.isWord(word));
        }

        if (notWords != null) {
            for (String notAWord : notWords) {
                Assert.assertFalse(log + notAWord + " should not be a word", trie.isWord(notAWord));
            }
        }

        for (String notAWord : NOT_WORDS) {
            Assert.assertFalse(log + notAWord + " should not be a word", trie.isWord(notAWord));
        }
    }

    public static void addWords(Trie trie, String[] words) {
        for (String word : words) {
            trie.addWord(word);
        }
    }

    public static byte[] serialize(Trie trie) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            trie.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            Assert.fail();
            return null;
        }
    }

}
