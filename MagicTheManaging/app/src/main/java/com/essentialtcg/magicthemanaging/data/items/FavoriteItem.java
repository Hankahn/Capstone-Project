package com.essentialtcg.magicthemanaging.data.items;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Shawn on 5/1/2016.
 */
public class FavoriteItem implements Parcelable {

    protected FavoriteItem(Parcel in) {
    }

    public static final Creator<FavoriteItem> CREATOR = new Creator<FavoriteItem>() {
        @Override
        public FavoriteItem createFromParcel(Parcel in) {
            return new FavoriteItem(in);
        }

        @Override
        public FavoriteItem[] newArray(int size) {
            return new FavoriteItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    // TODO: Implement FavoriteItem

}
