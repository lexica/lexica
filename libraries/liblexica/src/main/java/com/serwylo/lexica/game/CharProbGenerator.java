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

    public FiveByFiveBoard generateFiveByFiveBoard() {
        return new FiveByFiveBoard(generateBoard(25));
    }

    public FourByFourBoard generateFourByFourBoard() {
        return new FourByFourBoard(generateBoard(16));
    }

    public SixBySixBoard generateSixBySixBoard() {
        return new SixBySixBoard(generateBoard(36));
    }

    private String[] generateBoard(int size) {
        int total = 0;
        Random rng = new Random();

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

    public static class ProbabilityQueue {

        private final String letter;
        private final LinkedList<Integer> probQueue;

        ProbabilityQueue(String l) {
            letter = l;
            probQueue = new LinkedList<>();
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
