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
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Board;
import com.serwylo.lexica.game.Game;


public class BoardView extends View {

	private Board board;

	/** @see #highlight(Integer[]) */
	private Integer[] highlightedCells = new Integer[0];

	private final Paint p;
	public final int paddingSize;
	private String scoreType;

	public BoardView(Context context, AttributeSet attrs) {
		super(context,attrs);

		board = null;
		highlightedCells = new Integer[0];

		p = new Paint();
		p.setTextAlign(Paint.Align.CENTER);
		p.setAntiAlias(true);
		p.setStrokeWidth(2);

		paddingSize = getResources().getDimensionPixelSize(R.dimen.padding);
	}

	public void setScoreType(String scoreType) {
		this.scoreType = scoreType;
	}

	private boolean isCellHighlighted(int x, int y) {
		int cellNumber = y * board.getWidth() + x;
		for (int highlightedCell : highlightedCells) {
			if (highlightedCell == cellNumber) {
				return true;
			}
		}

		return false;
	}

	private final Rect textBounds = new Rect();

	@Override
	public void onDraw(Canvas canvas) {
		int width = getWidth() - paddingSize - 2;
		int height = getHeight() - paddingSize - 2;


		// Draw white box
		p.setARGB(255, 255, 255, 255);
		canvas.drawRect(paddingSize / 2, paddingSize, paddingSize / 2 + width, paddingSize + height, p);

		if (board == null) {
			return;
		}

		float boxsize = ((float) height) / board.getWidth();

		// Draw touched boxes
		p.setARGB(255, 255, 255, 0);
		for (int x = 0; x < board.getWidth(); x++) {
			for (int y = 0; y < board.getWidth(); y++) {
				if (!isCellHighlighted(x, y)) {
					continue;
				}

				float left = (paddingSize / 2) + (boxsize * x);
				float top = paddingSize + (boxsize * y);
				float right = (paddingSize / 2) + (boxsize * (x + 1));
				float bottom = paddingSize + (boxsize * (y + 1));

				canvas.drawRect(left, top, right, bottom, p);
			}
		}

		// Draw grid
		p.setARGB(255, 0, 0, 0);

		// Vertical lines
		for (float i = paddingSize / 2; i <= paddingSize / 2 + width; i += boxsize) {
			canvas.drawLine(i, paddingSize, i, paddingSize + height, p);
		}
		// Horizontal lines
		for (float i = paddingSize; i <= paddingSize + height; i += boxsize) {
			canvas.drawLine(paddingSize / 2, i, paddingSize / 2 + width, i, p);
		}

		p.setARGB(255, 0, 0, 0);
		p.setTypeface(Typeface.MONOSPACE);
		float textSize = boxsize * 0.8f;
		p.setTextSize(textSize);

		// Find vertical center offset
		p.getTextBounds("A", 0, 1, textBounds);
		float offset = textBounds.exactCenterY();

		// Draw letters
		for (int x = 0; x < board.getWidth(); x++) {
			for (int y = 0; y < board.getWidth(); y++) {
				String txt = board.elementAt(x, y).toUpperCase();
				p.setTextSize(textSize);
				p.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(txt,
						(paddingSize / 2) + (x * boxsize) + (boxsize / 2),
						paddingSize + (y * boxsize) + (boxsize / 2) - offset,
						p);
				if (Game.SCORE_LETTERS.equals(scoreType)) {
					String score = String.valueOf(Game.letterPoints(txt));
					p.setTextSize(textSize / 4);
					p.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(score,
							paddingSize / 2 + ((x + 1) * boxsize) - 4,
							paddingSize + ((y + 1) * boxsize) - 6,
							p);
				}
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

	/**
	 * Bitmask of highlighted cells on the board.
	 * The first bit to the right (i.e. represented by the integer "1") is the flag to say whether
	 * the first cell (i.e. x = 0, y = 0) is highlighted or not.
	 */
	public void highlight(Integer[] highlightedCells) {
		this.highlightedCells = highlightedCells;
	}

}
