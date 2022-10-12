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
import android.util.Log;
import android.util.SparseIntArray;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.serwylo.lexica.GameSaver;
import com.serwylo.lexica.R;
import com.serwylo.lexica.Synchronizer;
import com.serwylo.lexica.db.GameMode;
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
import java.util.Random;

public class Game implements Synchronizer.Counter {

    private static final String TAG = "Game";

    private long timeRemainingInMillis;

    /**
     * For a new game, the maxTime is always specified by the game mode. 30 second game? maxTime
     * will be 30 seconds. However, when resuming a game, maxTime is the amount of time left in
     * the game at the time it is resumed.
     */
    private long maxTimeSinceResumeInMillis;

    private Board board;
    private int score;

    public enum GameStatus {GAME_STARTING, GAME_RUNNING, GAME_PAUSED, GAME_FINISHED}

    public static final int SOUND_WORD_GOOD = 0;
    public static final int SOUND_WORD_ALREADYFOUND = 1;
    public static final int SOUND_WORD_BAD = 2;
    public static final int SOUND_1TILE = 3;
    public static final int SOUND_2TILES = 4;
    public static final int SOUND_3TILES = 5;
    public static final int SOUND_4TILES = 6;
    public static final int SOUND_5TILES = 7;
    public static final int SOUND_6TILES = 8;
    public static final int SOUND_7TILES = 9;
    public static final int SOUNDS_COUNTOF = 10;
    public static final int SOUNDS_TILES_COUNTOF = SOUND_7TILES - SOUND_1TILE + 1;

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

    private Map<String, List<Solution>> solutions;

    private AudioManager mgr;
    private SoundPool mSoundPool;
    private int[] sysSoundIds;

    private final GameMode gameMode;
    private final Language language;

