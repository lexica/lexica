package com.serwylo.lexica.activities.score;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.serwylo.lexica.lang.Language;

public class WordDefiner {

    @NonNull
    private Context context;

    private Language language;

    private String definitionProvider;

    WordDefiner(@NonNull Context context, @NonNull Language language) {
        this.context = context;
        this.language = language;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        // "duckduckgo" is a legacy value which has now been re-purposed to mean "Online source".
        definitionProvider = prefs.getString("definitionProvider", "duckduckgo");
    }

    public void define(@NonNull String word) {
        Intent intent;
        switch (definitionProvider) {
            case "aard2":
                intent = new Intent("aard2.lookup");
                intent.putExtra(Intent.EXTRA_TEXT, word);
                break;
            case "quickdic":
                intent = new Intent("com.hughes.action.ACTION_SEARCH_DICT");
                intent.putExtra(SearchManager.QUERY, word);
                break;
            default:
                intent = null;
        }

        if (intent != null && intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            searchWordOnline(word);
        }
    }

    private void searchWordOnline(@NonNull String word) {
        String url = language.getDefinitionUrl();
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri u = Uri.parse(String.format(url, word));
        intent.setData(u);
        context.startActivity(intent);
    }
}


