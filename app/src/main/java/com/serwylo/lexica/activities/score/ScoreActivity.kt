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
package com.serwylo.lexica.activities.score

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.serwylo.lexica.GameSaverTransient
import com.serwylo.lexica.R
import com.serwylo.lexica.ThemeManager
import com.serwylo.lexica.databinding.ScoreBinding
import com.serwylo.lexica.game.Game
import kotlinx.android.synthetic.main.score_view.view.*
import mehdi.sakout.fancybuttons.FancyButton

class ScoreActivity : AppCompatActivity() {

    private lateinit var binding: ScoreBinding
    private lateinit var game: Game

    private var buttonBackgroundColorSelected = 0
    private var buttonBackgroundColor = 0

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeManager.getInstance().applyTheme(this)

        // Surely there is a better way to get theme attributes then this. Unfortunately we can't
        // make use of the ThemeProperties helper class in Lexica because that is only useful
        // to Views not Activities, due to the way in which Views receive all of their attributes
        // when constructed.
        val themes = theme
        val themeValues = TypedValue()
        themes.resolveAttribute(
            R.attr.home__secondary_button_background_selected,
            themeValues,
            true
        )

        buttonBackgroundColorSelected = themeValues.data

        themes.resolveAttribute(R.attr.home__secondary_button_background, themeValues, true)

        buttonBackgroundColor = themeValues.data

        binding = ScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val game = initialiseGame(savedInstanceState)
        this.game = game

        initialiseView(game)
    }

    private fun initialiseGame(savedInstanceState: Bundle?): Game {
        return if (savedInstanceState != null) {
            Game(this, GameSaverTransient(savedInstanceState))
        } else {
            val intent = intent
            val bun = intent.extras
            Game(this, GameSaverTransient(bun))
        }
    }

    private fun initialiseView(game: Game) {
        binding.recyclerView.layoutManager = NonScrollingHorizontalLayoutManager(this)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = ScoreTabAdapter(this, game)

        val onlyFoundWords = intent.getBooleanExtra(ONLY_FOUND_WORDS, false)
        if (onlyFoundWords) {
            binding.missedWordsButton.visibility = View.GONE
        }

        binding.foundWordsButton.setBackgroundColor(buttonBackgroundColorSelected)
        binding.missedWordsButton.setBackgroundColor(buttonBackgroundColorSelected)
        binding.backButton.setBackgroundColor(buttonBackgroundColorSelected)

        binding.foundWordsButton.setOnClickListener {
            binding.recyclerView.scrollToPosition(0)
            binding.foundWordsButton.setBackgroundColor(buttonBackgroundColorSelected)
            binding.missedWordsButton.setBackgroundColor(buttonBackgroundColor)
        }

        binding.missedWordsButton.setOnClickListener {
            binding.recyclerView.scrollToPosition(1)
            binding.foundWordsButton.setBackgroundColor(buttonBackgroundColor)
            binding.missedWordsButton.setBackgroundColor(buttonBackgroundColorSelected)
        }

        binding.backButton.setOnClickListener { finish() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        game.save(GameSaverTransient(outState))
    }

    companion object {

        private const val TAG = "ScoreActivity"

        const val ONLY_FOUND_WORDS = "ONLY_FOUND_WORDS"

    }
}