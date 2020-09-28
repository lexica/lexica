package com.serwylo.lexica;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.databinding.ChooseGameModeBinding;
import com.serwylo.lexica.databinding.GameModeItemBinding;
import com.serwylo.lexica.db.Database;
import com.serwylo.lexica.db.GameMode;
import com.serwylo.lexica.db.GameModeRepository;

import java.util.ArrayList;
import java.util.List;

public class ChooseGameModeActivity extends AppCompatActivity {

    private ChooseGameModeBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.getInstance().applyTheme(this);

        binding = ChooseGameModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.gameModeList.setAdapter(new Adapter());
        binding.gameModeList.setLayoutManager(new LinearLayoutManager(this));
        binding.gameModeList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            addGameMode();
        }
        return true;
    }

    private void addGameMode() {
        startActivity(new Intent(this, NewGameModeActivity.class));
    }

    public class Adapter extends RecyclerView.Adapter<ViewHolder> {

        private List<GameMode> gameModes = new ArrayList<>();

        public Adapter() {
            Database.get(getApplicationContext())
                    .gameModeDao()
                    .getAllGameModes()
                    .observe(ChooseGameModeActivity.this, gameModes -> {
                        Adapter.this.gameModes = gameModes;
                        notifyDataSetChanged();
                    });
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            GameModeItemBinding binding = GameModeItemBinding.inflate(ChooseGameModeActivity.this.getLayoutInflater(), parent, false);
            return new ViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GameMode mode = gameModes.get(position);
            holder.bind(mode, v -> selectGameMode(mode));
        }

        @Override
        public int getItemCount() {
            return gameModes.size();
        }
    }

    private void selectGameMode(GameMode mode) {
        new GameModeRepository(getApplication()).saveCurrentGameMode(mode);
        NavUtils.navigateUpFromSameTask(this);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private GameModeItemBinding binding;

        public ViewHolder(GameModeItemBinding viewBinding) {
            super(viewBinding.getRoot());

            this.binding = viewBinding;
        }

        public void bind(GameMode gameMode, View.OnClickListener listener) {
            binding.label.setText(gameMode.getLabel());
            binding.description.setText(gameMode.isCustom() ? "Custom game mode" : gameMode.getDescription());

            binding.statusTime.setText((gameMode.getTimeLimitSeconds() / 60) + " mins");
            binding.statusBoardSize.setText((int)Math.sqrt(gameMode.getBoardSize()) + "x" + (int)Math.sqrt(gameMode.getBoardSize()));
            binding.statusScoreType.setText(gameMode.getScoreType().equals("W") ? "Letter" : "Length");
            binding.statusMinLength.setText("≥ " + gameMode.getMinWordLength());

            if (gameMode.hintModeColor() || gameMode.hintModeCount()) {
                binding.statusHintMode.setVisibility(View.VISIBLE);
                binding.statusHintMode.setText("Hints");
            } else {
                binding.statusHintMode.setVisibility(View.GONE);
            }

            binding.getRoot().setOnClickListener(listener);
        }
    }

}
