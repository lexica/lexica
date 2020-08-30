package com.serwylo.lexica;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.serwylo.lexica.lang.Language;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.List;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

/**
 * The current intention of this test is to be run manually before a release, rather than via
 * CI on every commit. Once it tests something more meaningful than starting a game and ending it
 * (e.g. simulating users dragging their fingers on a real board, selecting real words) then it
 * should probably be promoted to CI.
 * <p>
 * To be run on at least emulator 29 (e.g. because there are some system strings, such as the action
 * menu overflow "more options" text which may be specific to this version.
 */
@LargeTest
@RunWith(Parameterized.class)
public class GenerateLanguageBoards {

    private static final String TAG = "GenerateLanguageBoards";

    /**
     * Each language should have a static initializer block which contains a Scrabble-like score for
     * each letter. The compiler cannot enforce that we have
     */
    private final Language language;

    public GenerateLanguageBoards(Language language) {
        super();
        this.language = language;
    }

    @Parameterized.Parameters
    public static List<Language[]> getAllLanguages() {
        List<Language[]> langs = new ArrayList<>(Language.getAllLanguages().size());
        for (Language lang : Language.getAllLanguages().values()) {
            langs.add(new Language[]{lang});
        }
        return langs;
    }

    @Test
    public void generateBoards() {
        generateLanguageBoards(this.language);
    }

    @Rule
    public ActivityTestRule<MainMenuActivity> mActivityTestRule = new ActivityTestRule<>(MainMenuActivity.class);

    private void generateLanguageBoards(Language language) {
        Log.d(TAG, "Running test for " + language.getName() + " board");

        for (int i = 0; i < 3; i++) {
            fromHomeNavigateToPreferences();
            fromPreferencesSelectLanguage(language);
            fromPreferencesSelectBoardSize(i);
            fromPreferencesSelectScoreType(i == 0 ? 0 : 1); // Normal scoring first, then scrabble scoring
            fromPreferencesSelectHintMode(i); // First no hints, then just one type, then the other
            pressBack();

            fromHomeStartNewGame();
            fromGameRotateBoard();
            fromGameSaveGame();
            fromHomeRestoreGame();
            fromGameRotateBoard();
            fromGameEndGame();
            fromPostGameSelectMissingWords();
            fromMissingWordsViewWord(0);
            fromMissingWordsViewWord(1);
            fromMissingWordsReturnHome();
        }
    }

    private void fromHomeNavigateToPreferences() {
        ViewInteraction fancyButton = onView(allOf(withId(R.id.preferences), isDisplayed()));
        fancyButton.perform(click());
    }

    private void fromPreferencesSelectLanguage(Language language) {
        ViewInteraction dictionaryPreference = onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(android.R.id.list_container), 0)));
        dictionaryPreference.perform(actionOnItemAtPosition(0, click()));

        DataInteraction selectLangView = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(getIndexForLanguage(language));
        selectLangView.perform(click());
    }

    private void fromPreferencesSelectBoardSize(int index) {
        ViewInteraction boardSizePreference = onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(android.R.id.list_container), 0)));
        boardSizePreference.perform(actionOnItemAtPosition(1, click()));

        DataInteraction selectBoardSizeView = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(index);
        selectBoardSizeView.perform(click());
    }

    private void fromPreferencesSelectScoreType(int index) {
        ViewInteraction selectScoreTypePreference = onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(android.R.id.list_container), 0)));
        selectScoreTypePreference.perform(actionOnItemAtPosition(3, click()));

        DataInteraction selectScoreTypeView = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(index);
        selectScoreTypeView.perform(click());
    }

    private void fromPreferencesSelectHintMode(int index) {
        ViewInteraction selectScoreTypePreference = onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(android.R.id.list_container), 0)));
        selectScoreTypePreference.perform(actionOnItemAtPosition(7, click()));

        DataInteraction selectScoreTypeView = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(index);
        selectScoreTypeView.perform(click());
    }

    private int getIndexForLanguage(Language language) {
        int langIndex = -1;
        String[] languageValues = ApplicationProvider.getApplicationContext().getResources().getStringArray(R.array.dict_choices_entryvalues);
        for (int i = 0; i < languageValues.length; i++) {
            String lang = languageValues[i];
            if (lang.equals(language.getName()) || language.getName().equals("en_US") && lang.equals("US") || language.getName().equals("en_GB") && lang.equals("UK")) {
                langIndex = i;
                break;
            }
        }
        assertTrue(langIndex >= 0);
        return langIndex;
    }

    private void fromHomeStartNewGame() {
        ViewInteraction newGameButton = onView(allOf(withId(R.id.new_game), isDisplayed()));
        newGameButton.perform(click());
    }

    private void fromGameRotateBoard() {
        ViewInteraction actionMenuItemView = onView(allOf(withId(R.id.rotate), withContentDescription(R.string.menu_rotate_board), childAtPosition(childAtPosition(withId(R.id.toolbar), 2), 0), isDisplayed()));
        actionMenuItemView.perform(click());
    }

    private void fromGameSaveGame() {
        ViewInteraction upButton = onView(allOf(childAtPosition(allOf(withId(R.id.toolbar), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 0)), 1), isDisplayed()));
        upButton.perform(click());
    }

    private void fromHomeRestoreGame() {
        ViewInteraction restoreButton = onView(allOf(withId(R.id.restore_game), isDisplayed()));
        restoreButton.perform(click());
    }

    private void fromGameEndGame() {
        ViewInteraction endGame = onView(allOf(withId(R.id.end_game), withContentDescription(R.string.menu_end_game)));
        endGame.perform(click());
    }

    private void fromPostGameSelectMissingWords() {
        ViewInteraction missedWords = onView(withId(R.id.missed_words_button));
        missedWords.perform(click());
    }

    private void fromMissingWordsViewWord(int index) {
        ViewInteraction wordList = onView(allOf(withId(R.id.words), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 1)));
        wordList.perform(actionOnItemAtPosition(index, click()));
    }

    private void fromMissingWordsReturnHome() {
        ViewInteraction backButton = onView(allOf(withId(R.id.back_button), isDisplayed()));
        backButton.perform(click());
    }

    private static Matcher<View> childAtPosition(final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent) && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
