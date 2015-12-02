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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.view.LexicaView;

public class PlayLexica extends AppCompatActivity implements Synchronizer.Finalizer {

	protected static final String TAG = "PlayLexica";

	private Synchronizer synch;
	private Game game;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null) {
			try {
				restoreGame(savedInstanceState);
			} catch (Exception e) {
				// On API < 11, the above should work fine because onSaveInstanceState should be
				// called before onPause. However, on API >= 11, onPause is always called _before_
				// onSaveInstanceState. In these cases, we will have to resort to the preferences
				// in order to restore our game (http://stackoverflow.com/a/28549669).
				Log.e(TAG,"error restoring state from savedInstanceState, trying to look for saved game in preferences",e);
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
					newGame();
					break;
			}
		} catch (Exception e) {
			Log.e(TAG,"top level",e);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.rotate:
				game.rotateBoard();	
			break;
			case R.id.save_game:
				synch.abort();
				saveGame();
				finish();
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

	private void newGame() {
		game = new Game(this);

		LexicaView lv = new LexicaView(this,game);

		if(synch != null) {
			synch.abort();
		}
		synch = new Synchronizer();
		synch.setCounter(game);
		synch.addEvent(lv);
		synch.setFinalizer(this);

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
		setContentView(lv,lp);
		lv.setKeepScreenOn(true);
		lv.setFocusableInTouchMode(true);
	}

	private void restoreGame() {
		clearSavedGame();
		game = new Game(this, new GameSaverPersistent(this));
		restoreGame(game);
	}

	private void restoreGame(Bundle bun) {
		game = new Game(this,new GameSaverTransient(bun));
		restoreGame(game);
	}

	private void restoreGame(Game game) {
		LexicaView lv = new LexicaView(this,game);

		if(synch != null) {
			synch.abort();
		}
		synch = new Synchronizer();
		synch.setCounter(game);
		synch.addEvent(lv);
		synch.setFinalizer(this);

		ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.FILL_PARENT);
		setContentView(lv,lp);
		lv.setKeepScreenOn(true);
		lv.setFocusableInTouchMode(true);
	}

	private void saveGame() {
		if(game.getStatus() == Game.GameStatus.GAME_RUNNING) {
			game.pause();

			game.save(new GameSaverPersistent(this));

		}
	}

	private void saveGame(Bundle state) {
		if(game.getStatus() == Game.GameStatus.GAME_RUNNING) {
			game.pause();
			game.save(new GameSaverTransient(state));
		}
	}

	public void onPause() {
		super.onPause();
		synch.abort();
		saveGame();
	}

	public void onResume() {
		super.onResume();
		if(game == null) newGame();

		switch(game.getStatus()) {
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

		Bundle bun = new Bundle();
		game.save(new GameSaverTransient(bun));

		Intent scoreIntent = new Intent("com.serwylo.lexica.action.SCORE");
		scoreIntent.putExtras(bun);

		startActivity(scoreIntent);

		finish();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveGame(outState);
	}

}
