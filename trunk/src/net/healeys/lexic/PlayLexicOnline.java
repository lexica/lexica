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
import net.healeys.lexic.online.OnlineGame;
import net.healeys.lexic.view.LexicView;
import net.healeys.lexic.view.VisibilityToggle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.net.Uri;
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
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.Date;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.LinkedList;

public class PlayLexicOnline extends Activity implements 
	Synchronizer.Finalizer {

	protected static final String TAG = "PlayLexicOnline";

	private Synchronizer synch;
	private OnlineGame game;
	private Menu menu;

	private WebView wv;
	private boolean running;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);
		// Log.d(TAG,"onCreate");
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setProgress(0);

		if(savedInstanceState != null) {
			// Log.d(TAG,"restoring instance state");
			try {
				restoreGame(savedInstanceState);
			} catch (Exception e) {
				// Log.e(TAG,"error restoring state",e);
			}
			return;
		}

		setContentView(R.layout.loading);
		try {
			String url = getIntent().getData().toString();
			// Log.d(TAG,"url:"+url);
			newGame(url);
		} catch (Exception e) {
			// Log.e(TAG,"newGame failed!",e);
		}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu m) {
		// Log.d(TAG,"onCreateOptionsMenu");
		
		menu = m;

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.online_game_menu,menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Log.d(TAG,"onOptionsItemSelected");
		
		switch(item.getItemId()) {
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
		return game.getStatus() == Game.GameStatus.GAME_RUNNING;
	}

	private void newGame(String url) throws Exception {
		game = new OnlineGame(this,url);
		// Log.d(TAG,"created game");

		if(synch != null) {
			synch.abort();
		}

		synch = new Synchronizer();
		synch.setCounter(game);
		// Log.d(TAG,"newGame ends");
	}

	private void setLexicView() {
		LexicView lv = new LexicView(this,game);
		setContentView(lv);
		lv.setKeepScreenOn(true);
		lv.setFocusableInTouchMode(true);
		lv.requestFocus();

		synch.addEvent(lv);

		synch.setFinalizer(this);

		// Log.d(TAG,"set view");
	}

	private void restoreGame(Bundle bun) throws Exception {
		game = new OnlineGame(this,bun);

		restoreGame(game);
	}

	private void restoreGame(Game game) {

		// Log.d(TAG,"restored game");

		if(synch != null) {
			synch.abort();
		}

		synch = new Synchronizer();
		synch.setCounter(game);

		synch.setFinalizer(this);
	}

	private void saveGame(Bundle state) {
		// Log.d(TAG,"Saving");
		game.pause();
		game.save(state);
	}

	public void onPause() {
		super.onPause();
		// Log.d(TAG,"Pausing");
		synch.abort();
		game.pause();

		running = false;
	}

	public void onResume() {
		super.onResume();
		// Log.d(TAG,"onResume");

		running = true;

		switch(game.getStatus()) {
			case GAME_STARTING:
				new Thread() {
					public void run() {
						game.start();
						runOnUiThread(new Runnable() {
							public void run() {
								setLexicView();
								synch.start();
							}
						});
					}
				}.start();
			break;
			case GAME_PAUSED:
				game.unpause(true);
				setLexicView();
				synch.start();
			break;
			case GAME_FINISHED:
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
		// Log.d(TAG,"score() is starting");
		if(synch != null) {
			synch.abort();
		}

		setContentView(R.layout.online_score);
		View loading = findViewById(R.id.loading);
		loading.setVisibility(View.VISIBLE);

		View v = findViewById(R.id.scores);
		v.setVisibility(View.GONE);

		wv = (WebView) findViewById(R.id.webview);
		wv.setVisibility(View.GONE);
		wv.addJavascriptInterface(new ScoreViewer(),"score_viewer");
		wv.getSettings().setJavaScriptEnabled(true); 

		new Thread() {
			public void run() {
				game.submitWords(wv);
				runOnUiThread(new Runnable() {
					public void run() {
						View loading = findViewById(R.id.loading);
						loading.setVisibility(View.GONE);
						loading.setFocusable(false);
						wv.setVisibility(View.VISIBLE);
						wv.requestFocus();
					}
				});
			}
		}.start();
	}

	private ViewGroup initializeScrollView() {
		ScrollView sv = (ScrollView) findViewById(R.id.score_scroll);
		sv.removeAllViews();
		sv.setScrollBarStyle(sv.SCROLLBARS_OUTSIDE_INSET);

		ViewGroup.LayoutParams llLp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);
		
		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(llLp);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);

		return ll;
	}

	private void addScore(ViewGroup vg, OnlineGame.Score score) {

		LinearLayout rowLL = new LinearLayout(this);
		rowLL.setOrientation(LinearLayout.HORIZONTAL);
		rowLL.setLayoutParams(new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));

		TextView playerName = new TextView(this);
		playerName.setTextSize(24);
		playerName.setText(score.getName());
		playerName.setTextColor(0xff000000);
		playerName.setLayoutParams(new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			(float)1.0));
		rowLL.addView(playerName); 

		TextView points = new TextView(this);
		points.setTextSize(24);
		points.setText(new Integer(score.getPoints()).toString());
		points.setTextColor(0xff000000);
		rowLL.setLayoutParams(new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.FILL_PARENT));
		rowLL.addView(points);
			
		vg.addView(rowLL,new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));

		TextView playerScore = new TextView(this);
		playerScore.setTextSize(16);
		playerScore.setText(new Integer(score.getScore()).toString() + 
			" points");
		playerScore.setTextColor(0xff000000);

		vg.addView(playerScore, new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.FILL_PARENT));

	}

	private void addWordList(ViewGroup vg, OnlineGame.Score score,
		VisibilityToggle toggler) {
		
		TextView wordList = new TextView(this);
		wordList.setTextSize(16);
		wordList.setText(score.getUniqueWords());
		Linkify.addLinks(wordList,PlayLexic.DEFINE_PAT,PlayLexic.DEFINE_URL);
		wordList.setVisibility(View.GONE);
		wordList.setTextColor(0xff000000);
		wordList.setLinkTextColor(0xff000000);
		vg.addView(wordList, new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
		
		toggler.add(wordList);
	}

	public void scoreMulti() {
		// Log.d(TAG,"scoreMulti");

		Iterator <OnlineGame.Score> scores = game.getScores();

		wv = (WebView) findViewById(R.id.webview);
		wv.setVisibility(View.GONE);
	
		ViewGroup vg = initializeScrollView();
		VisibilityToggle toggler = new VisibilityToggle(R.string.hide_unique,
			R.string.show_unique);

		while(scores.hasNext()) {
			OnlineGame.Score score = scores.next();

			addScore(vg, score);
			addWordList(vg, score, toggler);
		}

		Button b = (Button) findViewById(R.id.close_score);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		b = (Button) findViewById(R.id.show_unique);
		b.setOnClickListener(toggler);

		View scoreView = findViewById(R.id.scores);
		scoreView.setVisibility(View.VISIBLE);
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
		saveGame(outState);
	}

	protected class ScoreViewer implements Runnable {
		private int progress;
		private int maxProgress;
		private Date start;
		private Handler handler;

		protected ScoreViewer() {
			handler = new Handler();
		}

		public void delay(int seconds) {
			if(seconds <= 0) {
				runOnUiThread(new Runnable() {
					public void run() {
						scoreMulti();
					}
				});
			}

			// Log.d(TAG,"Activating ScoreViewer:"+seconds);
			runOnUiThread(new Runnable() {
				public void run() {
					setProgressBarVisibility(true);
				}
			});
			start = new Date();
			progress = 0;
			maxProgress = Math.max(1000,seconds * 1000);
			handler.postDelayed(this, 100);
		}

		public void run() {
			// Log.d(TAG,"running ScoreViewer:"+progress+"/"+maxProgress);

			if(!running) return;

			Date curr = new Date();
			progress = (int) (curr.getTime() - start.getTime());
			progress = Math.min(progress,maxProgress);

			runOnUiThread(new Runnable() {
				public void run() {
					if(maxProgress == 0) {
						setProgress(10000 / 10000);
					} else {
						setProgress(progress * 10000 / maxProgress);
					}
				}
			});

			if(progress < maxProgress) {
				handler.postDelayed(this, 100);
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						scoreMulti();
					}
				});
			}
		}

	}

	public int getMaxTimeRemaining() {
		return 180000;
	}

}
