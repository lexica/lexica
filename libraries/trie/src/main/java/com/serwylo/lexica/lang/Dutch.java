package com.serwylo.lexica.lang;

import java.util.Locale;

public class Dutch extends Language {
    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("nl");
    }

    @Override
    public String getName() {
        return "nl";
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
