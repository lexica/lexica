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

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.preference.PreferenceManager;
import android.util.Log;

import com.serwylo.lexica.GameSaver;
import com.serwylo.lexica.R;
import com.serwylo.lexica.Synchronizer;
import net.healeys.trie.CompressedTrie;
import net.healeys.trie.Trie;
import net.healeys.trie.WordFilter;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;

public class Game implements Synchronizer.Counter {

	private static final String TAG = "Game";
	private int timeRemaining;
	private int maxTime;

	private int maxTimeRemaining;

	private Board board;

	public enum GameStatus { GAME_STARTING, GAME_RUNNING, GAME_PAUSED, GAME_FINISHED }

	public static final int WORD_POINTS[] = {
		0,0,0, // 0,1,2
		1,1,2, // 3,4,5
		3,5,8, // 6,7,8
		13,21,34, // 9,10,11
		55,89,144, // 12,13,14
		233,377,610, //14,15,16
		987,1597,2584, // 17,18,19
		4181,6765,10946, // 20,21,22
		17711,28657,46368, // 23,24,25
	};

	private GameStatus status;
	private RotateHandler mRotateHandler;

	private LinkedList<String> wordList;
	private LinkedHashSet<String> wordsUsed;
	private int wordCount;

	private Date start;
	private final Context context;

	private boolean usDict;
	private boolean ukDict;
	private int boardSize; // using an int so I can use much larger boards later
	private int minWordLength;

	private LinkedHashMap<String,Trie.Solution> solutions;

	private AudioManager mgr;
	private SoundPool mSoundPool;
	private int[] soundIds;

	public Game(Context c, GameSaver saver) {

		status = GameStatus.GAME_STARTING;
		wordCount = 0;

		context = c;
		loadPreferences(c);

		try {
			switch(saver.readBoardSize()) {
				case 16:
					setBoard(new FourByFourBoard(saver.readGameBoard()));
					break;
				case 25:
					setBoard(new FiveByFiveBoard(saver.readGameBoard()));
					break;
			}

			maxTimeRemaining = saver.readMaxTimeRemaining();
			timeRemaining = saver.readTimeRemaining();
			maxTime = timeRemaining;
			start = saver.readStart();

			String[] wordArray = saver.readWords();
			wordList = new LinkedList<>();
			wordsUsed = new LinkedHashSet<>();
			for (String word : wordArray) {
				wordList.add(word);
				wordsUsed.add(word);
			}
			wordCount = saver.readWordCount();

			status = saver.readStatus();
		} catch (Exception e) {
			Log.e(TAG,"Error Restoring Saved Game",e);
			status = GameStatus.GAME_FINISHED;
		}
	}

	public Game (Context c) {
		status = GameStatus.GAME_STARTING;
		wordCount = 0;
		wordList = new LinkedList<>();

		context = c;
		loadPreferences(c);

		switch(boardSize) {
			case 16:
				setBoard(new CharProbGenerator(c.getResources().
					openRawResource(R.raw.letters)).generateFourByFourBoard());
			break;
			case 25:
				setBoard(new CharProbGenerator(c.getResources().
					openRawResource(R.raw.letters)).generateFiveByFiveBoard());
			break;
		}

		timeRemaining = getMaxTimeRemaining();
		maxTime = getMaxTimeRemaining();

		wordsUsed = new LinkedHashSet<>();

	}

