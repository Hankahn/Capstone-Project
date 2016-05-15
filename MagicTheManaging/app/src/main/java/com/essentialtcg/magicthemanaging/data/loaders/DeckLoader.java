package com.essentialtcg.magicthemanaging.data.loaders;

import android.content.Context;
import android.content.CursorLoader;
import android.net.Uri;

import com.essentialtcg.magicthemanaging.data.contracts.DeckContract;

/**
 * Created by Shawn on 4/28/2016.
 */
public class DeckLoader extends CursorLoader {

    public static DeckLoader newAllDecksInstance(Context context) {
        return new DeckLoader(context, DeckContract.Decks.buildDirUri());
    }

    public static DeckLoader newInstanceForDeckId(Context context, long deckId) {
        return new DeckLoader(context, DeckContract.Decks.buildDeckUri(deckId));
    }

    private DeckLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, DeckContract.Decks.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                DeckContract.Decks._ID,
                DeckContract.Decks.NAME
        };

        int _ID = 0;
        int NAME = 1;

    }

}
