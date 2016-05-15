package com.essentialtcg.magicthemanaging.data.items;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Shawn on 4/28/2016.
 */
public class DeckItem implements Parcelable {

    @IntDef({FORMAT_STANDARD, FORMAT_MODERN, FORMAT_LEGACY, FORMAT_VINTAGE, FORMAT_COMMANDER,
            FORMAT_FREE_FORM})
    @Retention(RetentionPolicy.CLASS)
    public @interface DeckFormat {}

    public static final int FORMAT_STANDARD = 0;
    public static final int FORMAT_MODERN = 1;
    public static final int FORMAT_LEGACY = 2;
    public static final int FORMAT_VINTAGE = 3;
    public static final int FORMAT_COMMANDER = 4;
    public static final int FORMAT_FREE_FORM = 5;

    private int id;
    private String name;
    private String notes;
    private int format;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @DeckFormat
    public int getFormat() {
        return format;
    }

    public void setFormat(@DeckFormat int format) {
        this.format = format;
    }

    protected DeckItem(Parcel in) {
        id = in.readInt();
        name = in.readString();
        format = in.readInt();
    }

    public static final Creator<DeckItem> CREATOR = new Creator<DeckItem>() {
        @Override
        public DeckItem createFromParcel(Parcel in) {
            return new DeckItem(in);
        }

        @Override
        public DeckItem[] newArray(int size) {
            return new DeckItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(format);
    }

    // TODO: Implement DeckItem

}
