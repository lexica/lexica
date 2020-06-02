package com.serwylo.lexica.language;

import com.serwylo.lexica.lang.Language;

import java.io.File;
import java.io.IOException;

public class LanguageAnalysisApp {

    public static void main(String[] args) throws IOException, InterruptedException {
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

        final File trieDir = new File(args[1]);
        if (!trieDir.exists()) {
            printFileNotFound(trieDir);
            return;
        }

        final File trieFile = new File(trieDir, language.getTrieFileName());
        if (!trieFile.exists()) {
            printFileNotFound(trieFile);
            return;
        }

        final File dictDir = new File(args[2]);
        if (!dictDir.exists()) {
            printFileNotFound(dictDir);
            return;
        }

        final File dictFile = new File(dictDir, language.getDictionaryFileName());
        if (!dictFile.exists()) {
            printFileNotFound(dictFile);
            return;
        }

        final File outputDir = new File(args[3]);
        if (!outputDir.exists()) {
            printFileNotFound(outputDir);
            return;
        }

        new GeneticAlgorithm().run(trieDir, dictDir, outputDir, language);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("    java -jar language-analysis.jar language path/to/dictionary/ path/to/log/output/");
        System.out.println("        language                  en_US|en_GB|de_DE");
        System.out.println("        path/to/trie/dir/         Directory where words_en_us.bin et al. live.");
        System.out.println("        path/to/dictionary/dir/   Directory where dictionary.en_US.txt et al. live.");
        System.out.println("        path/to/log/output/       Output directories where each attempted analysis is stored.");
    }

    private static void printFileNotFound(File file) {
        System.out.println("Input file " + file + " does not exist.");
        printUsage();
    }

}
