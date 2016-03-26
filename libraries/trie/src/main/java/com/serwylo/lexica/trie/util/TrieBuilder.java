package com.serwylo.lexica.trie.util;

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
	private final File outputFile;

	public TrieBuilder(File usDictFile, File ukDictFile, File outputFile) throws IOException {
		this(new FileInputStream(usDictFile), new FileInputStream(ukDictFile), outputFile);
	}

	public TrieBuilder(InputStream usDictFileStream, InputStream ukDictFileStream, File outputFile) {
		this.usDictFile = usDictFileStream;
		this.ukDictFile= ukDictFileStream;
		this.outputFile = outputFile;
	}

	public void run() throws IOException {
		Trie outTrie = new Trie();

		readFileIntoTrie(usDictFile, outTrie, true, false);
		readFileIntoTrie(ukDictFile, outTrie, false, true);

		FileOutputStream of = null;
		try {
			of = new FileOutputStream(outputFile, false);
			outTrie.write(new DataOutputStream(of));
		} finally {
			if(of != null) {
				of.close();
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
