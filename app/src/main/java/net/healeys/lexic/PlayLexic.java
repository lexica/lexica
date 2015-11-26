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

package net.healeys.lexic;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;

import net.healeys.lexic.game.Game;
import net.healeys.lexic.view.LexicView;

public class PlayLexic extends Activity implements Synchronizer.Finalizer {

	protected static final String TAG = "PlayLexic";

	private Synchronizer synch;
	private Game game;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null) {
			try {
				restoreGame(savedInstanceState);
			} catch (Exception e) {
				Log.e(TAG,"error restoring state",e);
			}
			return;
		}
		try {
			String action = getIntent().getAction();
			switch (action) {
				case "net.healeys.lexic.action.RESTORE_GAME":
					restoreGame();
					break;
				case "net.healeys.lexic.action.NEW_GAME":
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
		return game.getStatus() == Game.GameStatus.GAME_RUNNING;
	}

	private void newGame() {
		game = new Game(this);

		LexicView lv = new LexicView(this,game);

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
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",MODE_PRIVATE);
		clearSavedGame();
		game = new Game(this,prefs);
		restoreGame(game);
	}

	private void restoreGame(Bundle bun) {
		game = new Game(this,bun);

		restoreGame(game);
	}

	private void restoreGame(Game game) {
		LexicView lv = new LexicView(this,game);

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
			SharedPreferences prefs = getSharedPreferences("prefs_game_file", MODE_PRIVATE);
			game.pause();

			SharedPreferences.Editor preferenceEditor = prefs.edit();
			game.save(preferenceEditor);
			preferenceEditor.commit();

		}
	}

	private void saveGame(Bundle state) {
		if(game.getStatus() == Game.GameStatus.GAME_RUNNING) {
			game.pause();
			game.save(state);
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

	private void clearSavedGame() {
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("activeGame",false);
		editor.commit();

	}

	private void score() {
		synch.abort();
		clearSavedGame();

		Bundle bun = new Bundle();
		game.save(bun);

		Intent scoreIntent = new Intent("net.healeys.lexic.action.SCORE");
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
