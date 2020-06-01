package com.serwylo.lexica.trie.util;

import com.serwylo.lexica.lang.Language;

import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class TrieBuilder {

    public static void run(Language language, File dictFile, File[] outputTrieFiles) throws IOException {
        Trie outTrie = new StringTrie(language);

        readCorpus(language, dictFile, outTrie);

        for (File outputFile : outputTrieFiles) {
            try (FileOutputStream of = new FileOutputStream(outputFile, false)) {
                outTrie.write(new DataOutputStream(of));
            }
        }
    }

    private static void readCorpus(Language language, File dictFile, Trie trie) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(dictFile), Charset.forName("UTF-8")));
        String line;
        while ((line = br.readLine()) != null) {
            String word = line.toLowerCase(language.getLocale());
            trie.addWord(word);
        }
    }

}
