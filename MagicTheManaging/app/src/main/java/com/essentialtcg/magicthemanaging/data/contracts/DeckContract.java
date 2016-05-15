package com.essentialtcg.magicthemanaging.data.contracts;

import android.net.Uri;

/**
 * Created by Shawn on 4/28/2016.
 */
public class DeckContract {

    public static final String CONTENT_AUTHORITY = "com.essentialtcg.magicthemanaging.decks";
    public static final Uri BASE_URI = Uri.parse("content://com.essentialtcg.magicthemanaging.decks");

    interface DeckColumns {

        String _ID = "ID";
        String NAME = "Name";
        String FORMAT = "Format";
        String NOTES = "Notes";

    }

    public static class Decks implements DeckColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.essentialtcg.magicthemanaging.data.decks";
        public static final String CONTENT_DECK_TYPE =
                "vnd.android.cursor.card/vnd.com.essentialtcg.magicthemanaging.data.decks";

        public static final String DEFAULT_SORT = NAME + " ASC";

        /**
         * Matches: /decks/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("decks").build();
        }

        /**
         * Matches: /decks/[ID]/
         */
        public static Uri buildDeckUri(long _id) {
            return BASE_URI.buildUpon().appendPath("decks").appendPath(Long.toString(_id)).build();
        }

        /**
         * Read deck ID deck detail URI.
         */
        public static long getDeckId(Uri deckUri) {
            return Long.parseLong(deckUri.getPathSegments().get(1));
        }
    }

    private DeckContract() {
    }

}
