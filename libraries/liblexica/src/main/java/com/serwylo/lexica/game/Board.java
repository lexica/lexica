/*
 *  Copyright (C) 2008-2009 Rev. Johnny Healey <rev.null@gmail.com>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.serwylo.lexica.game;

import net.healeys.trie.TransitionMap;


public abstract class Board implements TransitionMap {
    private String[] board;
    private Integer[] positions;
    private final long rotationInvariantHash;

    public Board(String[] b) {
        board = b;
        positions = new Integer[getSize()];
        for (int i = 0; i < getSize(); i++) {
            positions[i] = i;
        }

        rotationInvariantHash = calcRotationInvariantHash();
    }

    /**
     * May be more than one character (see {@link com.serwylo.lexica.lang.Language#applyMandatorySuffix(String)}).
     */
    public synchronized String elementAt(int i) {
        return board[i];
    }

    /**
     * May be more than one character (see {@link com.serwylo.lexica.lang.Language#applyMandatorySuffix(String)}).
     */
    public synchronized String elementAt(int x, int y) {
        return board[x + getWidth() * y];
    }

    public synchronized String valueAt(int i) {
        return board[i];
    }

    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String cell : board) {
            if (!first) {
                sb.append(',').append(cell);
            } else {
                first = false;
                sb.append(cell);
            }
        }
        return sb.toString();
    }

    public synchronized long getRotationInvariantHash() {
        return rotationInvariantHash;
    }

    public synchronized void rotate() {
        String[] newbrd = new String[getSize()];
        Integer[] newpos = new Integer[getSize()];

        int w = getWidth();

        for (int i = 0; i < getSize(); i++) {
            int adjusted = w * (i % w) + ((w - 1) - (i / w));
            newbrd[adjusted] = board[i];
            newpos[adjusted] = positions[i];
        }

        board = newbrd;
        positions = newpos;
    }

    public abstract int getWidth();

    public int getRotatedPosition(int x) {
        return positions[x];
    }

    @Override
    public boolean canTransition(int fromX, int fromY, int toX, int toY) {
        if (fromX >= getWidth() || fromY >= getWidth() || toX >= getWidth() || toY >= getWidth()) {
            return false;
        }

        int xDistance = Math.abs(fromX - toX);
        int yDistance = Math.abs(fromY - toY);

        return (xDistance == 1 && yDistance == 1 || xDistance == 0 && yDistance == 1 || xDistance == 1 && yDistance == 0);
    }

    @Override
    public boolean canRevisit() {
        return false;
    }

    public String[] getLetters() {
        return board;
    }

    private long calcRotationInvariantHash() {
        /*
           This function calculates an rotation invariant hash by XOR-ring the hashes of getString()
           output of all four rotations. Since the order of XOR-ing bits doesn't matter, this is invariant.
         */

        long res = 0;
        for (int i = 0; i < 4; i++) {
            res = res ^ this.toString().hashCode(); // ^ is the XOR operation
            rotate();
        }
        return res;
    }
}
