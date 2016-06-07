package com.essentialtcg.magicthemanaging.data.contracts;

import android.net.Uri;

/**
 * Created by Shawn on 4/28/2016.
 */
public class RulingContract {

    public static final String CONTENT_AUTHORITY = "com.essentialtcg.magicthemanaging.rulings";
    public static final Uri BASE_URI = Uri.parse("content://com.essentialtcg.magicthemanaging.rulings");

    interface RulingColumns {

        /**
         * Type: INTEGER PRIMARY KEY AUTOINCREMENT
         */
        String _ID = "card.ID";
        String DATE = "ruling.RulingDate";
        String TEXT = "ruling.RulingText";

    }

    public static class Rulings implements RulingColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.essentialtcg.magicthemanaging.data.rulings";
        public static final String CONTENT_RULING_TYPE =
                "vnd.android.cursor.card/vnd.com.essentialtcg.magicthemanaging.data.rulings";

        public static final String DEFAULT_SORT = DATE + " ASC";

        /**
         * Matches: /rulings/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("rulings").build();
        }

        /**
         * Matches: /rulings/[ID]/
         */
        public static Uri buildRulingUri(long _id) {
            return BASE_URI.buildUpon()
                    .appendPath("rulings")
                    .appendPath(Long.toString(_id))
                    .build();
        }

        /**
         * Matches: /rulings/card/[CARD_ID]/
         */
        public static Uri buildRulingsByCardIdUri(long cardId) {
            return BASE_URI.buildUpon()
                    .appendPath("rulings")
                    .appendPath("card")
                    .appendPath(String.valueOf(cardId))
                    .build();
        }

    }

    private RulingContract() {
    }

}
