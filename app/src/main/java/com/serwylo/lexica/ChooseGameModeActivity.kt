package com.serwylo.lexica

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.serwylo.lexica.databinding.ChooseGameModeBinding
import com.serwylo.lexica.databinding.GameModeItemBinding
import com.serwylo.lexica.db.Database
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.db.GameModeRepository
import kotlin.math.sqrt

class ChooseGameModeActivity : AppCompatActivity() {

    private lateinit var binding: ChooseGameModeBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ThemeManager.getInstance().applyTheme(this)

        binding = ChooseGameModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.gameModeList.layoutManager = LinearLayoutManager(this)
        binding.gameModeList.setHasFixedSize(false)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        AsyncTask.execute {
            val repo = GameModeRepository(applicationContext)
            val current = repo.loadCurrentGameMode() ?: error("No game mode present, should have run database migrations prior to navigating to choose game mode activity.")
            runOnUiThread {
                binding.gameModeList.adapter = Adapter(current)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.choose_game_mode_menu, binding.toolbar.menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.new_game_mode) {
            addGameMode()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addGameMode() {
        startActivity(Intent(this, NewGameModeActivity::class.java))
    }

    inner class Adapter(selectedItem: GameMode) : RecyclerView.Adapter<ViewHolder>() {

        private var gameModes: MutableList<GameMode> = ArrayList()
        private var selectedItem: GameMode

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = GameModeItemBinding.inflate(this@ChooseGameModeActivity.layoutInflater, parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val mode = gameModes[position]
            holder.bind(
                    mode,
                    selectedItem.gameModeId == mode.gameModeId,
                    clickListener = { selectGameMode(mode) },
                    longClickListener = { promptToDeleteGameMode(mode) },
            )
        }

        override fun getItemCount(): Int {
            return gameModes.size
        }

        init {
            Database.get(applicationContext)
                    .gameModeDao()
                    .getAllGameModes()
                    .observe(this@ChooseGameModeActivity, { gameModes ->
                        this@Adapter.gameModes = gameModes.toMutableList()
                        notifyDataSetChanged()
                    })
            this.selectedItem = selectedItem
        }

        private fun selectGameMode(mode: GameMode) {
            val repo = GameModeRepository(applicationContext)
            repo.saveCurrentGameMode(mode)
            this.selectedItem = mode
            notifyDataSetChanged() // TODO: Just notify the previous and newly selected items.
        }

        private fun promptToDeleteGameMode(mode: GameMode): Boolean {
            if (mode.type != GameMode.Type.CUSTOM) {
                return false
            }

            AlertDialog.Builder(this@ChooseGameModeActivity)
                    .setTitle(R.string.button_delete)
                    .setMessage(getString(R.string.delete_custom_game_mode, mode.customLabel))
                    .setPositiveButton(R.string.button_delete) { _, _ -> deleteGameMode(mode) }
                    .setNeutralButton(R.string.button_cancel) { _, _ -> }
                    .show()

            return true
        }

        private fun deleteGameMode(mode: GameMode) {
            val repo = GameModeRepository(applicationContext)
            gameModes.remove(mode)
            if (this.selectedItem == mode) {
                this.selectedItem = gameModes.first()
                repo.saveCurrentGameMode(this.selectedItem)
            }

            // Do this ony after changing the current game. Otherwise there is a risk that we delete,
            // then crash, then the current game mode is null.
            Database.writeExecutor.execute {
                repo.deleteGameMode(mode)
                // Don't wait for this, it can be run on a separate thread because we've already
                // updated the underlying data structure of the adapter (i.e. "gameModes" list).
                // and we don't really expect it to take all that long. If someone manages to refresh
                // the activity quickly it may get into a strange state, but nothing to bad.
            }

            // Can't be on the database thread.
            notifyDataSetChanged() // TODO: Just notify the removed, previous and newly selected items.
        }
    }

    inner class ViewHolder(private val binding: GameModeItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(gameMode: GameMode, isSelected: Boolean, clickListener: View.OnClickListener, longClickListener: View.OnLongClickListener) {

            val context = this@ChooseGameModeActivity

            binding.root.isLongClickable = true
            binding.root.setOnLongClickListener(longClickListener)
            binding.root.setOnClickListener(clickListener)
            binding.root.isSelected = isSelected
            binding.label.text = gameMode.label(context)
            binding.description.text = gameMode.description(context)

            if (isSelected) {
                binding.gameDetails.visibility = View.VISIBLE
                binding.gameDetails.setGameMode(gameMode)
            } else {
                binding.gameDetails.visibility = View.GONE
            }

        }
    }
}