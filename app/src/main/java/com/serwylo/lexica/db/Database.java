package com.serwylo.lexica.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.serwylo.lexica.db.converters.GameModeTypeConverter;
import com.serwylo.lexica.db.migration.Migration_1_2_RecordWhetherSelectedWordsAreWords;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {GameMode.class, Result.class, SelectedWord.class}, version = 2)
@TypeConverters(GameModeTypeConverter.class)
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
                            .addMigrations(new Migration_1_2_RecordWhetherSelectedWordsAreWords())
                            .build();
                }
            }
        }

        return instance;
    }

}
