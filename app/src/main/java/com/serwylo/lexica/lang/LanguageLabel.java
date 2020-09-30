package com.serwylo.lexica.lang;

import android.content.Context;

import androidx.annotation.NonNull;

public class LanguageLabel {

    public static String getLabel(Context context, @NonNull Language language) {
        String prefName = "pref_dict_" + language.getName();
        int resId = context.getResources().getIdentifier(prefName, "string", context.getPackageName());
        if (resId <= 0) {
            return null;
        }


        return context.getString(resId);
    }

}
