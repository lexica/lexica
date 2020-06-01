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

package com.serwylo.lexica;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.serwylo.lexica.activities.score.ScoreActivity;

import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

public class Lexica extends Activity {

	@SuppressWarnings("unused")
	protected static final String TAG = "Lexica";

    @Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ThemeManager.getInstance().applyTheme(this);
		splashScreen();
    }

	private void splashScreen() {
		setContentView(R.layout.splash);

		FancyButton newGame = findViewById(R.id.new_game);
		newGame.setOnClickListener(v -> startActivity(new Intent("com.serwylo.lexica.action.NEW_GAME")));

		if(savedGame()) {
			FancyButton restoreGame = findViewById(R.id.restore_game);
			restoreGame.setOnClickListener(v -> startActivity(new Intent("com.serwylo.lexica.action.RESTORE_GAME")));
			restoreGame.setEnabled(true);
		}

		FancyButton about = findViewById(R.id.about);
		about.setOnClickListener(v -> {
			Intent i = new Intent(Intent.ACTION_VIEW);
			Uri u = Uri.parse("https://github.com/lexica/lexica");
			i.setData(u);
			startActivity(i);
		});

		FancyButton preferences = findViewById(R.id.preferences);
		preferences.setOnClickListener(v -> startActivity(new Intent("com.serwylo.lexica.action.CONFIGURE")));

		int highScoreValue = ScoreActivity.getHighScore(this);

		TextView highScoreLabel = findViewById(R.id.high_score_label);
		highScoreLabel.setText(getResources().getString(R.string.high_score, 0));

		TextView highScore = findViewById(R.id.high_score);
		highScore.setText(String.format(Locale.getDefault(), "%d", highScoreValue));
	}

	public void onResume() {
		super.onResume();
		splashScreen();
	}

	public boolean savedGame() {
		return new GameSaverPersistent(this).hasSavedGame();
	}

}
