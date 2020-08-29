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

import java.util.*;

class MissedWordsViewBinder extends ScoreWordsViewBinder {

    MissedWordsViewBinder(@NonNull AppCompatActivity activity, FrameLayout parent, final @NonNull Game game, Comparator<Item> comparator) {

        super(activity, game);

        View missedWordsView = activity.getLayoutInflater().inflate(R.layout.score_missed_words, parent, true);

        final BoardView bv = missedWordsView.findViewById(R.id.missed_board);
        bv.setGame(game);

        final Set<String> possible = game.getSolutions().keySet();

        Iterator<String> uniqueWords = game.uniqueListIterator();
        while (uniqueWords.hasNext()) {
            String w = uniqueWords.next();
            possible.remove(w);
        }

        final RecyclerView words = missedWordsView.findViewById(R.id.words);
        words.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        words.setHasFixedSize(true);

        final List<Item> items = new ArrayList<>(possible.size());
        final Adapter adapter = new Adapter(items);

        ViewWordListener onViewWord = word -> {
            bv.highlight(game.getSolutions().get(word).get(0).getPositions());
            bv.invalidate();

            // Clear out the old selected item, find the new selected item, then notify the
            // adapter about both items so they can be updated.
            int index = -1;
            int previouslySelectedIndex = -1;
            Item previouslySelectedItem = adapter.getSelectedItem();
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

            adapter.setSelectedItem(newSelectedItem);

            if (index != -1) {
                adapter.notifyItemChanged(index);
            }

            if (previouslySelectedIndex != -1) {
                adapter.notifyItemChanged(previouslySelectedIndex);
            }
        };

        for (String word : possible) {
            items.add(new Item(word, game.getWordScore(word), true, onViewWord));
        }

        items.sort(comparator);

        words.setAdapter(adapter);

    }

}
