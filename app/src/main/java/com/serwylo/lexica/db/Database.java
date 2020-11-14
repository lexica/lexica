package com.serwylo.lexica.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.serwylo.lexica.db.migration.MigrateHighScoresFromPreferences;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {GameMode.class, Result.class, SelectedWord.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract GameModeDao gameModeDao();
    public abstract ResultDao resultDao();
    public abstract SelectedWordDao selectedWordDao();

    public static volatile Database instance;
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService writeExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static Database get(final Context context) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "lexica")
                            .build();
                }
            }
        }

        return instance;
    }

}
