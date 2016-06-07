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
import com.essentialtcg.magicthemanaging.data.contracts.LegalityContract;

import java.util.List;

/**
 * Created by Shawn on 4/28/2016.
 */
public class LegalityProvider extends ContentProvider {

    private SQLiteOpenHelper mOpenHelper;

    interface Tables {
        String CARD_LEGALITY = "card " +
                "INNER JOIN legality ON card.ExternalID = legality.ExternalID";
    }

    private static final int LEGALITY = 0;
    private static final int LEGALITY__ID = 1;
    private static final int LEGALITY__CARD = 2;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LegalityContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, "legalities", LEGALITY);
        matcher.addURI(authority, "legalities/#", LEGALITY__ID);
        matcher.addURI(authority, "legalities/card/#", LEGALITY__CARD);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new CardDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case LEGALITY:
                return LegalityContract.Legalities.CONTENT_TYPE;
            case LEGALITY__ID:
                return LegalityContract.Legalities.CONTENT_LEGALITY_TYPE;
            case LEGALITY__CARD:
                return LegalityContract.Legalities.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final SelectionBuilder builder = buildSelection(uri);
        final int match = sUriMatcher.match(uri);

        Cursor cursor;

        //cursor = builder.where(selection, selectionArgs).query(db, projection, sortOrder, "400");
        cursor = builder.table(Tables.CARD_LEGALITY).where(selection, selectionArgs).query(db, projection, sortOrder);

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private SelectionBuilder buildSelection(Uri uri) {
        final SelectionBuilder builder = new SelectionBuilder();
        final int match = sUriMatcher.match(uri);
        return buildSelection(uri, match, builder);
    }

    private SelectionBuilder buildSelection(Uri uri, int match, SelectionBuilder builder) {
        final List<String> paths = uri.getPathSegments();

        switch (match) {
            case LEGALITY: {
                return builder.table(Tables.CARD_LEGALITY);
            }
            case LEGALITY__ID: {
                final String _id = paths.get(1);

                return builder.table(Tables.CARD_LEGALITY).where(LegalityContract.Legalities._ID + "=?", _id);
            }
            case LEGALITY__CARD: {
                final String cardId = paths.get(2);

                return builder.table(Tables.CARD_LEGALITY).where(LegalityContract.Legalities._ID +
                        "=?", cardId);
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

}
