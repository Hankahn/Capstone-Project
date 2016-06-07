package com.essentialtcg.magicthemanaging.data.loaders;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

import com.essentialtcg.magicthemanaging.data.contracts.CardContract;
import com.essentialtcg.magicthemanaging.data.contracts.RulingContract;

/**
 * Created by Shawn on 4/28/2016.
 */
public class RulingLoader extends CursorLoader {

    public static RulingLoader newAllRulingsInstance(Context context) {
        return new RulingLoader(context, RulingContract.Rulings.buildDirUri());
    }

    public static RulingLoader newRulingsByCardIdInstance(Context context, long cardId) {
        return new RulingLoader(context, RulingContract.Rulings.buildRulingsByCardIdUri(cardId));
    }

    public static RulingLoader newInstanceForRulingId(Context context, long rulingId) {
        return new RulingLoader(context, RulingContract.Rulings.buildRulingUri(rulingId));
    }

    private RulingLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, CardContract.Cards.DEFAULT_SORT);
    }

    public RulingLoader(Context context) {
        super(context);
    }

    public interface Query {

        String[] PROJECTION = {
                RulingContract.Rulings._ID,
                RulingContract.Rulings.DATE,
                RulingContract.Rulings.TEXT
        };

        int _ID = 0;
        int DATE = 1;
        int TEXT = 2;

    }

}
