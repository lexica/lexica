package com.serwylo.lexica;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.serwylo.lexica.lang.Language;

import java.util.Collection;
import java.util.Locale;

public class Util {
    private static final String TAG = "Util";

    public String getLexiconString(Context context) {
        // Default to the language explicitly chosen by the user
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String chosenLanguage = prefs.getString("dict", null);

        if (chosenLanguage != null) {
            Log.d(TAG, "User explicitly chose " + chosenLanguage);
            return chosenLanguage;
        }

        Locale systemLocale = context.getResources().getConfiguration().locale;

        Language bestEffort = findBestMatchOrNull(systemLocale, Language.getAllLanguages().values());
        if (bestEffort != null) {
            return bestEffort.getLocale().toString();
        }

        // Default
        Log.d(TAG, "Could not detect language for system language " + systemLocale + ", defaulting to US");
        return "US";
    }

    /**
     * If we don't find an exact match, then we will still try to give the user a lexicon
     * from the same language, even if not the same country. In order of precedence, prefer:
     *
     *  - An exact match of language + country (e.g. "pt_BR").
     *  - Just lang without country (e.g. "pt_BR" should match "pt" over "pt_PT").
     *  - Same lang different country (e.g. "pt_BR" matches "pt_PT") <- Likely still better than choosing English.
     */
    public static Language findBestMatchOrNull(Locale toSearch, Collection<Language> availableLanguages) {

        Log.d(TAG, "Searching for lexicon that best matches the locale: \"" + toSearch.toString() + "\"");

        String searchLang = toSearch.getLanguage();
        String searchCountry = toSearch.getCountry();

        Language sameLanguageNoCountry = null;
        Language sameLanguageDifferentCountry = null;

        if (searchLang == null) {
            Log.w(TAG, "The locale we were searching for has no language code.");
            return null;
        }

        for (Language language : availableLanguages) {

            Locale langLocale = language.getLocale();

            if (!searchLang.equals(langLocale.getLanguage())) {
                continue;
            }

            Log.d(TAG, "Found matching language code \"" + toSearch.getLanguage() + "\". Will now check country code.");

            String langCountry = langLocale.getCountry();
            if (langCountry != null && langCountry.equals(searchCountry)) {

                Log.d(TAG, "Found exact match of language and country code: " + langLocale);
                return language;

            } else if (langCountry == null || langCountry.length() == 0) {

                if (searchCountry == null || searchCountry.length() == 0) {
                    Log.d(TAG, "Found exact match for locale (doesn't have a country code associated with it): \"" + langLocale + "\"");
                    return language;
                }

                Log.d(TAG, "Remembering language \"" + langLocale + "\" in case we don't find any better match.");
                sameLanguageNoCountry = language;

            } else {

                Log.d(TAG, "Found matching language, but different country. Remembering \"" + langLocale + "\" in case we don't find any better match.");
                sameLanguageDifferentCountry = language;

            }
        }

        if (sameLanguageNoCountry != null) {
            Log.d(TAG, "No exact match, so the best option is matching language code with no specific country: \"" + sameLanguageNoCountry.getLocale() + "\"");
            return sameLanguageNoCountry;
        }

        if (sameLanguageDifferentCountry != null) {
            Log.d(TAG, "No exact match, and no match for only the language code, so the best option is matching the smae language code with a different country code: \"" + sameLanguageDifferentCountry.getLocale() + "\"");
            return sameLanguageDifferentCountry;
        }

        Log.d(TAG, "No matching language found.");
        return null;
    }
}
