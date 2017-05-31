package com.serwylo.lexica;

import net.healeys.trie.Trie;

import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public abstract class TrieTest {

	private static final String[] NOT_WORDS = new String[] {
			"NotAWord",
			"DefinitelyNotAWord",
			"WellThisIsEmbarrassing",
			"Bleh",
			"Sneh"
	};

	static void assertTrieMatches(String message, Trie trie, String[] usWords, String[] ukWords, String[] bothDialects) {
		if (usWords != null) {
			for (String usWord : usWords) {
				String log = message + ": " + usWord + " [US]";
				Assert.assertTrue(log + " should be a US word", trie.isWord(usWord));
				Assert.assertTrue(log + " should be a US word", trie.isWord(usWord, true, false));

				// The word should not be considered a UK word.
				Assert.assertFalse(log + "should not be a UK word", trie.isWord(usWord, false, true));
				Assert.assertFalse(log + "should not be a UK word", trie.isWord(usWord, false, false));
			}
		}

		if (ukWords != null) {
			for (String ukWord : ukWords) {
				String log = message + ": " + ukWord + " [UK]";
				Assert.assertTrue(log + " should be a UK word", trie.isWord(ukWord));
				Assert.assertTrue(log + " should be a UK word", trie.isWord(ukWord, false, true));

				// The word should not be considered a US word.
				Assert.assertFalse(log + "should not be a US word", trie.isWord(ukWord, true, false));
				Assert.assertFalse(log + "should not be a US word", trie.isWord(ukWord, false, false));
			}
		}

		if (bothDialects != null) {
			for (String word : bothDialects) {
				String log = word + " [BOTH]";
				Assert.assertTrue(log + " should be a word", trie.isWord(word));
				Assert.assertTrue(log + " should be a word", trie.isWord(word, true, true));
				Assert.assertTrue(log + " should be considered a US word", trie.isWord(word, false, true));
				Assert.assertTrue(log + " should be considered a UK word", trie.isWord(word, true, false));
			}
		}

		for (String notAWord : NOT_WORDS) {
			String log = notAWord + " should not be a word";
			Assert.assertFalse(log, trie.isWord(notAWord));
			Assert.assertFalse(log, trie.isWord(notAWord, true, false));
			Assert.assertFalse(log, trie.isWord(notAWord, false, true));
			Assert.assertFalse(log, trie.isWord(notAWord, true, true));
			Assert.assertFalse(log, trie.isWord(notAWord, false, false));
		}
	}

	public static void addWords(Trie trie, String[] words, boolean isUs, boolean isUk) {
		for (String word : words) {
			trie.addWord(word, isUs, isUk);
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
