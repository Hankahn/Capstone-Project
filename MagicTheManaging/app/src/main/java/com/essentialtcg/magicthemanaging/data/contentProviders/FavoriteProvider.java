package com.essentialtcg.magicthemanaging.data.contentProviders;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.essentialtcg.magicthemanaging.data.CardDatabase;
import com.essentialtcg.magicthemanaging.data.SelectionBuilder;
import com.essentialtcg.magicthemanaging.data.contracts.FavoriteContract;

import java.util.List;

/**
 * Created by Shawn on 5/1/2016.
 */
public class FavoriteProvider extends ContentProvider {

    private SQLiteOpenHelper mOpenHelper;

    interface Tables {
        String FAVORITE = "Favorite";
    }

    private static final int FAVORITE__ID = 1;
    private static final int FAVORITE__BY_CARD = 2;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoriteContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "favorites/#", FAVORITE__ID);
        matcher.addURI(authority, "favorites/card/#", FAVORITE__BY_CARD);
        //matcher.addURI(authority, "cards", CARD);
        //matcher.addURI(authority, "cards/set/*", CARD__SET);
        //matcher.addURI(authority, "cards/search/name/*/sets/*", CARD__SEARCH);
        //matcher.addURI(authority, "cards/deck/#", CARD__DECK);
        //matcher.addURI(authority, "cards/favorites/", CARD__FAVORITES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CardDatabase(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE__ID:
                return FavoriteContract.Favorites.CONTENT_FAVORITE_TYPE;
            case FAVORITE__BY_CARD:
                return FavoriteContract.Favorites.CONTENT_TYPE;
            /*case CARD:
                return CardContract.Cards.CONTENT_TYPE;
            case CARD__SEARCH:
                return CardContract.Cards.CONTENT_TYPE;
            case CARD__DECK:
                return CardContract.Cards.CONTENT_TYPE;
            case CARD__FAVORITES:
                return CardContract.Cards.CONTENT_TYPE;*/
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        //final int match = sUriMatcher.match(uri);

        Cursor cursor;

        cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        final long _id = db.insertOrThrow(Tables.FAVORITE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return FavoriteContract.Favorites.buildFavoriteUri(_id);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int count;

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        count = db.delete(Tables.FAVORITE, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();

        switch (match) {
            case FAVORITE__ID: {
                final String _id = paths.get(1);

                return builder.table(Tables.FAVORITE).where(FavoriteContract.Favorites._ID + "=?", _id);
            }
            case FAVORITE__BY_CARD: {
                final String set = paths.get(2);

                return builder.table(Tables.FAVORITE).where(FavoriteContract.Favorites.CARD_ID +
                        "=?", set);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
