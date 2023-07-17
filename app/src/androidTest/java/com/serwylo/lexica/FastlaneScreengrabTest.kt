package com.serwylo.lexica

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.EnglishUS
import com.serwylo.lexica.lang.Japanese
import com.serwylo.lexica.lang.Language.Companion.allLanguages
import org.junit.ClassRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import tools.fastlane.screengrab.Screengrab
import tools.fastlane.screengrab.locale.LocaleTestRule

@LargeTest
@RunWith(AndroidJUnit4::class)
class FastlaneScreengrabTest {

    @JvmField
    @Rule
    var mActivityTestRule = ActivityTestRule(MainMenuActivity::class.java)

    @Test
    fun mainMenuActivityTest() {
        val context = mActivityTestRule.activity.applicationContext

        fromSplashSwitchTheme(context, ThemeManager.THEME_LIGHT)
        fromSplashSwitchGameMode(context, GameMode.Type.SPRINT)

        val language = Util.findBestMatchOrNull(mActivityTestRule.activity.resources.configuration.locale, allLanguages.values) ?: EnglishUS()
        fromSplashChooseLexicon()
        selectLanguage(context, language)

        Screengrab.screenshot("01_main_menu_light")

        fromSplashStartNewGame()

        Screengrab.screenshot("02_game_light")

        fromGameEndGame()
        fromScoreSelectMissedWords()
        fromMissedWordsSelectWordViaButton()

        Screengrab.screenshot("03_missed_words_light")

        navigateUpEmu27()
        fromSplashShowSettings()

        Screengrab.screenshot("04_preferences")

        navigateUpEmu27()
        fromSplashSwitchTheme(context, ThemeManager.THEME_DARK)
        fromSplashChooseLexicon()
        selectLanguage(context, Japanese())

        Screengrab.screenshot("05_main_menu_dark")

        fromSplashStartNewGame()

        Screengrab.screenshot("06_game_dark")
    }

    companion object {
        @JvmField
        @ClassRule
        val localeTestRule = LocaleTestRule()
    }
}