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
public class SelectedWord implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long foundWordId;

    private long resultId;

    @NonNull
    private String word;

    private int points;

    public SelectedWord(long resultId, @NonNull String word, int points) {
        this.resultId = resultId;
        this.word = word;
        this.points = points;
    }

    protected SelectedWord(Parcel in) {
        foundWordId = in.readLong();
        resultId = in.readLong();
        word = in.readString();
        points = in.readInt();
    }

    public static final Creator<SelectedWord> CREATOR = new Creator<SelectedWord>() {
        @Override
        public SelectedWord createFromParcel(Parcel in) {
            return new SelectedWord(in);
        }

        @Override
        public SelectedWord[] newArray(int size) {
            return new SelectedWord[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(foundWordId);
        dest.writeLong(resultId);
        dest.writeString(word);
        dest.writeInt(points);
    }
}
