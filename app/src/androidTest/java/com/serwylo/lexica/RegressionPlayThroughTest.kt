package com.serwylo.lexica


import androidx.test.espresso.Espresso.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * The current intention of this test is to be run manually before a release, rather than via
 * CI on every commit. Once it tests something more meaningful than starting a game and ending it
 * (e.g. simulating users dragging their fingers on a real board, selecting real words) then it
 * should probably be promoted to CI.
 *
 * To be run on at least emulator 29 (e.g. because there are some system strings, such as the action
 * menu overflow "more options" text which may be specific to this version.
 */
@LargeTest
@RunWith(Parameterized::class)
class RegressionPlayThroughTest (val language: Language) {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java, true, false)

    /**
     * https://stackoverflow.com/a/42908995/2391921 (for room database initialization)
     */
    @Before
    fun setup() {

        clearDbAndPreferences()
        mActivityTestRule.launchActivity(null)
    }

    @Test
    fun generateLanguageBoards2Test() {

        val context = mActivityTestRule.activity.applicationContext

        fromSplashMaybeDismissChangelog()
        fromSplashSwitchTheme(context, ThemeManager.THEME_LIGHT)
        fromSplashChooseLexicon()
        selectLanguage(context, language)

        fromSplashCreateGameMode("Test Mode With Long Name")
        fromSplashSwitchGameModeByLabel(context, "Test Mode With Long Name")
        fromSplashRunThroughGame()

        fromSplashSwitchGameMode(context, GameMode.Type.SPRINT)
        fromSplashRunThroughGame()

        fromSplashSwitchTheme(context, ThemeManager.THEME_DARK)
        fromSplashSwitchGameMode(context, GameMode.Type.LETTER_POINTS)
        fromSplashRunThroughGame()

        fromSplashSwitchTheme(context, ThemeManager.THEME_HIGH_CONTRAST)
        fromSplashSwitchGameMode(context, GameMode.Type.MARATHON)
        fromSplashRunThroughGame()
    }

    /**
     * General smoke testing of the game process, including saving, restoring, rotating, and
     * clicking around the score screen at the end, finally returning back to the splash screen.
     *
     * Note: This doesn't yet interact with the board itself in order to select words :(
     */
    private fun fromSplashRunThroughGame() {
        fromSplashStartNewGame()

        fromGameRotateBoard()
        navigateUpEmu27()

        fromSplashRestoreGame()
        fromGameRotateBoard()
        fromGameEndGame()

        fromScoreSelectMissedWords()

        // Four sorts (asc vs desc x score vs alphabetical)
        fromMissedWordsSort()
        fromMissedWordsSort()
        fromMissedWordsSort()
        fromMissedWordsSort()

        fromMissedWordsSelectWordViaRow()
        fromMissedWordsSelectWordViaButton()
        fromScoreSelectBack()

        fromSplashViewHighScores()
        fromHighScoresChangeLanguage()
        fromHighScoresChangeGameMode()

        fromHighScoresViewFoundWords()
        fromViewFoundWordsChangeLanguage()

        // First return from viewing found words, which takes us to the high scores, then return to the main menu.
        navigateUpEmu27()
        navigateUpEmu27()
    }

    companion object {
        @Parameterized.Parameters(name="{index}: {0}")
        @JvmStatic
        fun getAllLanguages(): Collection<Language> {
            return Language.allLanguages.values
        }
    }
}
