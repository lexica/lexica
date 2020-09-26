package com.serwylo.lexica;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.serwylo.lexica.databinding.NewGameModeBinding;
import com.serwylo.lexica.db.Database;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.db.GameModeDao;

public class NewGameModeActivity extends AppCompatActivity {

    private NewGameModeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // Not sure why, but this has to come before super.onCreate(), or else the light theme
        // will have a dark background, making the text very hard to read. This seems to fix it,
        // though I have not investigated why. Source: https://stackoverflow.com/a/15657428.
        ThemeManager.getInstance().applyTheme(this);

        super.onCreate(savedInstanceState);

        binding = NewGameModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        binding.toolbar.setNavigationOnClickListener(v -> NavUtils.navigateUpFromSameTask(NewGameModeActivity.this));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.choose_game_mode_menu, binding.toolbar.getMenu());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_game_mode) {
            createNewGameMode();
        }
        return true;
    }

    private void createNewGameMode() {
        int boardSize = selectedBoardSize();
        if (boardSize == -1) {
            showError();
            return;
        }

        int minWordLength = selectedMinWordLength();
        if (minWordLength == -1) {
            showError();
            return;
        }

        String scoreType = selectedScoreType();
        if (scoreType == null) {
            showError();
            return;
        }

        String label = selectedLabel();
        if (label.length() == 0) {
            showError();
            return;
        }

        GameMode gameMode = GameMode.builder()
                .boardSize(boardSize)
                .minWordLength(minWordLength)
                .scoreType(scoreType)
                .hintMode(selectedHintMode())
                .timeLimitSeconds(selectedTimeLimitInSeconds())
                .label(selectedLabel())
                .description("")
                .isCustom(true)
                .build();

        Database.writeExecutor.execute(() -> {

            GameModeDao dao = Database.get(this).gameModeDao();
            dao.insert(gameMode);
            onSaveComplete(gameMode);

        });
    }

    private void showError() {
        Toast.makeText(this, "All fields required.", Toast.LENGTH_SHORT).show();
    }

    private void onSaveComplete(GameMode gameMode) {
        finish();
    }

    @NonNull
    private String selectedLabel() {
        Editable label = binding.label.getText();
        return label == null ? "" : label.toString();
    }

    private int selectedTimeLimitInSeconds() {
        Editable timeString = binding.time.getText();
        if (timeString == null) {
            return -1;
        }

        try {

            int value = Integer.parseInt(timeString.toString());

            if (value > 0) {
                return value * 60;
            }

            return -1;

        } catch (NullPointerException | NumberFormatException e) {
            return -1;
        }
    }

    private int selectedBoardSize() {
        if (binding.boardSize4x4.isChecked()) {
            return 4 * 4;
        } else if (binding.boardSize5x5.isChecked()) {
            return 5 * 5;
        } else if (binding.boardSize6x6.isChecked()) {
            return 6 * 6;
        } else {
            return -1;
        }
    }

    private int selectedMinWordLength() {
        if (binding.minWordLength3.isChecked()) {
            return 3;
        } else if (binding.minWordLength4.isChecked()) {
            return 4;
        } else if (binding.minWordLength5.isChecked()) {
            return 5;
        } else if (binding.minWordLength6.isChecked()) {
            return 6;
        } else {
            return -1;
        }
    }

    @Nullable
    private String selectedScoreType() {
        if (binding.scoreTypeLetterPoints.isChecked()) {
            return GameMode.SCORE_LETTERS;
        } else if (binding.scoreTypeWordLength.isChecked()) {
            return GameMode.SCORE_WORDS;
        }

        return null;
    }

    @NonNull
    private String selectedHintMode() {
        boolean count = binding.hintTileCount.isChecked();
        boolean colour = binding.hintColour.isChecked();

        if (count && colour) {
            return "hint_both";
        } else if (colour) {
            return "hint_colour";
        } else if (count) {
            return "tile_count";
        }

        return "hint_none";
    }

}
