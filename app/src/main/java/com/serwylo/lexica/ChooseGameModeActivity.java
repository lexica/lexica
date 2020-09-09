package com.serwylo.lexica;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.serwylo.lexica.databinding.ChooseGameModeBinding;
import com.serwylo.lexica.databinding.GameModeItemBinding;
import com.serwylo.lexica.db.Database;
import com.serwylo.lexica.db.GameMode;

import java.util.ArrayList;
import java.util.List;

public class ChooseGameModeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ThemeManager.getInstance().applyTheme(this);

        ChooseGameModeBinding binding = ChooseGameModeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.gameModeList.setAdapter(new Adapter());
        binding.gameModeList.setLayoutManager(new LinearLayoutManager(this));

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            holder.bind(mode, v -> startGame(mode));
        }

        @Override
        public int getItemCount() {
            return gameModes.size();
        }
    }

    private void startGame(GameMode mode) {
        Intent intent = new Intent("com.serwylo.lexica.action.NEW_GAME");
        intent.putExtra("gameMode", mode);
        startActivity(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private GameModeItemBinding binding;

        public ViewHolder(GameModeItemBinding viewBinding) {
            super(viewBinding.getRoot());

            this.binding = viewBinding;
        }

        public void bind(GameMode gameMode, View.OnClickListener listener) {
            binding.label.setText(gameMode.getLabel());
            binding.description.setText(gameMode.getDescription());
            binding.getRoot().setOnClickListener(listener);
        }
    }

}
