package com.essentialtcg.magicthemanaging.factories;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Binder;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.contracts.CardContract;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.utils.CardUtil;
import com.essentialtcg.magicthemanaging.utils.Util;

/**
 * Created by Shawn on 5/31/2016.
 */
public class FavoritesViewsFactory implements RemoteViewsService.RemoteViewsFactory,
        Loader.OnLoadCompleteListener<Cursor> {

    private static final String TAG = "FavoritesViewsFactory";

    private Cursor mCursor = null;
    private Context mContext = null;
    private CardLoader mCardLoader = null;
    private int mAppWidgetId;

    private final String[] FAVORITE_COLUMNS = {
            CardContract.Cards._ID,
            CardContract.Cards.NAME,
            CardContract.Cards.NAME2,
            CardContract.Cards.RARITY,
            CardContract.Cards.SET_CODE,
            CardContract.Cards.TYPE
    };

    private final int _ID = 0;
    private final int NAME = 1;
    private final int NAME2 = 2;
    private final int RARITY = 3;
    private final int SET_CODE = 4;
    private final int TYPE = 5;

    public FavoritesViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        /*mCardLoader = CardLoader.newCardsByFavorites(mContext);
        mCardLoader.registerListener(5, this);
        mCardLoader.startLoading();*/
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (mCursor != null) {
            mCursor.close();
        }

        final long identityToken = Binder.clearCallingIdentity();

        Uri favoriteCardsUri = CardContract.Cards.buildCardsByFavoritesUri();

        mCursor = mContext.getContentResolver().query(favoriteCardsUri, FAVORITE_COLUMNS, null,
                null, CardContract.Cards.NAME + " ASC");

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    @Override
    public int getCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        if (position == AdapterView.INVALID_POSITION || mCursor == null ||
                !mCursor.moveToPosition(position)) {
            return null;
        }

        RemoteViews views =
                new RemoteViews(mContext.getPackageName(), R.layout.favorites_widget_row);

        views.setTextColor(R.id.favorites_widget_name_text_view, mContext.getResources().getColor(R.color.black));

        if (mCursor.getString(NAME2) != null) {
            views.setTextViewText(R.id.favorites_widget_name_text_view,
                    String.format(
                            mContext.getResources().getString(R.string.CARD_NAME_DOUBLE_SIDED),
                            mCursor.getString(NAME),
                            mCursor.getString(NAME2)
                    ));
        } else {
            views.setTextViewText(R.id.favorites_widget_name_text_view, mCursor.getString(NAME));
        }

        try {
            int resourceId = CardUtil.parseSetRarity(
                    mCursor.getString(SET_CODE),
                    mCursor.getString(RARITY));

            try {

                Drawable iconDrawable = ContextCompat.getDrawable(mContext, resourceId);
                int imageHeight = Util.dpToPx(mContext, 15);
                int imageWidth = Util.calculateWidth(iconDrawable.getIntrinsicWidth(),
                        iconDrawable.getIntrinsicHeight(), imageHeight);

                iconDrawable.setBounds(0, 0, imageWidth, imageHeight);

                views.setImageViewResource(R.id.favorites_widget_set_rarity_image_view,
                        resourceId);
                views.setViewVisibility(R.id.favorites_widget_set_rarity_text_view, View.INVISIBLE);
            } catch (Exception ex) {
                Log.e("MtM", ex.toString());
                views.setTextViewText(R.id.favorites_widget_set_rarity_text_view,
                        String.format("%s %s",
                                mCursor.getString(SET_CODE),
                                mCursor.getString(RARITY).substring(0, 1)));
                views.setViewVisibility(R.id.favorites_widget_set_rarity_image_view, View.INVISIBLE);
            }
        } catch (Exception ex) {
            Log.d("MtM", mCursor.getString(SET_CODE)
                    + "_" + mCursor.getString(RARITY));
        }

        views.setTextViewText(R.id.favorites_widget_type_text_view,
                mCursor.getString(TYPE));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onLoadComplete(Loader loader, Cursor cursor) {
        mCursor = cursor;
    }
}
