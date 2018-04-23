package com.serwylo.lexica.lang;

import java.util.Locale;

public class French extends Language {
    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return Locale.FRENCH;
    }

    @Override
    public String getName() {
        return "fr_FR";
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
