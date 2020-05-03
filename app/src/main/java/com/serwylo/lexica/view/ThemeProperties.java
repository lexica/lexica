package com.serwylo.lexica.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.serwylo.lexica.R;

public class ThemeProperties {

	public final int paddingSize;
	public final int textSizeSmall;
	public final int textSizeNormal;
	public final int textSizeLarge;
	public final int timerHeight;
	public final int timerBorderWidth;
	public final int tileBackgroundColour;
	public final int tileForegroundColour;
	public final int tileBorderColour;
	public final int tileBorderWidth;
	public final int backgroundColor;
	public final int currentWordColour;
	public final int currentWordSize;
	public final int previouslySelectedWordColour;
	public final int selectedWordColour;
	public final int notAWordColour;
	public final int scoreHeadingTextColour;
	public final int scoreTextColour;
	public final int scoreHeadingTextSize;
	public final int scoreTextSize;
	public final int scorePadding;
	public final int scoreBackgroundColour;
	public final int timerBackgroundColour;
	public final int timerStartForegroundColour;
	public final int timerMidForegroundColour;
	public final int timerEndForegroundColour;

	public ThemeProperties(Context context, AttributeSet attrs, int defStyle) {
		final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LexicaView, defStyle, R.style.Widget_LexicaView);

		paddingSize = array.getDimensionPixelSize(R.styleable.LexicaView_padding, 0 /* dp */);
		textSizeSmall = array.getDimensionPixelSize(R.styleable.LexicaView_textSizeSmall, 16 /* sp */);
		textSizeNormal = array.getDimensionPixelSize(R.styleable.LexicaView_textSizeNormal, 20 /* sp */);
		textSizeLarge = array.getDimensionPixelSize(R.styleable.LexicaView_textSizeLarge, 30 /* sp */);
		timerHeight = array.getDimensionPixelSize(R.styleable.LexicaView_timerHeight, 15 /* dp */);
		timerBorderWidth = array.getDimensionPixelSize(R.styleable.LexicaView_timerBorderWidth, 0 /* dp */);
		tileBackgroundColour = array.getColor(R.styleable.LexicaView_tileBackgroundColour, 0xf9f8d7);
		tileForegroundColour = array.getColor(R.styleable.LexicaView_tileForegroundColour, 0x3d3c3b);
		tileBorderColour = array.getColor(R.styleable.LexicaView_tileBorderColour, 0x3d3c3b);
		tileBorderWidth = array.getDimensionPixelSize(R.styleable.LexicaView_tileBorderWidth, 1 /* dp */);
		backgroundColor = array.getColor(R.styleable.LexicaView_colorPrimary, 0xedb641);
		currentWordColour = array.getColor(R.styleable.LexicaView_currentWordColour, 0xffffff);
		currentWordSize = array.getDimensionPixelSize(R.styleable.LexicaView_currentWordSize, 24 /* sp */);
		previouslySelectedWordColour = array.getColor(R.styleable.LexicaView_previouslySelectedWordColour, 0x88ffffff);
		selectedWordColour = array.getColor(R.styleable.LexicaView_selectedWordColour, 0xffffff);
		notAWordColour = array.getColor(R.styleable.LexicaView_notAWordColour, 0xffffff);
		scoreHeadingTextColour = array.getColor(R.styleable.LexicaView_scoreHeadingTextColour, 0xffffff);
		scoreTextColour = array.getColor(R.styleable.LexicaView_scoreTextColour, 0xffffff);
		scoreHeadingTextSize = array.getDimensionPixelSize(R.styleable.LexicaView_scoreHeadingTextSize, 22 /* sp */);
		scoreTextSize = array.getDimensionPixelSize(R.styleable.LexicaView_scoreTextSize, 22 /* sp */);
		scorePadding = array.getDimensionPixelSize(R.styleable.LexicaView_scorePadding, 12 /* dp */);
		scoreBackgroundColour = array.getColor(R.styleable.LexicaView_scoreBackgroundColour, 0xf0cb69);
		timerBackgroundColour = array.getColor(R.styleable.LexicaView_timerBackgroundColour, 0xf0cb69);
		timerStartForegroundColour = array.getColor(R.styleable.LexicaView_timerStartForegroundColour, 0xedb641);
		timerMidForegroundColour = array.getColor(R.styleable.LexicaView_timerMidForegroundColour, 0xedb641);
		timerEndForegroundColour = array.getColor(R.styleable.LexicaView_timerEndForegroundColour, 0xedb641);

		array.recycle();
	}

}
