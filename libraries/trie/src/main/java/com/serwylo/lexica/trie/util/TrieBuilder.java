package com.serwylo.lexica.trie.util;

import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TrieBuilder {

	private final InputStream usDictFile;
	private final InputStream ukDictFile;
	private final File[] outputFiles;

	public TrieBuilder(File usDictFile, File ukDictFile, File[] outputFiles) throws IOException {
		this(new FileInputStream(usDictFile), new FileInputStream(ukDictFile), outputFiles);
	}

	private TrieBuilder(InputStream usDictFileStream, InputStream ukDictFileStream, File[] outputFiles) {
		this.usDictFile = usDictFileStream;
		this.ukDictFile= ukDictFileStream;
		this.outputFiles = outputFiles;
	}

	public void run() throws IOException {
		Trie outTrie = new StringTrie();

		readFileIntoTrie(usDictFile, outTrie, true, false);
		readFileIntoTrie(ukDictFile, outTrie, false, true);

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

	private static void readFileIntoTrie(InputStream dictFile, Trie trie, boolean usWord, boolean ukWord) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(dictFile));
		String line;
		while((line = br.readLine()) != null) {
			trie.addWord(line, usWord, ukWord);
		}
	}

}
