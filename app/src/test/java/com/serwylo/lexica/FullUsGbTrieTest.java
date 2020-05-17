package com.serwylo.lexica;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.FourByFourBoard;
import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.lang.EnglishGB;
import com.serwylo.lexica.lang.EnglishUS;

import net.healeys.trie.Solution;
import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import org.junit.Ignore;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class FullUsGbTrieTest extends TrieTest {

	private static final Board BOARD = new FourByFourBoard(new String[] {
			"r", "qu", "o", "s",
			"w", "n", "o", "a",
			"t", "v", "d", "g",
			"n", "p", "u", "i",
	});

	private static final LinkedHashMap<String, Solution> SOLUTIONS = new LinkedHashMap<>();

	static {
		addSolution("quod", xy(1, 0), xy(2, 1), xy(2, 2));
		addSolution("ono", xy(2, 0), xy(1, 1), xy(2, 1));
		addSolution("son", xy(3, 0), xy(2, 0), xy(1, 1));
		addSolution("soon", xy(3, 0), xy(2, 0), xy(2, 1), xy(1, 1));
		addSolution("sod", xy(3, 0), xy(2, 1), xy(2, 2));
		addSolution("soda", xy(3, 0), xy(2, 1), xy(2, 2), xy(3, 1));
		addSolution("sad", xy(3, 0), xy(3, 1), xy(2, 2));
		addSolution("sadi", xy(3, 0), xy(3, 1), xy(2, 2), xy(3, 3));
		addSolution("sag", xy(3, 0), xy(3, 1), xy(3, 2));
		addSolution("sago", xy(3, 0), xy(3, 1), xy(3, 2), xy(2, 1));
		addSolution("nod", xy(1, 1), xy(2, 1), xy(2, 2));
		addSolution("nodi", xy(1, 1), xy(2, 1), xy(2, 2), xy(3, 3));
		addSolution("nog", xy(1, 1), xy(2, 1), xy(3, 2));
		addSolution("ado", xy(3, 1), xy(2, 2), xy(2, 1));
		addSolution("ago", xy(3, 1), xy(3, 2), xy(2, 1));
		addSolution("agon", xy(3, 1), xy(3, 2), xy(2, 1), xy(1, 1));
		addSolution("dos", xy(2, 2), xy(2, 1), xy(3, 0));
		addSolution("don", xy(2, 2), xy(2, 1), xy(1, 1));
		addSolution("dog", xy(2, 2), xy(2, 1), xy(3, 2));
		addSolution("dag", xy(2, 2), xy(3, 1), xy(3, 2));
		addSolution("dago", xy(2, 2), xy(3, 1), xy(3, 2), xy(2, 1));
		addSolution("dagos", xy(2, 2), xy(3, 1), xy(3, 2), xy(2, 1), xy(3, 0));
		addSolution("dug", xy(2, 2), xy(2, 3), xy(3, 2));
		addSolution("dig", xy(2, 2), xy(3, 3), xy(3, 2));
		addSolution("goo", xy(3, 2), xy(2, 1), xy(2, 0));
		addSolution("goon", xy(3, 2), xy(2, 1), xy(2, 0), xy(1, 1));
		addSolution("gos", xy(3, 2), xy(2, 1), xy(3, 0));
		addSolution("goa", xy(3, 2), xy(2, 1), xy(3, 1));
		addSolution("goad", xy(3, 2), xy(2, 1), xy(3, 1), xy(2, 2));
		addSolution("gov", xy(3, 2), xy(2, 1), xy(1, 2));
		addSolution("god", xy(3, 2), xy(2, 1), xy(2, 2));
		addSolution("gas", xy(3, 2), xy(3, 1), xy(3, 0));
		addSolution("gad", xy(3, 2), xy(3, 1), xy(2, 2));
		addSolution("guv", xy(3, 2), xy(2, 3), xy(1, 2));
		addSolution("guidon", xy(3, 2), xy(2, 3), xy(3, 3), xy(2, 2), xy(2, 1), xy(1, 1));
		addSolution("gid", xy(3, 2), xy(3, 3), xy(2, 2));
		addSolution("pud", xy(1, 3), xy(2, 3), xy(2, 2));
		addSolution("pug", xy(1, 3), xy(2, 3), xy(3, 2));
		addSolution("udo", xy(2, 3), xy(2, 2), xy(2, 1));
		addSolution("updo", xy(2, 3), xy(1, 3), xy(2, 2), xy(2, 1));

	}

	private static final String[] WORDS = new String[] {
			"quod", "ono", "son", "soon", "sod", "soda", "sad", "sadi", "sag", "sago", "nod", "nodi",
			"nog", "ado", "ago", "agon", "dos", "don", "dog", "dag", "dago", "dagos", "dug", "dig",
			"goo", "goon", "gos", "goa", "goad", "gov", "god", "gas", "gad", "guv", "guidon", "gid",
			"pud", "pug", "udo", "updo",
	};

	private static int xy(int x, int y) {
		return TransitionMapTest.xy(x, y);
	}

	private static void addSolution(String word, Integer ...positions) {
		SOLUTIONS.put(word, new StringTrie.StringSolution(word, positions));
	}

	@Test
	public void testLoadingCompressedTries() throws IOException {
		Language language = new EnglishUS();
		InputStream stream = FullUsGbTrieTest.class.getClassLoader().getResourceAsStream(language.getTrieFileName());
		Trie trie = new StringTrie.Deserializer().deserialize(stream, BOARD, language);
		assertTrieCorrect(trie);
	}

	private static void assertTrieCorrect(Trie trie) {
		Map<String, List<Solution>> solutions = trie.solver(BOARD, new WordFilter.MinLength(3));
		List<String> expectedWords = new ArrayList<>();
		Collections.addAll(expectedWords, WORDS);

		List<String> actualWords = new ArrayList<>();
		for (String w : solutions.keySet()) {
			actualWords.add(w);
		}

		Collections.sort(expectedWords);
		Collections.sort(actualWords);

		assertEquals(expectedWords, actualWords);

		for (Map.Entry<String, List<Solution>> actualEntry : solutions.entrySet()) {

			Solution actualSolution = actualEntry.getValue().get(0);
			assertEquals(actualEntry.getKey(), actualSolution.getWord());
			boolean found = false;

			for (Map.Entry<String, Solution> expectedEntry : SOLUTIONS.entrySet()) {
				if (expectedEntry.getKey().equals(actualEntry.getKey())) {
					found = true;

					Integer[] expectedPositions = expectedEntry.getValue().getPositions();
					Integer[] actualPositions = actualSolution.getPositions();

					assertArrayEquals("Comparing solutions for: " + expectedEntry.getKey(), expectedPositions, actualPositions);
				}
			}

			assertTrue(found);
		}
	}

	@Test
	@Ignore("Used to test performance optimizations. Remove @Ignore to use it.")
	public void testSolverPerformance() throws IOException {
		Language language = new EnglishUS();
		InputStream stream = FullUsGbTrieTest.class.getClassLoader().getResourceAsStream(language.getTrieFileName());
		Trie trie = new StringTrie.Deserializer().deserialize(stream, BOARD, language);
		assertEquals(40, trie.solver(BOARD, new WordFilter.MinLength(3)).size());
		long startTime = System.currentTimeMillis();
		trie.solver(BOARD, new WordFilter.MinLength(3));
		long totalTime = (System.currentTimeMillis() - startTime) ;
		fail("Took " + totalTime + "ms");
	}

	@Test
	public void testStringTrieUsDictionary() {
		testUsDictionary(new StringTrie(new EnglishUS()));
	}

	private void testUsDictionary(Trie trie) {
		Language language = new EnglishUS();
		String[] words = readDictionary(language);
		assertEquals(77517, words.length);

		addWords(trie, words);

		assertTrieMatches("After adding entire US dictionary to a new Trie", trie, words, null);
	}

	@Test
	public void testStringTrieGbDictionary() {
		testGbDictionary(new StringTrie(new EnglishGB()));
	}

	private void testGbDictionary(Trie trie) {
		Language language = new EnglishGB();
		String[] words = readDictionary(language);
		assertEquals(77097, words.length);

		addWords(trie, words);

		assertTrieMatches("After adding entire GB dictionary to a new Trie", trie, words, null);
	}

	static String[] readDictionary(Language language) {
		try {
			List<String> words = new ArrayList<>(80000);
			InputStream stream = FullUsGbTrieTest.class.getClassLoader().getResourceAsStream(language.getDictionaryFileName());
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
