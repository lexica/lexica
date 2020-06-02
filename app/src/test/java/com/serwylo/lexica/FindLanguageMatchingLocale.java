package com.serwylo.lexica;

import com.serwylo.lexica.lang.Language;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;

public class FindLanguageMatchingLocale {

    private final Locale en_GB = new Locale("en", "GB");
    private final Locale en_US = new Locale("en", "US");
    private final Locale pt = new Locale("pt");
    private final Locale gb = new Locale("gb");
    private final Locale pt_BR = new Locale("pt", "BR");
    private final Locale pt_PT = new Locale("pt", "PT");
    private final Locale ja_JP = new Locale("ja", "JP");

    private final Collection<Language> AVAILABLE_LANGS = Collections.unmodifiableCollection(Arrays.<Language>asList(new MockLanguage("en", "GB"), new MockLanguage("pt"), new MockLanguage("pt", "BR")));

    @Test
    public void findExactMatch() {
        assertLanguageMatchesLocale(Util.findBestMatchOrNull(en_GB, AVAILABLE_LANGS), en_GB);
        assertLanguageMatchesLocale(Util.findBestMatchOrNull(pt, AVAILABLE_LANGS), pt);
        assertLanguageMatchesLocale(Util.findBestMatchOrNull(pt_BR, AVAILABLE_LANGS), pt_BR);
    }

    @Test
    public void fallBackToSameLanguage() {
        // pt_PT is not in the available languages, so our next best guess is pt.
        assertLanguageMatchesLocale(Util.findBestMatchOrNull(pt_PT, AVAILABLE_LANGS), pt);
    }

    @Test
    public void fallBackToSameLanguageDifferentCountry() {
        // en_US is not in the available languages, so our next best guess is en_GB.
        assertLanguageMatchesLocale(Util.findBestMatchOrNull(en_US, AVAILABLE_LANGS), en_GB);
    }

    @Test
    public void cantFindMatch() {
        Assert.assertNull(Util.findBestMatchOrNull(ja_JP, AVAILABLE_LANGS));
        Assert.assertNull(Util.findBestMatchOrNull(gb, AVAILABLE_LANGS));

        Assert.assertNotNull(Util.findBestMatchOrNull(en_GB, AVAILABLE_LANGS));
        Assert.assertNotNull(Util.findBestMatchOrNull(en_US, AVAILABLE_LANGS));
        Assert.assertNotNull(Util.findBestMatchOrNull(pt, AVAILABLE_LANGS));
        Assert.assertNotNull(Util.findBestMatchOrNull(pt_BR, AVAILABLE_LANGS));
        Assert.assertNotNull(Util.findBestMatchOrNull(pt_PT, AVAILABLE_LANGS));
    }

    private void assertLanguageMatchesLocale(Language language, Locale locale) {
        Assert.assertNotNull(language);
        Assert.assertEquals(language.getLocale().getLanguage(), locale.getLanguage());
        Assert.assertEquals(language.getLocale().getCountry(), locale.getCountry());
    }

    public static class MockLanguage extends Language {

        private final String languageCode;
        private final String countryCode;

        MockLanguage(String languageCode) {
            this(languageCode, "");
        }

        MockLanguage(String languageCode, String countryCode) {
            this.languageCode = languageCode;
            this.countryCode = countryCode;
        }

        @Override
        public Locale getLocale() {
            return new Locale(languageCode, countryCode);
        }

        @Override
        public String getName() {
            return "Mock Language";
        }

        @Override
        protected Map<String, Integer> getLetterPoints() {
            return null;
        }

        @Override
        public String toDisplay(String value) {
            return null;
        }

        @Override
        public String applyMandatorySuffix(String value) {
            return null;
        }
    }

}
