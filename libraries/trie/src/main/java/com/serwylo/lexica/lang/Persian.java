package com.serwylo.lexica.lang;

import java.util.Locale;

public class Persian extends Language {
    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("fa");
    }

    @Override
    public String getName() {
        return "fa";
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
