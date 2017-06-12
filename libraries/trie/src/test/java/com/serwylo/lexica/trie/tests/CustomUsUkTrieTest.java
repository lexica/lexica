package com.serwylo.lexica.trie.tests;

import net.healeys.trie.Deserializer;
import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.Assert.fail;

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
		StringTrie trie = new StringTrie();

		addWords(trie, US_WORDS, true, false);
		addWords(trie, BOTH_DIALECTS, true, false);

		addWords(trie, UK_WORDS, false, true);
		addWords(trie, BOTH_DIALECTS, false, true);

		assertEverythingAboutTrie(trie, new StringTrie.Deserializer());
	}

	@Test
	public void testAddingTogether() {
		StringTrie trie = new StringTrie();

		addWords(trie, US_WORDS, true, false);
		addWords(trie, UK_WORDS, false, true);
		addWords(trie, BOTH_DIALECTS, true, true);

		assertEverythingAboutTrie(trie, new StringTrie.Deserializer());
	}

	// "aeinqt" => "a" (all), "quit" (all), "aqua" (all), "queen" (uk)
	// "abcehilmnor" => "america" (us), "monarch" (uk), "a" (all), "alibi" (all)
	private static <T extends Trie> void assertEverythingAboutTrie(T trie, Deserializer<T> deserializer) {
		try {
			assertTrieMatches("Before desrializing", trie, US_WORDS, UK_WORDS, BOTH_DIALECTS);

			byte[] serialized = serialize(trie);

			Trie deserializedAll = deserializer.deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(), true, true);
			assertTrieMatches("After deserializing all words", deserializedAll, US_WORDS, UK_WORDS, BOTH_DIALECTS);

			String[] aeinqt = new String[]{"a", "e", "i", "n", "qu", "t"};
			String[] aeinqtUsWords = new String[]{};
			String[] aeinqtUkWords = new String[]{"queen"};
			String[] aeinqtBothWords = new String[]{"a", "quit", "aqua"};

			Trie deserializedAeinqt = deserializer.deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(aeinqt), true, true);
			assertTrieMatches("After desrializing only a subset of words from the letters AEINQT", deserializedAeinqt, aeinqtUsWords, aeinqtUkWords, aeinqtBothWords);

			Trie deserializedAeinqtUs = deserializer.deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(aeinqt), true, false);
			assertTrieMatches("After desrializing only a subset of US words from the letters AEINQT", deserializedAeinqtUs, aeinqtUsWords, null, aeinqtBothWords);

			Trie deserializedAeinqtUk = deserializer.deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(aeinqt), false, true);
			assertTrieMatches("After desrializing only a subset of UK words from the letters AEINQT", deserializedAeinqtUk, null, aeinqtUkWords, aeinqtBothWords);

			String[] abcehilmnor = new String[]{"a", "b", "c", "e", "h", "i", "l", "m", "n", "o", "r"};
			String[] abcehilmnorUsWords = new String[]{"america"};
			String[] abcehilmnorUkWords = new String[]{"monarch"};
			String[] abcehilmnorBothWords = new String[]{"a", "alibi"};

			Trie deserializedAbcehilmnor = deserializer.deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(abcehilmnor), true, true);
			assertTrieMatches("After desrializing only a subset of words from the letters ABCEHILMNOR", deserializedAbcehilmnor, abcehilmnorUsWords, abcehilmnorUkWords, abcehilmnorBothWords);

			Trie deserializedAbcehilmnorUs = deserializer.deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(abcehilmnor), true, false);
			assertTrieMatches("After desrializing only a subset of US words from the letters ABCEHILMNOR", deserializedAbcehilmnorUs, abcehilmnorUsWords, null, abcehilmnorBothWords);

			Trie deserializedAbcehilmnorUk = deserializer.deserialize(new ByteArrayInputStream(serialized), new CanTransitionMap(abcehilmnor), false, true);
			assertTrieMatches("After desrializing only a subset of UK words from the letters ABCEHILMNOR", deserializedAbcehilmnorUk, null, abcehilmnorUkWords, abcehilmnorBothWords);
		} catch (IOException e) {
			fail("Error while deserializing trie: " + e.getMessage());
		}
	}

}