    public Game(Context context, GameSaver saver) {

        gameMode = saver.readGameMode();
        status = GameStatus.GAME_STARTING;
        wordCount = 0;

        language = saver.readLanguage();
        loadSounds(context);

        try {
            switch (gameMode.getBoardSize()) {
                case 16:
                    setBoard(context, new FourByFourBoard(saver.readGameBoard()));
                    break;
                case 25:
                    setBoard(context, new FiveByFiveBoard(saver.readGameBoard()));
                    break;
                case 36:
                    setBoard(context, new SixBySixBoard(saver.readGameBoard()));
                    break;
            }

            timeRemainingInMillis = saver.readTimeRemainingInMillis();
            maxTimeSinceResumeInMillis = timeRemainingInMillis;
            start = saver.readStart();

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



    public Game(Context context, GameMode gameMode, Language language, CharProbGenerator.BoardSeed boardSeed) {
        this.language = language;
        this.gameMode = gameMode;
        status = GameStatus.GAME_STARTING;
        wordCount = 0;
        wordList = new LinkedList<>();

        loadSounds(context);

        CharProbGenerator charProbs = getCharProbGenerator(context);
        Board board;

        switch (gameMode.getBoardSize()) {
            case 16:
                board = charProbs.generateFourByFourBoard(boardSeed);
                break;

            case 25:
                board = charProbs.generateFiveByFiveBoard(boardSeed);
                break;

            case 36:
                board = charProbs.generateSixBySixBoard(boardSeed);
                break;

            default:
                throw new IllegalStateException("Board must be 16, 25, or 36 large");
        }

        setBoard(context, board);

        timeRemainingInMillis = gameMode.getTimeLimitSeconds() * 1000L;
        maxTimeSinceResumeInMillis = gameMode.getTimeLimitSeconds() * 1000L;
        score = 0;
        wordsUsed = new LinkedHashSet<>();
        initializeWeights();
    }

    @NonNull
    private CharProbGenerator getCharProbGenerator(Context context) {
        String lettersFileName = language.getLetterDistributionFileName();
        int id = context.getResources().getIdentifier("raw/" + lettersFileName.substring(0, lettersFileName.lastIndexOf('.')), null, context.getPackageName());
        CharProbGenerator charProbs = new CharProbGenerator(context.getResources().openRawResource(id), getLanguage());
        return charProbs;
    }

    /**
     * TODO: This is not a very pure function. The Game constructor loads preferences, reads sounds from disk, and probably does
     *       many other things. This should be refactored so that it is more predictable what happens.
     */
    public static Game generateGame(@NonNull Context context, @NonNull GameMode gameMode, @NonNull Language language) {
        return generateGame(context, gameMode, language, null);
    }

    public static Game generateGame(@NonNull Context context, @NonNull GameMode gameMode, @NonNull Language language, Long seed) {
        Game bestGame = new Game(context, gameMode, language, new CharProbGenerator.BoardSeed(seed));
        Board lastBoard = bestGame.board; // needed so we still receive deterministic boards, even when skipping tries

        int numAttempts = 0;
        while (bestGame.getMaxWordCount() < 45 && numAttempts < 5) {
            Log.d(TAG, "Generating another board, because the previous one only had " + bestGame.getMaxWordCount() + " words, but we want at least 45. Will give up after 5 tries.");

            Game nextAttempt = new Game(context, gameMode, language, CharProbGenerator.BoardSeed.fromPreviousBoard(lastBoard));
            lastBoard = nextAttempt.getBoard();

            if (nextAttempt.getMaxWordCount() > bestGame.getMaxWordCount()) {
                bestGame = nextAttempt;
            }
            numAttempts ++;
        }

        Log.d(TAG, "Generated new board with " + bestGame.getMaxWordCount() + " words");
        return bestGame;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public Language getLanguage() {
        return language;
    }

    private void initSoundPool(Context c) {
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
        sysSoundIds = new int[SOUNDS_COUNTOF];

        sysSoundIds[SOUND_WORD_GOOD]         = mSoundPool.load(c, R.raw.word_good, 1);
        sysSoundIds[SOUND_WORD_ALREADYFOUND] = mSoundPool.load(c, R.raw.word_alreadyfound, 1);
        sysSoundIds[SOUND_WORD_BAD]          = mSoundPool.load(c, R.raw.word_bad, 1);
        sysSoundIds[SOUND_1TILE]             = mSoundPool.load(c, R.raw.tiles_1, 1);
        sysSoundIds[SOUND_2TILES]            = mSoundPool.load(c, R.raw.tiles_2, 1);
        sysSoundIds[SOUND_3TILES]            = mSoundPool.load(c, R.raw.tiles_3, 1);
        sysSoundIds[SOUND_4TILES]            = mSoundPool.load(c, R.raw.tiles_4, 1);
        sysSoundIds[SOUND_5TILES]            = mSoundPool.load(c, R.raw.tiles_5, 1);
        sysSoundIds[SOUND_6TILES]            = mSoundPool.load(c, R.raw.tiles_6, 1);
        sysSoundIds[SOUND_7TILES]            = mSoundPool.load(c, R.raw.tiles_7, 1);

        mgr = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
    }

    private void playSound(int soundId) {
        if (mSoundPool != null) {
            float actualVolume = (float) mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
            float maxVolume = (float) mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            float volume = actualVolume / maxVolume;
            mSoundPool.play(sysSoundIds[soundId], volume, volume, 1, 0, 1f);
        }
    }

    private void setBoard(Context context, Board b) {
        board = b;
        initializeDictionary(context);
    }

    private void loadSounds(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (prefs.getBoolean("soundsEnabled", false)) {
            initSoundPool(context);
        }
    }

    public void initializeDictionary(Context context) {
        initializeDictionary(context, language);
    }

    private void initializeDictionary(Context context, Language language) {
        try {
            String trieFileName = language.getTrieFileName();
            int id = context.getResources().getIdentifier("raw/" + trieFileName.substring(0, trieFileName.lastIndexOf('.')), null, context.getPackageName());
            Trie dict = new StringTrie.Deserializer().deserialize(context.getResources().openRawResource(id), board, language);

            solutions = dict.solver(board, w -> w.length() >= gameMode.getMinWordLength());

            Log.d(TAG, "Initializing " + language.getName() + " dictionary");
            for (String word : solutions.keySet()) {
                maxWordCountsByLength.put(word.length(), maxWordCountsByLength.get(word.length()) + 1);

                // For debugging and diagnosis, it is convenient to have access to all the words
                // for some boards printed to the log. This is especially true seeing as I can only
                // speak / read English, and thus am unable to play the boards of additional
                // languages without this aid. Once they go out of beta, then it seems inappropriate
                // to print this.
                if (language.isBeta()) {
                    StringBuilder sb = new StringBuilder();
                    for (char c : word.toCharArray()) {
                        sb.append(language.toRepresentation(Character.valueOf(c).toString()));
                    }
                    Log.d(TAG, "Word: " + sb.toString().toUpperCase(getLanguage().getLocale()));
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
        weights = new int[gameMode.getBoardSize()];

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
        saver.save(board, timeRemainingInMillis, gameMode, language, wordListToString(), wordCount, start, status);
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

    public void playTileSound(int numTiles) {
        if (numTiles < 1)
            numTiles = 1;
        else if (numTiles > SOUNDS_TILES_COUNTOF)
            numTiles = SOUNDS_TILES_COUNTOF;

        playSound(SOUND_1TILE + numTiles - 1);
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
                playSound(SOUND_WORD_ALREADYFOUND);
            } else {
                // Word has not been found before
                wordCount++;
                score += getWordScore(cap);
                wordCountsByLength.put(cap.length(), wordCountsByLength.get(cap.length()) + 1);
                wordList.addFirst(word);
                playSound(SOUND_WORD_GOOD);
                removeWeight(cap);

                if (wordCount == solutions.size()) {
                    endNow();
                }
            }
        } else {
            // Word is not really a word
            wordList.addFirst(word);
            playSound(SOUND_WORD_BAD);
        }
        wordsUsed.add(cap);
    }

    public int getWordScore(String word) {
        if (GameMode.SCORE_WORDS.equals(gameMode.getScoreType())) {
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
        return gameMode.getScoreType();
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
        return gameMode.hintModeCount();
    }

    public boolean hintModeColor() {
        return gameMode.hintModeColor();
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

    public long tick() {

        // It isn't jsut the tick which lowers this, the endGame functionality does too, so check first.
        if (timeRemainingInMillis <= 0) {
            status = GameStatus.GAME_FINISHED;
            timeRemainingInMillis = 0;
            return 0;
        }

        Date now = new Date();
        int timeElapsedInMillis = (int) (now.getTime() - start.getTime());
        timeRemainingInMillis = Math.max(0, maxTimeSinceResumeInMillis - timeElapsedInMillis);
        Log.d(TAG, "Tick " + timeRemainingInMillis);

        if (timeRemainingInMillis <= 0) {
            status = GameStatus.GAME_FINISHED;
            timeRemainingInMillis = 0;
        }

        return timeRemainingInMillis;
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
        maxTimeSinceResumeInMillis = timeRemainingInMillis;
        start = new Date();
    }

    public void endNow() {
        timeRemainingInMillis = 0;
    }

    public Map<String, List<Solution>> getSolutions() {
        return solutions;
    }

    public void rotateBoard() {
        board.rotate();
        if (mRotateHandler != null)
            mRotateHandler.onRotate();
    }

    public void setRotateHandler(RotateHandler rh) {
        mRotateHandler = rh;
    }

    public interface RotateHandler {
        void onRotate();
    }
}

