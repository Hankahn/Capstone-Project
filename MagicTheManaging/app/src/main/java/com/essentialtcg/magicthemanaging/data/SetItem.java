package com.essentialtcg.magicthemanaging.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Shawn on 4/4/2016.
 */
public class SetItem implements Parcelable {

    private int id;
    private String name;
    private String code;
    private String gathererCode;
    private String oldCode;
    private String magicCardsInfoCode;
    private String releaseDate;
    private String border;
    private String setType;
    private String block;
    private boolean onlineOnly;
    private String booster;
    private boolean firstSet;

    public SetItem() {

    }

    public SetItem(Parcel source) {
        id = source.readInt();
        name = source.readString();
        code = source.readString();
        gathererCode = source.readString();
        oldCode = source.readString();
        magicCardsInfoCode = source.readString();
        releaseDate = source.readString();
        border = source.readString();
        setType = source.readString();
        block = source.readString();
        onlineOnly = source.readByte() != 0;
        booster = source.readString();
        firstSet = source.readByte() != 0;
    }

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getGathererCode() {
        return gathererCode;
    }

    public void setGathererCode(String gathererCode) {
        this.gathererCode = gathererCode;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getMagicCardsInfoCode() {
        return magicCardsInfoCode;
    }

    public void setMagicCardsInfoCode(String magicCardsInfoCode) {
        this.magicCardsInfoCode = magicCardsInfoCode;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getBorder() {
        return border;
    }

    public void setBorder(String border) {
        this.border = border;
    }

    public String getSetType() {
        return setType;
    }

    public void setSetType(String setType) {
        this.setType = setType;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public boolean isOnlineOnly() {
        return onlineOnly;
    }

    public void setOnlineOnly(boolean onlineOnly) {
        this.onlineOnly = onlineOnly;
    }

    public String getBooster() {
        return booster;
    }

    public void setBooster(String booster) {
        this.booster = booster;
    }

    public boolean isFirstSet() {
        return firstSet;
    }

    public void setFirstSet(boolean firstSet) {
        this.firstSet = firstSet;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(code);
        dest.writeString(gathererCode);
        dest.writeString(oldCode);
        dest.writeString(magicCardsInfoCode);
        dest.writeString(releaseDate);
        dest.writeString(border);
        dest.writeString(setType);
        dest.writeString(block);
        dest.writeByte((byte) (onlineOnly == true ? 1 : 0));
        dest.writeString(booster);
        dest.writeByte((byte) (firstSet == true ? 1 : 0));
    }

    public static final Parcelable.Creator<SetItem> CREATOR
            = new Parcelable.ClassLoaderCreator<SetItem>() {

        @Override
        public SetItem createFromParcel(Parcel source) {
            return new SetItem(source);
        }

        @Override
        public SetItem[] newArray(int size) {
            return new SetItem[size];
        }

        @Override
        public SetItem createFromParcel(Parcel source, ClassLoader loader) {
            return new SetItem(source);
        }
    };

}
