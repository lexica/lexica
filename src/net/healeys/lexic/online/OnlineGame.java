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

package net.healeys.lexic.online;

import net.healeys.lexic.game.Board;
import net.healeys.lexic.game.FiveByFiveBoard;
import net.healeys.lexic.game.FourByFourBoard;
import net.healeys.lexic.game.Game;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.WebView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.HttpClient;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;

public class OnlineGame extends Game{

	private static final int MAX_ATTEMPTS=3;
	protected static final String TAG = "OnlineGame";
	public static final String BASE_URL="http://api.lexic-games.com/";
	//public static final String BASE_URL="http://dev.lexic-games.com/";
	
	private int id;

	private String uri;
	private String sessionid;
	private String userAgent;

	private HashMap<String,String> urls;

	public OnlineGame(Context c, String uri) throws Exception {
		super(c);

		this.uri = uri;

		// Set up userAgent
		PackageManager pm = c.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(c.getPackageName(),0);
		userAgent = "Lexic ("+pi.packageName+" "+pi.versionName+" "+
			pi.versionCode+")";

		// Set up session id
		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences(c);
		String loginId = prefs.getString("login_id",null);
		sessionid = "sessionid="+loginId;

		urls = new HashMap<String,String>();
	}

	public OnlineGame (Context c,Bundle bun) throws Exception {
		super(c,bun,true);

		uri = bun.getString("uri");

		// Set up userAgent
		PackageManager pm = c.getPackageManager();
		PackageInfo pi = pm.getPackageInfo(c.getPackageName(),0);
		userAgent = "Lexic ("+pi.packageName+" "+pi.versionName+" "+
			pi.versionCode+")";

		// Set up session id
		SharedPreferences prefs = 
			PreferenceManager.getDefaultSharedPreferences(c);
		String loginId = prefs.getString("login_id",null);
		sessionid = "sessionid="+loginId;

		urls = new HashMap<String,String>();
		urls.put("words",bun.getString("words_url"));
		urls.put("score",bun.getString("score_url"));

	}

	public int getMaxTimeRemaining() {
		return 18000;
	}

	public void save(Bundle bun) {
		super.save(bun);
		
		bun.putString("uri",uri);
		bun.putString("words_url",urls.get("words"));
		bun.putString("score_url",urls.get("score"));
		
	}

