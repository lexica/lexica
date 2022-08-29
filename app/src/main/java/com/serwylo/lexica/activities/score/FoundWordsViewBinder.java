package com.serwylo.lexica.activities.score;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;

import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

class FoundWordsViewBinder extends ScoreWordsViewBinder {

    private final Adapter adapter;
    private final List<Item> items;
    private final FancyButton sortButton;

    FoundWordsViewBinder(@NonNull ScoreActivity activity, FrameLayout parent, @NonNull Game game, @NonNull Sorter sorter) {
        super(activity, game, sorter);

        View foundWordsView = activity.getLayoutInflater().inflate(R.layout.score_found_words, parent, true);

        ScoreCalculator score = new ScoreCalculator(game);
        items = new ArrayList<>(score.getItems().size());
        for (ScoreCalculator.Selected selected : score.getItems()) {
            items.add(new Item(selected.getWord(), selected.getScore(), selected.isWord(), null));
        }

        adapter = new Adapter(items);

        RecyclerView words = foundWordsView.findViewById(R.id.words);
        words.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        words.setHasFixedSize(true);
        words.setAdapter(adapter);

        int totalScorePercentage = (int) (((double) score.getScore() / score.getMaxScore()) * 100);
        TextView scorePercentage = foundWordsView.findViewById(R.id.score_value);
        scorePercentage.setText(activity.getString(R.string.value_max_percentage, score.getScore(), score.getMaxScore(), totalScorePercentage));

        int totalWordsPercentage = (int) (((double) score.getNumWords() / score.getMaxWords()) * 100);
        TextView scoreValue = foundWordsView.findViewById(R.id.words_value);
        scoreValue.setText(activity.getString(R.string.value_max_percentage, score.getNumWords(), score.getMaxWords(), totalWordsPercentage));

        sortButton = foundWordsView.findViewById(R.id.btn_sort);
        sortButton.setIconResource(sorter.getIconResource());
        sortButton.setOnClickListener(v -> sorter.changeSort());

        sortItems();

    }

    protected FancyButton getSortButton() {
        return sortButton;
    }

    protected List<Item> getItems() {
        return items;
    }

    protected Adapter getAdapter() {
        return adapter;
    }

}
