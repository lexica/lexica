package com.serwylo.lexica.activities.score;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.game.Game;

class ScoreTabAdapter extends RecyclerView.Adapter<ScoreTabViewHolder> {

    private static final int VIEW_TYPE_FOUND_WORDS = 1;
    private static final int VIEW_TYPE_MISSED_WORDS = 2;

    private ScoreActivity scoreActivity;
    private Game game;

    ScoreTabAdapter(@NonNull ScoreActivity scoreActivity, @NonNull Game game) {
        this.scoreActivity = scoreActivity;
        this.game = game;
    }

    @NonNull
    @Override
    public ScoreTabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ScoreTabViewHolder viewHolder = createEmptyView(scoreActivity);
        if (viewType == VIEW_TYPE_FOUND_WORDS) {
            viewHolder.bindFoundWords(game);
        } else if (viewType == VIEW_TYPE_MISSED_WORDS) {
            viewHolder.bindMissedWords(game);
        } else {
            throw new IllegalArgumentException("The viewType should be either VIEW_TYPE_FOUND_WORDS or VIEW_TYPE_MISSED_WORDS, but got " + viewType);
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ScoreTabViewHolder holder, int position) {
        // Don't do anything, because the views are created once during onCreateViewHolder and
        // never updated.
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FOUND_WORDS;
        } else if (position == 1) {
            return VIEW_TYPE_MISSED_WORDS;
        }

        throw new IllegalArgumentException("Score activity adapter only support two items, but was asked for item at position " + position);
    }

    private ScoreTabViewHolder createEmptyView(ScoreActivity activity) {
        FrameLayout frame = new FrameLayout(activity);
        frame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new ScoreTabViewHolder(activity, frame);
    }

}
