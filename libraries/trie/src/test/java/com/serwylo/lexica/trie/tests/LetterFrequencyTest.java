package com.serwylo.lexica.trie.tests;

import com.serwylo.lexica.lang.EnglishGB;
import com.serwylo.lexica.lang.Persian;
import com.serwylo.lexica.trie.util.LetterFrequency;

import org.junit.Test;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LetterFrequencyTest {

    @Test
    public void countUnicodeLetters() {
        LetterFrequency letters = new LetterFrequency(new Persian());

        letters.addWord("ﻒﻗﺮﻫ");

        assertEquals(4, letters.getLetters().size());

        assertCounts(letters.getCountsForLetter("ﻒ"), 1);
        assertCounts(letters.getCountsForLetter("ﻗ"), 1);
        assertCounts(letters.getCountsForLetter("ﻫ"), 1);
        assertCounts(letters.getCountsForLetter("ﺮ"), 1);
    }

    @Test
    public void countLetters() {
        LetterFrequency letters = new LetterFrequency(new EnglishGB());

        letters.addWord("assessment");
        letters.addWord("sequential");
        letters.addWord("queen");

        assertCounts(letters.getCountsForLetter("a"), 2);
        assertCounts(letters.getCountsForLetter("s"), 1, 0, 0, 1);
        assertCounts(letters.getCountsForLetter("e"), 0, 3);
        assertCounts(letters.getCountsForLetter("m"), 1);
        assertCounts(letters.getCountsForLetter("n"), 3);
        assertCounts(letters.getCountsForLetter("t"), 2);
        assertCounts(letters.getCountsForLetter("q"), 2);
        assertCounts(letters.getCountsForLetter("u"));
    }

    private static void assertCounts(List<Integer> actualCounts, int ... expectedCounts) {
        assertEquals(expectedCounts.length, actualCounts.size());
        for (int i = 0; i < expectedCounts.length; i ++) {
            int actual = actualCounts.get(i);
            assertEquals(expectedCounts[i], actual);
        }
    }

    @Test
    public void countLettersForWord() {
        LetterFrequency letters = new LetterFrequency(new EnglishGB());

        HashMap<String, Integer> quitCount = letters.getLetterCountsForWord("quit");
        assertEquals(quitCount.size(), 3);
        assertCount(quitCount, "q", 1);
        assertCount(quitCount, "i", 1);
        assertCount(quitCount, "t", 1);

        HashMap<String, Integer> assessmentCount = letters.getLetterCountsForWord("assessment");
        assertEquals(assessmentCount.size(), 6);
        assertCount(assessmentCount, "a", 1);
        assertCount(assessmentCount, "s", 4);
        assertCount(assessmentCount, "e", 2);
        assertCount(assessmentCount, "m", 1);
        assertCount(assessmentCount, "n", 1);
        assertCount(assessmentCount, "t", 1);
    }

    private static void assertCount(HashMap<String, Integer> counts, String letter, int expectedCount) {
        int actualCount = counts.get(letter);
        assertEquals(expectedCount, actualCount);
    }

    @Test
    public void includeLetters() {
        LetterFrequency letters = new LetterFrequency(new EnglishGB());
        assertTrue(letters.shouldInclude("quit", 0));
        assertTrue(letters.shouldInclude("quit", 2));
        assertTrue(letters.shouldInclude("quit", 3));
    }

    @Test
    public void excludeLetters() {
        LetterFrequency letters = new LetterFrequency(new EnglishGB());
        assertFalse(letters.shouldInclude("qantas", 0));
        assertFalse(letters.shouldInclude("qi", 0));

        assertTrue(letters.shouldInclude("qi", 1));
        assertTrue(letters.shouldInclude("qantas", 2));
        assertTrue(letters.shouldInclude("qantas", 3));
        assertTrue(letters.shouldInclude("qantas", 4));
        assertTrue(letters.shouldInclude("qantas", 5));
    }

}
