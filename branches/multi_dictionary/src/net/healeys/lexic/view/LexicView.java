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

package net.healeys.lexic.view;

import net.healeys.lexic.game.Game;
import net.healeys.lexic.game.Board;
import net.healeys.lexic.Synchronizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ListIterator;

public class LexicView extends View implements Synchronizer.Event {
	protected static final String TAG = "LexicView";
	public static final int PADDING = 10;
	public static final int REDRAW_FREQ = 10;

	private FingerTracker tracker;
	private Game game;
	private int timeRemaining;
	private int redrawCount;

	private int width;
	private int height;
	private int gridsize;
	private int boxsize;

	private int boardWidth;

	private Paint p;

	public LexicView(Context context, Game g) {
		super(context);

		game = g;
		boardWidth = game.getBoard().getWidth();

		tracker = new FingerTracker(this,game);
		timeRemaining = 0;
		redrawCount = 1;

		p = new Paint();
		p.setTextAlign(Paint.Align.CENTER);
		p.setAntiAlias(true);

		setFocusable(true);
	}

	private void setDimensions(int w, int h) {
		// Log.d(TAG,"setDimensions:"+w+","+h);

		width = w;
		height = h;

		gridsize = Math.min(width,height) - 2*PADDING;
		boxsize = gridsize / boardWidth;

		// Log.d(TAG,"gridsize:"+gridsize);

		if(tracker != null) {
			tracker.boundBoard(PADDING,PADDING,
				PADDING+gridsize,PADDING+gridsize);
		}
	}

	private void drawBoard(Canvas canvas) {
		// Draw white box
		p.setARGB(255,255,255,255);
		canvas.drawRect(PADDING,PADDING,gridsize+PADDING,gridsize+PADDING,p);

		// Draw touched boxes
		p.setARGB(255,255,255,0);
		for(int i=0;i<tracker.numTouched;i++) {
			int x = tracker.touched[i] % boardWidth;
			int y = tracker.touched[i] / boardWidth;
			int left = PADDING + boxsize * x;
			int top = PADDING + boxsize * y;
			int right = PADDING + boxsize * (x+1);
			int bottom = PADDING + boxsize * (y+1);
			canvas.drawRect(left,top,right,bottom,p);
		}

		// Draw grid
		p.setARGB(255,0,0,0);
		for(int i=PADDING;i<=PADDING+gridsize;i+=boxsize) {
			canvas.drawLine(i,PADDING,i,gridsize+PADDING,p);
			canvas.drawLine(PADDING,i,gridsize+PADDING,i,p);
		}

		p.setARGB(255,0,0,0);
		p.setTextSize(boxsize-20);
		p.setTextAlign(Paint.Align.CENTER);

		p.setTypeface(Typeface.MONOSPACE);
		for(int x=0;x<boardWidth;x++) {
			for(int y=0;y<boardWidth;y++) {
				String txt = game.getBoard().elementAt(x,y);
				canvas.drawText(txt,PADDING+x*boxsize+boxsize/2,
					PADDING-10+(y+1)*boxsize,p);
			}
		}

	}

	private void drawTimer(Canvas canvas) {
		if(timeRemaining < 1000) {
			p.setARGB(255,255,0,0);
		} else if (timeRemaining < 3000) {
			p.setARGB(255,255,255,0);
		} else {
			p.setARGB(255,0,255,0);
		}
		for(int i=0;i<5;i++) {
			canvas.drawLine(0,i,width*timeRemaining/game.getMaxTimeRemaining(),
				i,p);
		}
	}

	private void drawWordCount(Canvas canvas, int left, int top) {
		p.setTypeface(Typeface.SANS_SERIF);
		p.setARGB(255,0,0,0);
		
		p.setTextSize(30);
		canvas.drawText(""+game.getWordCount()+"/"+game.getMaxWordCount(),
			left,top,p);

		p.setTextSize(20);
		canvas.drawText("WORDS",left,top+20,p);
	}

	private void drawWordList(Canvas canvas, int left, int top, int bottom) {
		int pos = top;

		ListIterator<String> li = game.listIterator();
		p.setTextSize(20);

		while(li.hasNext() && pos < bottom) {
			String w = li.next();
			if(game.isWord(w)) {
				p.setARGB(255,0,0,0);
			} else {
				p.setARGB(255,255,0,0);
			}
			canvas.drawText(w,left,pos,p);
			pos += 20;
		}
	}

