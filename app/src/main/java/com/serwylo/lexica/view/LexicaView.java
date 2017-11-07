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

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.Synchronizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

public class LexicaView extends View implements Synchronizer.Event, Game.RotateHandler {

	@SuppressWarnings("unused")
	protected static final String TAG = "LexicaView";

	public final int paddingSize;
	public static final int REDRAW_FREQ = 10;

	private final FingerTracker mFingerTracker;
	private final KeyboardTracker mKeyboardTracker;
	private final Game game;
	private int timeRemaining;
	private int redrawCount;

	private int width;
	private int height;
	private int gridsize;
	private float boxsize;

	private final int textSizeSmall;
	private final int textSizeNormal;
	private final int textSizeLarge;
	private final int timerHeight;

	private final int boardWidth;
	private String currentWord;

	private final Paint p;
	private Set<Integer> highlighted = new HashSet<>();

	public LexicaView(Context context, Game g) {
		super(context);

		game = g;
		boardWidth = game.getBoard().getWidth();

		mFingerTracker = new FingerTracker(game);
		mKeyboardTracker = new KeyboardTracker();
		timeRemaining = 0;
		redrawCount = 1;

		paddingSize = getResources().getDimensionPixelSize(R.dimen.padding);
		textSizeSmall = getResources().getDimensionPixelSize(R.dimen.textSizeSmall);
		textSizeNormal = getResources().getDimensionPixelSize(R.dimen.textSizeNormal);
		textSizeLarge = getResources().getDimensionPixelSize(R.dimen.textSizeLarge);
		timerHeight = getResources().getDimensionPixelSize(R.dimen.timerHeight);

		p = new Paint();
		p.setTextAlign(Paint.Align.CENTER);
		p.setAntiAlias(true);
		p.setStrokeWidth(2);

		setFocusable(true);

		g.setRotateHandler(this);
	}

	private void setDimensions(int w, int h) {
		width = w;
		height = h;

		if (width < height) {
			gridsize = width - (2 * paddingSize);
		} else {
			gridsize = height - (2 * paddingSize) - timerHeight;
		}
		boxsize = ((float) gridsize) / boardWidth;

		if (mFingerTracker != null) {
			mFingerTracker.boundBoard(paddingSize + gridsize, paddingSize + timerHeight + gridsize);
		}
	}

	private final Rect textBounds = new Rect();

	private void drawBoard(Canvas canvas) {
		// Draw white box
		p.setARGB(255, 255, 255, 255);
		int topOfGrid = paddingSize + timerHeight;
		canvas.drawRect(paddingSize, topOfGrid, gridsize + paddingSize, gridsize + topOfGrid, p);

		// Draw touched boxes
		p.setARGB(255, 255, 255, 0);
		for (int i = 0; i < game.getBoard().getSize(); i++) {
			if (!highlighted.contains(i)) {
				continue;
			}

			int x = i % game.getBoard().getWidth();
			int y = i / game.getBoard().getWidth();
			float left = paddingSize + (boxsize * x);
			float top = topOfGrid + (boxsize * y);
			float right = paddingSize + (boxsize * (x + 1));
			float bottom = topOfGrid + (boxsize * (y + 1));
			canvas.drawRect(left, top, right, bottom, p);
		}

		// Draw grid
		p.setARGB(255, 0, 0, 0);

		// Vertical lines
		for (float i = paddingSize; i <= paddingSize + gridsize; i += boxsize) {
			canvas.drawLine(i, topOfGrid, i, gridsize + topOfGrid, p);
		}
		// Horizontal lines
		for (float i = topOfGrid; i <= topOfGrid + gridsize; i += boxsize) {
			canvas.drawLine(paddingSize, i, gridsize + paddingSize, i, p);
		}

		p.setARGB(255, 0, 0, 0);
		p.setTypeface(Typeface.MONOSPACE);
		float textSize = boxsize * 0.8f;
		p.setTextSize(textSize);

		// Find vertical center offset
		p.getTextBounds("A", 0, 1, textBounds);
		float offset = textBounds.exactCenterY();


		for (int x = 0; x < boardWidth; x++) {
			for (int y = 0; y < boardWidth; y++) {
				String txt = game.getBoard().elementAt(x, y).toUpperCase();
				p.setTextSize(textSize);
				p.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(txt,
						paddingSize + (x * boxsize) + (boxsize / 2),
						topOfGrid + (y * boxsize) + (boxsize / 2) - offset,
						p);
				if (Game.SCORE_LETTERS.equals(game.getScoreType())) {
					String score = String.valueOf(Game.letterPoints(txt));
					p.setTextSize(textSize / 4);
					p.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(score,
							paddingSize + ((x + 1) * boxsize) - 4,
							topOfGrid + ((y + 1) * boxsize) - 6,
							p);
				}
			}
		}
	}

