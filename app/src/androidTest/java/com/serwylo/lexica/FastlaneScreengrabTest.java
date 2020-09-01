package com.serwylo.lexica;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.serwylo.lexica.lang.EnglishUS;
import com.serwylo.lexica.lang.Language;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import tools.fastlane.screengrab.Screengrab;
import tools.fastlane.screengrab.locale.LocaleTestRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FastlaneScreengrabTest {

    @ClassRule
    public static final LocaleTestRule localeTestRule = new LocaleTestRule();

    @Rule
    public ActivityTestRule<MainMenuActivity> mActivityTestRule = new ActivityTestRule<>(MainMenuActivity.class);

    @Test
    public void mainMenuActivityTest() {

        initialSetup();
        selectLanguage(mActivityTestRule.getActivity().getResources().getConfiguration().locale);

        Screengrab.screenshot("01_main_menu_light");

        startGame();

        Screengrab.screenshot("02_game_light");

        endGame();
        showMissedWords();
        selectMissedWord();

        Screengrab.screenshot("03_missed_words_light");

        back();
        showPreferences();

        Screengrab.screenshot("04_preferences");

        up();
        setupDarkTheme();
        selectLanguage(Locale.JAPANESE);

        Screengrab.screenshot("05_main_menu_dark");

        startGame();

        Screengrab.screenshot("06_game_dark");

    }

    private void showPreferences() {
        ViewInteraction fancyButton = onView(allOf(withId(R.id.preferences), isDisplayed()));
        fancyButton.perform(click());
    }

    private void setupDarkTheme() {
        ViewInteraction fancyButton = onView(allOf(withId(R.id.preferences), isDisplayed()));
        fancyButton.perform(click());

        // Dark mode
        ViewInteraction recyclerView = onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(android.R.id.list_container), 0)));
        recyclerView.perform(actionOnItemAtPosition(5, click()));

        DataInteraction appCompatCheckedTextView = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(1);
        appCompatCheckedTextView.perform(click());

        // Reset hint mode to "Disabled"
        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recycler_view), childAtPosition(withId(android.R.id.list_container), 0)));
        recyclerView2.perform(actionOnItemAtPosition(7, click()));

        DataInteraction appCompatCheckedTextView2 = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(0);
        appCompatCheckedTextView2.perform(click());

        up();
    }

    private void back() {
        ViewInteraction fancyButton4 = onView(allOf(withId(R.id.back_button), isDisplayed()));
        fancyButton4.perform(click());
    }

    private void up() {
        ViewInteraction upButton2 = onView(allOf(childAtPosition(allOf(withId(R.id.toolbar), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 0)), 1), isDisplayed()));
        upButton2.perform(click());
    }

    private void selectMissedWord() {
        ViewInteraction recyclerView5 = onView(allOf(withId(R.id.words), childAtPosition(withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")), 1)));
        recyclerView5.perform(actionOnItemAtPosition(1, click()));
    }

    private void showMissedWords() {
        ViewInteraction fancyButton3 = onView(allOf(withId(R.id.missed_words_button), isDisplayed()));
        fancyButton3.perform(click());
    }

    private void endGame() {
        ViewInteraction actionMenuItemView = onView(allOf(withId(R.id.end_game), isDisplayed()));
        actionMenuItemView.perform(click());
    }

    private void startGame() {
        ViewInteraction fancyButton2 = onView(allOf(withId(R.id.new_game), isDisplayed()));
        fancyButton2.perform(click());
    }

    /**
     * Because fastlane runs several times with the same install, we need to ensure we setup the appropriate preferences each time we start.
     */
    private void initialSetup() {

        ViewInteraction fancyButton = onView(allOf(withId(R.id.preferences), isDisplayed()));
        fancyButton.perform(click());

        ViewInteraction recyclerView = onView(allOf(withId(R.id.recycler_view), childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)));
        recyclerView.perform(actionOnItemAtPosition(5, click()));

        DataInteraction appCompatCheckedTextView = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(0);
        appCompatCheckedTextView.perform(click());

        ViewInteraction recyclerView2 = onView(allOf(withId(R.id.recycler_view), childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)));
        recyclerView2.perform(actionOnItemAtPosition(1, click()));

        DataInteraction appCompatCheckedTextView2 = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(0);
        appCompatCheckedTextView2.perform(click());

        ViewInteraction recyclerView3 = onView(allOf(withId(R.id.recycler_view), childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)));
        recyclerView3.perform(actionOnItemAtPosition(3, click()));

        DataInteraction appCompatCheckedTextView3 = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(0);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction recyclerView4 = onView(allOf(withId(R.id.recycler_view), childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)));
        recyclerView4.perform(actionOnItemAtPosition(7, click()));

        DataInteraction appCompatCheckedTextView4 = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(0);
        appCompatCheckedTextView4.perform(click());

        up();
    }

    private void selectLanguage(Locale locale) {
        Language language = Util.findBestMatchOrNull(locale, Language.getAllLanguages().values());
        int languageIndex = getIndexForLanguage(language == null ? new EnglishUS() : language);

        ViewInteraction fancyButton = onView(allOf(withId(R.id.preferences), isDisplayed()));
        fancyButton.perform(click());

        ViewInteraction recyclerView = onView(allOf(withId(R.id.recycler_view), childAtPosition(withClassName(is("android.widget.FrameLayout")), 0)));
        recyclerView.perform(actionOnItemAtPosition(0, click()));

        DataInteraction appCompatCheckedTextView = onData(anything()).inAdapterView(allOf(withId(R.id.select_dialog_listview), childAtPosition(withId(R.id.contentPanel), 0))).atPosition(languageIndex);
        appCompatCheckedTextView.perform(click());

        up();
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
