package com.serwylo.lexica.trie.tests;

import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.trie.util.LetterFrequency;
import com.sun.istack.internal.NotNull;

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
import static org.junit.Assert.assertTrue;

public abstract class TrieTest {

	private static final String[] NOT_WORDS = new String[] {
			"NotAWord",
			"DefinitelyNotAWord",
			"WellThisIsEmbarrassing",
			"Bleh",
			"Sneh"
	};

	private static void onlyContains(Language language, Trie trie, Set<String> expectedWords) {
		LetterFrequency frequency = new LetterFrequency(language);
		for (String word : expectedWords) {
			frequency.addWord(word);
		}

		Map<String, List<Solution>> solutions = trie.solver(new CanTransitionMap(frequency, language), new WordFilter() {
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

	static void assertTrieMatches(String message, Trie trie, String[] words, Language language) {
		Set<String> allWords = new HashSet<>();
		for (String word : words) {
			String log = message + ": ";
			word = word.toLowerCase(language.getLocale());
			allWords.add(word);
			assertTrue(log + word + " should be a word", trie.isWord(word));
		}

		onlyContains(language, trie, allWords);

		for (String notAWord : NOT_WORDS) {
			String log = notAWord + " should not be a word";
			notAWord = notAWord.toLowerCase();
			Assert.assertFalse(log, trie.isWord(notAWord));
		}
	}

	public static void addWords(Trie trie, String[] words) {
		for (String word : words) {
			trie.addWord(word.toLowerCase());
		}
	}

	@NotNull
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
