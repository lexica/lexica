package com.serwylo.lexica.db.migration;

import android.content.Context;

import com.serwylo.lexica.lang.Language;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

/*
 * Here is an example preference file containing high scores from a pre-lexica-having-a-database version.
 * Note that the preference key is a combination of all the things that dictate whether a high score is for a distinct
 * game type or not. The value itself is the high score.
 *
 * In order to ensure that peoples hard-earned high scores are not lost during the migration, these tests will
 * try to offer some level of assurance that we retain custom game modes.
 *
 * Here is an example from some saved boards in a test scenario:
 *
 * <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
 * <map>
 *     <int name="US36L600" value="8" />
 *     <int name="fr_FR16W180" value="5" />
 *     <int name="US16W180" value="7" />
 * </map>
 */

public class MigrateHighScoresFromPreferencesTest {

    @Test
    public void migrateHighScores() throws Language.NotFound {
        MigrateHighScoresFromPreferences migration = new MigrateHighScoresFromPreferences(mock(Context.class));

        // Given we are migrating during startup for a new version, we really don't want to crash, so it should
        // return null if it gets junk somehow.
        assertNull(migration.maybeGameModeFromPref("INVALID?", "15"));

        MigrateHighScoresFromPreferences.LegacyHighScore usLargeBoard = migration.maybeGameModeFromPref("US36L600", 8);
        assertEquals(Language.from("en_US").getName(), usLargeBoard.getLanguage().getName());
        assertEquals(36, usLargeBoard.getGameMode().getBoardSize());
        assertEquals("L", usLargeBoard.getGameMode().getScoreType());
        assertEquals(600, usLargeBoard.getGameMode().getTimeLimitSeconds());
        assertEquals(5, usLargeBoard.getGameMode().getMinWordLength()); // Not encoded in the preference, but based on the board size.
        assertEquals(8, usLargeBoard.getHighScore());

        MigrateHighScoresFromPreferences.LegacyHighScore frSmallerBoard = migration.maybeGameModeFromPref("fr_FR16W180", 132);
        assertEquals(Language.from("fr_FR").getName(), frSmallerBoard.getLanguage().getName());
        assertEquals(16, frSmallerBoard.getGameMode().getBoardSize());
        assertEquals("W", frSmallerBoard.getGameMode().getScoreType());
        assertEquals(180, frSmallerBoard.getGameMode().getTimeLimitSeconds());
        assertEquals(3, frSmallerBoard.getGameMode().getMinWordLength()); // Not encoded in the preference, but based on the board size.
        assertEquals(132, frSmallerBoard.getHighScore());

        MigrateHighScoresFromPreferences.LegacyHighScore deMediumBoard = migration.maybeGameModeFromPref("de_DE25W180", 7);
        assertEquals(Language.from("de_DE").getName(), deMediumBoard.getLanguage().getName());
        assertEquals(25, deMediumBoard.getGameMode().getBoardSize());
        assertEquals("W", deMediumBoard.getGameMode().getScoreType());
        assertEquals(180, deMediumBoard.getGameMode().getTimeLimitSeconds());
        assertEquals(4, deMediumBoard.getGameMode().getMinWordLength()); // Not encoded in the preference, but based on the board size.
        assertEquals(7, deMediumBoard.getHighScore());
    }

}
