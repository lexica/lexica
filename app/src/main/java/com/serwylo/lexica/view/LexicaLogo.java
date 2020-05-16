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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.serwylo.lexica.R;

import java.util.Random;

public class LexicaLogo extends View {

	private enum BoxColor {BACKGROUND, MAIN}

	private static final String LETTERS[] = {
		"A","B","C","D","E","F","G","H","I","J","K","L","M","N",
		"O","P","Qu","R","S","T","U","V","W","X","Y","Z"
	};

	private Picture cached;
	private final ThemeProperties theme;

	public LexicaLogo(Context context) {
		this(context, (AttributeSet) null);
	}

	public LexicaLogo(Context context, AttributeSet attrs) {
		this( context, attrs, R.attr.lexicaViewStyle );
	}

	public LexicaLogo(Context context, AttributeSet attrs, int defStyle) {
		super(context,attrs);
		setupSoftwareCanvas();
		cached = null;

		theme = new ThemeProperties(context, attrs, defStyle);
	}

	/**
	 * Newer versions of android don't allow Picture's to be drawn to hardware accelerated canvases.
	 * It seems that as of some time in the past, the default is to use hardware acceleration
	 * for canvases. This sets the view to use a software canvas.
	 */
	private void setupSoftwareCanvas() {
		setLayerType(LAYER_TYPE_SOFTWARE, null);
	}

	private void drawTile(Canvas canvas, Paint p, String letter, BoxColor color,
		int x, int y, int size, float offset) {

		switch(color) {
			case BACKGROUND:
				p.setColor(theme.homeScreenTileBackgroundColour);
			break;
			case MAIN:
				p.setColor(theme.tileBackgroundColour);
			break;
		}

		canvas.drawRect(x,y,x + size,y + size,p);
		p.setColor(theme.tileForegroundColour);
		p.setTypeface(Fonts.get().getSansSerifCondensed());
		float textSize = size * 0.8f;
		p.setTextSize(textSize);
		canvas.drawText(letter,x + size / 2,y + (size / 2) - offset, p);

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

		int paddingSize = getResources().getDimensionPixelSize(R.dimen.homeScreenLetterPadding);
		int height = getHeight();
		int width = getWidth();

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		int deviceHeight = displayMetrics.heightPixels;
		int deviceWidth = displayMetrics.widthPixels;

		int size = Math.min(deviceHeight,deviceWidth) / 8;
		p.setTextSize(size*8/10);

		// Find vertical center offset
		Rect textBounds = new Rect();
		p.getTextBounds("A", 0, 1, textBounds);
		float offset = textBounds.exactCenterY();

		Random rng = new Random();

		for(int i=0;i<20;i++) {
			String l = LETTERS[rng.nextInt(LETTERS.length)];
			int x = rng.nextInt(width-size-10)+5;
			int y = rng.nextInt(height-size-10)+5;

			drawTile(canvas,p,l,BoxColor.BACKGROUND,x,y,size,offset);
		}

		int outerPadding = paddingSize * 2;
		int totalInnerPadding = paddingSize * 5;

		size = (Math.min(deviceHeight,deviceWidth) - outerPadding - totalInnerPadding ) / 6;
		p.setTextSize(size*8/10);

		// Find vertical center offset
		p.getTextBounds("A", 0, 1, textBounds);
		offset = textBounds.exactCenterY();

		int totalWidthOfTiles = 6 * size;
		int y = (height - size) / 2;
		int dx = (width - totalWidthOfTiles - outerPadding) / 5 + size;
		int x = paddingSize;
		drawTile(canvas,p,"L",BoxColor.MAIN,x,y,size,offset);
		x += dx;
		drawTile(canvas,p,"E",BoxColor.MAIN,x,y,size,offset);
		x += dx;
		drawTile(canvas,p,"X",BoxColor.MAIN,x,y,size,offset);
		x += dx;
		drawTile(canvas,p,"I",BoxColor.MAIN,x,y,size,offset);
		x += dx;
		drawTile(canvas,p,"C",BoxColor.MAIN,x,y,size,offset);
		x += dx;
		drawTile(canvas,p,"A",BoxColor.MAIN,x,y,size,offset);

	}

}

