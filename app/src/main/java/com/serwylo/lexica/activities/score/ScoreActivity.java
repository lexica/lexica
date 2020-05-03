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

package com.serwylo.lexica.activities.score;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.GameSaverTransient;
import com.serwylo.lexica.R;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.view.BoardView;
import com.serwylo.lexica.view.ThemeProperties;

import mehdi.sakout.fancybuttons.FancyButton;

public class ScoreActivity extends AppCompatActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "ScoreActivity";

	public static final String SCORE_PREF_FILE = "prefs_score_file";

	private Game game;
	private BoardView bv;
	private View highlighted;
	private String definitionProvider;

	public ScoreActivity() {
		super();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.score);

		definitionProvider = initialiseDefinitionProvider();
		Game game = initialiseGame(savedInstanceState);
		initialiseView(game);
		this.game = game;
	}

	private String initialiseDefinitionProvider() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		return prefs.getString("definitionProvider", "duckduckgo");
	}

	@NonNull
	private Game initialiseGame(Bundle savedInstanceState) {

		Game game;

		if(savedInstanceState != null) {
			game = new Game(this, new GameSaverTransient(savedInstanceState));

		} else {
			Intent intent = getIntent();
			Bundle bun = intent.getExtras();
			game = new Game(this,new GameSaverTransient(bun));
		}

		game.initializeDictionary();

		return game;

	}

	private void initialiseView(@NonNull Game game) {

		final RecyclerView recycler = findViewById(R.id.recycler_view);
		recycler.setLayoutManager(new NonScrollingHorizontalLayoutManager(this));
		recycler.setHasFixedSize(true);
		recycler.setAdapter(new ScoreTabAdapter(this, game));

		FancyButton found = findViewById(R.id.found_words_button);
		found.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recycler.scrollToPosition(0);
			}
		});

		FancyButton missed = findViewById(R.id.missed_words_button);
		missed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recycler.scrollToPosition(1);
			}
		});

		FancyButton back = findViewById(R.id.back_button);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		game.save(new GameSaverTransient(outState));
	}

	void setHighScore(int score) {
		String key = ScoreActivity.highScoreKey(this);
		SharedPreferences prefs = getSharedPreferences(ScoreActivity.SCORE_PREF_FILE, Context.MODE_PRIVATE);
		int highScore = prefs.getInt(key, 0);
		if (score > highScore) {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt(key, score);
			edit.apply();
		}
	}

	static String highScoreKey(Context c) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		return prefs.getString("dict", "US")
				+ prefs.getString("boardSize", "16")
				+ prefs.getString(Game.SCORE_TYPE, Game.SCORE_WORDS)
				+ prefs.getString("maxTimeRemaining", "180");
	}

	public static int getHighScore(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(SCORE_PREF_FILE, Context.MODE_PRIVATE);
		return prefs.getInt(highScoreKey(c), 0);
	}

}

