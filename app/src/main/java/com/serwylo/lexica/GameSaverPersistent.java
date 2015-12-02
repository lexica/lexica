package com.serwylo.lexica;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.Game;

import java.util.Date;

public class GameSaverPersistent extends GameSaver {

	private static final String SAVE_PREF_FILE = "prefs_game_file";

	@NonNull
	private final Context context;

	public GameSaverPersistent(@NonNull Context context) {
		this.context = context;
	}

	public SharedPreferences getPrefs() {
		return context.getSharedPreferences(SAVE_PREF_FILE, Context.MODE_PRIVATE);
	}

	@Override
	public boolean hasSavedGame() {
		return getPrefs().getBoolean(ACTIVE_GAME, false);
	}

	@Override
	public int readWordCount() {
		return getPrefs().getInt(WORD_COUNT, DEFAULT_WORD_COUNT);
	}

	@Override
	public String[] readWords() {
		return safeSplit(getPrefs().getString(WORDS, null));
	}

	@Override
	public int readMaxTimeRemaining() {
		return getPrefs().getInt(MAX_TIME_REMAINING, DEFAULT_MAX_TIME_REMAINING);
	}

	@Override
	public int readTimeRemaining() {
		return getPrefs().getInt(TIME_REMAINING, DEFAULT_TIME_REMAINING);
	}

	@Override
	public String[] readGameBoard() {
		return safeSplit(getPrefs().getString(GAME_BOARD, null));
	}

	@Override
	public int readBoardSize() {
		return getPrefs().getInt(BOARD_SIZE, DEFAULT_BOARD_SIZE);
	}

	@Override
	public Game.GameStatus readStatus() {
		return Game.GameStatus.GAME_STARTING;
	}

	@Override
	public Date readStart() {
		return null;
	}

	@Override
	public void save(Board board, int timeRemaining, int maxTimeRemaining, String wordListToString, int wordCount, Date start, Game.GameStatus status) {

		SharedPreferences.Editor prefs = getPrefs().edit();
		prefs.putInt(BOARD_SIZE,board.getSize());

		prefs.putString(GAME_BOARD, board.toString());
		prefs.putInt(TIME_REMAINING, timeRemaining);
		prefs.putInt(MAX_TIME_REMAINING,maxTimeRemaining);
		prefs.putString(WORDS, wordListToString);
		prefs.putInt(WORD_COUNT, wordCount);

		prefs.putBoolean(ACTIVE_GAME, true);
		prefs.commit();
	}

	public void clearSavedGame() {
		SharedPreferences.Editor prefs = getPrefs().edit();
		prefs.putBoolean(ACTIVE_GAME, false);
		prefs.commit();
	}
}
