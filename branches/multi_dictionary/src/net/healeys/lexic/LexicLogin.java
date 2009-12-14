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

import net.healeys.lexic.online.OnlineGame;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LexicLogin extends Activity {

	public static final String TAG="LexicLogin";

	private View loading;
	private WebView wv;
	private Handler handler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     	super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

		handler = new Handler();
		initWebkit();

		if(savedInstanceState != null) {
			// Log.d(TAG,"restoring instance state");
			wv.restoreState(savedInstanceState);
			showWebview();
		} else {
			// Log.d(TAG,"new state");
			showLoading();
			wv.loadUrl(OnlineGame.BASE_URL+"lexic/session/");
		}

	}
	
	public void initWebkit() {

		setContentView(R.layout.webview);

		wv = (WebView) findViewById(R.id.webview);
		loading = findViewById(R.id.loading);

		wv.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				handler.post(new Runnable() {
					public void run() {
						setProgressBarIndeterminateVisibility(false);
						showWebview();
					}
				});
				super.onPageFinished(view,url);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				handler.post(new Runnable() {
					public void run() {
						setProgressBarIndeterminateVisibility(true);
					}
				});
				super.onLoadResource(view,url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				Log.d(TAG,"shouldOverrideUrlLoading: "+url);
				if(url.startsWith(OnlineGame.BASE_URL)) {
					return false;
				}
				Intent openURL = new Intent("android.intent.action.VIEW",
					Uri.parse(url));
				startActivity(openURL);
				return true;
			}
		});

		wv.addJavascriptInterface(new LoginProcessor(this),"login_processor");
		wv.addJavascriptInterface(new GameProcessor(),"game_processor");
		wv.getSettings().setJavaScriptEnabled(true); 
		wv.getSettings().setLightTouchEnabled(true); 

	}

	private void showLoading() {
		loading.setVisibility(View.VISIBLE);
		wv.setVisibility(View.GONE);
	}

	private void showWebview() {
		loading.setVisibility(View.GONE);
		loading.setFocusable(false);
		wv.setVisibility(View.VISIBLE);
		loading.setFocusable(true);
		wv.requestFocus();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		wv.saveState(outState);
	}

	protected void saveSessionId(String id, String username) {
		SharedPreferences.Editor editor = PreferenceManager.
			getDefaultSharedPreferences(this).edit();	
		
		editor.putString("login_id",id);
		editor.putString("login_name",username);
		editor.commit();
	}

	protected void endSession() {
		SharedPreferences.Editor editor = PreferenceManager.
			getDefaultSharedPreferences(this).edit();	

		editor.putString("login_id","");
		editor.putString("login_name","");
		editor.commit();

		wv.loadUrl(OnlineGame.BASE_URL+"lexic/logout/");
	}

	private void startGame(String gameUrl) {
		// Log.d(TAG,"startGame:"+gameUrl);
		startActivity(new Intent("net.healeys.lexic.action.ONLINE_GAME",
			Uri.parse(gameUrl)));
	}

	public class LoginProcessor {
		private LexicLogin login;

		protected LoginProcessor(LexicLogin login) {
			this.login = login;
		}

		public void process(String sessionid,String username) {
			// Log.d(TAG,"LoginProcessor.process id:"+sessionid);
			// Log.d(TAG,"LoginProcessor.process username:"+username);
			SessionIdSaver idsaver = new SessionIdSaver(login,sessionid,
				username);

			handler.post(idsaver);
		}

		public void logout() {
			handler.post(new Runnable() {
				public void run() {
					endSession();
					// Log.d(TAG,"LoginProcessor logout()");
				}
			});
		}
	}

	protected class SessionIdSaver implements Runnable {
		private String id;
		private String username;
		private LexicLogin login;
		protected SessionIdSaver(LexicLogin login, String id,
			String username) {
			this.id = id;
			this.login = login;
			this.username = username;
		}

		public void run() {
			login.saveSessionId(id,username);
		}
	}

	public class GameProcessor {
		private String gameUrl; // this seems like an ugly hack.
		
		public void start(int size) {
			// Log.d(TAG,"NewGameProcessor.start:"+size);
			SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(
				LexicLogin.this);

			String lang = prefs.getString("dict","US");
			gameUrl = OnlineGame.BASE_URL+"lexic/board/?lang="+lang+"&size="+
				size;

			handler.post(new Runnable() {
				public void run() {
					startGame(gameUrl);
				}
			});
		}
	}

	
	public void onRestart() {
		super.onRestart();
		if(wv != null) {
			showLoading();
			wv.loadUrl(OnlineGame.BASE_URL+"lexic/session/");
		}
	}
}
