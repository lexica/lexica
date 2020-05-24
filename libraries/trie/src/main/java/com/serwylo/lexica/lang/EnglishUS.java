package com.serwylo.lexica.lang;

import java.util.Locale;

public class EnglishUS extends English {
    @Override
    public String getName() {
        return "en_US";
    }

    @Override
    public Locale getLocale() {
        return new Locale("en", "US");
    }
}
