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
        for (int i = 0; i < dictionaryLanguages.length; i ++) {
            Language language = Language.fromOrNull(dictionaryLanguages[i]);
            if (language != null) {
                if (systemLanguage == language.getLocale().getLanguage()) {
                    Log.d(TAG, "Language " + dictionaryLanguages[i] + " best matches " + systemLanguage);
                    return dictionaryLanguages[i];
                }
            }
        }

        // Default
        Log.d(TAG, "Could not detect language for system language " + systemLanguage + ", defaulting to US");
        return "US";
    }
}
