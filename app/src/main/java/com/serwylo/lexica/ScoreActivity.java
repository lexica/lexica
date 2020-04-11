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

import android.app.SearchManager;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TabHost;
import android.widget.TextView;

import com.serwylo.lexica.game.Game;
import com.serwylo.lexica.view.BoardView;

import net.healeys.trie.Solution;

import java.util.Iterator;
import java.util.Set;

public class ScoreActivity extends TabActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "ScoreActivity";

	public static final String SCORE_PREF_FILE = "prefs_score_file";

	private Game game;
	private BoardView bv;
	private View highlighted;
	private String definitionProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		definitionProvider = prefs.getString("definitionProvider", "duckduckgo");

		if(savedInstanceState != null) {
			game = new Game(this, new GameSaverTransient(savedInstanceState));

		} else {
			Intent intent = getIntent();
			Bundle bun = intent.getExtras();
			game = new Game(this,new GameSaverTransient(bun));
		}
		game.initializeDictionary();

		// Set up the tabs
		TabHost host = getTabHost();
		LayoutInflater.from(this).inflate(R.layout.score_view, host.getTabContentView(), true);
		host.addTab(host.newTabSpec("found").setIndicator(getString(R.string.found_words)).setContent(R.id.found_words));
		host.addTab(host.newTabSpec("missed").setIndicator(getString(R.string.missed_words)).setContent(R.id.missed_words));

		bv = (BoardView) findViewById(R.id.missed_board);
		bv.setGame(game);
		bv.setScoreType(game.getScoreType());

		Set<String> possible = game.getSolutions().keySet();

		ViewGroup foundVG = initializeScrollView(R.id.found_scroll);
		ViewGroup missedVG = initializeScrollView(R.id.missed_scroll);

		int score = 0;
		int max_score;
		int words = 0;
		int max_words = possible.size();

		Iterator<String> li = game.uniqueListIterator();
		while(li.hasNext()) {
			String w = li.next();

			if(game.isWord(w) && game.getWordScore(w) > 0) {
				int points = game.getWordScore(w);
				addWord(foundVG,w,points,true);
				score += points;
				words++;
			} else {
				addWord(foundVG,w,0,false);
			}

			possible.remove(w);
		}

		setHighScore(score);

		max_score = score;
		li = possible.iterator();

		while(li.hasNext()) {
			String w = li.next();
			max_score += game.getWordScore(w);
			addMissedWord(missedVG,game.getSolutions().get(w).get(0));
		}

		int totalScorePercentage = (int)(((double)score/max_score)*100);
		TextView t = (TextView) findViewById(R.id.score_points);
		t.setText(getString(R.string.value_max_percentage, score, max_score, totalScorePercentage));

		int totalWordsPercentage = (int)(((double)words/max_words)*100);
		t = (TextView) findViewById(R.id.score_words);
		t.setText(getString(R.string.value_max_percentage, words, max_words, totalWordsPercentage));

		Button b = (Button) findViewById(R.id.close_score);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		b = (Button) findViewById(R.id.missed_close_score);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		game.save(new GameSaverTransient(outState));
	}

	private ViewGroup initializeScrollView(int resId) {
		ScrollView sv = (ScrollView) findViewById(resId);
		sv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);

		ViewGroup.LayoutParams llLp = new ViewGroup.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

		LinearLayout ll = new LinearLayout(this);
		ll.setLayoutParams(llLp);
		ll.setOrientation(LinearLayout.VERTICAL);
		sv.addView(ll);

		return ll;
	}

	private void addWord(ViewGroup vg, String w, int points, boolean valid) {
		int color = (valid) ? 0xff000000 : 0xff4c657f;
		LinearLayout.LayoutParams text1Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			(float) 1.0);
		TextView tv1 = new TextView(this);
		tv1.setGravity(Gravity.START);
		tv1.setLayoutParams(text1Lp);
		tv1.setTextSize(16);
		tv1.setTextColor(color);
		if (!valid) {
			tv1.setPaintFlags(tv1.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		}
		tv1.setText(w.toUpperCase());

		LinearLayout.LayoutParams text2Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView tv2 = new TextView(this);
		tv2.setGravity(Gravity.END);
		tv2.setLayoutParams(text2Lp);
		tv2.setTextSize(16);
		tv2.setTextColor(color);
		tv2.setText(points+"        ");

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		ll.addView(tv1);

		if(valid) {
			TextView tv3 = new TextView(this);
			tv3.setGravity(Gravity.END);
			tv3.setLayoutParams(text2Lp);
			tv3.setTextSize(16);
			tv3.setTextColor(0xff000000);
			tv3.setText(R.string.define_word);

			tv3.setOnClickListener(new DefinerListener(w));
			tv3.setFocusable(true);

			ll.addView(tv2);
			ll.addView(tv3);
		}


		vg.addView(ll, new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));

	}

	private void addMissedWord(ViewGroup vg, Solution solution) {
		String word = solution.getWord();
		int points = game.getWordScore(word);

		String wordForDisplay = word.toUpperCase();

		LinearLayout ll = new LinearLayout(this);
		ll.setOrientation(LinearLayout.HORIZONTAL);

		ViewGroup.LayoutParams text1Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT,
			(float) 1.0);
		TextView tv1 = new TextView(this);
		tv1.setGravity(Gravity.START);
		tv1.setLayoutParams(text1Lp);
		tv1.setTextSize(16);
		tv1.setTextColor(0xff000000);
		tv1.setText(wordForDisplay);

		ViewGroup.LayoutParams text2Lp = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);

		// The Word Highlighting Link
		TextView tv2 = new TextView(this);
		tv2.setGravity(Gravity.END);
		tv2.setLayoutParams(text2Lp);
		tv2.setTextSize(16);
		tv2.setTextColor(0xff000000);
		tv2.setText(R.string.view_word);

		tv2.setOnClickListener(new HighlighterListener(solution.getPositions(),ll));
		tv2.setFocusable(true);

		// The Definition Link
		TextView tv3 = new TextView(this);
		tv3.setGravity(Gravity.END);
		tv3.setLayoutParams(text2Lp);
		tv3.setTextSize(16);
		tv3.setTextColor(0xff000000);
		tv3.setText(R.string.define_word);

		tv3.setOnClickListener(new DefinerListener(word));
		tv3.setFocusable(true);

		//Score
		TextView tv4 = new TextView(this);
		tv4.setGravity(Gravity.END);
		tv4.setLayoutParams(text2Lp);
		tv4.setTextSize(16);
		tv4.setTextColor(0xff000000);
		tv4.setText(points+"        ");

		// Padding between the links
		TextView padding = new TextView(this);
		padding.setGravity(Gravity.END);
		padding.setLayoutParams(text2Lp);
		padding.setTextSize(16);
		padding.setText("        ");

		ll.addView(tv1);
		ll.addView(tv4);
		ll.addView(tv2);
		ll.addView(padding);
		ll.addView(tv3);

		vg.addView(ll, new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.FILL_PARENT,
			ViewGroup.LayoutParams.WRAP_CONTENT));
	}

	private class HighlighterListener implements View.OnClickListener {
		private final Integer[] positions;
		private final View parentView;

		private HighlighterListener(Integer[] positions, View parentView) {
			this.positions = positions;
			this.parentView = parentView;
		}

		public void onClick(View v) {
			bv.highlight(positions);
			bv.invalidate();

			if(highlighted != null) {
				highlighted.setBackgroundColor(0x00000000);
			}
			highlighted = parentView;
			highlighted.setBackgroundColor(0xffffff00);
		}
	}

	private class DefinerListener implements View.OnClickListener {
		final String word;

		private DefinerListener(String word) {
			this.word = word;
		}

		public void onClick(View v) {
			Intent intent;
			switch (definitionProvider) {
				case "aard2":
					intent = new Intent("aard2.lookup");
					intent.putExtra(Intent.EXTRA_TEXT, word);
					break;
				case "quickdic":
					intent = new Intent("com.hughes.action.ACTION_SEARCH_DICT");
					intent.putExtra(SearchManager.QUERY, word);
					break;
				default:
					intent = null;
			}

			if (intent != null && intent.resolveActivity(getPackageManager()) != null) {
				startActivity(intent);
			} else {
				searchWordOnline();
			}
		}

		private void searchWordOnline() {
			String url = game.getLanguage().getDefinitionUrl();
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri u = Uri.parse(String.format(url, word));
			intent.setData(u);
			startActivity(intent);
		}
	}

	private static String highScoreKey(Context c) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(c);
		return prefs.getString("dict", "US")
				+ prefs.getString("boardSize", "16")
				+ prefs.getString(Game.SCORE_TYPE, Game.SCORE_WORDS)
				+ prefs.getString("maxTimeRemaining", "180");
	}

	private void setHighScore(int score) {
		String key = highScoreKey(this);
		SharedPreferences prefs = getSharedPreferences(SCORE_PREF_FILE, Context.MODE_PRIVATE);
		int highScore = prefs.getInt(key, 0);
		if (score > highScore) {
			SharedPreferences.Editor edit = prefs.edit();
			edit.putInt(key, score);
			edit.commit();
		}
	}

	public static int getHighScore(Context c) {
		SharedPreferences prefs = c.getSharedPreferences(SCORE_PREF_FILE, Context.MODE_PRIVATE);
		return prefs.getInt(highScoreKey(c), 0);
	}
}

