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
        public final @Nullable ViewWordListener viewWordListener;

        Item(@NonNull String word, int points, boolean valid, @Nullable ViewWordListener viewWordListener) {
            this.word = word;
            this.points = points;
            this.valid = valid;
            this.viewWordListener = viewWordListener;
        }
    }

    class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private static final int TYPE_NORMAL = 0;
        private static final int TYPE_SELECTED = 1;

        private final List<Item> items;

        @Nullable
        private Item selectedItem = null;

        Adapter(List<Item> items) {
            this.items = items;
        }

        public void setSelectedItem(@Nullable Item item) {
            this.selectedItem = item;
        }

        @Nullable
        public Item getSelectedItem() {
            return selectedItem;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int layout = viewType == TYPE_SELECTED ? R.layout.score_summary_word__selected : R.layout.score_summary_word;
            return new ViewHolder(activity.getLayoutInflater().inflate(layout, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(items.get(position));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (items.get(position) == selectedItem) {
                return TYPE_SELECTED;
            }

            return TYPE_NORMAL;
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
            if (item.viewWordListener == null) {
                viewWord.setVisibility(View.GONE);
                viewWord.setOnClickListener(null);
                itemView.setOnClickListener(null);
            } else {
                viewWord.setVisibility(View.VISIBLE);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.viewWordListener.onViewWord(item.word);
                    }
                };

                viewWord.setOnClickListener(listener);
                itemView.setOnClickListener(listener);
            }
        }

    }

    interface ViewWordListener {
        public void onViewWord(String word);
    }

}
