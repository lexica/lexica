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
import android.util.SparseIntArray;

import com.serwylo.lexica.GameSaver;
import com.serwylo.lexica.R;
import com.serwylo.lexica.Synchronizer;
import com.serwylo.lexica.Util;
import com.serwylo.lexica.lang.EnglishGB;
import com.serwylo.lexica.lang.EnglishUS;
import com.serwylo.lexica.lang.Language;

import net.healeys.trie.Solution;
import net.healeys.trie.StringTrie;
import net.healeys.trie.Trie;

import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Game implements Synchronizer.Counter {

    public static final String HINT_MODE = "hintMode";
    public static final String SCORE_TYPE = "scoreType";
    public static final String SCORE_WORDS = "W";
    public static final String SCORE_LETTERS = "L";

    private static final String TAG = "Game";
    private int timeRemaining;
    private int maxTime;

    private int maxTimeRemaining;

    private Board board;
    private int score;
    private String scoreType;
    private String hintMode;

    public enum GameStatus {GAME_STARTING, GAME_RUNNING, GAME_PAUSED, GAME_FINISHED}

    private static int[] weights;

    private static final int[] WORD_POINTS = {0, 0, 0, // 0,1,2
            1, 1, 2, // 3,4,5
            3, 5, 8, // 6,7,8
            13, 21, 34, // 9,10,11
            55, 89, 144, // 12,13,14
            233, 377, 610, //14,15,16
            987, 1597, 2584, // 17,18,19
            4181, 6765, 10946, // 20,21,22
            17711, 28657, 46368, // 23,24,25
    };

    private GameStatus status;
    private RotateHandler mRotateHandler;

    private LinkedList<String> wordList;
    private LinkedHashSet<String> wordsUsed;
    private int wordCount;
    private final SparseIntArray wordCountsByLength = new SparseIntArray();
    private final SparseIntArray maxWordCountsByLength = new SparseIntArray();

    private Date start;
    private final Context context;

    private Language language;
    private int boardSize; // using an int so I can use much larger boards later
    private int minWordLength;

    private Map<String, List<Solution>> solutions;

    private AudioManager mgr;
    private SoundPool mSoundPool;
    private int[] soundIds;

    public Game(Context c, GameSaver saver) {

        status = GameStatus.GAME_STARTING;
        wordCount = 0;

        context = c;
        loadPreferences(c);

        try {
            switch (saver.readBoardSize()) {
                case 16:
                    setBoard(new FourByFourBoard(saver.readGameBoard()));
                    break;
                case 25:
                    setBoard(new FiveByFiveBoard(saver.readGameBoard()));
                    break;
                case 36:
                    setBoard(new SixBySixBoard(saver.readGameBoard()));
                    break;
            }

            maxTimeRemaining = saver.readMaxTimeRemaining();
            timeRemaining = saver.readTimeRemaining();
            maxTime = timeRemaining;
            start = saver.readStart();

            scoreType = saver.readScoreType();
            String[] wordArray = saver.readWords();
            wordList = new LinkedList<>();
            wordsUsed = new LinkedHashSet<>();
            for (String word : wordArray) {
                if (!word.startsWith("+")) {
                    if (isWord(word)) {
                        score += getWordScore(word);
                        wordCountsByLength.put(word.length(), wordCountsByLength.get(word.length()) + 1);
                    }
                    wordsUsed.add(word);
                }
                wordList.add(word);
            }
            wordCount = saver.readWordCount();

            status = saver.readStatus();
            initializeWeights();
        } catch (Exception e) {
            Log.e(TAG, "Error Restoring Saved Game", e);
            status = GameStatus.GAME_FINISHED;
        }
    }

    public Game(Context c) {
        status = GameStatus.GAME_STARTING;
        wordCount = 0;
        wordList = new LinkedList<>();

        context = c;
        loadPreferences(c);

        String lettersFileName = language.getLetterDistributionFileName();
        int id = context.getResources().getIdentifier("raw/" + lettersFileName.substring(0, lettersFileName.lastIndexOf('.')), null, context.getPackageName());
        CharProbGenerator charProbs = new CharProbGenerator(c.getResources().openRawResource(id), getLanguage());
        Board board;

        switch (boardSize) {
            case 16:
                board = charProbs.generateFourByFourBoard();
                break;

            case 25:
                board = charProbs.generateFiveByFiveBoard();
                break;

            case 36:
                board = charProbs.generateSixBySixBoard();
                break;

            default:
                throw new IllegalStateException("Board must be 16, 25, or 36 large");
        }

        setBoard(board);

        timeRemaining = getMaxTimeRemaining();
        maxTime = getMaxTimeRemaining();
        score = 0;
        wordsUsed = new LinkedHashSet<>();
        initializeWeights();
    }

    public Language getLanguage() {
        return language;
    }

    private void initSoundPool(Context c) {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
        soundIds = new int[3];

        soundIds[0] = mSoundPool.load(c, R.raw.sound1, 1);
        soundIds[1] = mSoundPool.load(c, R.raw.sound2, 1);
        soundIds[2] = mSoundPool.load(c, R.raw.sound3, 1);

        mgr = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
    }

    private void playSound(int soundId) {
        if (mSoundPool != null) {
            int streamVolume = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            mSoundPool.play(soundIds[soundId], streamVolume, streamVolume, 1, 0, 1f);
        }
    }

    public void setBoard(Board b) {
        board = b;
        boardSize = b.getSize();

        switch (boardSize) {
            case 16:
                minWordLength = 3;
                break;
            case 25:
                minWordLength = 4;
                break;
            case 36:
                minWordLength = 5;
                break;
        }

        initializeDictionary();
    }

    private void loadPreferences(Context c) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);

        String languageCode = new Util().getLexiconString(context);
        language = Language.fromOrNull(languageCode);
        if (language == null) {
            // Legacy preferences, which use either "US" or "UK" rather than the locale name (i.e. "en_US" or "en_GB")
            if ("UK".equals(languageCode)) {
                language = new EnglishGB();
            } else {
                language = new EnglishUS();
            }
        }
        Log.d(TAG, "Language (from preferences): " + language.getName());

        switch (prefs.getString("boardSize", "16")) {
            case "16":
                boardSize = 16;
                minWordLength = 3;
                break;
            case "25":
                boardSize = 25;
                minWordLength = 4;
                break;
            case "36":
                boardSize = 36;
                minWordLength = 5;
                break;
        }

        maxTimeRemaining = 100 * Integer.parseInt(prefs.getString("maxTimeRemaining", "180"));

        if (prefs.getBoolean("soundsEnabled", false)) {
            initSoundPool(c);
        }
        scoreType = prefs.getString(SCORE_TYPE, SCORE_WORDS);
        hintMode = prefs.getString(HINT_MODE, "hint_none");
    }

    public void initializeDictionary() {
        initializeDictionary(language);
    }

    private void initializeDictionary(Language language) {
        try {
            String trieFileName = language.getTrieFileName();
            int id = context.getResources().getIdentifier("raw/" + trieFileName.substring(0, trieFileName.lastIndexOf('.')), null, context.getPackageName());
            Trie dict = new StringTrie.Deserializer().deserialize(context.getResources().openRawResource(id), board, language);

            solutions = dict.solver(board, w -> w.length() >= minWordLength);

            Log.d(TAG, "Initializing " + language.getName() + " dictionary");
            for (String word : solutions.keySet()) {
                maxWordCountsByLength.put(word.length(), maxWordCountsByLength.get(word.length()) + 1);

                // For debugging and diagnosis, it is convenient to have access to all the words
                // for some boards printed to the log. This is especially true seeing as I can only
                // speak / read English, and thus am unable to play the boards of additional
                // languages without this aid. Once they go out of beta, then it seems inappropriate
                // to print this.
                if (language.isBeta()) {
                    Log.d(TAG, "Word: " + word);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Error initializing dictionary", e);
        }
    }

    /**
     * Initialize tile weights.
     * <p>
     * For each tile, count how many words that tile can be used for.
     */
    private void initializeWeights() {
        weights = new int[boardSize];

        for (Map.Entry<String, List<Solution>> entry : solutions.entrySet()) {
            // If we're restoring a game and the word was already used, don't include
            // it in the weights
            if (wordList.contains(entry.getKey()) || wordList.contains("+" + entry.getKey())) {
                continue;
            }

            // Handle multiple paths for the same word by keeping track of positions
            // already incremented.
            HashSet<Integer> seen = new HashSet<>();
            for (Solution sol : entry.getValue()) {
                for (int pos : sol.getPositions()) {
                    if (!seen.contains(pos)) {
                        seen.add(pos);
                        weights[pos]++;
                    }
                }
            }
        }
    }

    /**
     * Removes the tile weights for the given word
     *
     * @param word Word to remove tile weights for.
     */
    private void removeWeight(String word) {
        // Handle multiple paths for the same word by keeping track of positions
        // already decremented.
        HashSet<Integer> seen = new HashSet<>();
        for (Solution sol : solutions.get(word)) {
            for (Integer pos : sol.getPositions()) {
                if (!seen.contains(pos)) {
                    seen.add(pos);
                    weights[pos]--;
                }
            }
        }
    }

    public void save(GameSaver saver) {
        saver.save(board, timeRemaining, getMaxTimeRemaining(), wordListToString(), scoreType, wordCount, start, status);
    }

    public void start() {
        if (status == GameStatus.GAME_STARTING) {
            start = new Date();
            status = GameStatus.GAME_RUNNING;
        }
    }

    private String wordListToString() {
        StringBuilder sb = new StringBuilder();
        ListIterator<String> li = wordList.listIterator();

        while (li.hasNext()) {
            String w = li.next();
            sb.append(w);
            if (li.hasNext()) {
                sb.append(",");
            }
        }

        return sb.toString();
    }

    public void addWord(String word) {
        if (status != GameStatus.GAME_RUNNING) {
            return;
        }
        String cap = word.toLowerCase(language.getLocale());

        if (isWord(cap)) {
            if (wordsUsed.contains(cap)) {
                // Word has been found before
                wordList.addFirst("+" + word);
                playSound(1);
            } else {
                // Word has not been found before
                wordCount++;
                score += getWordScore(cap);
                wordCountsByLength.put(cap.length(), wordCountsByLength.get(cap.length()) + 1);
                wordList.addFirst(word);
                playSound(0);
                removeWeight(cap);
            }
        } else {
            // Word is not really a word
            wordList.addFirst(word);
            playSound(2);
        }
        wordsUsed.add(cap);
    }

    public int getWordScore(String word) {
        if (SCORE_WORDS.equals(scoreType)) {
            return WORD_POINTS[word.length()];
        } else {
            int score = 0;
            for (int i = 0; i < word.length(); i++) {
                // Manually iterating over characters of a word here, so we are responsible for ensuring that any
                // mandatory suffix is applied for each letter.
                String letter = language.applyMandatorySuffix(String.valueOf(word.charAt(i)).toLowerCase());
                score += language.getPointsForLetter(letter);

                // Advance the counter so that we can skip over any suffixes.
                i += letter.length() - 1;
            }
            return score;
        }
    }

    public int getWordCount() {
        return wordCount;
    }

    public int getScore() {
        return score;
    }

    public String getScoreType() {
        return scoreType;
    }

    public int getMaxWordCount() {
        return solutions.size();
    }

    public int getWeight(int pos) {
        return weights[pos];
    }

    public int getMaxWeight() {
        int max = 0;
        for (int weight : weights) {
            if (weight > max) {
                max = weight;
            }
        }
        return max;
    }

    public boolean hintModeCount() {
        return hintMode.equals("tile_count") || hintMode.equals("hint_both");
    }

    public boolean hintModeColor() {
        return hintMode.equals("hint_colour") || hintMode.equals("hint_both");
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
        if (timeRemaining <= 0) {
            status = GameStatus.GAME_FINISHED;
            timeRemaining = 0;
        } else {
            Date now = new Date();
            timeRemaining = Math.max(0, maxTime - (int) (now.getTime() - start.getTime()) / 10);
        }
        return timeRemaining;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void pause() {
        if (status == GameStatus.GAME_RUNNING)
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

    public Map<String, List<Solution>> getSolutions() {
        return solutions;
    }

    public void rotateBoard() {
        board.rotate();
        if (mRotateHandler != null)
            mRotateHandler.onRotate();
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

