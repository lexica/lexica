package com.serwylo.lexica.activities.score;

import com.serwylo.lexica.game.Game;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

public class ScoreCalculator {

    @Getter
    private int score = 0;

    @Getter
    private int maxScore = 0;

    @Getter
    private int numWords = 0;

    @Getter
    private int maxWords = 0;

    @Getter
    private List<Selected> items;

    public ScoreCalculator(Game game) {

        Set<String> possible = game.getSolutions().keySet();

        maxWords = possible.size();

        Iterator<String> uniqueWords = game.uniqueListIterator();
        items = new ArrayList<>();
        while (uniqueWords.hasNext()) {
            String w = uniqueWords.next();

            if (game.isWord(w) && game.getWordScore(w) > 0) {
                int points = game.getWordScore(w);
                items.add(new Selected(w, points, true));
                score += points;
                numWords++;
            } else {
                items.add(new Selected(w, 0, false));
            }

            possible.remove(w);
        }

        maxScore = score;

        for (String w : possible) {
            maxScore += game.getWordScore(w);
        }
    }

    @Data
    @AllArgsConstructor
    public static class Selected {
        private final String word;
        private final int score;
        private final boolean isWord;
    }
}
