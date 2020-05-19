package com.serwylo.lexica;

import android.app.Activity;
import android.content.Context;
import android.preference.PreferenceManager;

/**
 * This class is based on GPLv3 licensed code from F-Droid client:
 *
 *   https://gitlab.com/fdroid/fdroidclient/-/blob/master/app/src/main/java/org/fdroid/fdroid/FDroidApp.java
 *
 */
public class ThemeManager {

    private static ThemeManager instance;

    public ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }

        return instance;
    }

    private static final String PREFERENCE_NAME = "currentTheme";
    private static final String THEME_LIGHT = "currentTheme";
    private static final String THEME_DARK = "currentTheme";

    private String currentTheme = THEME_LIGHT;

    public void reloadTheme(Context context) {
        currentTheme = PreferenceManager
                .getDefaultSharedPreferences(context)
                .getString(PREFERENCE_NAME, THEME_LIGHT);
    }

    public void applyTheme(Activity activity) {
        activity.setTheme(getCurThemeResId());
    }

    public String getCurrentTheme() {
        return currentTheme;
    }

    public int getCurThemeResId() {
        if (THEME_DARK.equals(currentTheme)) {
            return R.style.AppTheme_Dark;
        } else {
            return R.style.AppTheme_Light;
        }
    }

}
