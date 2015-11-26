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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.ListIterator;

public class Lexic extends Activity {

	protected static final String TAG = "Lexic";

	private static final int DIALOG_NO_SAVED = 1;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		try {
       		super.onCreate(savedInstanceState);
			splashScreen();
		} catch (Exception e) {
			// Log.e(TAG,"top level",e);
		}
    }

	private void splashScreen() {
		setContentView(R.layout.splash);

		Button b = (Button) findViewById(R.id.new_game);
		// Log.d(TAG,"b="+b);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("net.healeys.lexic.action.NEW_GAME"));
			}
		});
		
		b = (Button) findViewById(R.id.play_online);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new 
					Intent("net.healeys.lexic.action.ONLINE_LOGIN"));
			}
		});

		if(savedGame()) {
			b = (Button) findViewById(R.id.restore_game);
			b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					if(savedGame()) {
						// Log.d(TAG,"restoring game");
						startActivity(new 
							Intent("net.healeys.lexic.action.RESTORE_GAME"));
					} else {
						// Log.d(TAG,"no saved game :(");
						showDialog(DIALOG_NO_SAVED);
					}
				}
			});
			b.setEnabled(true);
		}

		b = (Button) findViewById(R.id.about);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				Uri u = Uri.parse("http://www.lexic-games.com/help/");
				i.setData(u);
				startActivity(i);
			}
		});

		b = (Button) findViewById(R.id.preferences);
		b.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startActivity(new 
					Intent("net.healeys.lexic.action.CONFIGURE"));
			}
		});
	}

	public void onPause() {
		super.onPause();
		// Log.d(TAG,"Pausing");
	}

	public void onResume() {
		super.onResume();
		splashScreen();
	}

	public boolean savedGame() {
		Resources res = getResources();
		SharedPreferences prefs = getSharedPreferences("prefs_game_file",
			MODE_PRIVATE);

		return prefs.getBoolean("activeGame",false);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch(id) {
			case DIALOG_NO_SAVED:
				return new AlertDialog.Builder(this)
					.setTitle(getResources().
						getString(R.string.dialog_no_saved))
					.setPositiveButton(R.string.dialog_ok, 
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, 
								int whichButton) {
									// do nothing.
								}
						})
					.create();
		}
		return null;
	}

	private class DictListener implements DialogInterface.OnClickListener {
		
		private SharedPreferences prefs;
		private int selected;
		private int original;

		public DictListener(SharedPreferences p) {
			prefs = p;
			if(prefs.getString("dict","US").equals("UK")) {
				selected = 1;
			} else {
				selected = 0;
			}
			original = selected;
		}

		public void onClick(DialogInterface dialog,int whichButton) {
			// Log.d(TAG,"whichButton:"+whichButton);
			switch(whichButton) {
				case -1:
					SharedPreferences.Editor editor = prefs.edit();
					switch(selected) {
						case 0:
							editor.putString("dict","US");
						break;
						case 1:
							editor.putString("dict","UK");
						break;
					}
					editor.commit();
				break;
				case -2:
					selected = original;
				break;
				case 0: // US
					selected = 0;
				break;
				case 1: // UK
					selected = 1;
				break;
			}
		}

		public int getSelected() {
			return selected;
		}
	}
}
