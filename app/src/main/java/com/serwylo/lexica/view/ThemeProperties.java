package com.serwylo.lexica.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.Px;
import androidx.core.graphics.ColorUtils;

import com.serwylo.lexica.R;

@SuppressWarnings("WeakerAccess")
public class ThemeProperties {

	public final HomeProperties home;
	public final GameProperties game;
	public final BoardProperties board;

	public static class HomeProperties {

		public final TileProperties tile;

		@ColorInt public final int backgroundColor;

		public static class TileProperties {

			@ColorInt public final int backgroundColour;

			public TileProperties(TypedArray array) {
				backgroundColour = array.getColor(R.styleable.LexicaView_home__tile__background_colour, 0xffffff);
			}
		}

		public HomeProperties(TypedArray array) {
			this.tile = new TileProperties(array);

			backgroundColor = array.getColor(R.styleable.LexicaView_home__background_colour, 0xedb641);
		}
	}


	public static class BoardProperties {

		public final TileProperties tile;

		public static class TileProperties {

			@Px public final int borderWidth;
			@ColorInt public final int backgroundColour;
			@ColorInt public final int foregroundColour;
			@ColorInt public final int borderColour;
			@ColorInt public final int highlightColour;
			private final @ColorInt int[] hintModeColours;
			@ColorInt public final int hintModeUnusableLetterColour;
			@ColorInt public final int hintModeUnusableLetterBackgroundColour;

			public TileProperties(TypedArray array) {

				borderWidth = array.getDimensionPixelSize(R.styleable.LexicaView_game__tile__border_width, 1 /* dp */);

				backgroundColour = array.getColor(R.styleable.LexicaView_board__tile__background_colour, 0xf9f8d7);
				foregroundColour = array.getColor(R.styleable.LexicaView_board__tile__foreground_colour, 0x3d3c3b);
				borderColour = array.getColor(R.styleable.LexicaView_board__tile__border_colour, 0x3d3c3b);
				highlightColour = array.getColor(R.styleable.LexicaView_board__tile__highlight_colour, 0xffff00);
				hintModeColours = new int[] {
						array.getColor(R.styleable.LexicaView_board__hint_mode_colour_0, 0xffffff),
						array.getColor(R.styleable.LexicaView_board__hint_mode_colour_1, 0xffffff),
						array.getColor(R.styleable.LexicaView_board__hint_mode_colour_2, 0xffffff),
						array.getColor(R.styleable.LexicaView_board__hint_mode_colour_3, 0xffffff),
						array.getColor(R.styleable.LexicaView_board__hint_mode_colour_4, 0xffffff)
				};
				hintModeUnusableLetterColour = array.getColor(R.styleable.LexicaView_board__hint_mode_unusable_letter_colour, 0x888888);
				hintModeUnusableLetterBackgroundColour = array.getColor(R.styleable.LexicaView_board__hint_mode_unusable_letter_background_colour, 0x888888);

			}

			/**
			 * @param ratio Value between 0 and 1 representing percentage through the gradient value.
			 * @return An RGB value.
			 */
			public int getHintModeGradientColour(float ratio) {
				int firstStopIndex = (int)(ratio * hintModeColours.length);

				if (firstStopIndex <= 0) {
					return hintModeColours[0];
				}

				if (firstStopIndex >= hintModeColours.length - 1) {
					return hintModeColours[hintModeColours.length - 1];
				}

				int secondStopIndex = (int)(ratio * hintModeColours.length) + 1;

				int firstColour = hintModeColours[firstStopIndex];
				int secondColour = hintModeColours[secondStopIndex];

				float stepSize = 1.0f / hintModeColours.length;
				float ratioForCurrentStep = ratio;
				while (ratioForCurrentStep > stepSize) {
					ratioForCurrentStep -= stepSize;
				}
				ratioForCurrentStep *= hintModeColours.length;

				return ColorUtils.blendARGB(firstColour, secondColour, ratioForCurrentStep);
			}
		}

		public BoardProperties(TypedArray array) {
			this.tile = new TileProperties(array);
		}
	}

	public static class GameProperties {

