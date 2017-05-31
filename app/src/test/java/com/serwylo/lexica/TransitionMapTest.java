package com.serwylo.lexica;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.FourByFourBoard;

import net.healeys.trie.Solution;
import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TransitionMapTest {

    private static final Board BOARD = new FourByFourBoard(new String[] {
            "B", "E", "Z", "Z",
            "D", "A", "N", "Z",
            "Z", "R", "K", "Z",
            "Z", "N", "Z", "Z",
    });

    private static final LinkedHashMap<String, Solution> SOLUTIONS = new LinkedHashMap<>();

    static {
        // Note: Don't include "a", "an", or "be" because they is too short.
        SOLUTIONS.put("bed",   new Solution.Default("bed",   new int[] { xy(0, 0), xy(1, 0), xy(0, 1) }));
        SOLUTIONS.put("bad",   new Solution.Default("bad",   new int[] { xy(0, 0), xy(1, 1), xy(0, 1) }));
        SOLUTIONS.put("ban",   new Solution.Default("ban",   new int[] { xy(0, 0), xy(1, 1), xy(2, 1) }));
        SOLUTIONS.put("ran",   new Solution.Default("ran",   new int[] { xy(1, 2), xy(1, 1), xy(2, 1) }));
        SOLUTIONS.put("bean",  new Solution.Default("bean",  new int[] { xy(0, 0), xy(1, 0), xy(1, 1), xy(2, 1) }));
        SOLUTIONS.put("bane",  new Solution.Default("bane",  new int[] { xy(0, 0), xy(1, 1), xy(2, 1), xy(1, 0) }));
        SOLUTIONS.put("barn",  new Solution.Default("barn",  new int[] { xy(0, 0), xy(1, 1), xy(1, 2), xy(1, 3) }));
        SOLUTIONS.put("darn",  new Solution.Default("darn",  new int[] { xy(0, 1), xy(1, 1), xy(1, 2), xy(1, 3) }));
        SOLUTIONS.put("beard", new Solution.Default("beard", new int[] { xy(0, 0), xy(1, 0), xy(1, 1), xy(1, 2), xy(0, 1) }));
        SOLUTIONS.put("ear",   new Solution.Default("ear",   new int[] { xy(1, 0), xy(1, 1), xy(1, 2) }));
        SOLUTIONS.put("earn",  new Solution.Default("earn",  new int[] { xy(1, 0), xy(1, 1), xy(1, 2), xy(1, 3) }));
        SOLUTIONS.put("bard",  new Solution.Default("bard",  new int[] { xy(0, 0), xy(1, 1), xy(1, 2), xy(0, 1) }));
    }

    private static int xy(int x, int y) {
        return x + BOARD.getWidth() * y;
    }

    @Test
    public void stringTransitionTest() throws IOException {
        byte[] serialized = serializedUsTrie(new StringTrie());
        Trie trie = new StringTrie.Deserializer().deserialize(new ByteArrayInputStream(serialized), BOARD, true, true);
        LinkedHashMap<String, Solution> actualSolutions = trie.solver(BOARD, new WordFilter.MinLength(3));
        assertSolutions(SOLUTIONS, actualSolutions);
    }

    private static void assertSolutions(LinkedHashMap<String, Solution> expectedSolutions, LinkedHashMap<String, Solution> actualSolutions) {
        Set<String> expectedWords = expectedSolutions.keySet();
        Set<String> actualWords = actualSolutions.keySet();
        assertTrue(expectedWords.containsAll(actualWords));
        assertTrue(actualWords.containsAll(expectedWords));

        for (Map.Entry<String, Solution> expected : expectedSolutions.entrySet()) {
            Solution expectedSolution = expected.getValue();
            String expectedWord = expected.getKey();
            boolean found = false;
            for (Map.Entry<String, Solution> actual : actualSolutions.entrySet()) {
                Solution actualSolution = actual.getValue();
                String actualWord = actual.getKey();

                if (expectedWord.equals(actualWord)) {
                    found = true;
                    assertSolutionEquals(expectedSolution, actualSolution);
                }
            }

            assertTrue(found);
        }
    }

    private static void assertSolutionEquals(Solution expectedSolution, Solution actualSolution) {
        assertEquals(expectedSolution.getMask(), actualSolution.getMask());
    }

    private static byte[] serializedUsTrie(Trie trie) {
        TrieTest.addWords(trie, FullUsUkTrieTest.readDictionary("us.txt"), true, false);
        return TrieTest.serialize(trie);
    }

}
