package com.serwylo.lexica;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceManager;

import com.serwylo.lexica.databinding.SettingsBinding;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // Not sure why, but this has to come before super.onCreate(), or else the light theme
        // will have a dark background, making the text very hard to read. This seems to fix it,
        // though I have not investigated why. Source: https://stackoverflow.com/a/15657428.
        ThemeManager.getInstance().applyTheme(this);

        super.onCreate(savedInstanceState);

        SettingsBinding binding = SettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> NavUtils.navigateUpFromSameTask(SettingsActivity.this));

        getSupportFragmentManager().beginTransaction().replace(binding.settingsWrapper.getId(), new SettingsFragment()).commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (ThemeManager.PREFERENCE_NAME.equals(key)) {
            ThemeManager themeManager = ThemeManager.getInstance();
            themeManager.rememberTheme(this);
            themeManager.applyTheme(this);
            themeManager.forceRestartActivityToRetheme(this);
        }
    }
}
