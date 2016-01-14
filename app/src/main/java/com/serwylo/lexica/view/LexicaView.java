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
import android.graphics.Typeface;
import android.view.MotionEvent;
import android.view.View;
import android.view.KeyEvent;

import java.util.LinkedList;
import java.util.ListIterator;

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
	private int highlighted;

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
			gridsize = width - 2 * paddingSize;
		} else {
			gridsize = height - (2 * paddingSize) - timerHeight;
		}
		boxsize = ((float) gridsize) / boardWidth;

		if(mFingerTracker != null) {
			mFingerTracker.boundBoard(paddingSize + gridsize, paddingSize + timerHeight + gridsize);
		}
	}

	private void drawBoard(Canvas canvas) {
		// Draw white box
		p.setARGB(255, 255, 255, 255);
		int topOfGrid = paddingSize + timerHeight;
		canvas.drawRect(paddingSize, topOfGrid, gridsize + paddingSize, gridsize + topOfGrid, p);

		// Draw touched boxes
		p.setARGB(255,255,255,0);
		for(int i=0;i<game.getBoard().getSize();i++) {
			if(((1<<i)&highlighted) == 0) continue;
			int x = i % game.getBoard().getWidth();
			int y = i / game.getBoard().getWidth();
			float left = paddingSize + boxsize * x;
			float top = topOfGrid + boxsize * y;
			float right = paddingSize + boxsize * (x+1);
			float bottom = topOfGrid + boxsize * (y+1);
			canvas.drawRect(left, top, right, bottom, p);
		}

		// Draw grid
		p.setARGB(255,0,0,0);

		// Vertical lines
		for(float i = paddingSize; i <= paddingSize + gridsize; i += boxsize) {
			canvas.drawLine(i, topOfGrid, i, gridsize + topOfGrid, p);
		}
		// Horizontal lines
		for(float i = topOfGrid; i <= topOfGrid + gridsize; i += boxsize) {
			canvas.drawLine(paddingSize, i, gridsize + paddingSize, i, p);
		}

		p.setARGB(255, 0, 0, 0);
		p.setTextSize(boxsize-textSizeNormal);
		p.setTextAlign(Paint.Align.CENTER);

		p.setTypeface(Typeface.MONOSPACE);
		for(int x=0;x<boardWidth;x++) {
			for(int y=0;y<boardWidth;y++) {
				String txt = game.getBoard().elementAt(x,y);
				canvas.drawText(txt, paddingSize + x * boxsize + boxsize / 2, (y + 1) * boxsize, p);
			}
		}

	}

	private void drawTimer(Canvas canvas) {
		p.setColor(getResources().getColor(R.color.colorPrimaryDark));
		canvas.drawRect(0, 0, width, timerHeight + 2, p);

		if(timeRemaining < 1000) {
			p.setARGB(255,255,0,0);
		} else if (timeRemaining < 3000) {
			p.setARGB(255,255,255,0);
		} else {
			p.setARGB(255,0,255,0);
		}

		int pixelWidth = width * timeRemaining / game.getMaxTimeRemaining();
		canvas.drawRect(0, 1, pixelWidth, timerHeight + 1, p);
	}

	private int drawWordCount(Canvas canvas, int left, int top) {
		p.setTypeface(Typeface.SANS_SERIF);
		p.setARGB(255, 0, 0, 0);

		int topOfCount = top + paddingSize;
		p.setTextSize(textSizeLarge);
		canvas.drawText("" + game.getWordCount() + "/" + game.getMaxWordCount(), left, topOfCount, p);

		int topOfStaticText = topOfCount + textSizeNormal;

		p.setTextSize(textSizeNormal);
		canvas.drawText(getContext().getString(R.string.words), left, topOfStaticText, p);

		return topOfStaticText + textSizeNormal;
	}

	private void drawWordList(Canvas canvas, int left, int top, int bottom) {
		// draw current word
		p.setTextSize(textSizeNormal);
		p.setARGB(255,0,0,0);
		if(currentWord != null) {
			canvas.drawText(currentWord,left,top,p);
		}

		// draw words
		int pos = top+textSizeNormal;
		ListIterator<String> li = game.listIterator();
		p.setTextSize(textSizeSmall);

		while(li.hasNext() && pos < bottom) {
			String w = li.next();
			if(game.isWord(w)) {
				p.setARGB(255,0,0,0);
			} else {
				p.setARGB(255,255,0,0);
			}
			canvas.drawText(w,left,pos,p);
			pos += textSizeSmall;
		}
	}

	private int drawTextTimer(Canvas canvas, int left, int top) {
		if(timeRemaining < 1000) {
			p.setARGB(255,255,0,0);
		} else if (timeRemaining < 3000) {
			p.setARGB(255,255,255,0);
		} else {
			p.setARGB(255,0,0,0);
		}

		int secRemaining = timeRemaining / 100;
		int mins = secRemaining / 60;
		int secs = secRemaining % 60;

		String time = "" + mins + ":";
		if(secs < 10) {
			time += "0"+ (secRemaining % 60);
		} else {
			time += ""+ (secRemaining % 60);
		}

		p.setTextSize(textSizeLarge);
		canvas.drawText(time, left, top, p);

		return top + textSizeLarge;
	}

	private void drawScoreLandscape(Canvas canvas) {
		int textAreaTop = paddingSize + timerHeight;
		int textAreaHeight = height - 2*paddingSize;
		int textAreaLeft = 2*paddingSize + gridsize;
		int textAreaWidth = width - paddingSize - textAreaLeft;

		int paddedLeft = textAreaLeft + textAreaWidth / 2;

		int bottomOfTimer = drawWordCountAndTimer(canvas, paddedLeft, textAreaTop);
		drawWordList(canvas, paddedLeft, bottomOfTimer + paddingSize, textAreaTop + textAreaHeight);
	}

	private void drawScorePortrait(Canvas canvas) {
		int textAreaTop = 2 * paddingSize + gridsize + timerHeight;
		int textAreaHeight = height - paddingSize - textAreaTop;
		int textAreaLeft = paddingSize;
		int textAreaWidth = width - 2* paddingSize;

		p.setTypeface(Typeface.SANS_SERIF);

		int paddedLeft = textAreaLeft + textAreaWidth / 4;

		drawWordCountAndTimer(canvas, paddedLeft, textAreaTop);
		drawWordList(canvas, textAreaLeft + textAreaWidth * 3 / 4, textAreaTop + textSizeNormal,textAreaTop+textAreaHeight);

	}

	private int drawWordCountAndTimer(Canvas canvas, int left, int top) {
		int paddedTop = top + paddingSize * 3;
		int bottomOfWordCount = drawWordCount(canvas, left, paddedTop);

		int topOfTimer = bottomOfWordCount + paddingSize;
		return drawTextTimer(canvas, left, topOfTimer);
	}

	@Override
	public void onDraw(Canvas canvas) {
		setDimensions(getMeasuredWidth(), getMeasuredHeight());

		canvas.drawColor(getResources().getColor(R.color.background));

		if(game.getStatus() != Game.GameStatus.GAME_RUNNING) return;

		drawBoard(canvas);
		drawTimer(canvas);

		if(width > height) {
			drawScoreLandscape(canvas);
		} else {
			drawScorePortrait(canvas);
		}
	}

	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		if(mFingerTracker == null) return false;
		int action = event.getAction();
		switch(action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				mFingerTracker.touchScreen((int)event.getX(),(int)event.getY());
			break;
			case MotionEvent.ACTION_UP:
				mFingerTracker.release();
			break;
		}

		redraw();
		return true;
	}

	@Override
	public boolean onKeyDown (int keyCode, KeyEvent event) {
		if(keyCode >= KeyEvent.KEYCODE_A && keyCode <= KeyEvent.KEYCODE_Z) {
			mKeyboardTracker.processLetter(keyCode-KeyEvent.KEYCODE_A);
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
		if(--redrawCount <= 0) {
			doRedraw = true;
		}
		if(doRedraw) {
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
		private final byte touched[];
		private int touchedBits;

		private byte touching;

		private int left;
		private int top;
		private int width;
		private int height;

		private int box_width;
		private int radius_squared;

		FingerTracker(Game g) {
			game = g;
			touched = new byte[game.getBoard().getSize()];
			touchedBits = 0;

			reset();
		}

		private void reset() {
			for(int i=0;i<touched.length;i++) {
				touched[i] = -1;
			}

			if(numTouched > 0) {
				highlighted = 0;
			}
			touchedBits = 0;
			numTouched = 0;
			touching = -1;
		}
		
		private void countTouch() {
			int touchBit = 1 << touching;
			if((touchedBits & touchBit) > 0) return;

			touched[numTouched] = touching;
			touchedBits |= 1 << touching;
			highlighted = touchedBits;
			numTouched++;
			redraw();
		}

		void touchScreen(int x, int y) {
			if(x < left || x >= (left+width)) return;
			if(y < top || y >= (top+height)) return;

			// Log.d(TAG,"Touching:"+x+","+y);

			int bx = (x-left)*boardWidth/width;
			int by = (y-top)*boardWidth/height;

			touchBox(bx,by);
			
			if(canTouch(bx+boardWidth*by) && nearCenter(x,y,bx,by)) {
				countTouch();
			}
		}

		private boolean canTouch(int box) {
			int boxBits = 1<<box;
			currentWord = getWord();
			if((boxBits & touchedBits) > 0) return false;

			return (boxBits & game.getBoard().transitions(touched[numTouched-1]))>0;
		}

		private void touchBox(int x, int y) {
			int box = x+boardWidth*y;
			mKeyboardTracker.reset();

			if(touching < 0) {
				touching = (byte) box;
				countTouch();
			} else if(touching != box && canTouch(box)) {
				touching = (byte) box;
			}
		}

		private boolean nearCenter(int x, int y, int bx, int by) {
			int cx,cy;

			cx = left + bx * box_width + box_width / 2;
			cy = top + by * box_width + box_width / 2;

			int d_squared = (cx-x)*(cx-x)+(cy-y)*(cy-y);

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

			for(int i=0;i<numTouched;i++) {
				ret += game.getBoard().elementAt(touched[i]).toUpperCase();
			}
	
			return ret;
		}

		void release() {
			if(numTouched > 0) {
				String s = getWord();

				game.addWord(s);
				currentWord = null;
			}

			reset();
		}
	}

	private class KeyboardTracker {
		private int defaultAcceptableKeys;
		private LinkedList<State> defaultStates;

		private int acceptableKeys;
		private LinkedList<State> states;

		private String tracked;

		KeyboardTracker() {
			fullReset();
		}

		private void fullReset() {
			defaultStates = new LinkedList<>();
			defaultAcceptableKeys = 0;
			
			for(int i=0;i<game.getBoard().getSize();i++) {
				defaultStates.add(new State(game.getBoard().valueAt(i),i,(1<<i)));
				defaultAcceptableKeys |= 1<<game.getBoard().valueAt(i);
			}

			reset();
		}

		private void reset() {
			if(tracked != null) {
				game.addWord(tracked);
				highlighted=0;
				currentWord = null;
			}

			acceptableKeys = defaultAcceptableKeys;
			states = defaultStates;
			tracked = null;
		}

		private void processLetter(int letter) {
			mFingerTracker.reset();

			if(((1<<letter)&acceptableKeys)==0) return;

			LinkedList<State> subStates = new LinkedList<>();
			acceptableKeys = 0;
			ListIterator<State> iter = states.listIterator();

			boolean appendedString = false;

			while(iter.hasNext()) {
				State nState = iter.next();
				if(nState.key != letter) continue;
				if(!appendedString) {
					if(tracked == null) tracked = "";
					tracked += game.getBoard().elementAt(nState.pos);
					currentWord = tracked.toUpperCase();

					appendedString = true;
				}
				highlighted = nState.selected;
				acceptableKeys |= nState.getNextStates(subStates);
			}

			states = subStates;
		}

		private class State {
			final int key;
			final int pos;
			final int selected;

			State(int key, int pos, int selected) {
				this.key = key;
				this.pos = pos;
				this.selected = selected;
			}

			int getNextStates(LinkedList<State> possibleStates) {
				int trans = game.getBoard().transitions(pos);
				int possible = trans & ~selected;
				int ret = 0;

				for(int i=0;i<game.getBoard().getSize();i++) {
					int posbit = 1<<i;
					if((posbit&possible)==0) continue;
					possibleStates.add(new State(game.getBoard().valueAt(i),i,selected|posbit));
					ret |= 1<<game.getBoard().valueAt(i);
				}

				return ret;
			}
		}

	}

}
