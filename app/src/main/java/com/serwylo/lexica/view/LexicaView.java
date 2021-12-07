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

package com.serwylo.lexica.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.serwylo.lexica.GameActivity;
import com.serwylo.lexica.R;
import com.serwylo.lexica.Synchronizer;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.game.Game;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class LexicaView extends View implements Synchronizer.Event, Game.RotateHandler {

    @SuppressWarnings("unused")
    protected static final String TAG = "LexicaView";

    public static final int REDRAW_FREQ = 1;

    private FingerTracker mFingerTracker;
    private KeyboardTracker mKeyboardTracker;
    private Game game;
    private long timeRemainingInMillis;
    private int redrawCount;

    private int width;
    private int height;
    private int gridsize;
    private float boxsize;

    private final ThemeProperties theme;

    private int boardWidth;
    private String currentWord;

    private Paint p;
    private Set<Integer> highlighted = new HashSet<>();
    private int maxWeight;

    private float wordCountPosXLeft;
    private float wordCountPosXRight;
    private float wordCountPosYTop;

    public LexicaView(Context context, Game g) {
        this(context);

        game = g;
        maxWeight = game.getMaxWeight(); // Don't calculate this on each paint for performance.
        boardWidth = game.getBoard().getWidth();

        mFingerTracker = new FingerTracker(game);
        mKeyboardTracker = new KeyboardTracker();
        timeRemainingInMillis = 0;
        redrawCount = 1;

        p = new Paint();
        p.setTextAlign(Paint.Align.CENTER);
        p.setAntiAlias(true);
        p.setStrokeWidth(2);

        setFocusable(true);

        g.setRotateHandler(this);
    }

    public LexicaView(Context context) {
        this(context, (AttributeSet) null);
    }

    public LexicaView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.lexicaViewStyle);
    }

    public LexicaView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        theme = new ThemeProperties(context, attrs, defStyle);
    }

    private void setDimensions(int w, int h) {
        width = w;
        height = h;

        if (width < height) {
            gridsize = width - (2 * theme.padding);
        } else {
            gridsize = height - (2 * theme.padding) - theme.game.timer.height;
        }
        boxsize = ((float) gridsize) / boardWidth;

        if (mFingerTracker != null) {
            mFingerTracker.boundBoard(theme.padding + gridsize, theme.padding + theme.game.timer.height + gridsize);
        }
    }

    private final Rect textBounds = new Rect();

    public boolean isLayoutRtl() {
        return ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    private void drawBoard(Canvas canvas) {
        int topOfGrid = theme.padding;

        p.setColor(0x00ff00);
        canvas.drawRect(0, 0, 100, 200, p);

        // Draw boxes
        for (int i = 0; i < game.getBoard().getSize(); i++) {
            int pos = game.getBoard().getRotatedPosition(i);

            int x = i % game.getBoard().getWidth();
            int y = i / game.getBoard().getWidth();

            if (highlighted.contains(i)) {
                p.setColor(theme.board.tile.highlightColour);
            } else {
                if (game.hintModeColor()) {
                    int weight = game.getWeight(pos);
                    int colour = weight == 0 ? theme.board.tile.hintModeUnusableLetterBackgroundColour : theme.board.tile.getHintModeGradientColour((float) weight / maxWeight);
                    p.setColor(colour);
                } else {
                    p.setColor(theme.board.tile.backgroundColour);
                }
            }

            float left = theme.padding + (boxsize * x);
            float top = topOfGrid + (boxsize * y);
            float right = theme.padding + (boxsize * (x + 1));
            float bottom = topOfGrid + (boxsize * (y + 1));
            canvas.drawRect(left, top, right, bottom, p);
        }

        // Draw grid, but exclude the first and last line (both horizontally and vertically unless asked)
        p.setColor(theme.board.tile.borderColour);
        p.setStrokeWidth(theme.board.tile.borderWidth);

        // Vertical lines
        for (float i = theme.padding + boxsize; i <= theme.padding + gridsize - boxsize; i += boxsize) {
            canvas.drawLine(i, topOfGrid, i, gridsize + topOfGrid, p);
        }
        // Horizontal lines
        float finalHorizontalLinePosition = theme.board.hasOuterBorder ? topOfGrid + gridsize : topOfGrid + gridsize - boxsize;
        for (float i = topOfGrid + boxsize; i <= finalHorizontalLinePosition; i += boxsize) {
            canvas.drawLine(theme.padding, i, gridsize + theme.padding, i, p);
        }

        p.setColor(theme.board.tile.foregroundColour);
        p.setTypeface(Fonts.get().getSansSerifCondensed());
        float textSize = boxsize * 0.8f;
        p.setTextSize(textSize);

        // Find vertical center offset
        p.getTextBounds("A", 0, 1, textBounds);
        float offset = textBounds.exactCenterY();

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardWidth; y++) {
                int pos = game.getBoard().getRotatedPosition(y * boardWidth + x);
                int weight = game.getWeight(pos);

                if (game.hintModeColor() || game.hintModeCount()) {
                    int color = (weight == 0) ? theme.board.tile.hintModeUnusableLetterColour : theme.board.tile.foregroundColour;
                    p.setColor(color);
                } else {
                    p.setColor(theme.board.tile.foregroundColour);
                }

                if (game.hintModeCount()) {
                    p.setTextSize(textSize / 4);
                    p.setTextAlign(Paint.Align.LEFT);
                    canvas.drawText("" + weight, theme.padding + (x * boxsize) + 8, topOfGrid + ((y + 1) * boxsize) - 6, p);
                }

                String letter = game.getBoard().elementAt(x, y);
                String letterForDisplay = game.getLanguage().toDisplay(letter);
                p.setTextSize(textSize);
                p.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(letterForDisplay, theme.padding + (x * boxsize) + (boxsize / 2), topOfGrid + (y * boxsize) + (boxsize / 2) - offset, p);
                if (GameMode.SCORE_LETTERS.equals(game.getScoreType())) {
                    String score = String.valueOf(game.getLanguage().getPointsForLetter(letter));
                    p.setTextSize(textSize / 4);
                    p.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText(score, theme.padding + ((x + 1) * boxsize) - 8, topOfGrid + ((y + 1) * boxsize) - 6, p);
                }
            }
        }
    }

    private void drawTimer(Canvas canvas, boolean isRtl) {
        // Background for timer. Depending on the theme, may be the same colour as the rest
        // of the background.
        p.setColor(theme.game.timer.backgroundColour);
        canvas.drawRect(0, height - theme.game.timer.height - theme.game.timer.borderWidth - theme.game.timer.borderWidth, width, height, p);

        if (timeRemainingInMillis < 10000) {
            p.setColor(theme.game.timer.endForegroundColour);
        } else if (timeRemainingInMillis < 30000) {
            p.setColor(theme.game.timer.midForegroundColour);
        } else {
            p.setColor(theme.game.timer.startForegroundColour);
        }

        long pixelWidth = width * timeRemainingInMillis / (game.getGameMode().getTimeLimitSeconds() * 1000);

        canvas.drawRect(isRtl ? width - pixelWidth : 0, height - theme.game.timer.height - theme.game.timer.borderWidth, isRtl ? width : pixelWidth, height, p);
    }

    private FontHeightMeasurer fontHeights = new FontHeightMeasurer();

    /**
     * In each render loop, we need to do several measurements of different font sizes. The height
     * of a font wont change between renders, so we cache the height calculations.
     */
    private static class FontHeightMeasurer {
        private Map<Integer, Integer> fontSizeToPixelHeight = new HashMap<>();

        public int getHeight(int fontSize) {
            if (!fontSizeToPixelHeight.containsKey(fontSize)) {
                Paint p = new Paint();
                p.setTextSize(fontSize);
                Rect bounds = new Rect();
                p.getTextBounds("A", 0, 1, bounds);
                int height = bounds.height();
                fontSizeToPixelHeight.put(fontSize, height);
            }

            return fontSizeToPixelHeight.get(fontSize);
        }
    }

    /**
     * Each time we draw a word, we need to:
     * - Measure it and decide how much space it takes.
     * - Potentially fade it out if it is too far to the right.
     * - Potentially add a strike over the top of it if it is not a word.
     * - Colourise it correctly to indicate that it has already been used in the past.
     * - Maybe more?
     * <p>
     * After drawing, we can return the right hand size, to indicate how much space we took up
     * when rendering. This can be used to decide where to start the following word.
     * <p>
     * Note that this method is RTL aware. If the view should be drawn as RTL then the returned
     * position will be to the let of the starting point, rather than the right.
     */
    private float drawPastWord(@NonNull Canvas canvas, boolean isRtl, String word, float x, float y, boolean isWord, boolean hasBeenUsedBefore) {
        word = word.toUpperCase(game.getLanguage().getLocale());

        p.setTextSize(theme.game.pastWordTextSize);
        p.setTypeface(isWord && !hasBeenUsedBefore ? Fonts.get().getSansSerifBold() : Fonts.get().getSansSerifCondensed());
        p.getTextBounds(word, 0, word.length(), textBounds);
        float height = textBounds.height();
        float width = textBounds.width();

        p.setColor(!isWord ? theme.game.notAWordColour : (hasBeenUsedBefore ? theme.game.previouslySelectedWordColour : theme.game.selectedWordColour));

        p.setTextSize(theme.game.pastWordTextSize);
        p.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(word, isRtl ? x - width : x, y + height, p);

        if (!isWord) {
            // Strike-through
            p.setStrokeWidth(6);
            canvas.drawLine(isRtl ? x - width : x, y + height / 2, isRtl ? x : x + width, y + height / 2, p);
        }

        // Fade out the word as it approaches the end of the screen.
        if (isRtl) {
            if (x - width < theme.game.score.padding) {
                Shader shaderA = new LinearGradient(theme.game.score.padding * 5, y, theme.game.score.padding * 2, y, 0x00ffffff, theme.game.backgroundColor, Shader.TileMode.CLAMP);
                p.setShader(shaderA);
                canvas.drawRect(0, y - 2, theme.game.score.padding * 5, y + height + 2, p);
                p.setShader(null);
            }
        } else {
            if (x + width > getWidth() - theme.game.score.padding) {
                Shader shaderA = new LinearGradient(getWidth() - theme.game.score.padding * 5, y, getWidth() - theme.game.score.padding * 2, y, 0x00ffffff, theme.game.backgroundColor, Shader.TileMode.CLAMP);
                p.setShader(shaderA);
                canvas.drawRect(getWidth() - theme.game.score.padding * 5, y - 2, getWidth(), y + height + 2, p);
                p.setShader(null);
            }
        }

        return isRtl ? x - width : x + width;
    }

    private void drawWordList(Canvas canvas, boolean isRtl, float top, float bottom) {

        int currentWordHeight = fontHeights.getHeight(theme.game.currentWordSize);
        int pastWordHeight = fontHeights.getHeight(theme.game.pastWordTextSize);
        int wordListPadding = (int) ((bottom - top - currentWordHeight - pastWordHeight) / 3);

        float pos = top + wordListPadding + currentWordHeight;

        // If halfway through selecting the current word, then show that.
        // Otherwise, show the last word that was selected.
        String bigWordToShow = currentWord;
        int scoreForBigWord = 0;
        p.setColor(theme.game.currentWordColour);
        if (bigWordToShow == null) {
            ListIterator<String> pastWords = game.listIterator();
            if (pastWords.hasNext()) {
                String lastWord = pastWords.next();
                if (lastWord.startsWith("+")) {
                    p.setColor(theme.game.previouslySelectedWordColour);
                    bigWordToShow = lastWord.substring(1);
                    scoreForBigWord = game.getWordScore(bigWordToShow);
                } else if (game.isWord(lastWord)) {
                    bigWordToShow = lastWord;
                    scoreForBigWord = game.getWordScore(bigWordToShow);
                } else {
                    bigWordToShow = lastWord;
                }
            }
        }


        if (bigWordToShow != null) {
            p.setTextSize(theme.game.currentWordSize);
            p.setTypeface(Fonts.get().getSansSerifCondensed());
            p.setTextAlign(Paint.Align.CENTER);
            if (scoreForBigWord > 0) {
                bigWordToShow = isLayoutRtl() ? "+" + scoreForBigWord + " " + bigWordToShow : bigWordToShow + " +" + scoreForBigWord;
            }
            canvas.drawText(bigWordToShow.toUpperCase(game.getLanguage().getLocale()), width / 2f, pos, p);
        }


        // draw words
        pos += wordListPadding / 2f + theme.game.pastWordTextSize;

        float x = isRtl ? getWidth() - theme.game.score.padding : theme.game.score.padding;
        ListIterator<String> pastWords = game.listIterator();

        // Don't bother showing past words if there isn't enough vertical space on this screen.
        while (pastWords.hasNext() && pos < bottom) {
            String w = pastWords.next();
            float newX;
            if (w.startsWith("+")) {
                w = w.substring(1);
                newX = drawPastWord(canvas, isRtl, w, x, pos, true, true);
            } else {
                if (game.isWord(w)) {
                    newX = drawPastWord(canvas, isRtl, w, x, pos, true, false);
                } else {
                    newX = drawPastWord(canvas, isRtl, w, x, pos, false, false);
                }
            }

            x = isRtl ? newX - theme.game.score.padding : newX + theme.game.score.padding;

            // Don't bother rendering words which push off the screen.
            if (isLayoutRtl() && x < theme.game.score.padding || !isLayoutRtl() && x > getWidth() - theme.game.score.padding) {
                break;
            }
        }
    }

    private void drawScore(Canvas canvas, boolean isRtl) {
        float headingHeight = fontHeights.getHeight(theme.game.score.headingTextSize);
        float valueHeight = fontHeights.getHeight(theme.game.score.textSize);

        float scoreHeight = theme.game.score.padding + headingHeight + theme.game.score.padding / 2f + valueHeight + theme.game.score.padding;
        float totalTimerHeight = theme.game.timer.borderWidth * 2 + theme.game.timer.height;
        p.setColor(theme.game.score.backgroundColour);
        canvas.drawRect(0, height - totalTimerHeight - scoreHeight, width, height - totalTimerHeight, p);

        float scoreStartY = height - totalTimerHeight - scoreHeight;
        float panelWidth = width / 3f;

        drawWordList(canvas, isRtl, boardWidth * boxsize, scoreStartY);

        long secRemaining = timeRemainingInMillis / 1000;
        long mins = secRemaining / 60;
        long secs = secRemaining % 60;
        String displayTime = mins + ":" + (secs < 10 ? "0" : "") + secs;
        drawScorePanel(canvas, isRtl ? 2 : 0, panelWidth, scoreStartY, getContext().getString(R.string.time), displayTime);

        String displayWordCount = game.getWordCount() + "/" + game.getMaxWordCount();
        drawScorePanel(canvas, 1, panelWidth, scoreStartY, getContext().getString(R.string.words), displayWordCount);

        String displayScore = Integer.toString(game.getScore());
        drawScorePanel(canvas, isRtl ? 0 : 2, panelWidth, scoreStartY, getContext().getString(R.string.score), displayScore);
    }

    private void drawScorePanel(Canvas canvas, float panelNum, float panelWidth, float y, String heading, String value) {
        float x = panelNum * panelWidth;

        p.setTextAlign(Paint.Align.CENTER);

        float headingHeight = fontHeights.getHeight(theme.game.score.headingTextSize);

        p.setColor(theme.game.score.headingTextColour);
        p.setTextSize(theme.game.score.headingTextSize);
        p.setTypeface(Fonts.get().getSansSerifCondensed());
        canvas.drawText(heading, x + panelWidth / 2, y + theme.game.score.padding + headingHeight, p);

        float valueHeight = fontHeights.getHeight(theme.game.score.textSize);

        p.setColor(theme.game.score.textColour);
        p.setTextSize(theme.game.score.textSize);
        p.setTypeface(Fonts.get().getSansSerifBold());
        canvas.drawText(value, x + panelWidth / 2, y + theme.game.score.padding + headingHeight + theme.game.score.padding / 2f + valueHeight, p);

        if (panelNum == 1 && wordCountPosXLeft == 0) {
            wordCountPosXLeft = x;
            wordCountPosXRight = x + panelWidth;
            wordCountPosYTop = y;
        }
    }

    private void clearScreen(Canvas canvas) {
        p.setColor(theme.game.backgroundColor);
        canvas.drawRect(0, 0, width, height, p);
    }

    @Override
    public void onDraw(Canvas canvas) {
        setDimensions(getMeasuredWidth(), getMeasuredHeight());

        canvas.drawColor(theme.scoreScreenBackgroundColour);

        if (game.getStatus() != Game.GameStatus.GAME_RUNNING)
            return;

        boolean isRtl = isLayoutRtl(); // For performance, don't constantly ask this while drawing.

        clearScreen(canvas);
        drawBoard(canvas);
        drawScore(canvas, isRtl);
        drawTimer(canvas, isRtl);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mFingerTracker == null)
            return false;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                mFingerTracker.touchScreen((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_UP:
                mFingerTracker.release();
                checkWordCountTap(event);
                break;
        }

        redraw();
        return true;
    }

    private void checkWordCountTap(MotionEvent event) {
        if (event.getX() >= wordCountPosXLeft
         && event.getX() <= wordCountPosXRight
         && event.getY() >= wordCountPosYTop) {
            ((GameActivity)getContext()).showFoundWords();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
            String letter = Character.toString((char) event.getUnicodeChar()).toLowerCase();
            mKeyboardTracker.processLetter(game.getLanguage().applyMandatorySuffix(letter));
        } else if (keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER) {
            mKeyboardTracker.reset();
        }

        return false;
    }

    public void redraw() {
        redrawCount = REDRAW_FREQ;
        invalidate();
    }

    public void tick(long timeRemainingInMillis) {
        boolean doRedraw = false;

        this.timeRemainingInMillis = timeRemainingInMillis;
        if (--redrawCount <= 0) {
            doRedraw = true;
        }
        if (doRedraw) {
            redraw();
        }
    }

    public void onRotate() {
        mFingerTracker.reset();
        mKeyboardTracker.fullReset();
    }

    private class FingerTracker {
        private final Game game;

        private int numTouched;
        private final int[] touched;
        private Set<Integer> touchedCells;

        private int touching;

        private int left;
        private int top;
        private int width;
        private int height;

        private int box_width;
        private int radius_squared;

        FingerTracker(Game g) {
            game = g;
            touched = new int[game.getBoard().getSize()];
            touchedCells = new HashSet<>();

            reset();
        }

        private void reset() {
            Arrays.fill(touched, -1);

            if (numTouched > 0) {
                highlighted.clear();
            }

            touchedCells.clear();
            numTouched = 0;
            touching = -1;
        }

        private void countTouch() {
            if (touchedCells.contains(touching)) {
                return;
            }

            touched[numTouched] = touching;
            touchedCells.add(touching);
            highlighted = touchedCells;
            numTouched++;
            game.playTileSound(numTouched);
            redraw();
        }

        void touchScreen(int x, int y) {
            if (x < left || x >= (left + width))
                return;
            if (y < top || y >= (top + height))
                return;

            int bx = (x - left) * boardWidth / width;
            int by = (y - top) * boardWidth / height;

            touchBox(bx, by);

            if (canTouch(bx, by) && nearCenter(x, y, bx, by)) {
                countTouch();
            }
        }

        private boolean canTouch(int x, int y) {
            currentWord = getWord();

            int box = x + boardWidth * y;
            if (touchedCells.contains(box)) {
                return false;
            }

            int previousX = touched[numTouched - 1] % boardWidth;
            int previousY = touched[numTouched - 1] / boardWidth;
            return game.getBoard().canTransition(previousX, previousY, x, y);
        }

        private void touchBox(int x, int y) {
            int box = x + boardWidth * y;
            mKeyboardTracker.reset();

            if (touching < 0) {
                touching = box;
                countTouch();
            } else if (touching != box && canTouch(x, y)) {
                touching = box;
            }
        }

        private boolean nearCenter(int x, int y, int bx, int by) {
            int cx, cy;

            cx = left + (bx * box_width) + (box_width / 2);
            cy = top + (by * box_width) + (box_width / 2);

            int d_squared = (cx - x) * (cx - x) + (cy - y) * (cy - y);

            return d_squared < radius_squared;
        }

        void boundBoard(int w, int h) {
            left = theme.padding;
            top = theme.padding;
            width = w;
            height = h;

            box_width = width / boardWidth;

            radius_squared = box_width / 3;
            radius_squared *= radius_squared;
        }

        String getWord() {
            StringBuilder word = new StringBuilder();

            for (int i = 0; i < numTouched; i++) {
                word.append(game.getBoard().elementAt(touched[i]));
            }

            return word.toString();
        }

        void release() {
            if (numTouched > 0) {
                String s = getWord();

                game.addWord(s);
                currentWord = null;
            }

            reset();
        }
    }

    private class KeyboardTracker {
        private Set<String> defaultAcceptableLetters = new HashSet<>();
        private LinkedList<State> defaultStates;

        private Set<String> acceptableLetters;
        private LinkedList<State> states;

        private String tracked;

        KeyboardTracker() {
            fullReset();
        }

        private void fullReset() {
            defaultStates = new LinkedList<>();
            defaultAcceptableLetters.clear();

            for (int i = 0; i < game.getBoard().getSize(); i++) {
                defaultStates.add(new State(game.getBoard().valueAt(i), i));
                defaultAcceptableLetters.add(game.getBoard().valueAt(i));
            }

            reset();
        }

        private void reset() {
            if (tracked != null) {
                game.addWord(tracked);
                highlighted.clear();
                currentWord = null;
            }

            acceptableLetters = new HashSet<>(defaultAcceptableLetters);
            states = defaultStates;
            tracked = null;
        }

        private void processLetter(String letter) {
            mFingerTracker.reset();

            if (!acceptableLetters.contains(letter)) {
                return;
            }

            LinkedList<State> subStates = new LinkedList<>();
            acceptableLetters.clear();
            ListIterator<State> iter = states.listIterator();

            boolean appendedString = false;

            while (iter.hasNext()) {
                State nState = iter.next();
                if (!nState.letter.equals(letter)) {
                    continue;
                }

                if (!appendedString) {
                    if (tracked == null) {
                        tracked = "";
                    }

                    tracked += game.getBoard().elementAt(nState.pos);
                    currentWord = tracked;

                    appendedString = true;
                }
                highlighted = nState.selected;
                acceptableLetters.addAll(nState.getNextStates(subStates));
            }

            states = subStates;
        }

        /**
         * A "state" represents the set of letters that has been pressed so far, up until the last letter.
         */
        private class State {
            final String letter;
            final int pos;
            final Set<Integer> selected;

            State(String letter, int pos) {
                this.letter = letter;
                this.pos = pos;
                selected = new HashSet<>();
                selected.add(pos);
            }

            State(String letter, int pos, Set<Integer> selected) {
                this.letter = letter;
                this.pos = pos;
                this.selected = selected;
            }

            Set<String> getNextStates(LinkedList<State> possibleStates) {
                Set<String> canTransitionToNext = new HashSet<>();

                for (int i = 0; i < game.getBoard().getSize(); i++) {
                    if (selected.contains(i)) {
                        continue;
                    }

                    int fromX = pos % game.getBoard().getWidth();
                    int fromY = pos / game.getBoard().getWidth();

                    int toX = i % game.getBoard().getWidth();
                    int toY = i / game.getBoard().getWidth();

                    if (!game.getBoard().canTransition(fromX, fromY, toX, toY)) {
                        continue;
                    }

                    Set<Integer> newStatePositions = new HashSet<>(selected);
                    newStatePositions.add(i);

                    String letter = game.getBoard().valueAt(i);
                    possibleStates.add(new State(letter, i, newStatePositions));
                    canTransitionToNext.add(letter);
                }

                return canTransitionToNext;
            }
        }

    }

}
