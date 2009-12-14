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

import net.healeys.lexic.game.Game;
import net.healeys.lexic.view.LexicView;
import net.healeys.lexic.view.VisibilityToggle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

public class PlayLexic extends Activity implements Synchronizer.Finalizer {

	protected static final String TAG = "PlayLexic";

	public static final Pattern DEFINE_PAT = Pattern.compile("\\w+");
	public static final String DEFINE_URL = 
		"http://www.google.com/search?q=define%3a+";

	private Synchronizer synch;
	private Game game;
	private Menu menu;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);
		// Log.d(TAG,"onCreate");
		if(savedInstanceState != null) {
			// Log.d(TAG,"restoring instance state");
			try {
				restoreGame(savedInstanceState);
			} catch (Exception e) {
				// Log.e(TAG,"error restoring state",e);
			}
			return;
		}
		try {
			String action = getIntent().getAction();
			if(action.equals("net.healeys.lexic.action.RESTORE_GAME")) {
				// Log.d(TAG,"restoring game");
				restoreGame();
			} else if(action.equals("net.healeys.lexic.action.NEW_GAME")) {
				// Log.d(TAG,"starting new game");
				newGame();
			} else {
				// Log.d(TAG,"Whoa there, friend!");
			}
		} catch (Exception e) {
			// Log.e(TAG,"top level",e);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		// Log.d(TAG,"onCreateOptionsMenu");
		
		menu = m;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.game_menu,menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Log.d(TAG,"onOptionsItemSelected");
		
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
		// Log.d(TAG,"created game");

		LexicView lv = new LexicView(this,game);
		// Log.d(TAG,"created view="+lv);

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

		// Log.d(TAG,"set view");
		// Log.d(TAG,"newGame ends");
	}

	private void restoreGame() {
		Resources res = getResources();
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			this.MODE_PRIVATE);

		clearSavedGame();

		game = new Game(this,prefs);

		restoreGame(game);
	}

	private void restoreGame(Bundle bun) {
		game = new Game(this,bun);

		restoreGame(game);
	}

	private void restoreGame(Game game) {

		// Log.d(TAG,"restored game");

		LexicView lv = new LexicView(this,game);
		// Log.d(TAG,"created view="+lv);

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

		// Log.d(TAG,"set view");

		// Log.d(TAG,"restoreGame ends");
	}

	private void saveGame() {
		if(game.getStatus() == Game.GameStatus.GAME_RUNNING) {
			// Log.d(TAG,"Saving");
			SharedPreferences prefs = getSharedPreferences(
				 "prefs_game_file",this.MODE_PRIVATE);
			game.pause();
			game.save(prefs.edit());
		}
	}

	private void saveGame(Bundle state) {
		if(game.getStatus() == Game.GameStatus.GAME_RUNNING) {
			// Log.d(TAG,"Saving");
			game.pause();
			game.save(state);
		}
	}

	public void onPause() {
		super.onPause();
		// Log.d(TAG,"Pausing");
		synch.abort();
		game.pause();
		saveGame();
	}

	public void onResume() {
		super.onResume();
		// Log.d(TAG,"onResume:"+game+","+synch);
		if(game == null) newGame();

		switch(game.getStatus()) {
			case GAME_STARTING:
				// Log.d(TAG,"onResume: GAME_STARTING");
				game.start();
				synch.start();
			break;
			case GAME_PAUSED:
				// Log.d(TAG,"onResume: GAME_PAUSED");
				game.unpause();
				synch.start();
			break;
			case GAME_FINISHED:
				// Log.d(TAG,"onResume: GAME_FINISHED");
				score();
			break;
		}
		// Log.d(TAG,"onResume finished");
	}

	public void doFinalEvent() {
		score();
	}

	private void clearSavedGame() {
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			this.MODE_PRIVATE);

		SharedPreferences.Editor editor = prefs.edit();
		editor.putBoolean("activeGame",false);
		editor.commit();

	}

	private void score() {
		// Log.d(TAG,"Finishing");

		synch.abort();
		clearSavedGame();

		Bundle bun = new Bundle();
		game.save(bun);

		Intent scoreIntent = new Intent("net.healeys.lexic.action.SCORE");
		scoreIntent.putExtras(bun);

		startActivity(scoreIntent);

		finish();
	}

	public void onStop() {
		super.onStop();
		// Log.d(TAG,"onStop()");
	}

	public void onDestroy() {
		super.onDestroy();
		// Log.d(TAG,"onDestroy()"+isFinishing());
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// Log.d(TAG,"onSaveInstanceState");
		saveGame(outState);
	}

}
