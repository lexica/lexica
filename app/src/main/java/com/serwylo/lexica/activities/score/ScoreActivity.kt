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

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.serwylo.lexica.GameSaverTransient
import com.serwylo.lexica.R
import com.serwylo.lexica.ThemeManager
import com.serwylo.lexica.databinding.ScoreBinding
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.share.SharedGameData

class ScoreActivity : AppCompatActivity() {

    private lateinit var binding: ScoreBinding
    private lateinit var game: Game

    private var buttonBackgroundColorSelected = 0
    private var buttonBackgroundColor = 0

    private var isOnlyFoundWords = false

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

        isOnlyFoundWords = intent.getBooleanExtra(ONLY_FOUND_WORDS, false)

        binding = ScoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val game = initialiseGame(savedInstanceState)
        this.game = game

        initialiseView(game)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
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

        if (isOnlyFoundWords) {
            binding.foundWordsButton.visibility = View.GONE
            binding.missedWordsButton.visibility = View.GONE
            binding.toolbar.title = getString(R.string.found_words)
        }

        binding.foundWordsButton.setBackgroundColor(buttonBackgroundColorSelected)

        binding.foundWordsButton.setOnClickListener {
            binding.recyclerView.scrollToPosition(0)
            binding.foundWordsButton.setBackgroundColor(buttonBackgroundColorSelected)

            binding.missedWordsButton.setBackgroundColor(buttonBackgroundColor)
            binding.multiplayerNextRound.setBackgroundColor(buttonBackgroundColor)
        }

        binding.missedWordsButton.setOnClickListener {
            binding.recyclerView.scrollToPosition(1)
            binding.missedWordsButton.setBackgroundColor(buttonBackgroundColorSelected)

            binding.foundWordsButton.setBackgroundColor(buttonBackgroundColor)
            binding.multiplayerNextRound.setBackgroundColor(buttonBackgroundColor)
        }

        binding.multiplayerNextRound.setOnClickListener {
            binding.recyclerView.scrollToPosition(2)
            binding.multiplayerNextRound.setBackgroundColor(buttonBackgroundColorSelected)

            binding.foundWordsButton.setBackgroundColor(buttonBackgroundColor)
            binding.missedWordsButton.setBackgroundColor(buttonBackgroundColor)
        }
    }

    private fun share() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"

            val sharedGameData = SharedGameData(game.board.letters.toList(), game.language, game.gameMode, SharedGameData.Type.SHARE, game.wordCount, game.score)
            val uri = sharedGameData.serialize(SharedGameData.Platform.ANDROID)
            val webUri = sharedGameData.serialize(SharedGameData.Platform.WEB)
            val text = """
                ${getString(R.string.invite__challenge__description, game.wordCount, game.score)}
                
                $uri
                
                ${getString(R.string.invite__dont_have_lexica_installed)}
                
                ${getString(R.string.invite__dont_have_lexica_installed_web)}
                
                $webUri
            """.trimIndent()
            putExtra(Intent.EXTRA_TEXT, text)
        }

        startActivity(Intent.createChooser(sendIntent, getString(R.string.send_challenge_invite_to)))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (!isOnlyFoundWords) {
            menuInflater.inflate(R.menu.score_menu, binding.toolbar.menu);
            return true
        }

        return false;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> share()
        }
        return true
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