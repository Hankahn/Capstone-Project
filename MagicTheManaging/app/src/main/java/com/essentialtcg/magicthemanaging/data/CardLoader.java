package com.essentialtcg.magicthemanaging.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

import com.essentialtcg.magicthemanaging.CardViewActivity;

/**
 * Helper for loading a list of articles or a single article.
 */
public class CardLoader extends CursorLoader {

    public static CardLoader newAllCardsInstance(Context context) {
        return new CardLoader(context, CardContract.Cards.buildDirUri());
    }

    public static CardLoader newCardsBySetInstance(Context context, String setCode) {
        return new CardLoader(context, CardContract.Cards.buildCardsBySetUri(setCode));
    }

    public static CardLoader newCardsByCriteria(Context context, CardSearchParameters params) {
        return new CardLoader(context, CardContract.Cards.buildCardsByCriteriaUri(params));
    }

    public static CardLoader newInstanceForCardId(Context context, long cardId) {
        return new CardLoader(context, CardContract.Cards.buildCardUri(cardId));
    }

    private CardLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, CardContract.Cards.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                CardContract.Cards._ID,
                CardContract.Cards.NAME,
                CardContract.Cards.MANA_COST,
                CardContract.Cards.CARD_TEXT,
                CardContract.Cards.TYPE,
                CardContract.Cards.POWER,
                CardContract.Cards.TOUGHNESS,
                CardContract.Cards.LOYALTY,
                CardContract.Cards.SET_CODE,
                CardContract.Cards.RARITY,
                CardContract.Cards.CARD_NUMBER,
                CardContract.Cards.IMAGE_NAME,
                CardContract.Cards._ID2,
                CardContract.Cards.NAME2,
                CardContract.Cards.MANA_COST2,
                CardContract.Cards.TYPE2,
                CardContract.Cards.POWER2,
                CardContract.Cards.TOUGHNESS2,
                CardContract.Cards.LOYALTY2,
                CardContract.Cards.CARD_TEXT2,
                CardContract.Cards.CARD_NUMBER2,
                CardContract.Cards.IMAGE_NAME2,
                CardContract.Cards.FLAVOR_TEXT,
                CardContract.Cards.FLAVOR_TEXT2
        };

        int _ID = 0;
        int NAME = 1;
        int MANA_COST = 2;
        int CARD_TEXT = 3;
        int TYPE = 4;
        int POWER = 5;
        int TOUGHNESS = 6;
        int LOYALTY = 7;
        int SET_CODE = 8;
        int RARITY = 9;
        int CARD_NUMBER = 10;
        int IMAGE_NAME = 11;
        int _ID2 = 12;
        int NAME2 = 13;
        int MANA_COST2 = 14;
        int TYPE2 = 15;
        int POWER2 = 16;
        int TOUGHNESS2 = 17;
        int LOYALTY2 = 18;
        int CARD_TEXT2 = 19;
        int CARD_NUMBER2 = 20;
        int IMAGE_NAME2 = 21;
        int FLAVOR_TEXT = 22;
        int FLAVOR_TEXT2 = 23;

    }
}
