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

import com.serwylo.lexica.activities.score.ScoreActivity;
import com.serwylo.lexica.databinding.SplashBinding;

import java.util.Locale;

public class MainMenuActivity extends Activity {

    @SuppressWarnings("unused")
    protected static final String TAG = "Lexica";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().applyTheme(this);
        splashScreen();
    }

    private void splashScreen() {

        SplashBinding binding = SplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.newGame.setOnClickListener(v -> startActivity(new Intent("com.serwylo.lexica.action.CHOOSE_GAME_MODE")));

        if (savedGame()) {
            binding.restoreGame.setOnClickListener(v -> startActivity(new Intent("com.serwylo.lexica.action.RESTORE_GAME")));
            binding.restoreGame.setEnabled(true);
        }

        binding.about.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri u = Uri.parse("https://github.com/lexica/lexica");
            i.setData(u);
            startActivity(i);
        });

        binding.preferences.setOnClickListener(v -> startActivity(new Intent("com.serwylo.lexica.action.CONFIGURE")));

        // TODO: Leaving format argument here for now, until all strings.xml have been replaced for each lang to no
        //       longer have this argument. Otherwise, they will likely crash at runtime.
        binding.highScoreLabel.setText(getResources().getString(R.string.high_score, 0));
        binding.highScore.setText(String.format(Locale.getDefault(), "%d", ScoreActivity.getHighScore(this)));
    }

    public void onResume() {
        super.onResume();
        splashScreen();
    }

    public boolean savedGame() {
        return new GameSaverPersistent(this).hasSavedGame();
    }

}
