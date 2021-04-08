package com.serwylo.lexica.activities

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.serwylo.lexica.ThemeManager
import com.serwylo.lexica.databinding.MultiplayerLobbyBinding
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.db.GameModeRepository
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.share.SharedGameData

class MultiplayerLobbyActivity : AppCompatActivity() {

    private lateinit var binding: MultiplayerLobbyBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ThemeManager.getInstance().applyTheme(this)

        binding = MultiplayerLobbyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Will re-enable once we have parsed the Uri, ensured an appropriate game mode exists in DB, etc.
        binding.startGame.isEnabled = false

        val uri = intent.data
        if (uri == null) {
            Log.e(TAG, "Expected a URI containing the information required to start a multiplayer game, but received null.")
            Toast.makeText(this, "Received incorrect multiplayer link, cannot join game.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        try {
            val sharedGameData = SharedGameData.parseGame(uri)
            setup(sharedGameData)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "Error while parsing incoming game mode: $uri", e)
            Toast.makeText(
                this,
                "Received incorrect multiplayer link, cannot join game.",
                Toast.LENGTH_LONG
            ).show()
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
                binding.gameModeDetails.setLanguage(data.language)
                binding.gameModeDetails.setGameMode(gameMode)
                binding.startGame.isEnabled = true

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
        val TAG = MultiplayerLobbyActivity::class.simpleName
    }

}