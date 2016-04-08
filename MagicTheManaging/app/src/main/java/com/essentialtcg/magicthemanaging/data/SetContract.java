package com.essentialtcg.magicthemanaging.data;

import android.net.Uri;

/**
 * Created by Shawn on 4/4/2016.
 */
public class SetContract {

    public static final String CONTENT_AUTHORITY = "com.essentialtcg.magicthemanaging.sets";
    public static final Uri BASE_URI = Uri.parse("content://com.essentialtcg.magicthemanaging.sets");

    interface SetColumns {

        /** Type: INTEGER PRIMARY KEY AUTOINCREMENT */
        String _ID = "[set].ID";
        String NAME = "[set].Name";
        String CODE = "[set].Code";
        String GATHERER_CODE = "[set].GathererCode";
        String OLD_CODE = "[set].OldCode";
        String MAGIC_CARDS_INFO_CODE = "[set].MagicCardsInfoCode";
        String RELEASE_DATE = "[set].ReleaseDate";
        String BORDER = "[set].Border";
        String SET_TYPE = "[set].SetType";
        String BLOCK = "[set].Block";
        String ONLINE_ONLY = "[set].OnlineOnly";
        String BOOSTER = "[set].Booster";

    }

    public static class Sets implements SetColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.essentialtcg.magicthemanaging.data.sets";
        public static final String CONTENT_SET_TYPE =
                "vnd.android.cursor.set/vnd.com.essentialtcg.magicthemanaging.data.sets";

        public static final String DEFAULT_SORT =
                "(CASE " +
                        "WHEN " + SET_TYPE + " IN ('expansion', 'core') THEN 'AAAA' " +
                        "WHEN " + SET_TYPE + " = 'duel deck' THEN 'BBBB' " +
                        "WHEN " + SET_TYPE + " = 'commander' THEN 'CCCC' " +
                        "WHEN " + SET_TYPE + " = 'reprint' THEN 'DDDD' " +
                        "WHEN " + SET_TYPE + " = 'from the vault' THEN 'EEEE' " +
                        "WHEN " + SET_TYPE + " = 'conspiracy' THEN 'FFFF' " +
                        "WHEN " + SET_TYPE + " = 'promo' THEN 'ZZZZ' " +
                        "ELSE " + SET_TYPE + " END) ASC, " +
                        RELEASE_DATE + " DESC";

        /** Matches: /sets/ */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("sets").build();
        }

        /** Matches: /sets/[ID]/ */
        public static Uri buildSetUri(long _id) {
            return BASE_URI.buildUpon().appendPath("sets").appendPath(Long.toString(_id)).build();
        }

        /** Read set ID set detail URI. */
        public static long getSetId(Uri setUri) {
            return Long.parseLong(setUri.getPathSegments().get(1));
        }
    }

    private SetContract() {
    }

}