	private void drawTextTimer(Canvas canvas, int left, int top) {
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

		p.setTextSize(30);
		canvas.drawText(time,left,top,p);
	}

	private void drawScoreLandscape(Canvas canvas) {
		int textareatop = PADDING;
		int textareaheight = height - 2*PADDING;
		int textarealeft = 2*PADDING + gridsize;
		int textareawidth = width - PADDING - textarealeft;
	
		drawWordCount(canvas,textarealeft + textareawidth/2,
			textareatop+30);
		
		drawWordList(canvas, textarealeft + textareawidth/2, textareatop+110,
			textareatop+textareaheight);

		drawTextTimer(canvas,textarealeft + textareawidth/2,textareatop+80);
	}

	private void drawScorePortrait(Canvas canvas) {
		int textareatop = 2*PADDING + gridsize;
		int textareaheight = height - PADDING - textareatop;
		int textarealeft = PADDING;
		int textareawidth = width - 2*PADDING;

		p.setTypeface(Typeface.SANS_SERIF);

		drawWordCount(canvas,textarealeft + textareawidth/4,
			textareatop+30);
		
		drawWordList(canvas, textarealeft + textareawidth*3/4, textareatop+20,
			textareatop+textareaheight);

		drawTextTimer(canvas,textarealeft + textareawidth/4,textareatop+80);

	}

	@Override
	public void onDraw(Canvas canvas) {
		// Log.d(TAG,"onDraw starts, canvas="+canvas);
		setDimensions(getMeasuredWidth(),getMeasuredHeight());
		// Log.d(TAG, "onDraw:"+width+","+height);

		canvas.drawRGB(0x99,0xcc,0xff);

		if(game.getStatus() != Game.GameStatus.GAME_RUNNING) return;

		drawBoard(canvas);
		drawTimer(canvas);

		if(width > height) {
			drawScoreLandscape(canvas);
		} else {
			drawScorePortrait(canvas);
		}
		// Log.d(TAG,"onDraw stops");
	}

	@Override 
	public boolean onTouchEvent(MotionEvent event) {
		// Log.d(TAG,"onTouchEvent() starts");
		if(tracker == null) return false;
		int action = event.getAction();
		switch(action) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_MOVE:
				tracker.touchScreen((int)event.getX(),(int)event.getY());
			break;
			case MotionEvent.ACTION_UP:
				tracker.release();
			break;
		}

		redraw();
		// Log.d(TAG,"onTouchEvent() ends");
		return true;
	}

	public void redraw() {
		// Log.d(TAG,"redraw()");
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

	private class FingerTracker {
		private LexicView view;
		private Game game;
		
		private int numTouched;
		private byte touched[];
		private int touchedBits;

		private byte touching;

		private int left;
		private int top;
		private int width;
		private int height;

		private int box_width;
		private int radius_squared;

		FingerTracker(LexicView v, Game g) {
			view = v;
			game = g;
			touched = new byte[g.getBoard().getSize()];
			touchedBits = 0;

			reset();
		}

		private void reset() {
			// Log.d(TAG,"RESET");
			for(int i=0;i<touched.length;i++) {
				touched[i] = -1;
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
			if((boxBits & touchedBits) > 0) return false;

			return (boxBits & game.getBoard().
				transitions(touched[numTouched-1]))>0;
		}

		private void touchBox(int x, int y) {
			int box = x+boardWidth*y;
			
			// Log.d(TAG,"Box:"+x+","+y+"="+box);
			
			if(touching < 0) {
				touching = (byte) box;
				countTouch();
			} else if(touching != box && canTouch(box)) {
				touching = (byte) box;
			}

			// Log.d(TAG,"Touching:"+touching);

		}

		private boolean nearCenter(int x, int y, int bx, int by) {
			int cx,cy;

			cx = left + bx * box_width + box_width / 2;
			cy = top + by * box_width + box_width / 2;

			int d_squared = (cx-x)*(cx-x)+(cy-y)*(cy-y);

			return d_squared < radius_squared;
		}

		void boundBoard(int l, int t, int w, int h) {
			left = l;
			top = t;
			width = w;
			height = h;

			box_width = width / boardWidth;

			radius_squared = box_width / 3;
			radius_squared *= radius_squared;
		}

		void release() {
			String s = "";

			for(int i=0;i<numTouched;i++) {
				s += game.getBoard().elementAt(touched[i]);
			}

			game.addWord(s);

			// Log.d(TAG,s);

			reset();
		}
	}

}
