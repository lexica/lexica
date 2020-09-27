package com.serwylo.lexica.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Entity
@Data
@Builder
public class GameMode implements Parcelable {

    public static final String HINT_MODE = "hintMode";
    public static final String SCORE_TYPE = "scoreType";
    public static final String SCORE_WORDS = "W";
    public static final String SCORE_LETTERS = "L";

    @PrimaryKey(autoGenerate = true)
    private long gameModeId;

    @NonNull
    private String label;

    @NonNull
    private String description;

    /**
     * 16, 25, 36.
     */
    private int boardSize;

    private int timeLimitSeconds;

    /**
     * Between 3 and 9.
     */
    private int minWordLength;

    @NonNull
    private String scoreType;

    @NonNull
    private String hintMode;

    private boolean isCustom;

    public GameMode(@NonNull String label, @NonNull String description, int boardSize, int timeLimitSeconds, int minWordLength, @NonNull String scoreType, @NonNull String hintMode, boolean isCustom) {
        this.label = label;
        this.description = description;
        this.boardSize = boardSize;
        this.timeLimitSeconds = timeLimitSeconds;
        this.minWordLength = minWordLength;
        this.scoreType = scoreType;
        this.hintMode = hintMode;
        this.isCustom = isCustom;
    }

    protected GameMode(Parcel in) {
        gameModeId = in.readLong();
        label = in.readString();
        description = in.readString();
        boardSize = in.readInt();
        timeLimitSeconds = in.readInt();
        minWordLength = in.readInt();
        scoreType = in.readString();
        hintMode = in.readString();
        isCustom = in.readInt() == 1;
    }

    public boolean hintModeCount() {
        return hintMode.equals("tile_count") || hintMode.equals("hint_both");
    }

    public boolean hintModeColor() {
        return hintMode.equals("hint_colour") || hintMode.equals("hint_both");
    }

    public static final Creator<GameMode> CREATOR = new Creator<GameMode>() {
        @Override
        public GameMode createFromParcel(Parcel in) {
            return new GameMode(in);
        }

        @Override
        public GameMode[] newArray(int size) {
            return new GameMode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(gameModeId);
        dest.writeString(label);
        dest.writeString(description);
        dest.writeInt(boardSize);
        dest.writeInt(timeLimitSeconds);
        dest.writeInt(minWordLength);
        dest.writeString(scoreType);
        dest.writeString(hintMode);
        dest.writeInt(isCustom ? 1 : 0);
    }
}
