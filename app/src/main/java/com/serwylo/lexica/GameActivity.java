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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.serwylo.lexica.activities.score.ScoreActivity;
import com.serwylo.lexica.db.Database;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.db.ResultRepository;
import com.serwylo.lexica.game.CharProbGenerator;
import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.view.LexicaView;

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
                    if (!restoreGame()) {
                        finish();
                        return;
                    }
                }
            }
            return;
        }
        try {
            String action = getIntent().getAction();
            switch (action) {
                case "com.serwylo.lexica.action.RESTORE_GAME":
                    if (!restoreGame()) {
                        finish();
                        return;
                    }
                    break;

                case "com.serwylo.lexica.action.NEW_GAME":
                    GameMode gameMode = getIntent().getExtras().getParcelable("gameMode");
                    String[] board = getIntent().getExtras().getStringArray("board");

                    String langName = getIntent().getExtras().getString("lang");
                    Language language = Language.from(langName);

                    game = board != null
                        ? new Game(this, gameMode, language, new CharProbGenerator.BoardSeed(board))
                        : Game.generateGame(this, gameMode, language);

                    setupGameView(game);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating new Lexica game. Will finish() the GameActivity in the hope it gracefully returns to the main menu.", e);
            finish();
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
                promptToEndGame();
                break;
        }
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return game.getStatus() != Game.GameStatus.GAME_FINISHED;
    }

    private void promptToEndGame() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_end_game_confirm_title)
                .setMessage(R.string.dialog_end_game_confirm_message)
                .setPositiveButton(R.string.menu_end_game, (dialog, which) -> game.endNow())
                .setNegativeButton(R.string.button_cancel, null)
                .show();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean restoreGame() {
        try {
            clearSavedGame();
            game = new Game(this, new GameSaverPersistent(this));
            setupGameView(game);
            return true;
        } catch (Exception e) {
            // Be forgiving here, because although it only happens infrequently, we need the flexibility
            // to be able to change the format that game saves have on disk. Given Lexica is a very
            // casual game, so it hopefully isn't the end of the world to throw away games infrequently
            // upon updating Lexica.
            Log.e(TAG, "Error restoring game.", e);
            return false;
        }
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

    private void saveGamePersistent() {
        if (game.getStatus() == Game.GameStatus.GAME_RUNNING) {
            game.pause();
            game.save(new GameSaverPersistent(this));
        }
    }

    private void saveGameTransient(Bundle state) {
        if (game.getStatus() == Game.GameStatus.GAME_RUNNING) {
            game.pause();
            game.save(new GameSaverTransient(state));
        }
    }

    private void navigateToHome() {
        synch.abort();
        saveGamePersistent();
        NavUtils.navigateUpFromSameTask(this);
    }

    public void onPause() {
        super.onPause();
        synch.abort();
        saveGamePersistent();
    }

    public void onResume() {
        super.onResume();
        if (game == null) {
            Toast.makeText(this, R.string.error_restoring_game, Toast.LENGTH_SHORT).show();;
            NavUtils.navigateUpFromSameTask(this);
            return;
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
                finishGame();
                break;
        }
    }

    public void doFinalEvent() {
        finishGame();
    }

    private boolean hasSavedGame() {
        return new GameSaverPersistent(this).hasSavedGame();
    }

    private void clearSavedGame() {
        new GameSaverPersistent(this).clearSavedGame();
    }

    private void finishGame() {
        synch.abort();
        clearSavedGame();

        Database.writeExecutor.execute(() -> {
            ResultRepository repo = new ResultRepository(this);
            repo.recordGameResult(game);
        });

        Intent scoreIntent = createScoreIntent();
        startActivity(scoreIntent);
        finish();
    }

    public void showFoundWords() {
        synch.abort();
        saveGamePersistent();

        Intent scoreIntent = createScoreIntent();
        scoreIntent.putExtra(ScoreActivity.ONLY_FOUND_WORDS, true);
        startActivity(scoreIntent);
    }

    private Intent createScoreIntent() {
        final Bundle bundleWithSavedGame = new Bundle();
        game.save(new GameSaverTransient(bundleWithSavedGame));

        Intent scoreIntent = new Intent("com.serwylo.lexica.action.SCORE");
        scoreIntent.putExtras(bundleWithSavedGame);
        return scoreIntent;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        saveGameTransient(outState);
    }

}
