package com.serwylo.lexica.activities.score;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for end of game word list sorting.
 * <p>
 * Does three different things to do with sorting of words:
 * <p>
 * - Cycling through different sort orders (alphabetical asc -> alphabetical desc -> points asc -> points desc).
 * - Remembering the users last sort order (via preferences).
 * - Understanding which font awesome icon resource corresponds to the current order (alphabetical, points, asc, desc).
 */
class Sorter implements Comparator<ScoreWordsViewBinder.Item> {

    private static final String SCORE_SCREEN_SORT_BY = "scoreScreensSortBy";
    private static final String SCORE_SCREEN_SORT_ORDER = "scoreScreenSortOrder";

    private static final String SORT_BY_ALPHA = "alpha";
    private static final String SORT_BY_POINTS = "points";
    private static final String DEFAULT_SORT_BY = SORT_BY_POINTS;

    public static final String SORT_ASC = "asc";
    public static final String SORT_DESC = "desc";
    private static final String DEFAULT_SORT_ORDER = SORT_DESC;

    private static final String SORT_ICON_ALPHA_ASC = "\uf15d";
    private static final String SORT_ICON_ALPHA_DESC = "\uf15e";
    private static final String SORT_ICON_NUMERIC_ASC = "\uf162";
    private static final String SORT_ICON_NUMERIC_DESC = "\uf163";

    private static final Map<String, String> SORT_ICONS = new HashMap<>();

    static {
        SORT_ICONS.put(Sorter.SORT_BY_ALPHA + Sorter.SORT_ASC, SORT_ICON_ALPHA_ASC);
        SORT_ICONS.put(Sorter.SORT_BY_ALPHA + Sorter.SORT_DESC, SORT_ICON_ALPHA_DESC);
        SORT_ICONS.put(Sorter.SORT_BY_POINTS + Sorter.SORT_ASC, SORT_ICON_NUMERIC_ASC);
        SORT_ICONS.put(Sorter.SORT_BY_POINTS + Sorter.SORT_DESC, SORT_ICON_NUMERIC_DESC);
    }

    private final SharedPreferences preferences;

    private String sortBy;
    private String sortOrder;

    private final List<OnSortListener> listeners = new ArrayList<>(2);

    Sorter(Context context) {
        this.preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.sortBy = this.preferences.getString(SCORE_SCREEN_SORT_BY, DEFAULT_SORT_BY);
        this.sortOrder = this.preferences.getString(SCORE_SCREEN_SORT_ORDER, DEFAULT_SORT_ORDER);
    }

    public String getIconResource() {
        return SORT_ICONS.get(sortBy + sortOrder);
    }

    /**
     * Cycles through the four sorting conditions, the orders are:
     * - alphabetical asc
     * - alphabetical desc
     * - points asc
     * - points desc
     */
    public void changeSort() {
        if (SORT_DESC.equals(sortOrder)) {
            sortOrder = SORT_ASC;
            sortBy = SORT_BY_ALPHA.equals(sortBy) ? SORT_BY_POINTS : SORT_BY_ALPHA;
        } else {
            sortOrder = SORT_DESC;
        }

        this.preferences.edit().putString(SCORE_SCREEN_SORT_ORDER, sortOrder).putString(SCORE_SCREEN_SORT_BY, sortBy).apply();

        for (OnSortListener listener : listeners) {
            listener.onSort();
        }
    }

    @Override
    public int compare(ScoreWordsViewBinder.Item item1, ScoreWordsViewBinder.Item item2) {
        int value = 0;

        switch (sortBy) {
            case SORT_BY_ALPHA:
                value = compareAlpha(item1, item2);
                break;

            case SORT_BY_POINTS:
                value = comparePoints(item1, item2);
                if (value == 0) {
                    // Don't let this get down to the asc / desc question.
                    // If the user wants to sort by points, but points are equal, then always sort alphabetically next.
                    return compareAlpha(item1, item2);
                }
                break;
        }

        return SORT_ASC.equals(this.sortOrder) ? value : -value;
    }

    private int comparePoints(ScoreWordsViewBinder.Item item1, ScoreWordsViewBinder.Item item2) {
        return item1.points - item2.points;
    }

    private int compareAlpha(ScoreWordsViewBinder.Item item1, ScoreWordsViewBinder.Item item2) {
        return item1.word.compareTo(item2.word);
    }

    public void addListener(OnSortListener listener) {
        this.listeners.add(listener);
    }

    interface OnSortListener {
        void onSort();
    }

}
