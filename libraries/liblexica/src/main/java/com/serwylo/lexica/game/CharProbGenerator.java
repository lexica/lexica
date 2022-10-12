/*
 *  Copyright (C) 2008-2009 Rev. Johnny Healey <rev.null@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.serwylo.lexica.game;

import com.serwylo.lexica.lang.Language;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CharProbGenerator {
    @SuppressWarnings("unused")
    private static final String TAG = "CharProbGenerator";
    private final ArrayList<ProbabilityQueue> charProbs;

    public CharProbGenerator(InputStream letterSource, Language language) {

        BufferedReader br = new BufferedReader(new InputStreamReader(letterSource));

        charProbs = new ArrayList<>();

        try {
            for (String line = br.readLine(); line != null; line = br.readLine()) {
                // Allow for some comments and spacing. Ignore any empty lines or those starting with '#'.
                if (line.length() == 0 || line.charAt(0) == '#') {
                    continue;
                }

                String[] chunks = line.toLowerCase(language.getLocale()).split(" ");
                ProbabilityQueue pq = new ProbabilityQueue(language.applyMandatorySuffix(chunks[0]));
                for (int i = 1; i < chunks.length; i++) {
                    pq.addProb(language.applyMandatorySuffix(chunks[i]));
                }
                charProbs.add(pq);
            }
        } catch (Exception e) {
            // Log.e(TAG,"READING INPUT",e);
            // Checked exceptions considered harmful.
        }

    }

    public CharProbGenerator(CharProbGenerator charProbGenerator) {
        charProbs = new ArrayList<>(charProbGenerator.charProbs.size());
        for (ProbabilityQueue q : charProbGenerator.charProbs) {
            charProbs.add(new ProbabilityQueue(q));
        }
    }

    public Map<String, List<Integer>> getDistribution() {
        Map<String, List<Integer>> dist = new HashMap<>();
        for (ProbabilityQueue p : charProbs) {
            dist.put(p.letter, new ArrayList<>(p.probQueue));
        }
        return dist;
    }

    public List<String> getAlphabet() {
        List<String> letters = new ArrayList<>(charProbs.size());
        for (ProbabilityQueue prob : charProbs) {
            letters.add(prob.letter);
        }
        return Collections.unmodifiableList(letters);
    }

    public FourByFourBoard generateFourByFourBoard(BoardSeed boardSeed) {
        if (boardSeed == null) return generateFourByFourBoard(new Random().nextLong());
        else if (boardSeed.letters != null) return new FourByFourBoard(boardSeed.letters);
        else if (boardSeed.seed != null) return generateFourByFourBoard(boardSeed.seed);
        else return generateFourByFourBoard(new Random().nextLong());
    }

    public FourByFourBoard generateFourByFourBoard(long seed) {
        return new FourByFourBoard(generateBoard(16, seed));
    }

    public FiveByFiveBoard generateFiveByFiveBoard(BoardSeed boardSeed) {
        if (boardSeed == null) return generateFiveByFiveBoard(new Random().nextLong());
        else if (boardSeed.letters != null) return new FiveByFiveBoard(boardSeed.letters);
        else if (boardSeed.seed != null) return generateFiveByFiveBoard(boardSeed.seed);
        else return generateFiveByFiveBoard(new Random().nextLong());
    }

    public FiveByFiveBoard generateFiveByFiveBoard(long seed) {
        return new FiveByFiveBoard(generateBoard(25, seed));
    }

    public SixBySixBoard generateSixBySixBoard(BoardSeed boardSeed) {
        if (boardSeed == null) return generateSixBySixBoard(new Random().nextLong());
        else if (boardSeed.letters != null) return new SixBySixBoard(boardSeed.letters);
        else if (boardSeed.seed != null) return generateSixBySixBoard(boardSeed.seed);
        else return generateSixBySixBoard(new Random().nextLong());
    }

    public SixBySixBoard generateSixBySixBoard(long seed) {
        return new SixBySixBoard(generateBoard(36, seed));
    }

    private String[] generateBoard(int size, long seed) {
        int total = 0;
        Random rng = new Random(seed);

        String[] board = new String[size];

        for (int i = 0; i < charProbs.size(); i++) {
            total += charProbs.get(i).peekProb();
        }

        // get the letters
        for (int i = 0; i < size; i++) {
            ProbabilityQueue pq = null;
            int remaining = rng.nextInt(total);
            for (int j = 0; j < charProbs.size(); j++) {
                pq = charProbs.get(j);
                remaining -= pq.peekProb();
                if (pq.peekProb() > 0 && remaining <= 0) {
                    break;
                }
            }
            board[i] = pq.getLetter();
            total -= pq.getProb();
            total += pq.peekProb();
        }

        // Although strictly not necessary because they were randomly chosen above, it is not
        // uniformly random above - those with higher probabilities are probably selected first.
        // Hence, we shuffle again.
        for (int to = board.length - 1; to > 0; to--) {
            int from = rng.nextInt(to);
            String tmp = board[to];
            board[to] = board[from];
            board[from] = tmp;
        }

        return board;
    }


    // TODO: think of a more intuitive name
    public static class BoardSeed {
        /* Idea: We can deterministically generate boards based on either a given set of letters
            or a numerical seed for the random generator. This class tries to manage both cases,
            If both letters and seed are null, it generates a random board.
        */
        public String[] letters;
        public Long seed;

        public BoardSeed(String[] letters) {
            this.letters = letters;
            this.seed = null;
        }

        public BoardSeed(Long seed) {
            this.seed = seed;
            this.letters = null;
        }

        public static BoardSeed fromPreviousBoard(Board prevBoard) {
            return new BoardSeed(prevBoard.getRotationInvariantHash());
        }
    }

    public static class ProbabilityQueue {

        private final String letter;
        private final LinkedList<Integer> probQueue;

        ProbabilityQueue(String l) {
            letter = l;
            probQueue = new LinkedList<>();
        }

        ProbabilityQueue(ProbabilityQueue queue) {
            letter = queue.getLetter();
            probQueue = new LinkedList<>();
            for (var p : queue.probQueue) {
                probQueue.add(p);
            }
        }

        /**
         * Already has any mandatory suffix applied (hopefully!).
         */
        public String getLetter() {
            return letter;
        }

        void addProb(String s) {
            probQueue.add(Integer.valueOf(s));
        }

        int peekProb() {
            if (probQueue.isEmpty())
                return 0;
            return probQueue.peek();
        }

        int getProb() {
            if (probQueue.isEmpty())
                return 0;
            return probQueue.remove();
        }

    }

}
