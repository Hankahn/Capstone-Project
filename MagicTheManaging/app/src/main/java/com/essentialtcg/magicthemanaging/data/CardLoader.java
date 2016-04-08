package com.essentialtcg.magicthemanaging.data;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

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
                CardContract.Cards.TYPE,
                CardContract.Cards.POWER,
                CardContract.Cards.TOUGHNESS,
                CardContract.Cards.LOYALTY,
                CardContract.Cards.SET_CODE,
                CardContract.Cards.RARITY,
                CardContract.Cards._ID2,
                CardContract.Cards.NAME2
        };

        int _ID = 0;
        int NAME = 1;
        int MANA_COST = 2;
        int TYPE = 3;
        int POWER = 4;
        int TOUGHNESS = 5;
        int LOYALTY = 6;
        int SET_CODE = 7;
        int RARITY = 8;
        int _ID2 = 9;
        int NAME2 = 10;
        int MANA_COST2 = 11;
        int TYPE2 = 12;
        int POWER2 = 13;
        int TOUGHNESS2 = 14;
        int LOYALTY2 = 15;
    }
}
