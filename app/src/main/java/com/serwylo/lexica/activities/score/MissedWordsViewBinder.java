package com.serwylo.lexica.activities.score;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.view.BoardView;

import net.healeys.trie.Solution;

import java.util.Iterator;
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


        LinearLayout missedVG = missedWordsView.findViewById(R.id.words);
        for (String word : possible) {
            final Solution solution = game.getSolutions().get(word).get(0);
            addWord(missedVG, word, game.getWordScore(word), true, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bv.highlight(solution.getPositions());
                    bv.invalidate();
                    // TODO: Highlight selected word.
                }
            });
        }

    }
}
