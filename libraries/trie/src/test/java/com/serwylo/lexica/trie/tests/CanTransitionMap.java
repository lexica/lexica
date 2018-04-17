package com.serwylo.lexica.trie.tests;

import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.trie.util.LetterFrequency;

import net.healeys.trie.TransitionMap;

/**
 * Mock {@link TransitionMap} for testing, which only cares about the letters on the board, not
 * whether you are allowed to transition from one to another. You can _always_ transition from
 * one to another.
 *
 * There is some hackery here to allow for a number of letters which is not a perfect square.
 * For example, the english alphabet includes 26 letters, but a normal board can only have 25 or
 * 36 letters. Thus, this cheats by effectively repeating the last letter in {@link #getWidth()}
 * and {@link #valueAt(int)}.
 */
public class CanTransitionMap implements TransitionMap {

    private String[] letters;

    CanTransitionMap(String[] letters) {
        this.letters = letters;
    }

    CanTransitionMap(LetterFrequency frequency) {
        this.letters = new String[frequency.getLetters().size()];
        frequency.getLetters().toArray(this.letters);
    }

    CanTransitionMap() {
        this.letters = new String[]{
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "qu", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        };
    }

    @Override
    public boolean canTransition(int fromX, int fromY, int toX, int toY) {
        return true;
    }

    @Override
    public boolean canRevisit() {
        return true;
    }

    @Override
    public String valueAt(int position) {
        if (position >= letters.length) {
            position = letters.length - 1;
        }
        return letters[position];
    }

    @Override
    public int getSize() {
        return letters.length;
    }

    @Override
    public int getWidth() {
        return (int)Math.ceil(Math.sqrt(letters.length));
    }
}
