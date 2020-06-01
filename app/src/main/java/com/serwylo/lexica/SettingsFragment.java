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

package com.serwylo.lexica;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.serwylo.lexica.activities.score.ScoreActivity;
import com.serwylo.lexica.lang.Language;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
		addPreferencesFromResource(R.xml.preferences);
        getResetCoresPreference().setOnPreferenceClickListener(preference -> promptThenResetScores());
        highlightBetaLanguages();
        setUsedLexicon();
    }

    private void highlightBetaLanguages() {
        ListPreference pref = getLexiconPreferences();
        CharSequence[] entries = pref.getEntries();
        CharSequence[] values = pref.getEntryValues();
        for (int i = 0; i < entries.length; i ++) {
            Language language = Language.fromOrNull(values[i].toString());
            if (language != null) {
                if (language.isBeta()) {
                    entries[i] = getString(R.string.pref_dict_beta, entries[i]);
                }
            }
        }
        pref.setEntries(entries);
    }

    @NonNull
    private Preference getResetCoresPreference() {
        Preference preference = findPreference("resetScores");

        if (preference == null) {
            throw new IllegalArgumentException("Could not find reset scores preference.");
        }

        return preference;
    }

    @NonNull
    private ListPreference getLexiconPreferences() {
        ListPreference pref = (ListPreference) findPreference("dict");

        if (pref == null) {
            throw new IllegalArgumentException("Could not find lexicon/dictionary preference.");
        }

        return pref;
    }

    private void setUsedLexicon() {
        getLexiconPreferences().setValue(new Util().getLexiconString(getContext()));
    }

    public boolean promptThenResetScores() {
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.pref_resetScores))
                .setMessage(getString(R.string.reset_scores_prompt))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        clearHighScores();
                    }
                })
                .create().show();

        return true;
    }

    private void clearHighScores() {
        getContext().getSharedPreferences(ScoreActivity.SCORE_PREF_FILE, Context.MODE_PRIVATE).edit().clear().apply();
        Toast.makeText(getContext(), R.string.high_scores_reset, Toast.LENGTH_SHORT).show();
    }
}
