package com.essentialtcg.magicthemanaging.data.contentProviders;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.essentialtcg.magicthemanaging.data.CardDatabase;
import com.essentialtcg.magicthemanaging.data.SelectionBuilder;
import com.essentialtcg.magicthemanaging.data.contracts.CardContract;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shawn on 2/24/2016.
 */
public class CardProvider extends ContentProvider {

    private SQLiteOpenHelper mOpenHelper;

    interface Tables {
        String CARD = "card " +
                "INNER JOIN [set] ON card.SetCode = [set].Code " +
                "LEFT OUTER JOIN favorite ON card.ID = favorite.CardID";
        String CARD_DECK_CARD = "card INNER JOIN deck_card ON card.ID = deck_card.CardID";
        String CARD_FAVORITE = "card " +
                "INNER JOIN [set] ON card.SetCode = [set].Code " +
                "INNER JOIN favorite ON card.ID = favorite.CardID";
    }

    private static final int CARD = 0;
    private static final int CARD__ID = 1;
    private static final int CARD__SET = 2;
    private static final int CARD__SEARCH = 3;
    private static final int CARD__DECK = 4;
    private static final int CARD__FAVORITES = 5;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = CardContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "cards", CARD);
        matcher.addURI(authority, "cards/set/*", CARD__SET);
        matcher.addURI(authority, "cards/#", CARD__ID);
        matcher.addURI(authority, "cards/search/name/*/sets/*", CARD__SEARCH);
        matcher.addURI(authority, "cards/deck/#", CARD__DECK);
        matcher.addURI(authority, "cards/favorites/", CARD__FAVORITES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CardDatabase(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD:
                return CardContract.Cards.CONTENT_TYPE;
            case CARD__ID:
                return CardContract.Cards.CONTENT_CARD_TYPE;
            case CARD__SET:
                return CardContract.Cards.CONTENT_TYPE;
            case CARD__SEARCH:
                return CardContract.Cards.CONTENT_TYPE;
            case CARD__DECK:
                return CardContract.Cards.CONTENT_TYPE;
            case CARD__FAVORITES:
                return CardContract.Cards.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        final int match = sUriMatcher.match(uri);

        Cursor cursor;

        cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder, "400");
        //cursor = builder.table(Tables.CARD).where(selection, selectionArgs).query(db, projection, sortOrder);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CARD: {
                final long _id = db.insertOrThrow(Tables.CARD, null, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return CardContract.Cards.buildCardUri(_id);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).update(db, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        return builder.where(selection, selectionArgs).delete(db);
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();

        StringBuilder queryBuilder = new StringBuilder();

        switch (match) {
            case CARD: {
                return builder.table(Tables.CARD);
            }
            case CARD__ID: {
                final String _id = paths.get(1);

                /*builder.table(Tables.CARD_FULL).where(" (" + CardContract.CardColumns.NAMES +
                        " LIKE " + CardContract.CardColumns.NAME + " || '%' OR " +
                        CardContract.CardColumns.NAMES + " = '')");*/

                return builder.table(Tables.CARD).where(CardContract.Cards._ID + "=?", _id);
            }
            case CARD__SET: {
                final String set = paths.get(2);

                /*builder.table(Tables.CARD).where(" (" + CardContract.CardColumns.NAMES +
                        " LIKE " + CardContract.CardColumns.NAME + " || '%' OR " +
                        CardContract.CardColumns.NAMES + " = '')");*/

                return builder.table(Tables.CARD).where(CardContract.Cards.SET_CODE +
                        "=?", set);
            }
            case CARD__DECK: {
                final String deckId = paths.get(2);

                return builder.table(Tables.CARD_DECK_CARD).where(CardContract.Cards.DECK_ID +
                        "=?", deckId);
            }
            case CARD__FAVORITES: {
                return builder.table(Tables.CARD_FAVORITE);
            }
            case CARD__SEARCH: {
                final String name = paths.get(3);
                final String sets = paths.get(5);

                ArrayList<String> parameters = new ArrayList<>();

                builder.table(Tables.CARD);

                if (!name.equals("*")) {
                    queryBuilder.append(String.format("%s LIKE ? OR %s LIKE ?",
                            CardContract.Cards.NAME, CardContract.Cards.NAME2));

                    builder.table(Tables.CARD).where(queryBuilder.toString(),
                            "%" + name + "%", "%" + name + "%");
                }

                if (!sets.equals("*")) {
                    queryBuilder = new StringBuilder();

                    String[] setsSplit = sets.split(",");

                    queryBuilder.append(String.format("%s IN (?",
                            CardContract.Cards.SET_CODE));

                    parameters.add(setsSplit[0]);

                    for (int cnt = 1; cnt < setsSplit.length; cnt++) {
                        queryBuilder.append(String.format(", ?", setsSplit[cnt]));

                        parameters.add(setsSplit[cnt]);
                    }

                    queryBuilder.append(")");

                    builder.table(Tables.CARD).where(queryBuilder.toString(),
                            parameters.toArray(new String[parameters.size()]));
                }

                /*builder.table(Tables.CARD).where(" (" + CardContract.CardColumns.NAMES +
                        " LIKE " + CardContract.CardColumns.NAME + " || '%' OR " +
                        CardContract.CardColumns.NAMES + " = '')");*/

                return builder;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    /**
     * Apply the given set of {@link ContentProviderOperation}, executing inside
     * a {@link SQLiteDatabase} transaction. All changes will be rolled back if
     * any single one fails.
     */
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        db.beginTransaction();

        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];

            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }

            db.setTransactionSuccessful();

            return results;
        } finally {
            db.endTransaction();
        }
    }

}
