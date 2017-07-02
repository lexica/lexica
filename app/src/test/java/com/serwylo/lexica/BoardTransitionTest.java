package com.serwylo.lexica;

import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.FiveByFiveBoard;
import com.serwylo.lexica.game.FourByFourBoard;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BoardTransitionTest {

    @Test
    public void fiveByFive() {

        Board board = new FiveByFiveBoard(new String[] {
                "A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O",
                "P", "Q", "R", "S", "T",
                "U", "V", "W", "X", "Y",
        });

        assertCanTransition(board, "A", "B", "F", "G");
        assertCannotTransition(board, "A", "C", "D", "E", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y");

        assertCanTransition(board, "B", "A", "F", "G", "H", "C");
        assertCannotTransition(board, "B", "D", "E", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y");

        assertCanTransition(board, "C", "B", "G", "H", "I", "D");
        assertCannotTransition(board, "C", "A", "E", "F", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y");
    }

    @Test
    public void fourByFour() {

        Board board = new FourByFourBoard(new String[] {
                "A", "B", "C", "D",
                "E", "F", "G", "H",
                "I", "J", "K", "L",
                "M", "N", "O", "P",
        });

        assertCanTransition(board, "A", "B", "E", "F");
        assertCannotTransition(board, "A", "C", "D", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P");

        assertCanTransition(board, "B", "A", "E", "F", "G", "C");
        assertCannotTransition(board, "B", "D", "H", "I", "J", "K", "L", "M", "N", "O", "P");

        assertCanTransition(board, "C", "B", "F", "G", "H", "D");
        assertCannotTransition(board, "C", "A", "E", "I", "J", "K", "L", "M", "N", "O", "P");

        assertCanTransition(board, "D", "C", "G", "H");
        assertCannotTransition(board, "D", "A", "B", "E", "F", "I", "J", "K", "L", "M", "N", "O", "P");

        assertCanTransition(board, "E", "A", "B", "F", "J", "I");
        assertCannotTransition(board, "E", "C", "D", "G", "H", "K", "L", "M", "N", "O", "P");

        assertCanTransition(board, "F", "A", "B", "C", "G", "K", "J", "I", "E");
        assertCannotTransition(board, "F", "D", "H", "L", "M", "N", "O");

        assertCanTransition(board, "G", "B", "C", "D", "H", "L", "K", "J", "F");
        assertCannotTransition(board, "G", "A", "E", "I", "M", "N", "O", "P");

        assertCanTransition(board, "H", "D", "C", "G", "K", "L");
        assertCannotTransition(board, "H", "A", "B", "E", "F", "I", "J", "M", "N", "O", "P");

        assertCanTransition(board, "I", "E", "F", "J", "N", "M");
        assertCannotTransition(board, "I", "A", "B", "C", "D", "G", "H", "K", "L", "O", "P");

        assertCanTransition(board, "J", "E", "F", "G", "K", "O", "N", "M", "I");
        assertCannotTransition(board, "J", "A", "B", "C", "D", "H", "L", "P");

        assertCanTransition(board, "K", "F", "G", "H", "L", "P", "O", "N", "J");
        assertCannotTransition(board, "K", "A", "B", "C", "D", "E", "I");

        assertCanTransition(board, "L", "H", "G", "K", "O", "P");
        assertCannotTransition(board, "L", "A", "B", "C", "D", "E", "F", "I", "J", "M", "N");

        assertCanTransition(board, "M", "I", "J", "N");
        assertCannotTransition(board, "M", "A", "B", "C", "D", "E", "F", "G", "H", "K", "L", "O", "P");

        assertCanTransition(board, "N", "M", "I", "J", "K", "O");
        assertCannotTransition(board, "N", "A", "B", "C", "D", "E", "F", "G", "H", "L", "P");

        assertCanTransition(board, "O", "N", "J", "K", "L", "P");
        assertCannotTransition(board, "O", "A", "B", "C", "D", "E", "F", "G", "H", "I", "M");

        assertCanTransition(board, "P", "O", "K", "L");
        assertCannotTransition(board, "P", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "M", "N");

    }

    private void assertCanTransition(Board board, String from, String ... to) {
        for (String toLetter : to) {
            assertCanTransition(board, from, toLetter);
        }
    }

    private void assertCannotTransition(Board board, String from, String ... to) {
        for (String toLetter : to) {
            assertCannotTransition(board, from, toLetter);
        }
    }

    private void assertCanTransition(Board board, String from, String to) {
        int fromPosition = positionOf(board, from);
        int toPosition = positionOf(board, to);

        int fromX = fromPosition % board.getWidth();
        int fromY = fromPosition / board.getWidth();

        int toX = toPosition % board.getWidth();
        int toY = toPosition / board.getWidth();

        assertCanTransition(board, fromX, fromY, toX, toY);
    }

    private void assertCannotTransition(Board board, String from, String to) {
        int fromPosition = positionOf(board, from);
        int toPosition = positionOf(board, to);

        int fromX = fromPosition % board.getWidth();
        int fromY = fromPosition / board.getWidth();

        int toX = toPosition % board.getWidth();
        int toY = toPosition / board.getWidth();

        assertCannotTransition(board, fromX, fromY, toX, toY);
    }

    private int positionOf(Board board, String letter) {
        for (int i = 0; i < board.getSize(); i ++) {
            if (board.elementAt(i).equals(letter)) {
                return i;
            }
        }

        throw new IllegalArgumentException("Letter \"" + letter + "\" not on board.");
    }

    private void assertCanTransition(Board board, int fromX, int fromY, int toX, int toY) {
        String from = board.elementAt(xyToPosition(board, fromX, fromY));
        String to = board.elementAt(xyToPosition(board, toX, toY));
        assertTrue(from + " should be able to transition to " + to, board.canTransition(fromX, fromY, toX, toY));
    }

    private void assertCannotTransition(Board board, int fromX, int fromY, int toX, int toY) {
        String from = board.elementAt(xyToPosition(board, fromX, fromY));
        String to = board.elementAt(xyToPosition(board, toX, toY));
        assertFalse(from + " should not be able to transition to " + to, board.canTransition(fromX, fromY, toX, toY));
    }

    private static int xyToPosition(Board board, int x, int y) {
        return x + board.getWidth() * y;
    }

}
