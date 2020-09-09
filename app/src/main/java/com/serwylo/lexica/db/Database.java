package com.serwylo.lexica.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@androidx.room.Database(entities = {GameMode.class}, version = 1)
public abstract class Database extends RoomDatabase {

    public abstract GameModeDao gameModeDao();

    public static volatile Database instance;
    public static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService writeExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static Database get(final Context context) {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(), Database.class, "database")
                            .addCallback(initialiseDataCallback)
                            .build();
                }
            }
        }

        return instance;
    }

    private static RoomDatabase.Callback initialiseDataCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            writeExecutor.execute(() -> {
                GameModeDao dao = instance.gameModeDao();

                dao.insert(GameMode.builder()
                        .label("Marathon")
                        .description("Try to find all words, without any time pressure")
                        .timeLimitSeconds(600)
                        .build());

                dao.insert(GameMode.builder()
                        .label("Sprint")
                        .description("Short game to find as many words as possible")
                        .timeLimitSeconds(180)
                        .build());

                dao.insert(GameMode.builder()
                        .label("Easy")
                        .description("Use hints to help find words")
                        .timeLimitSeconds(180)
                        .build());

            });
        }
    };
}
