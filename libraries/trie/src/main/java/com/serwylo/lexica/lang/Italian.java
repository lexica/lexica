package com.serwylo.lexica.lang;

import java.util.Locale;

public class Italian extends Language {
    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return Locale.ITALIAN;
    }

    @Override
    public String getName() {
        return "it";
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
