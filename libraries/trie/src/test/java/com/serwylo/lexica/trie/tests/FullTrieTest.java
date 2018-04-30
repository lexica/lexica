package com.serwylo.lexica.trie.tests;

import com.serwylo.lexica.lang.French;
import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.lang.EnglishGB;
import com.serwylo.lexica.lang.EnglishUS;
import com.serwylo.lexica.lang.Persian;

import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FullTrieTest extends TrieTest {

	@Test
	public void testEnUsDictionary() {
		Language language = new EnglishUS();
		String[] words = readDictionary(language);
		Assert.assertEquals(77517, words.length);

		Trie trie = new StringTrie(language);
		addWords(trie, words);

		assertTrieMatches("After adding entire US dictionary to a new Trie", trie, words, new EnglishUS());
	}

	@Test
	public void testEnGbDictionary() {
		Language language = new EnglishGB();
		String[] words = readDictionary(language);
		Assert.assertEquals(77097, words.length);

		Trie trie = new StringTrie(language);
		addWords(trie, words);

		assertTrieMatches("After adding entire UK dictionary to a new Trie", trie, words, new EnglishGB());
	}

	@Test
	public void testFrenchDictionary() {
		Language language = new French();
		String[] words = readDictionary(language);
		Assert.assertEquals(144582, words.length);

		Trie trie = new StringTrie(language);
		addWords(trie, words);

		assertTrieMatches("After adding entire French dictionary to a new Trie", trie, words, new French());
	}

	@Test
	public void testPersianDictionary() {
		Language language = new Persian();
		String[] words = readDictionary(language);
		Assert.assertEquals(166715, words.length);

		Trie trie = new StringTrie(language);
		addWords(trie, words);

		char[] chars = "وزغهایمان".toCharArray();
		for (int i = 0; i < chars.length; i ++) {
			System.out.println(chars[i] + " " + "وزغهایمان".substring(i, i + 1));
		}

		assertTrieMatches("After adding entire Persian dictionary to a new Trie", trie, words, new Persian());
	}

	public static String[] readDictionary(Language language) {
		try {
			List<String> words = new ArrayList<>(80000);
			InputStream stream = FullTrieTest.class.getClassLoader().getResourceAsStream(language.getDictionaryFileName());
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
