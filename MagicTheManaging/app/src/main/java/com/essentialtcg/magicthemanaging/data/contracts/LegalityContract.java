package com.essentialtcg.magicthemanaging.data.contracts;

import android.net.Uri;

/**
 * Created by Shawn on 4/28/2016.
 */
public class LegalityContract {

    public static final String CONTENT_AUTHORITY = "com.essentialtcg.magicthemanaging.legalities";
    public static final Uri BASE_URI = Uri.parse("content://com.essentialtcg.magicthemanaging.legalities");

    interface LegalityColumns {

        /**
         * Type: INTEGER PRIMARY KEY AUTOINCREMENT
         */
        String _ID = "card.ID";
        String FORMAT = "legality.Format";
        String LEGALITY = "legality.Legality";

    }

    public static class Legalities implements LegalityColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.essentialtcg.magicthemanaging.data.legalities";
        public static final String CONTENT_LEGALITY_TYPE =
                "vnd.android.cursor.card/vnd.com.essentialtcg.magicthemanaging.data.legalities";

        public static final String DEFAULT_SORT = _ID + " ASC";

        /**
         * Matches: /legalities/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon()
                    .appendPath("legalities")
                    .build();
        }

        /**
         * Matches: /legalities/[ID]/
         */
        public static Uri buildLegalityUri(long _id) {
            return BASE_URI.buildUpon()
                    .appendPath("legalities")
                    .appendPath(Long.toString(_id))
                    .build();
        }

        /**
         * Matches: /legalities/card/[CARD_ID]/
         */
        public static Uri buildLegalitiesByCardIdUri(long cardId) {
            return BASE_URI.buildUpon()
                    .appendPath("legalities")
                    .appendPath("card")
                    .appendPath(String.valueOf(cardId))
                    .build();
        }

    }

    private LegalityContract() {
    }

}
