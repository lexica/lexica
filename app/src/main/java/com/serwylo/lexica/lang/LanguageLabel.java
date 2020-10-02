package com.serwylo.lexica.lang;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

public class LanguageLabel {

    private static final String TAG = "LanguageLabel";

    public static String getLabel(Context context, @NonNull Language language) {
        String prefName = "pref_dict_" + language.getName();
        int resId = context.getResources().getIdentifier(prefName, "string", context.getPackageName());
        if (resId <= 0) {
            Log.e(TAG, "Language " + language.getName() + " does not have a corresponding R.string.pref_dict_" + language.getName() + " value, defaulting to " + language.getName());
            return language.getName();
        }


        return context.getString(resId);
    }

}
