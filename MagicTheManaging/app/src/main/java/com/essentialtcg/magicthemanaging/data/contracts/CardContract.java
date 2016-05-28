package com.essentialtcg.magicthemanaging.data.contracts;

import android.net.Uri;

import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.items.SetItem;

public class CardContract {

    public static final String CONTENT_AUTHORITY = "com.essentialtcg.magicthemanaging.cards";
    public static final Uri BASE_URI = Uri.parse("content://com.essentialtcg.magicthemanaging.cards");

    interface CardColumns {

        /**
         * Type: INTEGER PRIMARY KEY AUTOINCREMENT
         */
        String _ID = "card.ID";
        String _ID2 = "ID2";
        String NAME = "card.Name";
        String NAMES = "Names";
        String NAME2 = "Name2";
        String MANA_COST = "ManaCost";
        String MANA_COST2 = "ManaCost2";
        String CARD_TEXT = "CardText";
        String CARD_TEXT2 = "CardText2";
        String TYPE = "CardType";
        String TYPE2 = "CardType2";
        String POWER = "power";
        String POWER2 = "power2";
        String TOUGHNESS = "Toughness";
        String TOUGHNESS2 = "Toughness2";
        String LOYALTY = "Loyalty";
        String LOYALTY2 = "Loyalty2";
        String SET_CODE = "SetCode";
        String SET_NAME = "[set].Name";
        String RARITY = "Rarity";
        String CARD_NUMBER = "CardNumber";
        String CARD_NUMBER2 = "CardNumber2";
        String IMAGE_NAME = "ImageName";
        String IMAGE_NAME2 = "ImageName2";
        String FLAVOR_TEXT = "Flavor";
        String FLAVOR_TEXT2 = "Flavor2";
        String IS_FAVORITE = "(CASE WHEN favorite.CardID IS null THEN 0 ELSE 1 END) IsFavorite";
        //String DECK_CARD_ID = "deck_card.ID";
        //String CARD_ID = "CardID";
        String DECK_ID = "DeckID";
        //String QUANTITY_MAIN = "QuantityMain";
        //String QUANTITY_SIDE = "QuantitySide";

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
         * Matches: /cards/deck/[DECK_ID]/
         */
        public static Uri buildCardsByDeckUri(long deckId) {
            return BASE_URI.buildUpon()
                    .appendPath("cards")
                    .appendPath("deck")
                    .appendPath(Long.toString(deckId)).build();
        }

        /**
         * Matches: /cards/favorites/
         */
        public static Uri buildCardsByFavoritesUri() {
            return BASE_URI.buildUpon()
                    .appendPath("cards")
                    .appendPath("favorites").build();
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
