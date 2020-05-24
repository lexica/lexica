package com.serwylo.lexica.lang;

import java.util.Locale;

public class EnglishGB extends English {
    @Override
    public String getName() {
        return "en_GB";
    }

    @Override
    public Locale getLocale() {
        return new Locale("en", "GB");
    }
}
