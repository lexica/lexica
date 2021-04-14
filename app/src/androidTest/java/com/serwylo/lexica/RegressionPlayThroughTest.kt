package com.serwylo.lexica


import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.serwylo.lexica.db.Database
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.lang.LanguageLabel
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
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
     * https://stackoverflow.com/a/42908995/2391921 (for room database initialization
     */
    @Before
    fun setup() {
        val targetContext = InstrumentationRegistry.getTargetContext()

        Database.get(targetContext).clearAllTables()
        PreferenceManager.getDefaultSharedPreferences(targetContext).edit().clear().apply()

        mActivityTestRule.launchActivity(null)
    }

    @Test
    fun generateLanguageBoards2Test() {

        fromSplashMaybeDismissChangelog()
        fromSplashSwitchTheme(ThemeManager.THEME_LIGHT)
        fromSplashChooseLeicon()
        selectLanguage(language)

        fromSplashCreateGameMode("Test Mode With Long Name")
        fromSplashSwitchGameModeByLabel("Test Mode With Long Name")
        fromSplashRunThroughGame()

        fromSplashSwitchGameMode(GameMode.Type.SPRINT)
        fromSplashRunThroughGame()

        fromSplashSwitchTheme(ThemeManager.THEME_DARK)
        fromSplashSwitchGameMode(GameMode.Type.LETTER_POINTS)
        fromSplashRunThroughGame()

        fromSplashSwitchTheme(ThemeManager.THEME_HIGH_CONTRAST)
        fromSplashSwitchGameMode(GameMode.Type.MARATHON)
        fromSplashRunThroughGame()

    }

    private fun fromSplashCreateGameMode(gameModeName: String) {

        clickId(R.id.game_mode_button)
        clickId(R.id.new_game_mode)

        val label = onView(allOf(withId(R.id.label), isDisplayed()))
        label.perform(replaceText(gameModeName), ViewActions.closeSoftKeyboard())

        val time = onView(allOf(withId(R.id.time), isDisplayed()))
        time.perform(replaceText("120"), ViewActions.closeSoftKeyboard())

        clickId(R.id.board_size_6x6)
        clickId(R.id.score_type_letter_points)
        clickId(R.id.min_word_length_4)
        clickId(R.id.hint_tile_count)
        clickId(R.id.hint_colour)

        clickId(R.id.save)
        pressBack() // Save takes us back to the list of game modes, so lets return to the splash screen.

    }

    private fun fromSplashSwitchGameMode(type: GameMode.Type) {
        fromSplashChooseGameMode()
        selectGameModeItem { it.type == type }
        pressBack()
    }

    private fun fromSplashSwitchGameModeByLabel(label: String) {
        fromSplashChooseGameMode()
        selectGameModeItem { it.type == GameMode.Type.CUSTOM && it.customLabel == label }
        pressBack()
    }

    private fun fromSplashSwitchTheme(theme: String) {
        fromSplashShowSettings()
        fromSettingsOpenThemeChooser()
        fromSettingsSelectTheme(theme)
        pressBack()
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

    private fun fromSplashMaybeDismissChangelog() {
        try {
            clickIdWithLabel(R.id.button, R.string.whats_new_continue);
        } catch (_: NoMatchingViewException) {
            // When running through multiple play through's, the first play should *not* have this view, but all others likely will.
        }
    }

    private fun fromHighScoresViewFoundWords() {
        clickId(R.id.view_found_words)
    }

    private fun fromSplashViewHighScores() {
        clickId(R.id.high_score_label)
    }

    private fun fromViewFoundWordsChangeLanguage() {
        clickId(R.id.language)

        onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                        0))
                .atPosition(5)
                .perform(click())
    }

    private fun fromHighScoresChangeGameMode() {
        clickId(R.id.game_mode)

        onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                        0))
                .atPosition(2)
                .perform(click())
    }

    private fun fromHighScoresChangeLanguage() {
        clickId(R.id.language)

        onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
                        0))
                .atPosition(5)
                .perform(click())
    }

    private fun fromSettingsSelectTheme(theme: String) {
        val themes = mActivityTestRule.activity.applicationContext.resources.getStringArray(R.array.theme_choices_entryvalues)
        val themeIndex = themes.indexOf(theme)

        onData(anything())
                .inAdapterView(withId(R.id.select_dialog_listview))
                .atPosition(themeIndex)
                .perform(click())
    }

    private fun fromSettingsOpenThemeChooser() {
        val recyclerView4 = onView(
                allOf(withId(R.id.recycler_view),
                        childAtPosition(
                                withId(android.R.id.list_container),
                                0)))
        recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(1, click()))
    }

    private fun fromSplashShowSettings() {
        clickId(R.id.preferences)
    }

    private fun fromScoreSelectBack() {
        clickId(R.id.back_button)
    }

    private fun fromMissedWordsSelectWordViaButton() {
        val fancyButton9 = onView(
                allOf(withId(R.id.view_word),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.words),
                                        1),
                                2),
                        isDisplayed()))
        fancyButton9.perform(click())
    }

    private fun fromMissedWordsSelectWordViaRow() {
        val recyclerView3 = onView(
                allOf(withId(R.id.words),
                        childAtPosition(
                                withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                                1)))
        recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(0, click()))
    }

    private fun fromMissedWordsSort() {
        clickId(R.id.btn_sort)
    }

    private fun fromScoreSelectMissedWords() {
        clickId(R.id.missed_words_button)
    }

    private fun fromGameEndGame() {
        clickId(R.id.end_game)
    }

    private fun fromSplashRestoreGame() {
        clickId(R.id.restore_game)
    }

    private fun navigateUpEmu27() {
        val appCompatImageButton = onView(allOf(withContentDescription("Navigate up"), isDisplayed()))
        appCompatImageButton.perform(click())
    }

    private fun fromGameRotateBoard() {
        clickId(R.id.rotate)
    }

    private fun fromSplashStartNewGame() {
        clickId(R.id.new_game)
    }

    private fun selectLanguage(language: Language) {
        val languageNames = LanguageLabel.getAllLanguagesSorted(mActivityTestRule.activity.applicationContext).map { it.name }
        val languageIndex = languageNames.indexOf(language.name)

        onView(withId(R.id.lexicon_list))
                .perform(actionOnItemAtPosition<ChooseLexiconActivity.ViewHolder>(languageIndex, click()))
    }

    private fun fromSplashChooseLeicon() {
        clickId(R.id.language_button)
    }

    private fun selectGameModeItem(filter: (GameMode) -> Boolean) {
        val db = Database.get(mActivityTestRule.activity.applicationContext)
        val gameModeIndex = db.gameModeDao()
                .getAllGameModesSynchronous()
                .indexOfFirst(filter)

        onView(withId(R.id.game_mode_list))
                .perform(actionOnItemAtPosition<ChooseLexiconActivity.ViewHolder>(gameModeIndex, click()))
    }

    private fun fromSplashChooseGameMode() {
        clickId(R.id.game_mode_button)
    }

    private fun clickId(@IdRes id: Int) {
        onView(allOf(withId(id), isDisplayed())).perform(click())
    }

    private fun clickIdWithLabel(@IdRes id: Int, @StringRes stringId: Int) {
        onView(allOf(withId(id), isDisplayed())).perform(click())
    }

    private fun childAtPosition(
            parentMatcher: Matcher<View>, position: Int): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }

    companion object {
        @Parameterized.Parameters(name="{index}: {0}")
        @JvmStatic
        fun getAllLanguages(): Collection<Language> {
            return Language.allLanguages.values
        }
    }
}
