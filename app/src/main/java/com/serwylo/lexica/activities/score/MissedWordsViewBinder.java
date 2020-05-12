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

import net.healeys.trie.Solution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class MissedWordsViewBinder extends ScoreWordsViewBinder {

    MissedWordsViewBinder(@NonNull AppCompatActivity activity, FrameLayout parent, @NonNull Game game) {

        super(activity, game);

        View missedWordsView = activity.getLayoutInflater().inflate(R.layout.score_missed_words, parent, true);

        final BoardView bv = missedWordsView.findViewById(R.id.missed_board);
        bv.setGame(game);

        Set<String> possible = game.getSolutions().keySet();

        Iterator<String> uniqueWords = game.uniqueListIterator();
        while(uniqueWords.hasNext()) {
            String w = uniqueWords.next();
            possible.remove(w);
        }

        List<Item> items = new ArrayList<>(possible.size());
        for (String word : possible) {
            final Solution solution = game.getSolutions().get(word).get(0);
            items.add(new Item(word, game.getWordScore(word), true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bv.highlight(solution.getPositions());
                    bv.invalidate();
                    // TODO: Highlight selected word.
                }
            }));
        }

        RecyclerView words = missedWordsView.findViewById(R.id.words);
        words.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        words.setHasFixedSize(true);
        words.setAdapter(new Adapter(items));

    }

}
