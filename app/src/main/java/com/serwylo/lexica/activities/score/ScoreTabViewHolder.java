package com.serwylo.lexica.activities.score;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.game.Game;

class ScoreTabViewHolder extends RecyclerView.ViewHolder {

    private final ScoreActivity activity;
    private final FrameLayout parent;

    ScoreTabViewHolder(@NonNull ScoreActivity activity, @NonNull FrameLayout parent) {
        super(parent);
        this.activity = activity;
        this.parent = parent;
    }

    void bindFoundWords(@NonNull Game game, @NonNull Sorter sorter) {
        new FoundWordsViewBinder(activity, parent, game, sorter);
    }

    void bindMissedWords(@NonNull Game game, @NonNull Sorter sorter) {
        new MissedWordsViewBinder(activity, parent, game, sorter);
    }

}
