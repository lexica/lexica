package com.serwylo.lexica.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Entity
@Builder
public class GameMode implements Parcelable {

    @Getter
    @Setter
    @PrimaryKey(autoGenerate = true)
    private int id;

    @Getter
    @Setter
    @NonNull
    private String label;

    @Getter
    @Setter
    @NonNull
    private String description;

    @Getter
    @Setter
    private int timeLimitSeconds;

    public GameMode(@NonNull String label, @NonNull String description, int timeLimitSeconds) {
        this.label = label;
        this.description = description;
        this.timeLimitSeconds = timeLimitSeconds;
    }

    protected GameMode(Parcel in) {
        id = in.readInt();
        label = in.readString();
        description = in.readString();
        timeLimitSeconds = in.readInt();
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
        dest.writeInt(id);
        dest.writeString(label);
        dest.writeString(description);
        dest.writeInt(timeLimitSeconds);
    }
}
