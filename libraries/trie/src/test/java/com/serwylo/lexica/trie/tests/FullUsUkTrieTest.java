package com.serwylo.lexica.trie.tests;

import net.healeys.trie.Trie;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FullUsUkTrieTest extends TrieTest {

	@Test
	public void testUsDictionary() {
		String[] words = readDictionary("us.txt");
		Assert.assertEquals(77517, words.length);

		Trie trie = new Trie();
		addWords(trie, words, true, false);

		assertTrieMatches(trie, words, null, null);
	}

	@Test
	public void testUkDictionary() {
		String[] words = readDictionary("uk.txt");
		Assert.assertEquals(77097, words.length);

		Trie trie = new Trie();
		addWords(trie, words, true, false);

		assertTrieMatches(trie, words, null, null);
	}

	private String[] readDictionary(String fileName) {
		try {
			List<String> words = new ArrayList<>(80000);
			InputStream stream = getClass().getClassLoader().getResourceAsStream(fileName);
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
			Assert.fail();
			throw new RuntimeException(e);
		}
	}

}
