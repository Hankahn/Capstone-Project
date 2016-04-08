package com.essentialtcg.magicthemanaging.data;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Shawn on 4/4/2016.
 */
public class CardTransform {

    public static ArrayList<CardItem> transform(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<CardItem> cardItems = new ArrayList<>();

        while (cursor.moveToNext()) {
            CardItem cardItem = new CardItem();

            cardItem.setId(cursor.getInt(CardLoader.Query._ID));
            cardItem.setName(cursor.getString(CardLoader.Query.NAME));
            cardItem.setManaCost(cursor.getString(CardLoader.Query.MANA_COST));
            cardItem.setType(cursor.getString(CardLoader.Query.TYPE));
            cardItem.setPower(cursor.getString(CardLoader.Query.POWER));
            cardItem.setToughness(cursor.getString(CardLoader.Query.TOUGHNESS));
            cardItem.setLoyalty(cursor.getString(CardLoader.Query.LOYALTY));
            cardItem.setSetCode(cursor.getString(CardLoader.Query.SET_CODE));
            cardItem.setRarity(cursor.getString(CardLoader.Query.RARITY));
            cardItem.setSecondName(cursor.getString(CardLoader.Query.NAME2));

            cardItems.add(cardItem);
        }

        return cardItems;
    }

}
