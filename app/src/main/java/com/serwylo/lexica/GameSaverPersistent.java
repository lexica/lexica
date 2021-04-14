package com.serwylo.lexica;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.lang.Language;

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
    public GameMode readGameMode() {
        String gameModeString = getPrefs().getString(GAME_MODE, null);
        if (gameModeString == null) {
            throw new IllegalStateException("Could not deserialize game mode for saved game, as the saved value was null.");
        }

        return GameMode.deserialize(gameModeString);
    }

    @Override
    public long readTimeRemainingInMillis() {
        return getPrefs().getLong(TIME_REMAINING_IN_MILLIS, DEFAULT_TIME_REMAINING);
    }

    @Override
    public String[] readGameBoard() {
        return safeSplit(getPrefs().getString(GAME_BOARD, null));
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
    public Language readLanguage() {
        String langName = getPrefs().getString(LANGUAGE, null);
        if (langName == null) {
            throw new IllegalStateException("Could not deserialize language for saved game, as the saved value was null.");
        }

        try {
            return Language.from(langName);
        } catch (Language.NotFound notFound) {
            throw new IllegalStateException("Could not deserialize language for saved game, as \"" + langName + "\" is not a valid language.");
        }
    }

    @Override
    public void save(Board board, long timeRemainingInMillis, GameMode gameMode, Language language, String wordListToString, int wordCount, Date start, Game.GameStatus status) {

        SharedPreferences.Editor prefs = getPrefs().edit();
        prefs.putString(GAME_MODE, gameMode.serialize());
        prefs.putString(LANGUAGE, language.getName());
        prefs.putString(GAME_BOARD, board.toString());
        prefs.putLong(TIME_REMAINING_IN_MILLIS, timeRemainingInMillis);
        prefs.putString(WORDS, wordListToString);
        prefs.putInt(WORD_COUNT, wordCount);

        prefs.putBoolean(ACTIVE_GAME, true);
        prefs.apply();
    }

    public void clearSavedGame() {
        SharedPreferences.Editor prefs = getPrefs().edit();
        prefs.putBoolean(ACTIVE_GAME, false);
        prefs.apply();
    }
}