		public final TimerProperties timer;
		public final ScoreProperties score;

		@Px public final int pastWordTextSize;
		@Px public final int currentWordSize;
		@ColorInt public final int currentWordColour;
		@ColorInt public final int previouslySelectedWordColour;
		@ColorInt public final int selectedWordColour;
		@ColorInt public final int notAWordColour;
		@ColorInt public final int backgroundColor;

		public static class TimerProperties {

			@Px public final int height;
			@Px public final int borderWidth;
			@ColorInt public final int backgroundColour;
			@ColorInt public final int startForegroundColour;
			@ColorInt public final int midForegroundColour;
			@ColorInt public final int endForegroundColour;

			private TimerProperties(TypedArray array) {
				height = array.getDimensionPixelSize(R.styleable.LexicaView_game__timer__height, 15 /* dp */);
				borderWidth = array.getDimensionPixelSize(R.styleable.LexicaView_game__timer__border_width, 0 /* dp */);
				backgroundColour = array.getColor(R.styleable.LexicaView_game__timer__background_colour, 0xf0cb69);
				startForegroundColour = array.getColor(R.styleable.LexicaView_game__timer__start_foreground_colour, 0xedb641);
				midForegroundColour = array.getColor(R.styleable.LexicaView_game__timer__mid_foreground_colour, 0xedb641);
				endForegroundColour = array.getColor(R.styleable.LexicaView_game__timer__end_foreground_colour, 0xedb641);
			}

		}

		public static class ScoreProperties {

			@ColorInt public final int headingTextColour;
			@ColorInt public final int textColour;
			@Px public final int headingTextSize;
			@Px public final int textSize;
			@Px public final int padding;
			@ColorInt public final int backgroundColour;

			public ScoreProperties(TypedArray array) {
				headingTextColour = array.getColor(R.styleable.LexicaView_game__score__heading_text_colour, 0xffffff);
				textColour = array.getColor(R.styleable.LexicaView_game__score__text_colour, 0xffffff);
				headingTextSize = array.getDimensionPixelSize(R.styleable.LexicaView_game__score__heading_text_size, 22 /* sp */);
				textSize = array.getDimensionPixelSize(R.styleable.LexicaView_game__score__value_text_size, 22 /* sp */);
				padding = array.getDimensionPixelSize(R.styleable.LexicaView_game__score__padding, 12 /* dp */);
				backgroundColour = array.getColor(R.styleable.LexicaView_game__score__background_colour, 0xf0cb69);
			}

		}

		private GameProperties(TypedArray array) {
			this.timer = new TimerProperties(array);
			this.score = new ScoreProperties(array);

			pastWordTextSize = array.getDimensionPixelSize(R.styleable.LexicaView_game__past_word_size, 20 /* sp */);
			currentWordColour = array.getColor(R.styleable.LexicaView_game__current_word_colour, 0xffffff);
			currentWordSize = array.getDimensionPixelSize(R.styleable.LexicaView_game__current_word_size, 24 /* sp */);
			previouslySelectedWordColour = array.getColor(R.styleable.LexicaView_game__previously_selected_word_colour, 0x88ffffff);
			selectedWordColour = array.getColor(R.styleable.LexicaView_game__selected_word_colour, 0xffffff);
			notAWordColour = array.getColor(R.styleable.LexicaView_game__not_a_word_colour, 0xffffff);
			backgroundColor = array.getColor(R.styleable.LexicaView_game__background_colour, 0xedb641);
		}

	}

	@Px public final int padding;
	@ColorInt public final int scoreScreenBackgroundColour;

	public ThemeProperties(Context context, AttributeSet attrs, int defStyle) {

		final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.LexicaView, defStyle, R.style.AppTheme_Base);

		this.home = new HomeProperties(array);
		this.game = new GameProperties(array);
		this.board = new BoardProperties(array);

		padding = array.getDimensionPixelSize(R.styleable.LexicaView_padding, 0 /* dp */);
		scoreScreenBackgroundColour = array.getColor(R.styleable.LexicaView_score__background_colour, 0xedb641);

		array.recycle();

	}

}
