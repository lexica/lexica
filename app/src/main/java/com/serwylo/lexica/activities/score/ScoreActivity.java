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

package com.serwylo.lexica.activities.score;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.GameSaverTransient;
import com.serwylo.lexica.R;
import com.serwylo.lexica.ThemeManager;
import com.serwylo.lexica.game.Game;

import mehdi.sakout.fancybuttons.FancyButton;

public class ScoreActivity extends AppCompatActivity {

    @SuppressWarnings("unused")
    private static final String TAG = "ScoreActivity";

    private Game game;

    private int buttonBackgroundColorSelected;
    private int buttonBackgroundColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.getInstance().applyTheme(this);

        // Surely there is a better way to get theme attributes then this. Unfortunately we can't
        // make use of the ThemeProperties helper class in Lexica because that is only useful
        // to Views not Activities, due to the way in which Views receive all of their attributes
        // when constructed.
        Resources.Theme themes = getTheme();
        TypedValue themeValues = new TypedValue();
        themes.resolveAttribute(R.attr.home__secondary_button_background_selected, themeValues, true);
        buttonBackgroundColorSelected = themeValues.data;
        themes.resolveAttribute(R.attr.home__secondary_button_background, themeValues, true);
        buttonBackgroundColor = themeValues.data;

        setContentView(R.layout.score);

        Game game = initialiseGame(savedInstanceState);
        this.game = game;
        initialiseView(game);
    }

    @NonNull
    private Game initialiseGame(Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            return new Game(this, new GameSaverTransient(savedInstanceState));

        } else {
            Intent intent = getIntent();
            Bundle bun = intent.getExtras();
            return new Game(this, new GameSaverTransient(bun));
        }

    }

    private void initialiseView(@NonNull Game game) {

        final RecyclerView recycler = findViewById(R.id.recycler_view);
        recycler.setLayoutManager(new NonScrollingHorizontalLayoutManager(this));
        recycler.setHasFixedSize(true);
        recycler.setAdapter(new ScoreTabAdapter(this, game));

        final FancyButton found = findViewById(R.id.found_words_button);
        final FancyButton missed = findViewById(R.id.missed_words_button);

        found.setBackgroundColor(buttonBackgroundColorSelected);

        found.setOnClickListener(view -> {
            recycler.scrollToPosition(0);
            found.setBackgroundColor(buttonBackgroundColorSelected);
            missed.setBackgroundColor(buttonBackgroundColor);
        });

        missed.setOnClickListener(view -> {
            recycler.scrollToPosition(1);
            found.setBackgroundColor(buttonBackgroundColor);
            missed.setBackgroundColor(buttonBackgroundColorSelected);
        });

        FancyButton back = findViewById(R.id.back_button);
        back.setOnClickListener(view -> finish());

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        game.save(new GameSaverTransient(outState));
    }

}

