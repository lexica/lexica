package com.serwylo.lexica.trie.tests;

import net.healeys.trie.Trie;

import org.junit.Test;

public class CustomUsUkTrieTest extends TrieTest {

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

}
