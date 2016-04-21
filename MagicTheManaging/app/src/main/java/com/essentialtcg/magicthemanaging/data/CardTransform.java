package com.essentialtcg.magicthemanaging.data;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Shawn on 4/4/2016.
 */
public class CardTransform {

    public static CardItem transformInstance(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        CardItem cardItem = null;

        if (!cursor.isAfterLast()) {
            cardItem = new CardItem();

            cardItem.setId(cursor.getInt(CardLoader.Query._ID));
            cardItem.setName(cursor.getString(CardLoader.Query.NAME));
            cardItem.setManaCost(cursor.getString(CardLoader.Query.MANA_COST));
            cardItem.setCardText(cursor.getString(CardLoader.Query.CARD_TEXT));
            cardItem.setFlavorText(cursor.getString(CardLoader.Query.FLAVOR_TEXT));
            cardItem.setType(cursor.getString(CardLoader.Query.TYPE));
            cardItem.setPower(cursor.getString(CardLoader.Query.POWER));
            cardItem.setToughness(cursor.getString(CardLoader.Query.TOUGHNESS));
            cardItem.setLoyalty(cursor.getString(CardLoader.Query.LOYALTY));
            cardItem.setSetCode(cursor.getString(CardLoader.Query.SET_CODE));
            cardItem.setRarity(cursor.getString(CardLoader.Query.RARITY));
            cardItem.setCardNumber(cursor.getString(CardLoader.Query.CARD_NUMBER));
            cardItem.setImageName(cursor.getString(CardLoader.Query.IMAGE_NAME));
            cardItem.setSecondName(cursor.getString(CardLoader.Query.NAME2));
            cardItem.setSecondManaCost(cursor.getString(CardLoader.Query.MANA_COST2));
            cardItem.setSecondCardText(cursor.getString(CardLoader.Query.CARD_TEXT2));
            cardItem.setSecondFlavorText(cursor.getString(CardLoader.Query.FLAVOR_TEXT2));
            cardItem.setSecondType(cursor.getString(CardLoader.Query.TYPE2));
            cardItem.setSecondPower(cursor.getString(CardLoader.Query.POWER2));
            cardItem.setSecondToughness(cursor.getString(CardLoader.Query.TOUGHNESS2));
            cardItem.setSecondLoyalty(cursor.getString(CardLoader.Query.LOYALTY2));
            cardItem.setSecondCardNumber(cursor.getString(CardLoader.Query.CARD_NUMBER2));
            cardItem.setSecondImageName(cursor.getString(CardLoader.Query.IMAGE_NAME2));

            if (cardItem.getSecondName() != null && cardItem.getSecondName().length() >  0) {
                cardItem.setHasSecondCard(true);
            } else {
                cardItem.setHasSecondCard(false);
            }

        }

        return cardItem;
    }

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
            cardItem.setCardText(cursor.getString(CardLoader.Query.CARD_TEXT));
            cardItem.setFlavorText(cursor.getString(CardLoader.Query.FLAVOR_TEXT));
            cardItem.setType(cursor.getString(CardLoader.Query.TYPE));
            cardItem.setPower(cursor.getString(CardLoader.Query.POWER));
            cardItem.setToughness(cursor.getString(CardLoader.Query.TOUGHNESS));
            cardItem.setLoyalty(cursor.getString(CardLoader.Query.LOYALTY));
            cardItem.setSetCode(cursor.getString(CardLoader.Query.SET_CODE));
            cardItem.setRarity(cursor.getString(CardLoader.Query.RARITY));
            cardItem.setCardNumber(cursor.getString(CardLoader.Query.CARD_NUMBER));
            cardItem.setSecondName(cursor.getString(CardLoader.Query.NAME2));
            cardItem.setSecondManaCost(cursor.getString(CardLoader.Query.MANA_COST2));
            cardItem.setSecondCardText(cursor.getString(CardLoader.Query.CARD_TEXT2));
            cardItem.setSecondFlavorText(cursor.getString(CardLoader.Query.FLAVOR_TEXT2));
            cardItem.setSecondType(cursor.getString(CardLoader.Query.TYPE2));
            cardItem.setSecondPower(cursor.getString(CardLoader.Query.POWER2));
            cardItem.setSecondToughness(cursor.getString(CardLoader.Query.TOUGHNESS2));
            cardItem.setSecondLoyalty(cursor.getString(CardLoader.Query.LOYALTY2));
            cardItem.setSecondCardNumber(cursor.getString(CardLoader.Query.CARD_NUMBER2));

            if (cardItem.getSecondName() != null && cardItem.getSecondName().length() >  0) {
                cardItem.setHasSecondCard(true);
            } else {
                cardItem.setHasSecondCard(false);
            }

            cardItems.add(cardItem);
        }

        return cardItems;
    }

}