	public boolean start() {
		Pattern pat = Pattern.compile("(\\w+):(.+)");
		for(int attempt=0; attempt<MAX_ATTEMPTS; attempt++) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet get = new HttpGet(uri);
				addHeaders(get);

				HttpResponse resp = httpClient.execute(get);

				BufferedReader br = new BufferedReader(
					new InputStreamReader(resp.getEntity().getContent()));

				String line;
				while((line = br.readLine()) != null) {
					// Log.d(TAG,"line:"+line);
					Matcher mat = pat.matcher(line);
					if(mat.find()) {
						String key = line.substring(
							mat.start(1),mat.end(1));
						String value = line.substring(
							mat.start(2),mat.end(2));
						// Log.d(TAG,"key:"+key);
						// Log.d(TAG,"value:"+value);

						if(key.equals("board")) {
							String[] letters = value.split(",");
							if(letters.length == 16) {
								setBoard(new FourByFourBoard(letters));
							} else if(letters.length == 25) {
								setBoard(new FiveByFiveBoard(letters));
							}
						} else if(key.equals("id")) {
							id = Integer.parseInt(value);
						} else {
							urls.put(key,value);
						}

					}
				}
				
				super.start();
				return true;
			} catch (Exception e) {
				// Log.e(TAG,"Connection Error in constructor",e);
			}
		}

		super.start();
		return false;
	}

	private void addHeaders(HttpMessage http) {
		http.setHeader("User-agent",userAgent);
		http.setHeader("Cookie",sessionid);
	}

	public boolean submitWords(WebView display) {
		Pattern contentPat = Pattern.compile("([^;]+); charset=(.+)");
		for(int attempt=0; attempt<MAX_ATTEMPTS; attempt++) {
			String url = BASE_URL+urls.get("words");

			Iterator<String> li = uniqueListIterator();
			StringBuffer sb = new StringBuffer(4096);
			while(li.hasNext()) {
				sb.append(li.next());
				if(li.hasNext()) sb.append(',');
			}
			String data = URLEncoder.encode(sb.toString());

			try {
				
				HttpClient httpClient = new DefaultHttpClient();
				HttpPost post = new HttpPost(url);
				addHeaders(post);

				post.setEntity(new StringEntity("words="+data));

				HttpResponse resp = httpClient.execute(post);
				BufferedReader br = new BufferedReader(
					new InputStreamReader(resp.getEntity().getContent()));

				sb = new StringBuffer(4096);
				String line;
				while((line = br.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}

				String contentHeader = resp.getFirstHeader("Content-type")
					.getValue();
				String contentType;
				String contentEncoding;
				Matcher mat = contentPat.matcher(contentHeader);
				if(mat.find()) {
					contentType = contentHeader.substring(
					mat.start(1),mat.end(1));
					contentEncoding = contentHeader.substring(
						mat.start(2),mat.end(2));
				} else {
					contentType = contentHeader;
					contentEncoding = "utf-8";
				}

				// Log.d(TAG,"url:"+url);
				// Log.d(TAG,"data:"+sb.toString());
				// Log.d(TAG,"contentType:"+contentType);
				// Log.d(TAG,"contentEncoding:"+contentEncoding);

				display.loadDataWithBaseURL(url,sb.toString(),
					"text/html","utf-8",null);
				return true;
			} catch(Exception e) {
				// Log.d(TAG,"error submitting words",e);
			}
		}
		return false;
	}

	public Iterator<Score> getScores() {
		Pattern startPat = Pattern.compile("^!START:([^,]+),(\\d+),(-?\\d+)$");
		Pattern endPat = Pattern.compile("^!END$");
		
		for(int attempt=0; attempt<MAX_ATTEMPTS; attempt++) {
			try {
				LinkedList<Score> scores = new LinkedList<Score>();
		
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet get = new HttpGet(BASE_URL+urls.get("score"));
				addHeaders(get);

				HttpResponse resp = httpClient.execute(get);

				BufferedReader br = new BufferedReader(
					new InputStreamReader(resp.getEntity().getContent()));

				String username = "";
				int points = 0;
				int score = 0;
				String line;
				StringBuffer sb = new StringBuffer(0);
				while((line = br.readLine()) != null) {
					// Log.d(TAG,"line:"+line);
					Matcher mat;
					if((mat = startPat.matcher(line)).find()) {
						// Log.d(TAG,"startPat matched"+line);
						username = line.substring(
							mat.start(1),mat.end(1));
						points = Integer.parseInt(line.substring(
							mat.start(2),mat.end(2)));
						score = Integer.parseInt(line.substring(
							mat.start(3),mat.end(3)));
						sb = new StringBuffer(2048);
					} else if((mat = endPat.matcher(line)).find()) {
						// Log.d(TAG,"endPat matched"+line);
						scores.add(new Score(username,score,points,
							sb.toString()));
					} else if(sb != null && line.length() > 1) {
						sb.append(line);
						sb.append('\n');
					}
				}

				return scores.iterator();
			} catch (Exception e) {
				// Log.e(TAG,"getScores error",e);
			}
		}

		return null;
	}

	public class Score {
		private String username;
		private int score;
		private int points;
		private String uniqueWords;

		protected Score(String username, int score, int points, 
			String uniqueWords) {
			this.username = username;
			this.points = points;
			this.score = score;
			this.uniqueWords = uniqueWords;
		}

		public String getName() {
			return username;
		}

		public int getScore() {
			return score;
		}

		public int getPoints() {
			return points;
		}

		public String getUniqueWords() {
			return uniqueWords;
		}
	}
}
