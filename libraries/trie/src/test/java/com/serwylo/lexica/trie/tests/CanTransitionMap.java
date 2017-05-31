package com.serwylo.lexica.trie.tests;

import net.healeys.trie.TransitionMap;

/**
 * Mock {@link TransitionMap} for testing, which only cares about the letters on the board, not
 * whether you are allowed to transition from one to another. You can _always_ transition from
 * one to another.
 */
public class CanTransitionMap implements TransitionMap {

    private String[] letters;

    CanTransitionMap(String[] letters) {
        this.letters = letters;
    }

    @Override
    public boolean canTransition(int fromX, int fromY, int toX, int toY) {
        return true;
    }

    @Override
    public String valueAt(int position) {
        return letters[position];
    }

    @Override
    public int getSize() {
        return letters.length;
    }

    @Override
    public int getWidth() {
        return (int)Math.sqrt(letters.length);
    }
}
