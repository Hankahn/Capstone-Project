package com.essentialtcg.magicthemanaging.data;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Shawn on 3/17/2016.
 */
public class CardDatabase extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "card_database.db";
    private static final int DATABASE_VERSION = 1;

    public CardDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

}
