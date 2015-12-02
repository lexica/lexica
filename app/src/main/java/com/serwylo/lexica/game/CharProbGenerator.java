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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class CharProbGenerator {
	@SuppressWarnings("unused")
	private static final String TAG = "CharProbGenerator";
	private final ArrayList<ProbabilityQueue> charProbs;

	public CharProbGenerator(InputStream letter_stream) {

		BufferedReader br = new BufferedReader(new InputStreamReader(
			letter_stream));

		charProbs = new ArrayList<>();

		try {
			for(String line=br.readLine();line != null; line=br.readLine()) {
				String chunks[] = line.split(" ");
				ProbabilityQueue pq = new ProbabilityQueue(chunks[0]);
				for(int i=1;i<chunks.length;i++) {
					pq.addProb(chunks[i]);
				}
				charProbs.add(pq);
			}
		} catch (Exception e) {
			// Log.e(TAG,"READING INPUT",e);
			// Checked exceptions considered harmful.
		}

	}

	public FiveByFiveBoard generateFiveByFiveBoard() {
		return new FiveByFiveBoard(generateBoard(25));
	}

	public FourByFourBoard generateFourByFourBoard() {
		return new FourByFourBoard(generateBoard(16));
	}

	public String[] generateBoard(int size) {
		int total = 0;
		Random rng = new Random();

		String board[] = new String[size];

		for(int i=0;i<charProbs.size();i++) {
			total += charProbs.get(i).peekProb();
		}

		// get the letters
		for(int i=0;i<size;i++) {
			ProbabilityQueue pq = null;
			int remaining = rng.nextInt(total);
			// Log.d(TAG,"remaining:"+remaining+"/"+total);
			for(int j=0;j<charProbs.size();j++) {
				pq = charProbs.get(j);
				remaining -= pq.peekProb();
				if(pq.peekProb() > 0 && remaining <= 0) {
					break;
				}
			}
			board[i] = pq.getLetter();
			total -= pq.getProb();
			total += pq.peekProb();
		}

		// shuffle the letters
		for(int to=15;to>0;to--) {
			int from = rng.nextInt(to);
			String tmp = board[to];
			board[to] = board[from];
			board[from] = tmp;
		}

		return board;
	}

	private class ProbabilityQueue {
		private final String letter;
		private final LinkedList<Integer> probQueue;

		public ProbabilityQueue(String l) {
			letter = l;
			probQueue = new LinkedList<>();
		}

		public String getLetter() {
			return letter;
		}

		public void addProb(String s) {
			probQueue.add(Integer.valueOf(s));
		}

		public int peekProb() {
			if(probQueue.isEmpty()) return 0;
			return probQueue.peek();
		}

		public int getProb() {
			if(probQueue.isEmpty()) return 0;
			return probQueue.remove();
		}

	}

}
