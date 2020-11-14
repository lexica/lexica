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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.preference.PreferenceManager;

import com.serwylo.lexica.activities.score.ScoreActivity;
import com.serwylo.lexica.databinding.SplashBinding;
import com.serwylo.lexica.db.Database;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.db.GameModeRepository;
import com.serwylo.lexica.db.Result;
import com.serwylo.lexica.db.ResultRepository;
import com.serwylo.lexica.db.migration.MigrateHighScoresFromPreferences;
import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.lang.LanguageLabel;

import java.util.Locale;

public class MainMenuActivity extends Activity {

    @SuppressWarnings("unused")
    protected static final String TAG = "Lexica";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().applyTheme(this);
        load();
    }

    private void splashScreen(GameMode gameMode, Result highScore) {

        SplashBinding binding = SplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.newGame.setOnClickListener(v -> {
            Intent intent = new Intent("com.serwylo.lexica.action.NEW_GAME");
            intent.putExtra("gameMode", gameMode);
            startActivity(intent);
        });

        binding.gameModeButton.setOnClickListener(v -> startActivity(new Intent(this, ChooseGameModeActivity.class)));
        binding.gameModeButton.setText(gameMode.getLabel());

        String languageCode = new Util().getLexiconString(this);
        Language language = Language.fromOrNull(languageCode);
        binding.languageButton.setText(LanguageLabel.getLabel(this, language));
        binding.languageButton.setOnClickListener(v -> startActivity(new Intent(this, ChooseLexiconActivity.class)));

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
        long score = ScoreActivity.getHighScore(this);
        score = highScore == null ? 0 : highScore.getScore();
        binding.highScore.setText(String.format(Locale.getDefault(), "%d", score));
    }

    public void onResume() {
        super.onResume();
        load();
    }

    public boolean savedGame() {
        return new GameSaverPersistent(this).hasSavedGame();
    }

    private void load() {
        AsyncTask.execute(() -> {

            // Force migrations to run prior to querying the database.
            // This is required because we populate default game modes, for which we need at least one to be present.
            // https://stackoverflow.com/a/55067991
            final Database db = Database.get(this);
            db.getOpenHelper().getReadableDatabase();

            String languageCode = new Util().getLexiconString(this);
            Language language = Language.fromOrNull(languageCode);

            final GameModeRepository gameModeRepository = new GameModeRepository(getApplication());
            final ResultRepository resultRepository = new ResultRepository(getApplication());

            if (!gameModeRepository.hasGameModes()) {
                new MigrateHighScoresFromPreferences(this).initialiseDb(db.gameModeDao(), db.resultDao());
            }

            final GameMode gameMode = gameModeRepository.loadCurrentGameMode();
            final Result highScore = resultRepository.findHighScore(gameMode, language);

            runOnUiThread(() -> splashScreen(gameMode, highScore));

        });
    }

}
