package com.serwylo.lexica

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.test.InstrumentationRegistry
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withClassName
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.serwylo.lexica.db.Database
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.lang.LanguageLabel
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.anything
import org.hamcrest.Matchers.`is`
import org.hamcrest.TypeSafeMatcher

/**
 * Setup Lexica as if it was freshly installed. Be sure to call *before* the
 * call to `mActivityTestRule.launchActivity(null)`.
 */
fun clearDbAndPreferences() {
    val targetContext = InstrumentationRegistry.getTargetContext()
    Database.get(targetContext).clearAllTables()
    PreferenceManager.getDefaultSharedPreferences(targetContext).edit().clear().apply()
}

fun fromSplashCreateGameMode(gameModeName: String) {

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

fun fromSplashSwitchGameMode(context: Context, type: GameMode.Type) {
    fromSplashChooseGameMode()
    selectGameModeItem(context) { it.type == type }
    pressBack()
}

fun fromSplashSwitchGameModeByLabel(context: Context, label: String) {
    fromSplashChooseGameMode()
    selectGameModeItem(context) { it.type == GameMode.Type.CUSTOM && it.customLabel == label }
    pressBack()
}

fun fromSplashSwitchTheme(context: Context, theme: String) {
    fromSplashShowSettings()
    fromSettingsOpenThemeChooser()
    fromSettingsSelectTheme(context, theme)
    pressBack()
}

fun fromSplashShowSettings() {
    clickId(R.id.preferences)
}

fun fromScoreSelectBack() {
    navigateUpEmu27()
}

fun fromMissedWordsSelectWordViaButton() {
    val materialButton = onView(
        allOf(withId(R.id.view_word),
            childAtPosition(
                childAtPosition(
                    withId(R.id.words),
                    1),
                2),
            isDisplayed()))
    materialButton.perform(click())
}

fun fromMissedWordsSelectWordViaRow() {
    val recyclerView3 = onView(
        allOf(withId(R.id.words),
            childAtPosition(
                withClassName(`is`("androidx.constraintlayout.widget.ConstraintLayout")),
                1)))
    recyclerView3.perform(actionOnItemAtPosition<ViewHolder>(0, click()))
}

fun fromMissedWordsSort() {
    clickId(R.id.btn_sort)
}

fun fromScoreSelectMissedWords() {
    clickId(R.id.missed_words_button)
}

fun fromGameEndGame() {
    val targetContext = InstrumentationRegistry.getTargetContext()

    val overflowMenuButton = onView(
        allOf(
            withContentDescription("More options"),
            childAtPosition(
                childAtPosition(
                    withId(R.id.toolbar),
                    2
                ),
                1
            ),
            isDisplayed()
        )
    )
    overflowMenuButton.perform(click())

    val endGameMenuButton = onView(
        allOf(
            withId(androidx.drawerlayout.R.id.title), withText(targetContext.getString(R.string.menu_end_game)),
            childAtPosition(
                childAtPosition(
                    withId(com.google.android.material.R.id.content),
                    0
                ),
                0
            ),
            isDisplayed()
        )
    )
    endGameMenuButton.perform(click())

    val dialogConfirmButton = onView(
        allOf(withId(android.R.id.button1), withText(targetContext.getString(R.string.menu_end_game)),
            childAtPosition(
                childAtPosition(
                    withId(com.google.android.material.R.id.buttonPanel),
                    0),
                3)));
    dialogConfirmButton.perform(scrollTo(), click());
}

fun fromSplashRestoreGame() {
    clickId(R.id.restore_game)
}

fun navigateUpEmu27() {
    val appCompatImageButton = onView(allOf(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description), isDisplayed()));
    appCompatImageButton.perform(click());
}

fun fromGameRotateBoard() {
    clickId(R.id.rotate)
}

fun fromSplashStartNewGame() {
    clickId(R.id.new_game)
}

fun selectLanguage(context: Context, language: Language) {
    val languageNames = LanguageLabel.getAllLanguagesSorted(context).map { it.name }
    val languageIndex = languageNames.indexOf(language.name)

    onView(withId(R.id.lexicon_list))
        .perform(actionOnItemAtPosition<ChooseLexiconActivity.ViewHolder>(languageIndex, click()))
}

fun fromSplashChooseLexicon() {
    clickId(R.id.language_button)
}

private fun selectGameModeItem(context: Context, filter: (GameMode) -> Boolean) {
    val db = Database.get(context)
    val gameModeIndex = db.gameModeDao()
        .getAllGameModesSynchronous()
        .indexOfFirst(filter)

    onView(withId(R.id.game_mode_list))
        .perform(actionOnItemAtPosition<ChooseLexiconActivity.ViewHolder>(gameModeIndex, click()))
}

private fun fromSplashChooseGameMode() {
    clickId(R.id.game_mode_button)
}

fun clickId(@IdRes id: Int) {
    onView(allOf(withId(id), isDisplayed())).perform(click())
}

fun clickIdWithLabel(@IdRes id: Int, @StringRes stringId: Int) {
    onView(allOf(withId(id), isDisplayed())).perform(click())
}

fun childAtPosition(
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

fun fromSplashMaybeDismissChangelog() {
    try {
        clickIdWithLabel(R.id.button, R.string.whats_new_continue);
    } catch (_: NoMatchingViewException) {
        // When running through multiple play through's, the first play should *not* have this view, but all others likely will.
    }
}

fun fromHighScoresViewFoundWords() {
    clickId(R.id.view_found_words)
}

fun fromSplashViewHighScores() {
    clickId(R.id.high_score_label)
}

fun fromViewFoundWordsChangeLanguage() {
    clickId(R.id.language)

    onData(anything())
        .inAdapterView(childAtPosition(
            withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
            0))
        .atPosition(5)
        .perform(click())
}

fun fromHighScoresChangeGameMode() {
    clickId(R.id.game_mode)

    onData(anything())
        .inAdapterView(childAtPosition(
            withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
            0))
        .atPosition(2)
        .perform(click())
}

fun fromHighScoresChangeLanguage() {
    clickId(R.id.language)

    onData(anything())
        .inAdapterView(childAtPosition(
            withClassName(`is`("android.widget.PopupWindow\$PopupBackgroundView")),
            0))
        .atPosition(5)
        .perform(click())
}

private fun fromSettingsSelectTheme(context: Context, theme: String) {
    val themes = context.resources.getStringArray(R.array.theme_choices_entryvalues)
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

    recyclerView4.perform(actionOnItemAtPosition<ViewHolder>(0, click()))
}