	private void drawTimer(Canvas canvas) {
		p.setColor(getResources().getColor(R.color.colorPrimaryDark));
		canvas.drawRect(0, 0, width, timerHeight + 2, p);

		if (timeRemaining < 1000) {
			p.setARGB(255, 255, 0, 0);
		} else if (timeRemaining < 3000) {
			p.setARGB(255, 255, 255, 0);
		} else {
			p.setARGB(255, 0, 255, 0);
		}

		int pixelWidth = width * timeRemaining / game.getMaxTimeRemaining();
		canvas.drawRect(0, 1, pixelWidth, timerHeight + 1, p);
	}

	private int drawWordCount(Canvas canvas, int left, int top, int bottom) {
		p.setTypeface(Typeface.SANS_SERIF);
		p.setARGB(255, 0, 0, 0);
		float actualBottom = top;

		if (!game.showBreakdown()) {
			float textSize = (bottom - top - paddingSize) / 4f;
			if (textSize > textSizeNormal) {
				textSize = textSizeNormal;
			}
			p.setTextSize(textSize);

			actualBottom += textSize;
			canvas.drawText(getContext().getString(R.string.score), left, actualBottom, p);

			actualBottom += textSize;
			canvas.drawText(Integer.toString(game.getScore()), left, actualBottom, p);

			actualBottom += paddingSize + textSize;
			canvas.drawText(game.getWordCount() + "/" + game.getMaxWordCount(), left, actualBottom, p);

			actualBottom += textSize;
			canvas.drawText(getContext().getString(R.string.words), left, actualBottom, p);
		} else {
			SparseIntArray maxWordCounts = game.getMaxWordCountsByLength();
			int lines = 0;
			int lenPad = 1;
			int countPad = 2;

			for (int i = 0; i < maxWordCounts.size(); i++) {
				int count = maxWordCounts.valueAt(i);
				if (count > 0) {
					lines++;
					if (count > 99) {
						countPad = 3;
					}
					int length = maxWordCounts.keyAt(i);
					if (length > 9) {
						lenPad = 2;
					}
				}
			}
			float textSize = (bottom - top) / lines;
			if (textSize > textSizeNormal) {
				textSize = textSizeNormal;
			}
			if (textSize < getResources().getDimension(R.dimen.textSizeMinimum)) {
				textSize = getResources().getDimension(R.dimen.textSizeMinimum);
			}
			p.setTextSize(textSize);
			p.setTypeface(Typeface.MONOSPACE);

			SparseIntArray wordCounts = game.getWordCountsByLength();
			for (int i = 0; i < maxWordCounts.size(); i++) {
				int count = maxWordCounts.valueAt(i);
				if (count > 0) {
					int length = maxWordCounts.keyAt(i);
					actualBottom += textSize;
					String scoreLine = pad(length, lenPad) + ":"
							+ pad(wordCounts.get(length), countPad) + "/"
							+ pad(count, countPad);
					canvas.drawText(scoreLine, left, actualBottom, p);
				}
			}

		}

		return (int)actualBottom;
	}

	private void drawWordList(Canvas canvas, int left, int top, int bottom) {
		int pos = top + textSizeNormal;
		// draw current word
		p.setTextSize(textSizeNormal);
		p.setARGB(255, 0, 0, 0);
		p.setTypeface(Typeface.SANS_SERIF);
		if (currentWord != null) {
			canvas.drawText(currentWord.toUpperCase(), left, pos, p);
		}

		// draw words
		ListIterator<String> li = game.listIterator();
		p.setTextSize(textSizeSmall);

		pos += textSizeSmall;
		while (li.hasNext() && pos < bottom) {
			String w = li.next();
			if (game.isWord(w)) {
				w += "  " + game.getWordScore(w);
				p.setARGB(255, 0, 0, 0);
			} else {
				p.setARGB(255, 255, 0, 0);
			}
			canvas.drawText(w.toUpperCase(), left, pos, p);
			pos += textSizeSmall;
		}
	}