	private void initSoundPool(Context c) {
		mSoundPool = new SoundPool(3,AudioManager.STREAM_MUSIC,100);
		soundIds = new int[3];

		soundIds[0] = mSoundPool.load(c,R.raw.sound1,1);
		soundIds[1] = mSoundPool.load(c,R.raw.sound2,1);
		soundIds[2] = mSoundPool.load(c,R.raw.sound3,1);

		mgr = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE); 
	}

	private void playSound(int soundId) {
		if(mSoundPool != null) {
			int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
			mSoundPool.play(soundIds[soundId],streamVolume,streamVolume,1,0,1f);
		}
	}

	public void setBoard(Board b) {
		board = b;
		boardSize = b.getSize();

		switch(boardSize) {
			case 16:
				minWordLength = 3;
			break;
			case 25:
				minWordLength = 4;
			break;
		}

		initializeDictionary();
	}

	private void loadPreferences(Context c) {
		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences(c);

		if(prefs.getString("dict","US").equals("UK")) {
			// Log.d(TAG,"UK DICT");
			usDict = false;
			ukDict = true;
		} else {
			// Log.d(TAG,"US DICT");
			ukDict = false;
			usDict = true;
		}

		if(prefs.getString("boardSize","16").equals("25")) {
			boardSize = 25;
			minWordLength = 4;
		} else {
			boardSize = 16;
			minWordLength = 3;
		}

		maxTimeRemaining = 100 * Integer.parseInt(
			prefs.getString("maxTimeRemaining","180"));

		if(prefs.getBoolean("soundsEnabled",false)) {
			initSoundPool(c);
		}
	}

	public void initializeDictionary() {
		initializeDictionary(usDict,ukDict);
	}

	private void initializeDictionary(boolean usDict, boolean ukDict) {
		// Log.d(TAG,"initializeDictionary");
		int mask = 0;
		int neighborMasks[] = new int[26];
		CompressedTrie dict;

		for(int i=0;i<board.getSize();i++) {
			int ival = Trie.charToOffset(board.elementAt(i).charAt(0));
			mask |= 1<<ival;

			for(int j=0;j<board.getSize();j++) {
				if((board.transitions(i)&(1<<j))!= 0) {
					neighborMasks[ival] |= 1<<Trie.charToOffset(board.elementAt(j).
							charAt(0));
				}
			}
		}
		try {
			dict = new CompressedTrie(context.getResources().
				openRawResource(R.raw.words),mask,neighborMasks,
				usDict,ukDict);
			solutions = dict.solver(board,new WordFilter() {
				public boolean isWord(String w) {
					return w.length() >= minWordLength;
				}
			});
		} catch(IOException e) {
			// Log.e(TAG,"initializeDictionary",e);
		}
	}

	public void save(GameSaver saver) {
		saver.save(
				board,
				timeRemaining,
				getMaxTimeRemaining(),
				wordListToString(),
				wordCount,
				start,
				status
		);
	}

	public void start() {
		if(status == GameStatus.GAME_STARTING) {
			start = new Date();
			status = GameStatus.GAME_RUNNING;
		}
	}

	private String wordListToString() {
		StringBuilder sb = new StringBuilder();
		ListIterator<String> li = wordList.listIterator();
		
		while(li.hasNext()) {
			String w = li.next();
			sb.append(w);
			if(li.hasNext()) {
				sb.append(",");
			}
		}

		return sb.toString();
	}

	public void addWord(String word) {
		if(status != GameStatus.GAME_RUNNING) {
			return;
		}
		String cap = word.toUpperCase();
		wordList.addFirst(cap);

		if(isWord(cap)) {
			if(wordsUsed.contains(cap)) {
				// Word has been found before
				playSound(1);
			} else {
				// Word has not been found before
				wordCount++;
				playSound(0);
			}
		} else {
			// Word is not really a word
			playSound(2);
		}
		wordsUsed.add(cap);
	}
	
	public int getWordCount() {
		return wordCount;
	}

	public int getMaxWordCount() {
		return solutions.size();
	}

	public ListIterator<String> listIterator() {
		return wordList.listIterator();
	}

	public Iterator<String> uniqueListIterator() {
		return wordsUsed.iterator();
	}

	public boolean isWord(String word) {
		return solutions.containsKey(word);
	}

	public Board getBoard() {
		return board;
	}

	public int tick() {
		timeRemaining--;
		if(timeRemaining <= 0) {
			status = GameStatus.GAME_FINISHED;
			timeRemaining = 0;
		} else {
			Date now = new Date();
			timeRemaining = Math.max(0,maxTime-
				(int)(now.getTime()-start.getTime())/10);
		}
		return timeRemaining;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void pause() {
		if(status == GameStatus.GAME_RUNNING) 
			status = GameStatus.GAME_PAUSED;
	}

	public void unpause() {
		status = GameStatus.GAME_RUNNING;
		maxTime = timeRemaining;
		start = new Date();
	}

	public void endNow() {
		// Log.d(TAG,"endNow");
		timeRemaining = 0;
	}

	public LinkedHashMap<String,Trie.Solution> getSolutions() {
		return solutions;
	}

	public void rotateBoard() {
		board.rotate();
		if(mRotateHandler != null) mRotateHandler.onRotate();
	}

	public int getMaxTimeRemaining() {
		return maxTimeRemaining;
	}

	public void setRotateHandler(RotateHandler rh) {
		mRotateHandler = rh;
	}

	public interface RotateHandler {
		void onRotate();
	}
}

