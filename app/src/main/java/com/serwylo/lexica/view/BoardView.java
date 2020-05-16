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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import android.os.Build;
import android.util.AttributeSet;
import android.view.View;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BoardView extends View {

	@SuppressWarnings("unused")
	protected static final String TAG = "LexicaView";

	private Game game;

	private final ThemeProperties theme;

	private int width;
	private int height;
	private int gridsize;
	private float boxsize;
	private int boardWidth;
	private Paint p;
	private Set<Integer> highlightedPositions = new HashSet<>();
	private int maxWeight;

	public BoardView(Context context) {
		this(context, null);
	}

	public BoardView(Context context, AttributeSet attrs) {
		this( context, attrs, R.attr.lexicaViewStyle );
	}

	public BoardView(Context context, AttributeSet attrs, int defStyle) {
		super( context, attrs, defStyle);

		theme = new ThemeProperties(context, attrs, defStyle);

		p = new Paint();
		p.setTextAlign(Paint.Align.CENTER);
		p.setAntiAlias(true);
		p.setStrokeWidth(2);
	}

	@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
	public BoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);

		theme = new ThemeProperties(context, attrs, defStyleAttr);

		p = new Paint();
		p.setTextAlign(Paint.Align.CENTER);
		p.setAntiAlias(true);
		p.setStrokeWidth(2);
	}

	public void setGame(Game game) {
		this.game = game;

		maxWeight = game.getMaxWeight(); // Don't calculate this on each paint for performance.
		boardWidth = game.getBoard().getWidth();
	}

	public void highlight(Integer[] highlightedPositions) {
		this.highlightedPositions = new HashSet<>();
		this.highlightedPositions.addAll(Arrays.asList(highlightedPositions));
	}

	private void setDimensions(int w, int h) {
		width = w;
		height = h;

		gridsize = Math.min(width, height);
		boxsize = ((float) gridsize) / boardWidth;
	}

	private final Rect textBounds = new Rect();

	private void drawBoard(Canvas canvas) {
		if (game == null) {
			return;
		}

		// Draw boxes
		for (int i = 0; i < game.getBoard().getSize(); i++) {
			int pos = game.getBoard().getRotatedPosition(i);

			int x = i % game.getBoard().getWidth();
			int y = i / game.getBoard().getWidth();

			if (highlightedPositions.contains(i)) {
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

			float left = boxsize * x;
			float top = boxsize * y;
			float right = boxsize * (x + 1);
			float bottom = boxsize * (y + 1);
			canvas.drawRect(left, top, right, bottom, p);
		}

		// Draw grid, but exclude the first and last line (both horizontally and vertically.
		p.setColor(theme.tileBorderColour);
		p.setStrokeWidth(theme.tileBorderWidth);

		// Vertical lines
		for (float i = boxsize; i <= gridsize - boxsize; i += boxsize) {
			canvas.drawLine(i, 0, i, gridsize, p);
		}
		// Horizontal lines
		for (float i = boxsize; i <= gridsize - boxsize; i += boxsize) {
			canvas.drawLine(0, i, gridsize, i, p);
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
					int colour = (weight == 0) ? theme.hintModeUnusableLetterColour : theme.tileForegroundColour;
					p.setColor(colour);
				} else {
					p.setColor(theme.tileForegroundColour);
				}

				if (game.hintModeCount()) {
					p.setTextSize(textSize / 4);
					p.setTextAlign(Paint.Align.LEFT);
					canvas.drawText(""+weight,
							(x * boxsize) + 8,
							((y + 1) * boxsize) - 6,
							p);
				}

				String letter = game.getBoard().elementAt(x, y);
				String letterForDisplay = game.getLanguage().toDisplay(letter);
				p.setTextSize(textSize);
				p.setTextAlign(Paint.Align.CENTER);
				canvas.drawText(letterForDisplay,
						(x * boxsize) + (boxsize / 2),
						(y * boxsize) + (boxsize / 2) - offset,
						p);
				if (Game.SCORE_LETTERS.equals(game.getScoreType())) {
					String score = String.valueOf(game.getLanguage().getPointsForLetter(letter));
					p.setTextSize(textSize / 4);
					p.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(score,
							((x + 1) * boxsize) - 8,
							((y + 1) * boxsize) - 6,
							p);
				}
			}
		}
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

	private void clearScreen(Canvas canvas) {
		p.setColor(theme.backgroundColor);
		canvas.drawRect(0, 0, width / 2, height, p);
	}

	@Override
	public void onDraw(Canvas canvas) {
		setDimensions(getMeasuredWidth(), getMeasuredHeight());

		clearScreen(canvas);
		drawBoard(canvas);
	}

}
