package com.serwylo.lexica.trie.tests;

import net.healeys.trie.CompressedTrie;
import net.healeys.trie.Trie;

public class Utils {

	/**
	 * The way {@link CompressedTrie}s are stored in memory after being read from disk is to
	 * only include words that can be made from the letters available.
	 */
	public static int calcLetterMask(String availableLetters) {
		int mask = 0;
		for (int i = 0; i < availableLetters.length(); i ++) {
			char character = availableLetters.charAt(i);
			mask |= 1 << Trie.charToOffset(character);
		}
		return mask;
	}

	/**
	 * TODO: Be more specific about valid transitions in future tests.
	 */
	public static int[] calcNeighbourMasks() {
		int[] neighbourMasks = new int[26];
		for (int i = 0; i < 26; i ++) {
			neighbourMasks[i] = 0xffffffff;
		}
		return neighbourMasks;
	}

}
