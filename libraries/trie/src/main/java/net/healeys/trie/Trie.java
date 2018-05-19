package net.healeys.trie;

import com.serwylo.lexica.lang.Language;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public abstract class Trie implements WordFilter {

	protected Language language;

	public Trie(Language language) {
		this.language = language;
	}

	public abstract void addWord(String w);

	public abstract boolean isWord(String w);

	public abstract void write(OutputStream out) throws IOException;

	public abstract Map<String,Solution> solver(TransitionMap m, WordFilter filter);

}
