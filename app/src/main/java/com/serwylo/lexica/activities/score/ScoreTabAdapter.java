package com.serwylo.lexica.activities.score;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.game.Game;

class ScoreTabAdapter extends RecyclerView.Adapter<ScoreTabViewHolder> {

    private static final int VIEW_TYPE_FOUND_WORDS = 1;
    private static final int VIEW_TYPE_MISSED_WORDS = 2;
    private static final int VIEW_TYPE_NEXT_ROUND = 3;

    private final ScoreActivity scoreActivity;
    private final Game game;
    private final Sorter sorter;

    ScoreTabAdapter(@NonNull ScoreActivity scoreActivity, @NonNull Game game) {
        this.scoreActivity = scoreActivity;
        this.game = game;
        this.sorter = new Sorter(scoreActivity);
    }

    @NonNull
    @Override
    public ScoreTabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ScoreTabViewHolder viewHolder = createEmptyView(scoreActivity);
        if (viewType == VIEW_TYPE_FOUND_WORDS) {
            viewHolder.bindFoundWords(game, sorter);
        } else if (viewType == VIEW_TYPE_MISSED_WORDS) {
            viewHolder.bindMissedWords(game, sorter);
        } else if (viewType == VIEW_TYPE_NEXT_ROUND) {
            viewHolder.bindNextRound(game);
        } else {
            throw new IllegalArgumentException("The viewType should be either VIEW_TYPE_FOUND_WORDS, VIEW_TYPE_MISSED_WORDS, or VIEW_TYPE_NEXT_ROUND, but got " + viewType);
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
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_FOUND_WORDS;
        } else if (position == 1) {
            return VIEW_TYPE_MISSED_WORDS;
        } else if (position == 2) {
            return VIEW_TYPE_NEXT_ROUND;
        }

        throw new IllegalArgumentException("Score activity adapter only support two items, but was asked for item at position " + position);
    }

    private ScoreTabViewHolder createEmptyView(ScoreActivity activity) {
        FrameLayout frame = new FrameLayout(activity);
        frame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return new ScoreTabViewHolder(activity, frame);
    }

}
