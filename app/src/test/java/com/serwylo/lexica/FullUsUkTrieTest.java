package com.serwylo.lexica;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.FourByFourBoard;

import net.healeys.trie.Solution;
import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FullUsUkTrieTest extends TrieTest {

	private static final Board BOARD = new FourByFourBoard(new String[] {
			"r", "qu", "o", "s",
			"w", "n", "o", "a",
			"t", "v", "d", "g",
			"n", "p", "u", "i",
	});

	private static final String[] WORDS = new String[] {
			"quod", "ono", "son", "soon", "sod", "soda", "sad", "sadi", "sag", "sago", "nod", "nodi",
			"nog", "ado", "ago", "agon", "dos", "don", "dog", "dag", "dago", "dagos", "dug", "dig",
			"goo", "goon", "gos", "goa", "goad", "gov", "god", "gas", "gad", "guv", "guidon", "gid",
			"pud", "pug", "udo", "updo",
	};

	@Test
	public void testPerformanceLoadingTries() throws IOException {
		long startTime = System.currentTimeMillis();
		InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream("words.bin");
		Trie trie =new StringTrie.Deserializer().deserialize(stream, BOARD, true, true);assertEquals(41, trie.solver(BOARD, new WordFilter.MinLength(3)).size());
		long totalTime = (System.currentTimeMillis() - startTime);

		assertTrieCorrect(trie);
		// fail("Took " + totalTime + "ms");
	}

	private static void assertTrieCorrect(Trie trie) {
		LinkedHashMap<String, Solution> solutions = trie.solver(BOARD, new WordFilter.MinLength(3));
		List<String> expectedWords = new ArrayList<>();
		for (String w : WORDS) {
			expectedWords.add(w);
		}

		List<String> actualWords = new ArrayList<>();
		for (String w : solutions.keySet()) {
			actualWords.add(w);
		}

		Collections.sort(expectedWords);
		Collections.sort(actualWords);

		assertEquals(expectedWords, actualWords);
	}

	@Test
	public void testSolverPerformance() throws IOException {
			InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream("words.bin");
			Trie trie =new StringTrie.Deserializer().deserialize(stream, BOARD, true, true);assertEquals(41, trie.solver(BOARD, new WordFilter.MinLength(3)).size());
		long startTime = System.currentTimeMillis();
		trie.solver(BOARD, new WordFilter.MinLength(3));
		long totalTime = (System.currentTimeMillis() - startTime) ;
		fail("Took " + totalTime + "ms");
	}

	@Test
	public void testStringTrieUsDictionary() {
		testUsDictionary(new StringTrie());
	}

	private void testUsDictionary(Trie trie) {
		String[] words = readDictionary("us.txt");
		assertEquals(77517, words.length);

		addWords(trie, words, true, false);

		assertTrieMatches("After adding entire US dictionary to a new Trie", trie, words, null, null);
	}

	@Test
	public void testStringTrieUkDictionary() {
		testUkDictionary(new StringTrie());
	}

	private void testUkDictionary(Trie trie) {
		String[] words = readDictionary("uk.txt");
		assertEquals(77097, words.length);

		addWords(trie, words, true, false);

		assertTrieMatches("After adding entire UK dictionary to a new Trie", trie, words, null, null);
	}

	static String[] readDictionary(String fileName) {
		try {
			List<String> words = new ArrayList<>(80000);
			InputStream stream = FullUsUkTrieTest.class.getClassLoader().getResourceAsStream(fileName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = reader.readLine();
			while (line != null) {
				words.add(line);
				line = reader.readLine();
			}
			String[] wordsArray = new String[words.size()];
			words.toArray(wordsArray);
			return wordsArray;
		} catch (IOException e) {
			fail();
			throw new RuntimeException(e);
		}
	}

}
