package com.serwylo.lexica.trie.tests;

import net.healeys.trie.CompressedTrie;
import net.healeys.trie.Trie;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CustomUsUkTrieTest {

	private static final String[] UK_WORDS = new String[] {
			"queen",
			"MONARCH",
			"UnitedKingdom",
			"Commonwealth",
	};

	private static final String[] US_WORDS = new String[] {
			"president",
			"REPUBLIC",
			"America",
	};

	private static final String[] BOTH_DIALECTS = new String[] {
			"quit",
			"aqua",
			"a",
			"alibi",
			"LongerWordThanA"
	};

	private static final String[] NOT_WORDS = new String[] {
			"NotAWord",
			"DefinitelyNotAWord",
			"WellThisIsEmbarrassing",
			"Bleh",
			"Sneh"
	};

	@Test
	public void testAddingSeparately() {
		Trie trie = new Trie();

		addWords(trie, US_WORDS, true, false);
		addWords(trie, BOTH_DIALECTS, true, false);

		addWords(trie, UK_WORDS, false, true);
		addWords(trie, BOTH_DIALECTS, false, true);

		assertEverythingAboutTrie(trie);
	}

	@Test
	public void testAddingTogether() {
		Trie trie = new Trie();

		addWords(trie, US_WORDS, true, false);
		addWords(trie, UK_WORDS, false, true);
		addWords(trie, BOTH_DIALECTS, true, true);

		assertEverythingAboutTrie(trie);
	}

	@Test
	public void testLetterMask() {
		int actualMaskAbc = calcLetterMask("abc");
		int actualMaskCde = calcLetterMask("CdE");
		int actualMaskAllLower = calcLetterMask("abcdefghijklmnopqrstuvwxyz");
		int actualMaskAllUpper = calcLetterMask("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
		int actualMaskAllMixed = calcLetterMask("aBcDeFgHiJkLmNoPqRsTuVwXyZ");

		// TODO: Does "Qu" need any special treatment here? I suspect not.

		int expectedMaskAbc = (1 << 0) | (1 << 1) | (1 << 2);
		int expectedMaskCde = (1 << 2) | (1 << 3) | (1 << 4);
		int expectedMaskAll =
				(1 << 0) | (1 << 1) | (1 << 2) | (1 << 3) | (1 << 4) |
				(1 << 5) | (1 << 6) | (1 << 7) | (1 << 8) | (1 << 9) |
				(1 << 10) | (1 << 11) | (1 << 12) | (1 << 13) | (1 << 14) |
				(1 << 15) | (1 << 16) | (1 << 17) | (1 << 18) | (1 << 19) |
				(1 << 20) | (1 << 21) | (1 << 22) | (1 << 23) | (1 << 24) |
				(1 << 25);

		Assert.assertEquals(expectedMaskAbc, actualMaskAbc);
		Assert.assertEquals(expectedMaskCde, actualMaskCde);
		Assert.assertEquals(expectedMaskAll, actualMaskAllLower);
		Assert.assertEquals(expectedMaskAll, actualMaskAllUpper);
		Assert.assertEquals(expectedMaskAll, actualMaskAllMixed);
	}

	// "aeinqt" => "a" (all), "quit" (all), "aqua" (all), "queen" (uk)
	// "abcehilmnor" => "america" (us), "monarch" (uk), "a" (all), "alibi" (all)
	private static void assertEverythingAboutTrie(Trie trie) {
		assertTrieMatches(trie, US_WORDS, UK_WORDS, BOTH_DIALECTS);

		byte[] serialized = serialize(trie);

		Trie deserializedAll = deserialize(serialized);
		assertTrieMatches(deserializedAll, US_WORDS, UK_WORDS, BOTH_DIALECTS);

		String aeinqt = "aeinqt";
		String[] aeinqtUsWords = new String[] {};
		String[] aeinqtUkWords = new String[] { "queen" };
		String[] aeinqtBothWords = new String[] { "a", "quit", "aqua" };

		Trie deserializedAeinqt = deserialize(serialized, aeinqt);
		assertTrieMatches(deserializedAeinqt, aeinqtUsWords, aeinqtUkWords, aeinqtBothWords);

		String abcehilmnor = "abcehilmnor";
		String[] abcehilmnorUsWords = new String[] { "america" };
		String[] abcehilmnorUkWords = new String[] { "monarch" };
		String[] abcehilmnorBothWords = new String[] { "a", "alibi" };

		Trie deserializedAbcehilmnor = deserialize(serialized, abcehilmnor);
		assertTrieMatches(deserializedAbcehilmnor, abcehilmnorUsWords, abcehilmnorUkWords, abcehilmnorBothWords);
	}

	private static void assertTrieMatches(Trie trie, String[] usWords, String[] ukWords, String[] bothDialects) {
		for (String usWord : usWords) {
			String log = usWord + " [US]";
			Assert.assertTrue(log + " should be a US word", trie.isWord(usWord));
			Assert.assertTrue(log + " should be a US is word", trie.isWord(usWord, true, false));

			// The word should not be considered a UK word.
			Assert.assertFalse(log + "should not be a UK word", trie.isWord(usWord, false, true));
			Assert.assertFalse(log + "should not be a UK word", trie.isWord(usWord, false, false));
		}

		for (String ukWord : ukWords) {
			String log = ukWord + " [UK]";
			Assert.assertTrue(log + " should be a UK word", trie.isWord(ukWord));
			Assert.assertTrue(log + " should be a UK word", trie.isWord(ukWord, false, true));

			// The word should not be considered a US word.
			Assert.assertFalse(log + "should not be a US word", trie.isWord(ukWord, true, false));
			Assert.assertFalse(log + "should not be a US word", trie.isWord(ukWord, false, false));
		}

		for (String word : bothDialects) {
			String log = word + " [BOTH]";
			Assert.assertTrue(log + " should be a word", trie.isWord(word));
			Assert.assertTrue(log + " should be a word", trie.isWord(word, true, true));
			Assert.assertTrue(log + " should be considered a US word", trie.isWord(word, false, true));
			Assert.assertTrue(log + " should be considered a UK word", trie.isWord(word, true, false));
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

	private static void addWords(Trie trie, String[] words, boolean isUs, boolean isUk) {
		for (String word : words) {
			trie.addWord(word, isUs, isUk);
		}
	}

	private static byte[] serialize(Trie trie) {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			trie.write(outputStream);
			return outputStream.toByteArray();
		} catch (IOException e) {
			Assert.fail();
			return null;
		}
	}

	private static CompressedTrie deserialize(byte[] bytes) {
		return deserialize(bytes, "abcdefghijklmnopqrstuvwxyz");
	}

	private static CompressedTrie deserialize(byte[] bytes, String availableLetters) {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
			int letterMask = calcLetterMask(availableLetters);
			int[] neighborMasks = calcNeighbourMasks();

			return new CompressedTrie(inputStream, letterMask, neighborMasks, true, true);
		} catch (IOException e) {
			Assert.fail();
			return null;
		}
	}

	/**
	 * The way {@link CompressedTrie}s are stored in memory after being read from disk is to
	 * only include words that can be made from the letters available.
	 */
	private static int calcLetterMask(String availableLetters) {
		int mask = 0;
		for (int i = 0; i < availableLetters.length(); i ++) {
			char character = availableLetters.charAt(i);
			mask |= 1 << Trie.ctoi(character);
		}
		return mask;
	}

	// TODO: Be more specific about valid transitions in future tests.
	private static int[] calcNeighbourMasks() {
		int[] neighbourMasks = new int[26];
		for (int i = 0; i < 26; i ++) {
			neighbourMasks[i] = 0xffffffff;
		}
		return neighbourMasks;
	}

}
