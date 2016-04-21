package com.essentialtcg.magicthemanaging.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import com.essentialtcg.magicthemanaging.utils.SetArrayList;

/**
 * Created by Shawn on 3/26/2016.
 */
public class CardSearchParameters implements Parcelable {

    private String nameFilter;
    private String textFilter;
    private SetArrayList setFilter;
    private ArrayList<String> formatFilter;
    private ArrayList<String> colorFilter;
    private ArrayList<String> typeFilter;
    private ArrayList<String> rarityFilter;
    private int cmcFilter;
    private int powerFilter;
    private int toughnessFilter;

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public String getTextFilter() {
        return textFilter;
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    public SetArrayList getSetFilter() {
        return setFilter;
    }

    public void setSetFilter(SetArrayList setFilter) {
        this.setFilter = setFilter;
    }

    public ArrayList<String> getFormatFilter() {
        return formatFilter;
    }

    public void setFormatFilter(ArrayList<String> formatFilter) {
        this.formatFilter = formatFilter;
    }

    public ArrayList<String> getColorFilter() {
        return colorFilter;
    }

    public void setColorFilter(ArrayList<String> colorFilter) {
        this.colorFilter = colorFilter;
    }

    public ArrayList<String> getTypeFilter() {
        return typeFilter;
    }

    public void setTypeFilter(ArrayList<String> typeFilter) {
        this.typeFilter = typeFilter;
    }

    public ArrayList<String> getRarityFilter() {
        return rarityFilter;
    }

    public void setRarityFilter(ArrayList<String> rarityFilter) {
        this.rarityFilter = rarityFilter;
    }

    public int getCmcFilter() {
        return cmcFilter;
    }

    public void setCmcFilter(int cmcFilter) {
        this.cmcFilter = cmcFilter;
    }

    public int getPowerFilter() {
        return powerFilter;
    }

    public void setPowerFilter(int powerFilter) {
        this.powerFilter = powerFilter;
    }

    public int getToughnessFilter() {
        return toughnessFilter;
    }

    public void setToughnessFilter(int toughnessFilter) {
        this.toughnessFilter = toughnessFilter;
    }

    public CardSearchParameters() {

    }

    public CardSearchParameters(Parcel source) {
        nameFilter = source.readString();
        setFilter = new SetArrayList();
        source.readList(setFilter, SetItem.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameFilter);
        dest.writeList(setFilter);
    }

    public static final Parcelable.Creator<CardSearchParameters> CREATOR
        = new Parcelable.ClassLoaderCreator<CardSearchParameters>() {

        @Override
        public CardSearchParameters createFromParcel(Parcel source) {
            return new CardSearchParameters(source);
        }

        @Override
        public CardSearchParameters[] newArray(int size) {
            return new CardSearchParameters[size];
        }

        @Override
        public CardSearchParameters createFromParcel(Parcel source, ClassLoader loader) {
            return new CardSearchParameters(source);
        }

    };

}
