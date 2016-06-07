package com.essentialtcg.magicthemanaging.data.loaders;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

import com.essentialtcg.magicthemanaging.data.contracts.CardContract;
import com.essentialtcg.magicthemanaging.data.contracts.LegalityContract;

/**
 * Created by Shawn on 4/28/2016.
 */
public class LegalityLoader extends CursorLoader {

    public static LegalityLoader newAllLegalitiesInstance(Context context) {
        return new LegalityLoader(context, LegalityContract.Legalities.buildDirUri());
    }

    public static LegalityLoader newLegalitiesByCardIdInstance(Context context, long cardId) {
        return new LegalityLoader(context, LegalityContract.Legalities.buildLegalitiesByCardIdUri(cardId));
    }

    public static LegalityLoader newInstanceForLegalityId(Context context, long legalityId) {
        return new LegalityLoader(context, LegalityContract.Legalities.buildLegalityUri(legalityId));
    }

    private LegalityLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, CardContract.Cards.DEFAULT_SORT);
    }

    public LegalityLoader(Context context) {
        super(context);
    }

    public interface Query {

        String[] PROJECTION = {
                LegalityContract.Legalities._ID,
                LegalityContract.Legalities.FORMAT,
                LegalityContract.Legalities.LEGALITY
        };

        int _ID = 0;
        int FORMAT = 1;
        int LEGALITY = 2;

    }

}
