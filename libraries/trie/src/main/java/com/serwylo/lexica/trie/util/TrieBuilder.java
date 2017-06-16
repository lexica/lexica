package com.serwylo.lexica.trie.util;

import com.serwylo.lexica.lang.Language;

import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;

public class TrieBuilder {

	public static void run(Language language, File dictFile, File[] outputFiles) throws IOException {
		Trie outTrie = new StringTrie(language);

		readFileIntoTrie(dictFile, outTrie);

		for (File outputFile : outputFiles) {
			FileOutputStream of = null;
			try {
				of = new FileOutputStream(outputFile, false);
				outTrie.write(new DataOutputStream(of));
			} finally {
				if (of != null) {
					of.close();
				}
			}
		}
	}

	private static void readFileIntoTrie(File dictFile, Trie trie) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dictFile)));
		String line;
		while((line = br.readLine()) != null) {
			trie.addWord(line.toLowerCase(Locale.ENGLISH));
		}
	}

}
