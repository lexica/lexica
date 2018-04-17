package com.serwylo.lexica.lang;

import java.util.Locale;

public abstract class English extends Language {

    @Override
    public Locale getLocale() {
        return Locale.ENGLISH;
    }

    @Override
    public String toDisplay(String value) {
        if (value.equals("qu")) {
            return "Qu";
        }

        return value.toUpperCase(getLocale());
    }

    @Override
    public String applyMandatorySuffix(String value) {
        if (value.equals("q")) {
            return "qu";
        }

        return value;
    }
}
