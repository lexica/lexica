package com.serwylo.lexica.activities.score;

import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;

import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

class ScoreWordsViewBinder {

    protected final AppCompatActivity activity;
    protected final WordDefiner definer;

    ScoreWordsViewBinder(AppCompatActivity activity, @NonNull Game game) {
        this.activity = activity;
        this.definer = new WordDefiner(activity, game.getLanguage());
    }

    static class Item {
        public final @NonNull String word;
        public final int points;
        public final boolean valid;
        public final @Nullable View.OnClickListener onViewWord;

        Item(@NonNull String word, int points, boolean valid, @Nullable View.OnClickListener onViewWord) {
            this.word = word;
            this.points = points;
            this.valid = valid;
            this.onViewWord = onViewWord;
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private final List<Item> items;

        Adapter(List<Item> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(activity.getLayoutInflater().inflate(R.layout.score_summary_word, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(final Item item) {

            TextView word = itemView.findViewById(R.id.word);
            word.setText(item.word.toUpperCase());
            TextView score = itemView.findViewById(R.id.score);
            score.setText("+" + item.points);

            FancyButton define = itemView.findViewById(R.id.define);

            if (item.valid) {

                word.setPaintFlags(word.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                score.setVisibility(View.VISIBLE);
                define.setVisibility(View.VISIBLE);
                define.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        definer.define(item.word);
                    }
                });

            } else {

                word.setPaintFlags(word.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                score.setVisibility(View.GONE);
                define.setVisibility(View.GONE);
                define.setOnClickListener(null);

            }

            FancyButton viewWord = itemView.findViewById(R.id.view_word);
            if (item.onViewWord == null) {
                viewWord.setVisibility(View.GONE);
                viewWord.setOnClickListener(null);
            } else {
                viewWord.setVisibility(View.VISIBLE);
                viewWord.setOnClickListener(item.onViewWord);
            }
        }

    }

}
