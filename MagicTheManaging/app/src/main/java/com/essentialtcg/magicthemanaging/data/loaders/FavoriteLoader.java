package com.essentialtcg.magicthemanaging.data.loaders;

import android.content.Context;
import android.support.v4.content.CursorLoader;
import android.net.Uri;

import com.essentialtcg.magicthemanaging.data.contracts.FavoriteContract;

/**
 * Created by Shawn on 5/1/2016.
 */
public class FavoriteLoader extends CursorLoader {

    public static FavoriteLoader newInstanceForFavoriteId(Context context, long _id) {
        return new FavoriteLoader(context, FavoriteContract.Favorites.buildFavoriteUri(_id));
    }

    public static FavoriteLoader newInstanceFavoriteByCardInstance(Context context, long cardId) {
        return new FavoriteLoader(context, FavoriteContract.Favorites.buildFavoriteCardUri(cardId));
    }

    private FavoriteLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, FavoriteContract.Favorites.DEFAULT_SORT);
    }

    public interface Query {

        String[] PROJECTION = {
                FavoriteContract.Favorites._ID,
                FavoriteContract.Favorites.CARD_ID
        };

        int _ID = 0;
        int CARD_ID = 1;

    }

}