	private int drawTextTimer(Canvas canvas, int left, int top) {
		if (timeRemaining < 1000) {
			p.setARGB(255, 255, 0, 0);
		} else if (timeRemaining < 3000) {
			p.setARGB(255, 255, 255, 0);
		} else {
			p.setARGB(255, 0, 0, 0);
		}
		p.setTypeface(Typeface.SANS_SERIF);

		int secRemaining = timeRemaining / 100;
		int mins = secRemaining / 60;
		int secs = secRemaining % 60;

		String time = mins + ":" + (secs < 10 ? "0" : "") + secs;

		if (game.showBreakdown()) {
			time += "    " + game.getScore();
		}

		p.setTextAlign(Paint.Align.CENTER);
		p.setTextSize(textSizeLarge);
		int bottom = top + textSizeLarge;
		canvas.drawText(time, left, bottom, p);

		return bottom;
	}

	private void drawScoreLandscape(Canvas canvas) {
		int textAreaTop = paddingSize + timerHeight;
		int textAreaHeight = height - 2 * paddingSize;
		int textAreaBottom = textAreaTop + textAreaHeight;
		int textAreaLeft = 2 * paddingSize + gridsize;
		int textAreaWidth = width - paddingSize - textAreaLeft;

		int paddedLeft = textAreaLeft + (textAreaWidth / 2);

		int bottomOfTimer = drawWordCountAndTimer(canvas, paddedLeft, textAreaTop, (textAreaBottom / 2) - paddingSize);
		drawWordList(canvas, paddedLeft, bottomOfTimer + paddingSize, textAreaBottom);
	}

	private void drawScorePortrait(Canvas canvas) {
		int textAreaTop = 2 * paddingSize + gridsize + timerHeight;
		int textAreaHeight = height - paddingSize - textAreaTop;
		int textAreaBottom = textAreaTop + textAreaHeight;
		int textAreaLeft = paddingSize;
		int textAreaWidth = width - 2 * paddingSize;

		int paddedLeft = textAreaLeft + (textAreaWidth / 4);

		drawWordCountAndTimer(canvas, paddedLeft, textAreaTop, textAreaBottom);
		drawWordList(canvas, textAreaLeft + textAreaWidth * 3 / 4, textAreaTop, textAreaBottom);
	}

	private int drawWordCountAndTimer(Canvas canvas, int left, int top, int bottom) {
		int bottomOfTimer = drawTextTimer(canvas, left, top);
		return drawWordCount(canvas, left, bottomOfTimer + paddingSize, bottom);
	}

	private String pad(int i, int width) {
		String s = Integer.toString(i);
		StringBuilder sb = new StringBuilder();
		while (sb.length() < width - s.length()) {
			sb.append(" ");
		}
		sb.append(s);
		return sb.toString();
	}

	@Override
	public void onDraw(Canvas canvas) {
		setDimensions(getMeasuredWidth(), getMeasuredHeight());

		canvas.drawColor(getResources().getColor(R.color.background));

		if (game.getStatus() != Game.GameStatus.GAME_RUNNING) return;

		drawBoard(canvas);
		drawTimer(canvas);

		if (width > height) {
			drawScoreLandscape(canvas);
		} else {
			drawScorePortrait(canvas);
		}
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
			mKeyboardTracker.processLetter(letter.equals("q") ? "qu" : letter);
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
			for (int i = 0; i < touched.length; i++) {
				touched[i] = -1;
			}

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
			left = paddingSize;
			top = paddingSize;
			width = w;
			height = h;

			box_width = width / boardWidth;

			radius_squared = box_width / 3;
			radius_squared *= radius_squared;
		}

		String getWord() {
			String ret = "";

			for (int i = 0; i < numTouched; i++) {
				ret += game.getBoard().elementAt(touched[i]);
			}

			return ret;
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
					currentWord = tracked.toUpperCase();

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
