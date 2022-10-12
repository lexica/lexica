package com.serwylo.lexica.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.serwylo.lexica.R
import com.serwylo.lexica.ThemeManager
import com.serwylo.lexica.databinding.ShareGameLobbyBinding
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.db.GameModeRepository
import com.serwylo.lexica.game.CharProbGenerator
import com.serwylo.lexica.game.Game
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.share.SharedGameData

class ShareGameLobbyActivity : AppCompatActivity() {

    private lateinit var binding: ShareGameLobbyBinding

    private var isMultiplayer = false

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ThemeManager.getInstance().applyTheme(this)

        binding = ShareGameLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Will re-enable once we have parsed the Uri, ensured an appropriate game mode exists in DB, etc.
        binding.startGame.isEnabled = false

        val uri = intent.data
        if (uri == null) {
            Log.e(TAG, "Expected a URI containing the information required to start a multiplayer/shared game, but received null.")
            Toast.makeText(this, R.string.invalid_multiplayer_link, Toast.LENGTH_LONG).show()
            finish()
            return
        }

        isMultiplayer =
            uri.scheme == "lexica" && uri.host == "multiplayer" ||
            uri.host == "lexica.github.io" && uri.pathSegments.firstOrNull() == "m"

        try {
            val sharedGameData = SharedGameData.parseGame(uri)
            setup(sharedGameData)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Error while parsing incoming game mode: $uri", e)
            Toast.makeText(this, R.string.invalid_multiplayer_link, Toast.LENGTH_LONG).show()
            finish()
            return
        }

    }

    /**
     * Ensure the relevant game mode exists, then setup the view to reflect this.
     */
    private fun setup(data: SharedGameData) {
        val repo = GameModeRepository(applicationContext)

        AsyncTask.execute {
            val gameMode = repo.ensureRulesExist(data)
            runOnUiThread {

                if (!isMultiplayer) {
                    binding.textToJoin.text = getString(R.string.invite__challenge__description, data.numWordsToBeat, data.scoreToBeat)
                    binding.startGame.setText(getString(R.string.multiplayer__start_game))
                    binding.toolbar.title = "Social Challenge"
                }

                binding.gameModeDetails.setLanguage(data.language)
                binding.gameModeDetails.setGameMode(gameMode)
                binding.startGame.isEnabled = true

                val game = Game(this, gameMode, data.language, CharProbGenerator.BoardSeed(data.board.toTypedArray()))
                binding.multiplayerGameNumAvailableWords.text = resources.getQuantityString(R.plurals.num_available_words_in_game, game.maxWordCount, game.maxWordCount)

                binding.startGame.setOnClickListener { startGame(data.language, gameMode, data.board) }
            }
        }
    }

    private fun startGame(language: Language, gameMode: GameMode, board: List<String>) {
        val intent = Intent("com.serwylo.lexica.action.NEW_GAME").apply {
            putExtra("gameMode", gameMode)
            putExtra("lang", language.name)
            putExtra("board", board.toTypedArray())
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
        }

        startActivity(intent)
        finish()
    }

    companion object {
        val TAG = ShareGameLobbyActivity::class.simpleName
    }

}