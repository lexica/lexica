package com.serwylo.lexica.db.migration;

import android.content.Context;
import android.util.Log;

import com.serwylo.lexica.activities.score.ScoreActivity;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.db.GameModeDao;
import com.serwylo.lexica.db.Result;
import com.serwylo.lexica.db.ResultDao;
import com.serwylo.lexica.lang.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;

public class MigrateHighScoresFromPreferences {

    private static final String TAG = "MigrateScoresFromPrefs";

    private final Context context;

    public MigrateHighScoresFromPreferences(Context context) {
        this.context = context;
    }

    public void initialiseDb(GameModeDao gameModeDao, ResultDao resultDao) {

        Map<Long, GameMode> savedGameModes = new HashMap<>();

        Log.i(TAG, "Creating default game modes.");
        for (GameMode defaultGameMode : defaultGameModes()) {
            savedGameModes.put(gameModeDao.insert(defaultGameMode), defaultGameMode);
        }

        Log.i(TAG, "Looking through legacy preferences to find high scores and migrate to the database.");
        for (LegacyHighScore score : migrateHighScoresFromPrefs()) {

            Log.i(TAG, "Migrating legacy high score (" + score.getHighScore() + " and creating associated custom game mode.");

            long gameModeId = findMatchingGameModeId(score.getGameMode(), savedGameModes);
            if (gameModeId > 0) {
                Log.i(TAG, "Found another high score for game mode " + gameModeId + ", this time for language " + score.getLanguage().getName());
            } else {
                gameModeId = gameModeDao.insert(score.getGameMode());
                savedGameModes.put(gameModeId, score.getGameMode());

                Log.i(TAG, "Created new custom game mode " + gameModeId + " to record high score for language " + score.getLanguage().getName());
            }

            Log.i(TAG, "Saving high score of " + score.getHighScore() + " against game mode " + gameModeId + " for language " + score.getLanguage().getName() + ". Note - will not record other stats like max possible score for that run because they are not available.");
            Result result = Result.builder().gameModeId(gameModeId).langCode(score.getLanguage().getName()).maxNumWords(0).numWords(0).maxScore(score.getHighScore()).score(score.getHighScore()).build();

            resultDao.insert(result);
        }

    }

    long findMatchingGameModeId(GameMode gameMode, Map<Long, GameMode> gameModesToSearch) {
        for (Map.Entry<Long, GameMode> entry : gameModesToSearch.entrySet()) {

            GameMode existingGameMode = entry.getValue();

            if (existingGameMode.getMinWordLength() == gameMode.getMinWordLength() && existingGameMode.getScoreType().equals(gameMode.getScoreType()) && existingGameMode.getTimeLimitSeconds() == gameMode.getTimeLimitSeconds() && existingGameMode.getHintMode().equals(gameMode.getHintMode()) && existingGameMode.getBoardSize() == gameMode.getBoardSize()) {
                return entry.getKey();
            }

        }

        return -1;
    }

    List<GameMode> defaultGameModes() {

        List<GameMode> gameModes = new ArrayList<>(3);

        gameModes.add(GameMode.builder()
                .label("Marathon")
                .description("Try to find all words, without any time pressure")
                .timeLimitSeconds(1800)
                .boardSize(36)
                .hintMode("")
                .minWordLength(5)
                .scoreType(GameMode.SCORE_LETTERS)
                .isCustom(false)
                .build());

        gameModes.add(GameMode.builder()
                .label("Sprint")
                .description("Short game to find as many words as possible")
                .timeLimitSeconds(180)
                .boardSize(25)
                .hintMode("")
                .minWordLength(4)
                .scoreType(GameMode.SCORE_LETTERS)
                .isCustom(false)
                .build());

        gameModes.add(GameMode.builder()
                .label("Beginner")
                .description("Use hints to help find words")
                .timeLimitSeconds(180)
                .boardSize(16)
                .hintMode("hint_both")
                .minWordLength(3)
                .scoreType(GameMode.SCORE_WORDS)
                .isCustom(false)
                .build());

        return gameModes;
    }

    List<LegacyHighScore> migrateHighScoresFromPrefs() {

        List<LegacyHighScore> customGameModes = new ArrayList<>();

        Map<String, ?> prefs = context.getSharedPreferences(ScoreActivity.SCORE_PREF_FILE, Context.MODE_PRIVATE).getAll();;
        for (String key : prefs.keySet()) {

            try {

                LegacyHighScore gameMode = maybeGameModeFromPref(key, prefs.get(key));
                if (gameMode != null) {
                    customGameModes.add(gameMode);
                }

            } catch (NullPointerException | NumberFormatException e) {
                // Be extremely defensive here, because we could end up causing a crash loop when upgrading to a
                // new version of lexica, which is much worse than neglecting to port a high score (I think?)
                Log.e(TAG, "Error while checking for high score preference, ignoring this preference.", e);
            }

        }

        return customGameModes;
    }

    /**
     * The code which used to save high scores generates preference keys like so:
     * <p>
     * prefs.getString("dict", "US")
     * + prefs.getString("boardSize", "16")
     * + prefs.getString(GameMode.SCORE_TYPE, GameMode.SCORE_WORDS)
     * + prefs.getString("maxTimeRemaining", "180");
     * <p>
     * We will use the capture groups of the regex to construct a custom {@link GameMode} with the
     * relevant properties set, and record the {@link Language} the score was for as well.
     */
    LegacyHighScore maybeGameModeFromPref(String key, Object value) {
        Log.d(TAG, "Checking existing preference \"" + key + "\" to see if it is a high score.");

        if (key == null) {
            Log.w(TAG, "Key should have been supplied, but got null.");
            return null;
        }

        Pattern pattern = Pattern.compile("^(\\w\\w(_\\w\\w)?)(\\d+)([WL])(\\d+)$");
        Matcher matcher = pattern.matcher(key);
        if (!matcher.find()) {
            Log.d(TAG, "Does not seem to be a high score.");
            return null;
        }

        Log.i(TAG, "Found existing high score preference \"" + key + "\" with value " + value);

        if (!(value instanceof Integer)) {
            Log.w(TAG, "Expected " + value + " to be an integer.");
            return null;
        }

        String langCode = matcher.group(1);
        int boardSize = Integer.parseInt(Objects.requireNonNull(matcher.group(3)));
        String scoreType = matcher.group(4);
        int maxTimeRemaining = Integer.parseInt(Objects.requireNonNull(matcher.group(5)));

        int highScore = (Integer) value;
        Language language = Language.fromOrNull(langCode);

        if (language == null) {
            Log.e(TAG, "Found legacy high score for \"" + key + "\": " + value + ", but the languge code \"" + langCode + "\" doesn't seem to match a language we know about, so skipping.");
            return null;
        }

        GameMode gameMode = GameMode.builder()
                .label("Custom")
                .description("Used in earlier versions of Lexica")
                .isCustom(true)
                .boardSize(boardSize)
                .scoreType(scoreType)
                .timeLimitSeconds(maxTimeRemaining)
                .minWordLength(getMinWordLength(boardSize))
                .hintMode("")
                .build();

        return new LegacyHighScore(language, gameMode, highScore);
    }

    static int getMinWordLength(int boardSize) {
        switch (boardSize) {
            case 16:
                return 3;
            case 25:
                return 4;
            case 36:
                return 5;
        }

        return 3;
    }

    @AllArgsConstructor
    @Data
    public static class LegacyHighScore {
        private final Language language;
        private final GameMode gameMode;
        private final int highScore;
    }
}
