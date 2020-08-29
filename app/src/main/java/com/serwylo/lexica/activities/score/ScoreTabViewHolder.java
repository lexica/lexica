package com.serwylo.lexica.activities.score;

import android.os.Build;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.game.Game;

import java.util.Comparator;

class ScoreTabViewHolder extends RecyclerView.ViewHolder {

    private static final String MISSED_WORDS_SORT = "missedWordsSort";
    private static final String DEFAULT_SORT = "alphabetically";

    private final ScoreActivity activity;
    private final FrameLayout parent;

    ScoreTabViewHolder(@NonNull ScoreActivity activity, @NonNull FrameLayout parent) {
        super(parent);
        this.activity = activity;
        this.parent = parent;
    }

    void bindFoundWords(@NonNull Game game) {
        new FoundWordsViewBinder(activity, parent, game);
    }

    void bindMissedWords(@NonNull Game game) {
        String sortMode = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext()).getString(MISSED_WORDS_SORT, DEFAULT_SORT);
        Comparator<ScoreWordsViewBinder.Item> comparator = (item0, item1) -> 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            switch (sortMode) {
                case "missedWordsSort_length":
                    comparator = Comparator.comparing(item -> item.word.length());
                    break;
                case "missedWordsSort_score":
                    comparator = Comparator.comparing(item -> item.points);
                    break;
                default:
                    comparator = (item0, item1) -> 0;
                    break;
            }
        }

        new MissedWordsViewBinder(activity, parent, game, comparator);

    }
}
