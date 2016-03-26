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
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.serwylo.lexica.game.Board;


public class BoardView extends View {

	private final String TAG = "BoardView";

	private Board board;
	private int highlighted = 0;
	private final Paint p;

	public BoardView(Context context,AttributeSet attrs) {
		super(context,attrs);

		board = null;
		highlighted = 0;

		p = new Paint();
		p.setTextAlign(Paint.Align.CENTER);
		p.setAntiAlias(true);
		p.setStrokeWidth(2);

	}

	@Override
	public void onDraw(Canvas canvas) {
		int width = getWidth();
		int height = getHeight();

		p.setARGB(255,255,255,255);
		canvas.drawRect(0,0,width,height,p);

		if(board == null) return;

		float boxSize = ((float) width) / board.getWidth();

		// Draw touched boxes
		p.setARGB(255,255,255,0);
		for(int i=0;i<board.getSize();i++) {
			if(((1<<i)&highlighted) == 0) continue;
			int x = i % board.getWidth();
			int y = i / board.getWidth();
			float left = boxSize * x;
			float top = boxSize * y;
			float right = boxSize * (x+1);
			float bottom = boxSize * (y+1);
			canvas.drawRect(left,top,right,bottom,p);
		}

		// Draw grid
		p.setARGB(255,0,0,0);
		for(float i=0;i<=width;i+=boxSize) {
			canvas.drawLine(i,0,i,width,p);
			canvas.drawLine(0,i,width,i,p);
		}

		p.setARGB(255,0,0,0);
		p.setTextSize(boxSize-20);
		p.setTextAlign(Paint.Align.CENTER);

		p.setTypeface(Typeface.MONOSPACE);
		for(int x=0;x<board.getWidth();x++) {
			for(int y=0;y<board.getWidth();y++) {
				String txt = board.elementAt(x,y);
				canvas.drawText(txt,x*boxSize+boxSize/2,
					-10+(y+1)*boxSize,p);
			}
		}

	}

	@Override
	protected void onMeasure (int wSpec, int hSpec) {
		int side = Math.min(MeasureSpec.getSize(wSpec), MeasureSpec.getSize(hSpec));
		setMeasuredDimension(side,side);
	}

	public void setBoard(Board b) {
		board = b;
	}

	public void highlight(int h) {
		highlighted = h;
	}
}