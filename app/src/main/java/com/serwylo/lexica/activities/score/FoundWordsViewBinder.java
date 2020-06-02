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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class FoundWordsViewBinder extends ScoreWordsViewBinder {

    FoundWordsViewBinder(@NonNull ScoreActivity activity, FrameLayout parent, @NonNull Game game) {
        super(activity, game);

        View foundWordsView = activity.getLayoutInflater().inflate(R.layout.score_found_words, parent, true);

        Set<String> possible = game.getSolutions().keySet();

        int score = 0;
        int max_score;
        int numWords = 0;
        int max_words = possible.size();

        Iterator<String> uniqueWords = game.uniqueListIterator();
        List<Item> items = new ArrayList<>();
        while (uniqueWords.hasNext()) {
            String w = uniqueWords.next();

            if (game.isWord(w) && game.getWordScore(w) > 0) {
                int points = game.getWordScore(w);
                items.add(new Item(w, points, true, null));
                score += points;
                numWords++;
            } else {
                items.add(new Item(w, 0, false, null));
            }

            possible.remove(w);
        }

        RecyclerView words = foundWordsView.findViewById(R.id.words);
        words.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        words.setHasFixedSize(true);
        words.setAdapter(new Adapter(items));

        activity.setHighScore(score);

        max_score = score;

        for (String w : possible) {
            max_score += game.getWordScore(w);
        }

        int totalScorePercentage = (int) (((double) score / max_score) * 100);
        TextView scorePercentage = foundWordsView.findViewById(R.id.score_value);
        scorePercentage.setText(activity.getString(R.string.value_max_percentage, score, max_score, totalScorePercentage));

        int totalWordsPercentage = (int) (((double) numWords / max_words) * 100);
        TextView scoreValue = foundWordsView.findViewById(R.id.words_value);
        scoreValue.setText(activity.getString(R.string.value_max_percentage, numWords, max_words, totalWordsPercentage));

    }

}
