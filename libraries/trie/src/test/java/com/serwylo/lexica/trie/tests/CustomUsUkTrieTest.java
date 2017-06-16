package com.serwylo.lexica.trie.tests;

import com.serwylo.lexica.lang.UkEnglish;
import com.serwylo.lexica.lang.UsEnglish;

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
	public void testAdding() {
		StringTrie usTrie = new StringTrie(new UsEnglish());
		StringTrie ukTrie = new StringTrie(new UkEnglish());

		addWords(usTrie, US_WORDS);
		addWords(usTrie, BOTH_DIALECTS);

		addWords(ukTrie, UK_WORDS);
		addWords(ukTrie, BOTH_DIALECTS);

		assertEverythingAboutTrie(usTrie, ukTrie, new StringTrie.Deserializer());
	}

	private static String[] join(String[] one, String[] two) {
		String[] joined = new String[one.length + two.length];
		System.arraycopy(one, 0, joined, 0, one.length);
		System.arraycopy(two, 0, joined, one.length, two.length);
		return joined;
	}

	// "aeinqt" => "a" (all), "quit" (all), "aqua" (all), "queen" (uk)
	// "abcehilmnor" => "america" (us), "monarch" (uk), "a" (all), "alibi" (all)
	private static <T extends Trie> void assertEverythingAboutTrie(T usTrie, T ukTrie, Deserializer<T> deserializer) {
		try {
			assertTrieMatches("Before desrializing US", usTrie, join(US_WORDS, BOTH_DIALECTS));
			assertTrieMatches("Before desrializing UK", ukTrie, join(UK_WORDS, BOTH_DIALECTS));

			byte[] serializedUs = serialize(usTrie);
			byte[] serializedUk = serialize(ukTrie);

			Trie deserializedAllUs = deserializer.deserialize(new ByteArrayInputStream(serializedUs), new CanTransitionMap(), new UsEnglish());
			Trie deserializedAllUk = deserializer.deserialize(new ByteArrayInputStream(serializedUk), new CanTransitionMap(), new UkEnglish());
			assertTrieMatches("After deserializing all US words", deserializedAllUs, join(US_WORDS, BOTH_DIALECTS));
			assertTrieMatches("After deserializing all UK words", deserializedAllUk, join(UK_WORDS, BOTH_DIALECTS));

			String[] aeinqt = new String[]{"a", "e", "i", "n", "qu", "t"};
			String[] aeinqtUsWords = new String[]{};
			String[] aeinqtUkWords = new String[]{"queen"};
			String[] aeinqtBothWords = new String[]{"a", "quit", "aqua"};

			Trie deserializedAeinqtUs = deserializer.deserialize(new ByteArrayInputStream(serializedUs), new CanTransitionMap(aeinqt), new UsEnglish());
			Trie deserializedAeinqtUk = deserializer.deserialize(new ByteArrayInputStream(serializedUk), new CanTransitionMap(aeinqt), new UkEnglish());
			assertTrieMatches("After desrializing only a subset of words from the letters AEINQT in US", deserializedAeinqtUs, join(aeinqtUsWords, aeinqtBothWords));
			assertTrieMatches("After desrializing only a subset of words from the letters AEINQT in UK", deserializedAeinqtUk, join(aeinqtUkWords, aeinqtBothWords));

			String[] abcehilmnor = new String[]{"a", "b", "c", "e", "h", "i", "l", "m", "n", "o", "r"};
			String[] abcehilmnorUsWords = new String[]{"america"};
			String[] abcehilmnorUkWords = new String[]{"monarch"};
			String[] abcehilmnorBothWords = new String[]{"a", "alibi"};

			Trie deserializedAbcehilmnorUs = deserializer.deserialize(new ByteArrayInputStream(serializedUs), new CanTransitionMap(abcehilmnor), new UsEnglish());
			Trie deserializedAbcehilmnorUk = deserializer.deserialize(new ByteArrayInputStream(serializedUk), new CanTransitionMap(abcehilmnor), new UkEnglish());
			assertTrieMatches("After desrializing only a subset of words from the letters ABCEHILMNOR in US", deserializedAbcehilmnorUs, join(abcehilmnorUsWords, abcehilmnorBothWords));
			assertTrieMatches("After desrializing only a subset of words from the letters ABCEHILMNOR in UK", deserializedAbcehilmnorUk, join(abcehilmnorUkWords, abcehilmnorBothWords));
		} catch (IOException e) {
			fail("Error while deserializing trie: " + e.getMessage());
		}
	}

}
