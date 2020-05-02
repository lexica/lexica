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

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import mehdi.sakout.fancybuttons.FancyButton;

public class ScoreActivity extends AppCompatActivity {

	@SuppressWarnings("unused")
	private static final String TAG = "ScoreActivity";

	private Adapter adapter;

	private static final int VIEW_TYPE_FOUND_WORDS = 1;
	private static final int VIEW_TYPE_MISSED_WORDS = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.score);

		adapter = new Adapter();

		final RecyclerView recycler = findViewById(R.id.recycler_view);
		recycler.setLayoutManager(new NonScrollingHorizontalLayoutManager(this));
		recycler.setHasFixedSize(true);
		recycler.setAdapter(new Adapter());

		FancyButton found = findViewById(R.id.found_words_button);
		found.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recycler.scrollToPosition(0);
			}
		});

		FancyButton missed = findViewById(R.id.missed_words_button);
		missed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				recycler.scrollToPosition(1);
			}
		});

		FancyButton back = findViewById(R.id.back_button);
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

	}

	private class Adapter extends RecyclerView.Adapter<ViewHolder> {

		@NonNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
			ViewHolder viewHolder = createEmptyView(ScoreActivity.this);
			if (viewType == VIEW_TYPE_FOUND_WORDS) {
				viewHolder.bindFoundWords();
			} else if (viewType == VIEW_TYPE_MISSED_WORDS) {
				viewHolder.bindMissedWords();
			} else {
				throw new IllegalArgumentException("The viewType should be either VIEW_TYPE_FOUND_WORDS or VIEW_TYPE_MISSED_WORDS, but got " + viewType);
			}
			return viewHolder;
		}

		@Override
		public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
			// Don't do anything, because the views are created once during onCreateViewHolder and
			// never updated.
		}

		@Override
		public int getItemCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return VIEW_TYPE_FOUND_WORDS;
			} else if (position == 1) {
				return VIEW_TYPE_MISSED_WORDS;
			}

			throw new IllegalArgumentException("Score activity adapter only support two items, but was asked for item at position " + position);
		}

		private ViewHolder createEmptyView(AppCompatActivity activity) {
			FrameLayout frame = new FrameLayout(activity);
			frame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT));
			return new ViewHolder(activity, frame);
		}

	}

	private static class ViewHolder extends RecyclerView.ViewHolder {

		private final AppCompatActivity activity;
		private final FrameLayout parent;

		public ViewHolder(@NonNull AppCompatActivity activity, @NonNull FrameLayout parent) {
			super(parent);
			this.activity = activity;
			this.parent = parent;
		}

		public void bindFoundWords() {
			new FoundWordsViewBinder(activity, parent);
		}

		public void bindMissedWords() {
			new MissedWordsViewBinder(activity, parent);
		}
	}

	private static class FoundWordsViewBinder {

		private final AppCompatActivity activity;

		public FoundWordsViewBinder(@NonNull AppCompatActivity activity, FrameLayout parent) {
			this.activity = activity;
			View foundWordsView = activity.getLayoutInflater().inflate(R.layout.score_found_words, parent, true);
			TextView textView = foundWordsView.findViewById(R.id.text);
		}
	}

	private static class MissedWordsViewBinder {

		private final AppCompatActivity activity;

		public MissedWordsViewBinder(@NonNull AppCompatActivity activity, FrameLayout parent) {
			this.activity = activity;
			View missedWordsView = activity.getLayoutInflater().inflate(R.layout.score_missed_words, parent, true);
			TextView textView = missedWordsView.findViewById(R.id.text);
		}
	}

	private static class NonScrollingHorizontalLayoutManager extends LinearLayoutManager {
		NonScrollingHorizontalLayoutManager(Context context) {
			super(context, LinearLayoutManager.HORIZONTAL, false);
		}

		@Override
		public boolean canScrollHorizontally() {
			return false;
		}

		@Override
		public boolean canScrollVertically() {
			return false;
		}
	}


}

