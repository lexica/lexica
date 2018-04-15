package com.serwylo.lexica.trie;

import java.io.File;
import java.io.IOException;

import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.trie.util.TrieBuilder;

public class TrieBuilderApp {

	public static void main(String[] args) throws IOException {
		if (args.length < 3) {
			printUsage();
			return;
		}

		final Language language;
		try {
			 language = Language.from(args[0]);
		} catch (Language.NotFound e) {
			System.out.println(e.getMessage());
			return;
		}

		final File dictDir = new File(args[1]);
		if (!dictDir.exists()) {
			printFileNotFound(dictDir);
			return;
		}

		final File dictFile = new File(dictDir, language.getDictionaryFileName());
		if (!dictFile.exists()) {
			printFileNotFound(dictFile);
			return;
		}

		int outputFileCount = args.length - 2;
		final File[] outputTrieFiles = new File[outputFileCount];
		for (int i = 0; i < outputFileCount; i ++) {
			File file = new File(args[i + 2]);
			if (!file.exists()) {
				printFileNotFound(file);
				return;
			}

			outputTrieFiles[i] = new File(file, language.getTrieFileName());
		}

		TrieBuilder.run(language, dictFile, outputTrieFiles);
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("    java -jar trie-builder.jar language path/to/dictionaries/ path/to/trie/output/ ...");
		System.out.println("        language                  en_US|en_GB|de_DE");
		System.out.println("        path/to/dictionaries/     Directory where dictionary.en_US.txt et al. belong.");
		System.out.println("        path/to/trie/output/ ...  Output directories where the trie of all words will get written.");
	}

	private static void printFileNotFound(File file) {
		System.out.println("Input file " + file + " does not exist.");
		printUsage();
	}

}
