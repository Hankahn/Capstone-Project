package com.essentialtcg.magicthemanaging.data.contracts;

import android.net.Uri;

/**
 * Created by Shawn on 5/18/2016.
 */
public class FavoriteContract {

    public static final String CONTENT_AUTHORITY = "com.essentialtcg.magicthemanaging.favorites";
    public static final Uri BASE_URI = Uri.parse("content://com.essentialtcg.magicthemanaging.favorites");

    interface FavoriteColumns {

        String _ID = "ID";
        String CARD_ID = "CardID";

    }

    public static class Favorites implements FavoriteColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.essentialtcg.magicthemanaging.data.favorites";
        public static final String CONTENT_FAVORITE_TYPE =
                "vnd.android.cursor.card/vnd.com.essentialtcg.magicthemanaging.data.favorites";

        public static final String DEFAULT_SORT = CARD_ID + " ASC";

        /**
         * Matches: /favorites/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("favorites").build();
        }

        /**
         * Matches: /favorites/card/[ID]/
         */
        public static Uri buildFavoriteUri(long _id) {
            return BASE_URI.buildUpon()
                    .appendPath("favorites")
                    .appendPath(Long.toString(_id)).build();
        }

        /**
         * Matches: /favorites/card/[ID]/
         */
        public static Uri buildFavoriteCardUri(long cardId) {
            return BASE_URI.buildUpon()
                    .appendPath("favorites")
                    .appendPath("card")
                    .appendPath(Long.toString(cardId)).build();
        }

        /**
         * Read favorite ID favorite detail URI.
         */
        public static long getFavoriteId(Uri favoriteUri) {
            return Long.parseLong(favoriteUri.getPathSegments().get(1));
        }

    }

    private FavoriteContract() {
    }

}
