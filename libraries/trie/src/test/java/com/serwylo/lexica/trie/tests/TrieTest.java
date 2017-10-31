package com.serwylo.lexica.trie.tests;

import net.healeys.trie.Solution;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import org.junit.Assert;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class TrieTest {

	private static final String[] NOT_WORDS = new String[] {
			"NotAWord",
			"DefinitelyNotAWord",
			"WellThisIsEmbarrassing",
			"Bleh",
			"Sneh"
	};

	private static void onlyContains(Trie trie, Set<String> expectedWords) {
		Map<String, Solution> solutions = trie.solver(new CanTransitionMap(), new WordFilter() {
			@Override
			public boolean isWord(String word) {
				return true;
			}
		});

		List<String> expected = new ArrayList<>(expectedWords);
		List<String> actual = new ArrayList<>(solutions.keySet());

		Set<String> expectedSet = new HashSet<>(expected);
		Set<String> actualSet = new HashSet<>(actual);
		expectedSet.removeAll(actualSet);

		// Special case for words which are legitimate words in the dictionary file, but which
		// are not eligible to be part of this game due to violating the "q is followed by u" rule.
		for (String word : expectedSet) {
			if (word.contains("q") && !word.contains("qu")) {
				expected.remove(word);
			}
		}

		Collections.sort(expected);
		Collections.sort(actual);

		assertArrayEquals("Words don't match", expected.toArray(), actual.toArray());
	}

	static void assertTrieMatches(String message, Trie trie, String[] usWords, String[] ukWords, String[] bothDialects) {
		Set<String> allWords = new HashSet<>();
		if (usWords != null) {
			for (String usWord : usWords) {
				String log = message + ": " + usWord + " [US]";
				usWord = usWord.toLowerCase();
				allWords.add(usWord);
				assertTrue(log + " should be a US word", trie.isWord(usWord));
				assertTrue(log + " should be a US word", trie.isWord(usWord, true, false));

				// The word should not be considered a UK word.
				Assert.assertFalse(log + "should not be a UK word", trie.isWord(usWord, false, true));
				Assert.assertFalse(log + "should not be a UK word", trie.isWord(usWord, false, false));
			}
		}

		if (ukWords != null) {
			for (String ukWord : ukWords) {
				String log = message + ": " + ukWord + " [UK]";
				ukWord = ukWord.toLowerCase();
				allWords.add(ukWord);
				assertTrue(log + " should be a UK word", trie.isWord(ukWord));
				assertTrue(log + " should be a UK word", trie.isWord(ukWord, false, true));

				// The word should not be considered a US word.
				Assert.assertFalse(log + "should not be a US word", trie.isWord(ukWord, true, false));
				Assert.assertFalse(log + "should not be a US word", trie.isWord(ukWord, false, false));
			}
		}

		if (bothDialects != null) {
			for (String word : bothDialects) {
				String log = word + " [BOTH]";
				word = word.toLowerCase();
				allWords.add(word);
				assertTrue(log + " should be a word", trie.isWord(word));
				assertTrue(log + " should be a word", trie.isWord(word, true, true));
				assertTrue(log + " should be considered a US word", trie.isWord(word, false, true));
				assertTrue(log + " should be considered a UK word", trie.isWord(word, true, false));
			}
		}

		onlyContains(trie, allWords);

		for (String notAWord : NOT_WORDS) {
			String log = notAWord + " should not be a word";
			notAWord = notAWord.toLowerCase();
			Assert.assertFalse(log, trie.isWord(notAWord));
			Assert.assertFalse(log, trie.isWord(notAWord, true, false));
			Assert.assertFalse(log, trie.isWord(notAWord, false, true));
			Assert.assertFalse(log, trie.isWord(notAWord, true, true));
			Assert.assertFalse(log, trie.isWord(notAWord, false, false));
		}
	}

	public static void addWords(Trie trie, String[] words, boolean isUs, boolean isUk) {
		for (String word : words) {
			trie.addWord(word.toLowerCase(), isUs, isUk);
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
