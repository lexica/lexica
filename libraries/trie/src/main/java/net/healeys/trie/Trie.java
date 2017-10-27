package net.healeys.trie;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public interface Trie extends WordFilter {
	void addWord(String w, boolean usWord, boolean ukWord);

	boolean isWord(String w, boolean usWord, boolean ukWord);

	boolean isWord(String w);

	void write(OutputStream out) throws IOException;

	Map<String,Solution> solver(TransitionMap m, WordFilter filter);

}
