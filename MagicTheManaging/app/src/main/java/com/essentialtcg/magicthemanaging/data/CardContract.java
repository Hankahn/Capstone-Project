package com.essentialtcg.magicthemanaging.data;

import android.net.Uri;

public class CardContract {

    public static final String CONTENT_AUTHORITY = "com.essentialtcg.magicthemanaging.cards";
    public static final Uri BASE_URI = Uri.parse("content://com.essentialtcg.magicthemanaging.cards");

    interface CardColumns {

        /**
         * Type: INTEGER PRIMARY KEY AUTOINCREMENT
         */
        String _ID = "card.ID";
        String _ID2 = "card2.ID";
        /**
         * Type: TEXT NOT NULL
         */
        String NAME = "card.Name";
        String NAMES = "card.Names";
        String NAME2 = "card2.Name";
        String MANA_COST = "card.ManaCost";
        String MANA_COST2 = "card2.ManaCost";
        String CARD_TEXT = "card.CardText";
        String CARD_TEXT2 = "card2.CardText";
        String TYPE = "card.CardType";
        String TYPE2 = "card2.CardType";
        String POWER = "card.power";
        String POWER2 = "card2.power";
        String TOUGHNESS = "card.Toughness";
        String TOUGHNESS2 = "card2.Toughness";
        String LOYALTY = "card.Loyalty";
        String LOYALTY2 = "card2.Loyalty";
        String SET_CODE = "card.SetCode";
        String RARITY = "card.Rarity";
        String CARD_NUMBER = "card.CardNumber";
        String CARD_NUMBER2 = "card2.CardNumber";
        String IMAGE_NAME = "card.ImageName";
        String IMAGE_NAME2 = "card2.ImageName";
        String FLAVOR_TEXT = "card.Flavor";
        String FLAVOR_TEXT2 = "card2.Flavor";

    }

    public static class Cards implements CardColumns {

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/vnd.com.essentialtcg.magicthemanaging.data.cards";
        public static final String CONTENT_CARD_TYPE =
                "vnd.android.cursor.card/vnd.com.essentialtcg.magicthemanaging.data.cards";

        public static final String DEFAULT_SORT = NAME + " ASC";

        /**
         * Matches: /cards/
         */
        public static Uri buildDirUri() {
            return BASE_URI.buildUpon().appendPath("cards").build();
        }

        /**
         * Matches: /cards/[ID]/
         */
        public static Uri buildCardUri(long _id) {
            return BASE_URI.buildUpon().appendPath("cards").appendPath(Long.toString(_id)).build();
        }

        /**
         * Matches: /cards/set/[SET_CODE]/
         */
        public static Uri buildCardsBySetUri(String setCode) {
            return BASE_URI.buildUpon()
                    .appendPath("cards")
                    .appendPath("set")
                    .appendPath(setCode).build();
        }

        /**
         * Matches: /cards/search/name/[NAME]/sets/[SET_CODE]/
         */
        public static Uri buildCardsByCriteriaUri(CardSearchParameters params) {
            StringBuilder setFilterCodeText = new StringBuilder();

            if (params.getSetFilter() != null && params.getSetFilter().size() > 0) {
                for (SetItem set : params.getSetFilter()) {
                    if (setFilterCodeText.length() > 0) {
                        setFilterCodeText.append(",");
                    }

                    setFilterCodeText.append(set.getCode());
                }
            }

            Uri uri = BASE_URI.buildUpon()
                    .appendPath("cards")
                    .appendPath("search")
                    .appendPath("name")
                    .appendPath(params.getNameFilter() != null && params.getNameFilter().length() > 0 ?
                            params.getNameFilter() : "*")
                    .appendPath("sets")
                    .appendPath(setFilterCodeText.length() > 0 ?
                            setFilterCodeText.toString() : "*").build();

            return uri;
        }

        /**
         * Read card ID card detail URI.
         */
        public static long getCardId(Uri cardUri) {
            return Long.parseLong(cardUri.getPathSegments().get(1));
        }
    }

    private CardContract() {
    }

}
