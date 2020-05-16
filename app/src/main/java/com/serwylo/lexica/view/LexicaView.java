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

import com.serwylo.lexica.R;
import com.serwylo.lexica.Synchronizer;
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

	public static final int REDRAW_FREQ = 10;

	private FingerTracker mFingerTracker;
	private KeyboardTracker mKeyboardTracker;
	private Game game;
	private int timeRemaining;
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

	public LexicaView(Context context, Game g) {
		this(context);

		game = g;
		maxWeight = game.getMaxWeight(); // Don't calculate this on each paint for performance.
		boardWidth = game.getBoard().getWidth();

		mFingerTracker = new FingerTracker(game);
		mKeyboardTracker = new KeyboardTracker();
		timeRemaining = 0;
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
		this( context, attrs, R.attr.lexicaViewStyle );
	}

	public LexicaView(Context context, AttributeSet attrs, int defStyle) {
		super( context, attrs, defStyle );

		theme = new ThemeProperties(context, attrs, defStyle);
	}

	private void setDimensions(int w, int h) {
		width = w;
		height = h;

		if (width < height) {
			gridsize = width - (2 * theme.paddingSize);
		} else {
			gridsize = height - (2 * theme.paddingSize) - theme.timerHeight;
		}
		boxsize = ((float) gridsize) / boardWidth;

		if (mFingerTracker != null) {
			mFingerTracker.boundBoard(theme.paddingSize + gridsize, theme.paddingSize + theme.timerHeight + gridsize);
		}
	}

	private final Rect textBounds = new Rect();

	private void drawBoard(Canvas canvas) {
		int topOfGrid = theme.paddingSize;

		// Draw boxes
		for (int i = 0; i < game.getBoard().getSize(); i++) {
			int pos = game.getBoard().getRotatedPosition(i);

			int x = i % game.getBoard().getWidth();
			int y = i / game.getBoard().getWidth();

			if (highlighted.contains(i)) {
				p.setColor(theme.tileHighlightColour);
			} else {
				if (game.hintModeColor()) {
					int weight = game.getWeight(pos);
					int colour = weight == 0 ? theme.hintModeUnusableLetterBackgroundColour : theme.getHintModeGradientColour((float)weight / maxWeight);
					p.setColor(colour);
				} else {
					p.setColor(theme.tileBackgroundColour);
				}
			}

			float left = theme.paddingSize + (boxsize * x);
			float top = topOfGrid + (boxsize * y);
			float right = theme.paddingSize + (boxsize * (x + 1));
			float bottom = topOfGrid + (boxsize * (y + 1));
			canvas.drawRect(left, top, right, bottom, p);
		}

		// Draw grid, but exclude the first and last line (both horizontally and vertically.
		p.setColor(theme.tileBorderColour);
		p.setStrokeWidth(theme.tileBorderWidth);

		// Vertical lines
		for (float i = theme.paddingSize + boxsize; i <= theme.paddingSize + gridsize - boxsize; i += boxsize) {
			canvas.drawLine(i, topOfGrid, i, gridsize + topOfGrid, p);
		}
		// Horizontal lines
		for (float i = topOfGrid + boxsize; i <= topOfGrid + gridsize - boxsize; i += boxsize) {
			canvas.drawLine(theme.paddingSize, i, gridsize + theme.paddingSize, i, p);
		}

		p.setColor(theme.tileForegroundColour);
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
					int color = (weight == 0) ? theme.hintModeUnusableLetterColour : 0;
					p.setColor(color);
				} else {
					p.setColor(theme.tileForegroundColour);
				}

				if (game.hintModeCount()) {
					p.setTextSize(textSize / 4);
					p.setTextAlign(Paint.Align.LEFT);
					canvas.drawText(""+weight,
							theme.paddingSize + (x * boxsize) + 8,
							topOfGrid + ((y + 1) * boxsize) - 6,
							p);
				}

				String letter = game.getBoard().elementAt(x, y);
				String letterForDisplay = game.getLanguage().toDisplay(letter);
				p.setTextSize(textSize);
				p.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(letterForDisplay,
						theme.paddingSize + (x * boxsize) + (boxsize / 2),
						topOfGrid + (y * boxsize) + (boxsize / 2) - offset,
						p);
				if (Game.SCORE_LETTERS.equals(game.getScoreType())) {
					String score = String.valueOf(game.getLanguage().getPointsForLetter(letter));
					p.setTextSize(textSize / 4);
					p.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(score,
							theme.paddingSize + ((x + 1) * boxsize) - 8,
							topOfGrid + ((y + 1) * boxsize) - 6,
							p);
				}
			}
		}
	}

	private void drawTimer(Canvas canvas) {
		// Background for timer. Depending on the theme, may be the same colour as the rest
		// of the background.
		p.setColor(theme.timerBackgroundColour);
		canvas.drawRect(0, height - theme.timerHeight - theme.timerBorderWidth - theme.timerBorderWidth, width, height, p);

		if (timeRemaining < 1000) {
			p.setColor(theme.timerEndForegroundColour);
		} else if (timeRemaining < 3000) {
			p.setColor(theme.timerMidForegroundColour);
		} else {
			p.setColor(theme.timerStartForegroundColour);
		}

		int pixelWidth = width * timeRemaining / game.getMaxTimeRemaining();
		canvas.drawRect(0, height - theme.timerHeight - theme.timerBorderWidth, pixelWidth, height, p);
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
	 *  - Measure it and decide how much space it takes.
	 *  - Potentially fade it out if it is too far to the right.
	 *  - Potentially add a strike over the top of it if it is not a word.
	 *  - Colourise it correctly to indicate that it has already been used in the past.
	 *  - Maybe more?
	 *
	 *  After drawing, we can return the right hand size, to indicate how much space we took up
	 *  when rendering. This can be used to decide where to start the following word.
	 */
	private float drawWord(@NonNull Canvas canvas, String word, float x, float y, boolean isWord, boolean hasBeenUsedBefore) {
		word = word.toUpperCase(game.getLanguage().getLocale());

		p.setTextSize(theme.textSizeNormal);
		p.setTypeface(Fonts.get().getSansSerifBold());
		p.getTextBounds(word, 0, word.length(), textBounds);
		float height = textBounds.height();
		float width = textBounds.width();

		p.setColor(hasBeenUsedBefore ? theme.previouslySelectedWordColour : theme.selectedWordColour);

		p.setTextSize(theme.textSizeNormal);
		p.setTypeface(Fonts.get().getSansSerifBold());
		p.setTextAlign(Paint.Align.LEFT); // TODO: RTL support.
		canvas.drawText(word, x, y + height, p);

		if (!isWord) {
			// Strike-through
			p.setStrokeWidth(6);
			canvas.drawLine(x, y + height / 2, x + width, y + height / 2, p);
		}

		if (x + width > getWidth() - theme.scorePadding) {
			// Fade out the word as it approaches the end of the screen.
			Shader shaderA = new LinearGradient(getWidth() - theme.scorePadding * 5, y, getWidth() - theme.scorePadding * 2, y, 0x00ffffff, theme.backgroundColor, Shader.TileMode.CLAMP);
			p.setShader(shaderA);
			canvas.drawRect(getWidth() - theme.scorePadding * 5, y - 2, getWidth(), y + height + 2, p);
			p.setShader(null);
		}

		return x + width;
	}

	private void drawWordList(Canvas canvas, float top, float bottom) {

		int currentWordHeight = fontHeights.getHeight(theme.currentWordSize);
		int pastWordHeight = fontHeights.getHeight(theme.textSizeNormal);
		int wordListPadding = (int)((bottom - top - currentWordHeight - pastWordHeight) / 3);

		float pos = top + wordListPadding + currentWordHeight;

		// If halfway through selecting the current word, then show that.
		// Otherwise, show the last word that was selected.
		String bigWordToShow = currentWord;
		if (bigWordToShow != null) {
			p.setColor(theme.currentWordColour);
		} else {
			ListIterator<String> pastWords = game.listIterator();
			if (pastWords.hasNext()) {
				String lastWord = pastWords.next();
				if (lastWord.startsWith("+")) {
					bigWordToShow = lastWord.substring(1);
					// p.setColor(previouslySelectedWordColour);
				} else if (game.isWord(lastWord)) {
					bigWordToShow = lastWord;
					// p.setColor(selectedWordColour);
				} else {
					bigWordToShow = lastWord;
					// p.setColor(notAWordColour);
				}
			}
		}


		if (bigWordToShow != null) {
			p.setColor(theme.currentWordColour);
			p.setTextSize(theme.currentWordSize);
			p.setTypeface(Fonts.get().getSansSerifCondensed());
			p.setTextAlign(Paint.Align.CENTER);
			canvas.drawText(bigWordToShow.toUpperCase(game.getLanguage().getLocale()), width / 2, pos, p);
		}


		// draw words
		pos += wordListPadding / 2 + theme.textSizeSmall;

		float x = theme.scorePadding;
		ListIterator<String> pastWords = game.listIterator();

		// Don't bother showing past words if there isn't enough vertical space on this screen.
		while (pastWords.hasNext() && pos < bottom) {
			String w = pastWords.next();
			float newX;
			if (w.startsWith("+")) {
				w = w.substring(1);
				newX = drawWord(canvas, w, x, pos, true, true);
			} else {
				if (game.isWord(w)) {
					newX = drawWord(canvas, w, x, pos, true, false);
				} else {
					newX = drawWord(canvas, w, x, pos, false, false);
				}
			}
			x = newX + theme.scorePadding;

			// Don't bother rendering words which push off the screen.
			if (x > getWidth() - theme.scorePadding) {
				break;
			}
		}
	}

	private void drawScore(Canvas canvas) {
		float headingHeight = fontHeights.getHeight(theme.scoreHeadingTextSize);
		float valueHeight = fontHeights.getHeight(theme.scoreTextSize);

		float scoreHeight = theme.scorePadding + headingHeight + theme.scorePadding / 2 + valueHeight + theme.scorePadding;
		float totalTimerHeight = theme.timerBorderWidth * 2 + theme.timerHeight;
		p.setColor(theme.scoreBackgroundColour);
		canvas.drawRect(0,height - totalTimerHeight - scoreHeight, width,height - totalTimerHeight, p);

		float scoreStartY = height - totalTimerHeight - scoreHeight;
		float panelWidth = width / 3;

		drawWordList(canvas, boardWidth * boxsize, scoreStartY);

		int secRemaining = timeRemaining / 100;
		int mins = secRemaining / 60;
		int secs = secRemaining % 60;
		String displayTime = mins + ":" + (secs < 10 ? "0" : "") + secs;
		drawScorePanel(canvas, 0, panelWidth, scoreStartY, getContext().getString(R.string.time), displayTime);

		String displayWordCount = game.getWordCount() + "/" + game.getMaxWordCount();
		drawScorePanel(canvas, 1, panelWidth, scoreStartY, getContext().getString(R.string.words), displayWordCount);

		String displayScore = Integer.toString(game.getScore());
		drawScorePanel(canvas, 2, panelWidth, scoreStartY, getContext().getString(R.string.score), displayScore);
	}

	private void drawScorePanel(Canvas canvas, float panelNum, float panelWidth, float y, String heading, String value) {
		float x = panelNum * panelWidth;

		p.setTextAlign(Paint.Align.CENTER);

		float headingHeight = fontHeights.getHeight(theme.scoreHeadingTextSize);

		p.setColor(theme.scoreHeadingTextColour);
		p.setTextSize(theme.scoreHeadingTextSize);
		p.setTypeface(Fonts.get().getSansSerifCondensed());
		canvas.drawText(heading, x + panelWidth / 2, y + theme.scorePadding + headingHeight, p);

		float valueHeight = fontHeights.getHeight(theme.scoreTextSize);

		p.setColor(theme.scoreTextColour);
		p.setTextSize(theme.scoreTextSize);
		p.setTypeface(Fonts.get().getSansSerifBold());
		canvas.drawText(value, x + panelWidth / 2, y + theme.scorePadding + headingHeight + theme.scorePadding / 2 + valueHeight, p);
	}

	private void clearScreen(Canvas canvas) {
		p.setColor(theme.backgroundColor);
		canvas.drawRect(0, 0, width, height, p);
	}

	@Override
	public void onDraw(Canvas canvas) {
		setDimensions(getMeasuredWidth(), getMeasuredHeight());

		canvas.drawColor(getResources().getColor(R.color.background));

		if (game.getStatus() != Game.GameStatus.GAME_RUNNING) return;

		clearScreen(canvas);
		drawBoard(canvas);
		drawScore(canvas);
		drawTimer(canvas);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mFingerTracker == null) return false;
		int action = event.getAction();
		switch (action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				mFingerTracker.touchScreen((int) event.getX(), (int) event.getY());
				break;
			case MotionEvent.ACTION_UP:
				mFingerTracker.release();
				break;
		}

		redraw();
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
			String letter = Character.toString((char) event.getUnicodeChar()).toLowerCase();
			mKeyboardTracker.processLetter(game.getLanguage().applyMandatorySuffix(letter));
		} else if (keyCode == KeyEvent.KEYCODE_SPACE ||
				keyCode == KeyEvent.KEYCODE_ENTER) {
			mKeyboardTracker.reset();
		}

		return false;
	}

	public void redraw() {
		redrawCount = REDRAW_FREQ;
		invalidate();
	}

	public void tick(int time) {
		boolean doRedraw = false;

		timeRemaining = time;
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
			redraw();
		}

		void touchScreen(int x, int y) {
			if (x < left || x >= (left + width)) return;
			if (y < top || y >= (top + height)) return;

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
			left = theme.paddingSize;
			top = theme.paddingSize;
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
