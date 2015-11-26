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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

class LexicLogo extends View {

	private enum BoxColor { WHITE, YELLOW};

	private static final String LETTERS[] = {
		"A","B","C","D","E","F","G","H","I","J","K","L","M","N",
		"O","P","Qu","R","S","T","U","V","W","X","Y","Z"
	};

	private Picture cached;

	public LexicLogo(Context context,AttributeSet attrs) {
		super(context,attrs);

		cached = null;
	}

	private void drawTile(Canvas canvas, Paint p, String letter, BoxColor color,
		int x, int y, int size) {
		
		switch(color) {
			case WHITE:
				p.setARGB(255,255,255,255);
			break;
			case YELLOW:
				p.setARGB(255,255,255,0);
			break;
		}

		// draw background
		canvas.drawRect(x,y,x+size,y+size,p);

		// draw border
		p.setARGB(255,0,0,0);
		canvas.drawLine(x,y,x+size,y,p);
		canvas.drawLine(x,y,x,y+size,p);
		canvas.drawLine(x+size,y,x+size,y+size,p);
		canvas.drawLine(x,y+size,x+size,y+size,p);
		
		canvas.drawText(letter,x+size/2,y+(size*8/10),p);

	}

	@Override
	public void onDraw(Canvas canvas) {
		if(cached == null) {
			int height = getHeight();
			int width = getWidth();
			cached = new Picture();
			drawOnCanvas(cached.beginRecording(width,height));
			cached.endRecording();
		}

		cached.draw(canvas);
	}

	private void drawOnCanvas(Canvas canvas) {
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setTypeface(Typeface.MONOSPACE);
		p.setTextAlign(Paint.Align.CENTER);

		int height = getHeight();
		int width = getWidth();

		int size = Math.min(height,width) / 7;
		p.setTextSize(size*8/10);

		Random rng = new Random();

		for(int i=0;i<20;i++) {
			String l = LETTERS[rng.nextInt(LETTERS.length)];
			int x = rng.nextInt(width-size-10)+5;
			int y = rng.nextInt(height-size-10)+5;

			drawTile(canvas,p,l,BoxColor.WHITE,x,y,size);
		}

		size = Math.min(height,width) / 6;
		p.setTextSize(size*8/10);

		int y = (height - size) / 2;
		int dx = (width - 10 - 5 * size) / 4 + size;
		int x = 5;
		drawTile(canvas,p,"L",BoxColor.YELLOW,x,y,size);
		x += dx;
		drawTile(canvas,p,"E",BoxColor.YELLOW,x,y,size);
		x += dx;
		drawTile(canvas,p,"X",BoxColor.YELLOW,x,y,size);
		x += dx;
		drawTile(canvas,p,"I",BoxColor.YELLOW,x,y,size);
		x += dx;
		drawTile(canvas,p,"C",BoxColor.YELLOW,x,y,size);

	}

}

