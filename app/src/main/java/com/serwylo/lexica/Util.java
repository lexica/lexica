package com.serwylo.lexica;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.serwylo.lexica.lang.Language;

public class Util {
    private static final String TAG = "Util";

    public String getLexiconString(Context context) {
        // Default to the language explicitly chosen by the user
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String chosenLanguage = prefs.getString("dict", null);

        if (chosenLanguage != null) {
            Log.d(TAG, "User explicitly chose " + chosenLanguage);
            return chosenLanguage;
        }

        String systemLanguage = context.getResources().getConfiguration().locale.getLanguage();
        String[] dictionaryLanguages = context.getResources().getStringArray(R.array.dict_choices_entryvalues);
        for (String dictionaryLanguage : dictionaryLanguages) {
            Language language = Language.fromOrNull(dictionaryLanguage);
            if (language != null) {
                if (systemLanguage != null && systemLanguage.equals(language.getLocale().getLanguage())) {
                    Log.d(TAG, "Language " + dictionaryLanguage + " best matches " + systemLanguage);
                    return dictionaryLanguage;
                }
            }
        }

        // Default
        Log.d(TAG, "Could not detect language for system language " + systemLanguage + ", defaulting to US");
        return "US";
    }
}
