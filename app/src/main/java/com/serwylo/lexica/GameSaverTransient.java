package com.serwylo.lexica;

import android.os.Bundle;

import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.lang.Language;

import java.util.Date;

public class GameSaverTransient extends GameSaver {

    private final Bundle bundle;

    public GameSaverTransient(Bundle bundle) {
        this.bundle = bundle;
    }

    @Override
    public boolean hasSavedGame() {
        return bundle.getBoolean(ACTIVE_GAME, false);
    }

    @Override
    public int readWordCount() {
        return bundle.getInt(WORD_COUNT, DEFAULT_WORD_COUNT);
    }

    @Override
    public String[] readWords() {
        return safeSplit(bundle.getString(WORDS));
    }

    @Override
    public GameMode readGameMode() {
        return bundle.getParcelable(GAME_MODE);
    }

    @Override
    public long readTimeRemainingInMillis() {
        return bundle.getLong(TIME_REMAINING_IN_MILLIS, DEFAULT_TIME_REMAINING);
    }

    @Override
    public String[] readGameBoard() {
        return safeSplit(bundle.getString(GAME_BOARD));
    }

    @Override
    public Game.GameStatus readStatus() {
        String status = bundle.getString(STATUS);
        return status == null ? null : Game.GameStatus.valueOf(status);
    }

    @Override
    public Date readStart() {
        long start = bundle.getLong(START, 0);
        return start == 0 ? null : new Date(start);
    }

    @Override
    public Language readLanguage() {
        String langName = bundle.getString(LANGUAGE);
        if (langName == null) {
            throw new IllegalStateException("Unable to resume game as language is not specified.");
        }

        try {
            return Language.from(langName);
        } catch (Language.NotFound e) {
            throw new IllegalStateException("Unable to resume game as language \"" + langName + "\" not found.");
        }
    }

    @Override
    public void save(Board board, long timeRemainingInMillis, GameMode gameMode, Language language, String wordListToString, int wordCount, Date start, Game.GameStatus status) {
        bundle.putString(GameSaver.GAME_BOARD, board.toString());
        bundle.putLong(GameSaver.TIME_REMAINING_IN_MILLIS, timeRemainingInMillis);
        bundle.putParcelable(GameSaver.GAME_MODE, gameMode);
        bundle.putString(GameSaver.LANGUAGE, language.getName());
        bundle.putString(GameSaver.WORDS, wordListToString);
        bundle.putInt(GameSaver.WORD_COUNT, wordCount);
        bundle.putLong(GameSaver.START, start == null ? 0 : start.getTime());
        bundle.putString(GameSaver.STATUS, status.toString());

        bundle.putBoolean(GameSaver.ACTIVE_GAME, true);
    }
}
