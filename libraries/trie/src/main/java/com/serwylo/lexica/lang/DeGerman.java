package com.serwylo.lexica.lang;

import java.util.Locale;

public class DeGerman extends Language {
    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return Locale.GERMAN;
    }

    @Override
    public String getName() {
        return "de_DE";
    }

    @Override
    public String toDisplay(String value) {
        return value.toUpperCase(getLocale());
    }

    @Override
    public String applyMandatorySuffix(String value) {
        return value;
    }
}
