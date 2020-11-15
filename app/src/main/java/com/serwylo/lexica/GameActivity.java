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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.serwylo.lexica.activities.score.ScoreCalculator;
import com.serwylo.lexica.db.Database;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.db.Result;
import com.serwylo.lexica.db.SelectedWord;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.view.LexicaView;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends AppCompatActivity implements Synchronizer.Finalizer {

    protected static final String TAG = "PlayLexica";

    private Synchronizer synch;
    private Game game;
    private FrameLayout gameWrapper;
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().applyTheme(this);

        setContentView(R.layout.game);

        gameWrapper = findViewById(R.id.game_wrapper);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> navigateToHome());

        if (savedInstanceState != null) {
            try {
                restoreGame(savedInstanceState);
            } catch (Exception e) {
                // On API < 11, the above should work fine because onSaveInstanceState should be
                // called before onPause. However, on API >= 11, onPause is always called _before_
                // onSaveInstanceState. In these cases, we will have to resort to the preferences
                // in order to restore our game (http://stackoverflow.com/a/28549669).
                Log.e(TAG, "error restoring state from savedInstanceState, trying to look for saved game in preferences", e);
                if (hasSavedGame()) {
                    restoreGame();
                }
            }
            return;
        }
        try {
            String action = getIntent().getAction();
            switch (action) {
                case "com.serwylo.lexica.action.RESTORE_GAME":
                    restoreGame();
                    break;
                case "com.serwylo.lexica.action.NEW_GAME":
                    newGame(getIntent().getExtras().getParcelable("gameMode"));
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "top level", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, toolbar.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rotate:
                game.rotateBoard();
                break;
            case R.id.end_game:
                game.endNow();
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return game.getStatus() != Game.GameStatus.GAME_FINISHED;
    }

    private void newGame(GameMode gameMode) {
        Game bestGame = new Game(this, gameMode);
        int numAttempts = 0;
        while (bestGame.getMaxWordCount() < 45 && numAttempts < 5) {
            Log.d(TAG, "Generating another board, because the previous one only had " + bestGame.getMaxWordCount() + " words, but we want at least 45. Will give up after 5 tries.");
            Game nextAttempt = new Game(this, gameMode);
            if (nextAttempt.getMaxWordCount() > bestGame.getMaxWordCount()) {
                bestGame = nextAttempt;
            }
            numAttempts ++;
        }

        Log.d(TAG, "Generated new board with " + bestGame.getMaxWordCount() + " words");
        this.game = bestGame;
        setupGameView(game);
    }

    private void restoreGame() {
        clearSavedGame();
        game = new Game(this, new GameSaverPersistent(this));
        setupGameView(game);
    }

    private void restoreGame(Bundle bun) {
        game = new Game(this, new GameSaverTransient(bun));
        setupGameView(game);
    }

    private void setupGameView(Game game) {
        LexicaView lv = new LexicaView(this, game);

        if (synch != null) {
            synch.abort();
        }

        synch = new Synchronizer();
        synch.setCounter(game);
        synch.addEvent(lv);
        synch.setFinalizer(this);

        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lv.setKeepScreenOn(true);
        lv.setFocusableInTouchMode(true);

        gameWrapper.removeAllViews();
        gameWrapper.addView(lv, lp);
    }

    private void saveGame() {
        if (game.getStatus() == Game.GameStatus.GAME_RUNNING) {
            game.pause();

            game.save(new GameSaverPersistent(this));

        }
    }

    private void saveGame(Bundle state) {
        if (game.getStatus() == Game.GameStatus.GAME_RUNNING) {
            game.pause();
            game.save(new GameSaverTransient(state));
        }
    }

    private void navigateToHome() {
        synch.abort();
        saveGame();
        NavUtils.navigateUpFromSameTask(this);
    }

    public void onPause() {
        super.onPause();
        synch.abort();
        saveGame();
    }

    public void onResume() {
        super.onResume();
        if (game == null) {
            Toast.makeText(this, "An error occured restoring your game", Toast.LENGTH_SHORT).show();;
            NavUtils.navigateUpFromSameTask(this);
        }

        switch (game.getStatus()) {
            case GAME_STARTING:
                game.start();
                synch.start();
                break;
            case GAME_PAUSED:
                game.unpause();
                synch.start();
                break;
            case GAME_FINISHED:
                score();
                break;
        }
    }

    public void doFinalEvent() {
        score();
    }

    private boolean hasSavedGame() {
        return new GameSaverPersistent(this).hasSavedGame();
    }

    private void clearSavedGame() {
        new GameSaverPersistent(this).clearSavedGame();
    }

    private void score() {
        synch.abort();
        clearSavedGame();

        final Bundle bun = new Bundle();
        game.save(new GameSaverTransient(bun));

        Database.writeExecutor.execute(() -> {

            ScoreCalculator score = new ScoreCalculator(game);

            Result result = new Result(
                    0,
                    game.getGameMode().getGameModeId(),
                    game.getLanguage().getName(),
                    score.getScore(),
                    score.getMaxScore(),
                    score.getNumWords(),
                    score.getMaxWords()
            );

            Database.get(this).resultDao().insert(result);

            List<SelectedWord> words = new ArrayList<>(score.getItems().size());
            for (ScoreCalculator.Selected word : score.getItems()) {
                words.add(new SelectedWord(0, result.getResultId(), word.getWord(), word.getScore()));
            }

            Database.get(this).selectedWordDao().insert(words);

            showScore(bun);
        });
    }

    private void showScore(Bundle bundleWithSavedGame) {
        Intent scoreIntent = new Intent("com.serwylo.lexica.action.SCORE");
        scoreIntent.putExtras(bundleWithSavedGame);

        startActivity(scoreIntent);

        finish();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveGame(outState);
    }

}
