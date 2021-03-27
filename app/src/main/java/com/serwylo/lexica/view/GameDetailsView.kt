package com.serwylo.lexica.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.serwylo.lexica.R
import com.serwylo.lexica.databinding.GameDetailsBinding
import com.serwylo.lexica.db.GameMode
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.lang.LanguageLabel
import kotlin.math.sqrt

@SuppressLint("NewApi") // JVMOverloads will ensure we have non-new-API compatability.
class GameDetailsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private val binding = GameDetailsBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        binding.language.visibility = View.GONE
    }

    fun setLanguage(language: Language?) {
        if (language == null) {
            binding.language.visibility = View.GONE
        } else {
            binding.language.visibility = View.VISIBLE
            binding.language.text = LanguageLabel.getLabel(context, language);
        }
    }

    fun setGameMode(gameMode: GameMode) {

        val boardWidth = sqrt(gameMode.boardSize.toDouble()).toInt()
        binding.statusTime.setText(context.resources.getQuantityString(R.plurals.num_minutes, gameMode.timeLimitSeconds / 60, gameMode.timeLimitSeconds / 60))
        binding.statusBoardSize.setText("${boardWidth}x${boardWidth}")
        binding.statusScoreType.setText(if (gameMode.scoreType == "W") context.getString(R.string.word_length) else context.getString(R.string.letter_points))
        binding.statusMinLength.setText("≥ " + gameMode.minWordLength) // TODO: RTL

        if (gameMode.hintModeColor() || gameMode.hintModeCount()) {
            binding.statusHintMode.visibility = View.VISIBLE
            binding.statusHintMode.setText(context.getString(R.string.pref_hintMode))
        } else {
            binding.statusHintMode.visibility = View.GONE
        }

    }

}