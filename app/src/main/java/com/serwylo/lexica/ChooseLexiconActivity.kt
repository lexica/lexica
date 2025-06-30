package com.serwylo.lexica

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.serwylo.lexica.databinding.ChooseLexiconBinding
import com.serwylo.lexica.databinding.LexiconListItemBinding
import com.serwylo.lexica.lang.Language
import com.serwylo.lexica.lang.LanguageLabel
import androidx.core.content.edit

class ChooseLexiconActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        ThemeManager.getInstance().applyTheme(this)

        val binding = ChooseLexiconBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.lexiconList.adapter = Adapter()
        binding.lexiconList.layoutManager = LinearLayoutManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    inner class Adapter : RecyclerView.Adapter<ViewHolder>() {

        private val languages: List<Language> = LanguageLabel.getAllLanguagesSorted(this@ChooseLexiconActivity)
        private val selectedLanguage: Language = Util().getSelectedLanguageOrDefault(this@ChooseLexiconActivity)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = LexiconListItemBinding.inflate(this@ChooseLexiconActivity.layoutInflater, parent, false)
            return ViewHolder(this@ChooseLexiconActivity, binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val language = languages[position]
            holder.bind(language, language.name == selectedLanguage.name) { selectLexicon(language) }
        }

        override fun getItemCount(): Int {
            return languages.size
        }

    }

    private fun selectLexicon(language: Language) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit {
                    putString("dict", language.name)
                }

        NavUtils.navigateUpFromSameTask(this)
    }

    class ViewHolder(private val context: Context, private val binding: LexiconListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(language: Language, isSelected: Boolean, listener: View.OnClickListener) {

            binding.root.isSelected = isSelected
            binding.language.text = LanguageLabel.getLabel(context, language)

            val description = LanguageLabel.getDescription(context, language)
            binding.lexiconDescription.text = description

            binding.lexiconDescription.visibility = if (description != null) View.VISIBLE else View.GONE
            binding.lexiconIsInBeta.visibility = if (language.isBeta) View.VISIBLE else View.GONE

            binding.root.setOnClickListener(listener)

        }

    }
}