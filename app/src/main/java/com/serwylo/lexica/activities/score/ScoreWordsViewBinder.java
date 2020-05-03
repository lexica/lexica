package com.serwylo.lexica.activities.score;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;

import mehdi.sakout.fancybuttons.FancyButton;

class ScoreWordsViewBinder {

    protected final AppCompatActivity activity;
    protected final WordDefiner definer;

    ScoreWordsViewBinder(AppCompatActivity activity, @NonNull Game game) {
        this.activity = activity;
        this.definer = new WordDefiner(activity, game.getLanguage());
    }

    void addWord(ViewGroup vg, final String w, int points, boolean valid, @Nullable View.OnClickListener onViewWord) {

        View view = activity.getLayoutInflater().inflate(R.layout.score_summary_word, vg, false);

        TextView word = view.findViewById(R.id.word);
        word.setText(w.toUpperCase());
        TextView score = view.findViewById(R.id.score);
        score.setText("+" + points);

        FancyButton define = view.findViewById(R.id.define);

        if (valid) {

            word.setPaintFlags(word.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
            score.setVisibility(View.VISIBLE);
            define.setVisibility(View.VISIBLE);
            define.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    definer.define(w);
                }
            });

        } else {

            word.setPaintFlags(word.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            score.setVisibility(View.GONE);
            define.setVisibility(View.GONE);
            define.setOnClickListener(null);

        }

        FancyButton viewWord = view.findViewById(R.id.view_word);
        if (onViewWord == null) {
            viewWord.setVisibility(View.GONE);
            viewWord.setOnClickListener(null);
        } else {
            viewWord.setVisibility(View.VISIBLE);
            viewWord.setOnClickListener(onViewWord);
        }

        vg.addView(view);
    }

}
