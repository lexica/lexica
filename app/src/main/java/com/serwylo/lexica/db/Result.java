package com.serwylo.lexica.db;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
public class Result implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long resultId;

    private long gameModeId;

    @NonNull
    private String langCode;

    private long score;

    private long maxScore;

    private int numWords;

    private int maxNumWords;

    protected Result(Parcel in) {
        resultId = in.readLong();
        gameModeId = in.readLong();
        langCode = in.readString();
        score = in.readLong();
        maxScore = in.readLong();
    }

    public static final Creator<Result> CREATOR = new Creator<Result>() {
        @Override
        public Result createFromParcel(Parcel in) {
            return new Result(in);
        }

        @Override
        public Result[] newArray(int size) {
            return new Result[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(resultId);
        dest.writeLong(gameModeId);
        dest.writeString(langCode);
        dest.writeLong(score);
        dest.writeLong(maxScore);
    }
}
