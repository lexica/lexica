package com.serwylo.lexica.trie;

import java.io.File;
import java.io.IOException;

import com.serwylo.lexica.trie.util.TrieBuilder;

public class TrieBuilderApp {

	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			printUsage();
			return;
		}

		final File usDictFile = new File(args[0]);
		final File ukDictFile = new File(args[1]);

		int outputFileCount = args.length - 2;
		final File[] outputFiles = new File[outputFileCount];
		for (int i = 0; i < outputFileCount; i ++) {
			outputFiles[i] = new File(args[i + 2]);
		}

		if (!usDictFile.exists()) {
			printFileNotFound(usDictFile);
			return;
		}

		if (!ukDictFile.exists()) {
			printFileNotFound(ukDictFile);
			return;
		}

		final TrieBuilder builder = new TrieBuilder(usDictFile, ukDictFile, outputFiles);
		builder.run();
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("    java -jar trie-builder.jar path/to/usDict.txt path/to/ukDict.txt path/to/words.bin");
		System.out.println("        usDict.txt|ukDict.txt  Input text files, one word per line.");
		System.out.println("        words.bin ...          Output file(s) containing a trie of all the words.");
	}

	private static void printFileNotFound(File file) {
		System.out.println("Input file " + file + " does not exist.");
		printUsage();
	}

}
