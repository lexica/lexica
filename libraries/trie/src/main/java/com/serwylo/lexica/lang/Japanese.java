package com.serwylo.lexica.lang;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Japanese extends Language {

    private static Map<String, Integer> letterPoints = new HashMap<>();

    static {
        // These point values are from an "unofficial Japanese Hiragana Scrabble set" according to
        // wikipedia: https://en.wikipedia.org/wiki/Scrabble_letter_distributions#Japanese_Hiragana
        letterPoints.put("い", 1);
        letterPoints.put("ぃ", 1);
        letterPoints.put("う", 1);
        letterPoints.put("ゔ", 1);
        letterPoints.put("ぅ", 1);
        letterPoints.put("か", 1);
        letterPoints.put("し", 1);
        letterPoints.put("じ", 1);
        letterPoints.put("た", 1);
        letterPoints.put("だ", 1);
        letterPoints.put("て", 1);
        letterPoints.put("で", 1);
        letterPoints.put("と", 1);
        letterPoints.put("ど", 1);
        letterPoints.put("の", 1);
        letterPoints.put("ん", 1);

        letterPoints.put("き", 2);
        letterPoints.put("く", 2);
        letterPoints.put("ぐ", 2);
        letterPoints.put("こ", 2);
        letterPoints.put("ご", 2);
        letterPoints.put("つ", 2);
        letterPoints.put("づ", 2);
        letterPoints.put("っ", 2);
        letterPoints.put("な", 2);
        letterPoints.put("に", 2);
        letterPoints.put("は", 2);
        letterPoints.put("ば", 2);
        letterPoints.put("ぱ", 2);
        letterPoints.put("よ", 2);
        letterPoints.put("ょ", 2);
        letterPoints.put("れ", 2);

        letterPoints.put("あ", 3);
        letterPoints.put("ぁ", 3);
        letterPoints.put("け", 3);
        letterPoints.put("げ", 3);
        letterPoints.put("す", 3);
        letterPoints.put("ず", 3);
        letterPoints.put("せ", 3);
        letterPoints.put("ぜ", 3);
        letterPoints.put("も", 3);
        letterPoints.put("り", 3);
        letterPoints.put("る", 3);
        letterPoints.put("わ", 3);
        letterPoints.put("ゎ", 3);
        letterPoints.put("ら", 3);

        letterPoints.put("さ", 4);
        letterPoints.put("ざ", 4);
        letterPoints.put("そ", 4);
        letterPoints.put("ぞ", 4);
        letterPoints.put("ち", 4);
        letterPoints.put("ぢ", 4);
        letterPoints.put("ま", 4);

        letterPoints.put("お", 5);
        letterPoints.put("ぉ", 5);
        letterPoints.put("ひ", 5);
        letterPoints.put("び", 5);
        letterPoints.put("ぴ", 5);
        letterPoints.put("ふ", 5);
        letterPoints.put("ぶ", 5);
        letterPoints.put("ぷ", 5);
        letterPoints.put("ゆ", 5);
        letterPoints.put("ゅ", 5);

        letterPoints.put("ほ", 6);
        letterPoints.put("ぼ", 6);
        letterPoints.put("ぽ", 6);
        letterPoints.put("め", 6);
        letterPoints.put("や", 6);
        letterPoints.put("ゃ", 6);

        letterPoints.put("え", 8);
        letterPoints.put("ぇ", 8);
        letterPoints.put("へ", 8);
        letterPoints.put("べ", 8);
        letterPoints.put("ぺ", 8);
        letterPoints.put("み", 8);

        letterPoints.put("ね", 10);
        letterPoints.put("む", 10);
        letterPoints.put("ろ", 10);

        letterPoints.put("ぬ", 12);

        // After moving the "small letters" and the kanji with diacritics, this is
        // the remaining pieces that I don't have the knowledge (or wikipedia skills)
        // to figure out.
        letterPoints.put("ゐ", 1);
        letterPoints.put("ゑ", 1);
        letterPoints.put("を", 1);
        letterPoints.put("〜", 1);
        letterPoints.put("が", 1);
        letterPoints.put("ぎ", 1);
        letterPoints.put("ー", 1);
    }

    @Override
    public boolean isBeta() {
        return true;
    }

    @Override
    public Locale getLocale() {
        return new Locale("jp");
    }

    @Override
    public String getName() {
        return "jp";
    }

    @Override
    public String toDisplay(String value) {
        return value.toUpperCase(getLocale());
    }

    @Override
    public String applyMandatorySuffix(String value) {
        return value;
    }

    @Override
    protected Map<String, Integer> getLetterPoints() {
        return letterPoints;
    }
}
