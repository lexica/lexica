package com.serwylo.lexica;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.databinding.ChooseLexiconBinding;
import com.serwylo.lexica.databinding.LexiconListItemBinding;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.lang.Language;
import com.serwylo.lexica.lang.LanguageLabel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseLexiconActivity extends AppCompatActivity {

    private ChooseLexiconBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.getInstance().applyTheme(this);

        binding = ChooseLexiconBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.lexiconList.setAdapter(new Adapter());
        binding.lexiconList.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Language> languages;

        private Language selectedLanguage;

        public Adapter() {
            Map<String, Language> languagesWithLabels = getLanguagesWithLabels();

            List<String> labels = new ArrayList<>(languagesWithLabels.size());
            labels.addAll(languagesWithLabels.keySet());
            Collections.sort(labels);

            languages = new ArrayList<>(languagesWithLabels.size());
            for (String label : labels) {
                languages.add(languagesWithLabels.get(label));
            }

            String languageCode = new Util().getLexiconString(ChooseLexiconActivity.this);
            selectedLanguage = Language.fromOrNull(languageCode);
        }

        private Map<String, Language> getLanguagesWithLabels() {
            Map<String, Language> languagesWithLabels = new HashMap<>();
            for (Language language : Language.getAllLanguages().values()) {
                String label = LanguageLabel.getLabel(ChooseLexiconActivity.this, language);
                languagesWithLabels.put(label, language);
            }

            return languagesWithLabels;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LexiconListItemBinding binding = LexiconListItemBinding.inflate(ChooseLexiconActivity.this.getLayoutInflater(), parent, false);
            return new ViewHolder(ChooseLexiconActivity.this, binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Language language = languages.get(position);
            holder.bind(language, language.getName().equals(selectedLanguage.getName()), v -> selectLexicon(language));
        }

        @Override
        public int getItemCount() {
            return languages.size();
        }
    }

    private void selectLexicon(Language language) {
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("dict", language.getName())
                .apply();

        NavUtils.navigateUpFromSameTask(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LexiconListItemBinding binding;
        private Context context;

        public ViewHolder(Context context, LexiconListItemBinding viewBinding) {
            super(viewBinding.getRoot());

            this.binding = viewBinding;
            this.context = context;
        }

        public void bind(Language language, boolean isSelected, View.OnClickListener listener) {
            binding.getRoot().setSelected(isSelected);

            String label = LanguageLabel.getLabel(context, language);

            binding.language.setText(label);
            binding.description.setVisibility(language.isBeta() ? View.VISIBLE : View.GONE);

            binding.getRoot().setOnClickListener(listener);
        }
    }

}
