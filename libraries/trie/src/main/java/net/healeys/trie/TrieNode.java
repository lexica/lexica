package net.healeys.trie;

import com.serwylo.lexica.lang.Language;

import java.io.IOException;
import java.io.OutputStream;

public abstract class TrieNode {

    protected Language language;

    public TrieNode(Language language) {
        this.language = language;
    }

    public abstract void writeNode(OutputStream out) throws IOException;

    public abstract TrieNode addSuffix(String word, int currentPosition);

    public abstract boolean word();

    public abstract boolean isTail();

}
