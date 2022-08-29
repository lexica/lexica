package com.serwylo.lexica.activities.score;

import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;

import java.util.Collections;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

abstract class ScoreWordsViewBinder {

    protected final AppCompatActivity activity;
    protected final WordDefiner definer;
    protected final Sorter sorter;
    protected final Game game;

    ScoreWordsViewBinder(AppCompatActivity activity, @NonNull Game game, @NonNull Sorter sorter) {
        this.game = game;
        this.activity = activity;
        this.definer = new WordDefiner(activity, game.getLanguage());
        this.sorter = sorter;

        this.sorter.addListener(this::sortItems);
    }

    public void sortItems() {
        getSortButton().setIconResource(sorter.getIconResource());
        Collections.sort(getItems(), this.sorter);
        getAdapter().notifyDataSetChanged();
    }

    abstract protected FancyButton getSortButton();

    abstract protected List<Item> getItems();

    abstract protected Adapter getAdapter();

    static class Item {
        public final @NonNull
        String word;
        public final int points;
        public final boolean valid;
        public final @Nullable
        ViewWordListener viewWordListener;

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

        private int validWordColour;
        private int invalidWordColour;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            int[] attrs = new int[] {
                    R.attr.game__selected_word_colour,
                    R.attr.game__not_a_word_colour,
            };
            TypedArray attrValues = itemView.getContext().obtainStyledAttributes(attrs);
            validWordColour = attrValues.getColor(0, 0xffffff);
            invalidWordColour = attrValues.getColor(1, 0xffffff);
            attrValues.recycle();

        }

        void bind(final Item item) {

            TextView word = itemView.findViewById(R.id.word);
            word.setText(game.getLanguage().toRepresentation(item.word).toUpperCase());
            TextView score = itemView.findViewById(R.id.score);
            score.setText("+" + item.points);

            FancyButton define = itemView.findViewById(R.id.define);

            if (item.valid) {

                word.setPaintFlags(word.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);
                word.setTypeface(null, Typeface.BOLD);
                word.setTextColor(validWordColour);
                score.setVisibility(View.VISIBLE);
                define.setVisibility(View.VISIBLE);
                define.setOnClickListener(v -> definer.define(item.word));

            } else {

                word.setPaintFlags(word.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                word.setTypeface(null, Typeface.NORMAL);
                word.setTextColor(invalidWordColour);
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
                View.OnClickListener listener = v -> item.viewWordListener.onViewWord(item.word);

                viewWord.setOnClickListener(listener);
                itemView.setOnClickListener(listener);
            }
        }

    }

    interface ViewWordListener {
        void onViewWord(String word);
    }

}
