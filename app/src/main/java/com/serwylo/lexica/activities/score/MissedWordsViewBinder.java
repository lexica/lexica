package com.serwylo.lexica.activities.score;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.view.BoardView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mehdi.sakout.fancybuttons.FancyButton;

class MissedWordsViewBinder extends ScoreWordsViewBinder {

    private final Adapter adapter;
    private final BoardView boardView;
    private final List<Item> items;
    private final FancyButton sortButton;

    MissedWordsViewBinder(@NonNull AppCompatActivity activity, FrameLayout parent, final @NonNull Game game, @NonNull Sorter sorter) {

        super(activity, game, sorter);

        View missedWordsView = activity.getLayoutInflater().inflate(R.layout.score_missed_words, parent, true);

        this.boardView = missedWordsView.findViewById(R.id.missed_board);
        this.boardView.setGame(game);

        final Set<String> possible = game.getSolutions().keySet();

        Iterator<String> uniqueWords = game.uniqueListIterator();
        while (uniqueWords.hasNext()) {
            String w = uniqueWords.next();
            possible.remove(w);
        }

        final RecyclerView words = missedWordsView.findViewById(R.id.words);
        words.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        words.setHasFixedSize(true);

        items = new ArrayList<>(possible.size());
        for (String word : possible) {
            items.add(new Item(game.getLanguage().toRepresentation(word), game.getWordScore(word), true, this::onViewWord));
        }

        adapter = new Adapter(items);
        words.setAdapter(this.adapter);

        sortButton = missedWordsView.findViewById(R.id.btn_sort);
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

    private void onViewWord(String word) {
        boardView.highlight(game.getSolutions().get(word).get(0).getPositions());
        boardView.invalidate();

        // Clear out the old selected item, find the new selected item, then notify the
        // adapter about both items so they can be updated.
        int index = -1;
        int previouslySelectedIndex = -1;
        Item previouslySelectedItem = this.adapter.getSelectedItem();
        Item newSelectedItem = null;
        for (int i = 0; i < items.size(); i++) {
            String itemWord = items.get(i).word;
            if (itemWord.equals(word)) {
                index = i;
                newSelectedItem = items.get(i);
            } else if (previouslySelectedItem != null && itemWord.equals(previouslySelectedItem.word)) {
                previouslySelectedIndex = i;
            }

            if (index != -1 && (previouslySelectedItem == null || previouslySelectedIndex != -1)) {
                break;
            }
        }

        this.adapter.setSelectedItem(newSelectedItem);

        if (index != -1) {
            this.adapter.notifyItemChanged(index);
        }

        if (previouslySelectedIndex != -1) {
            this.adapter.notifyItemChanged(previouslySelectedIndex);
        }
    }

}
