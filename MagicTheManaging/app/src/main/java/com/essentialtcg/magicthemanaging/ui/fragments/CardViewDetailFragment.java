package com.essentialtcg.magicthemanaging.ui.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.callback.GetPriceCallback;
import com.essentialtcg.magicthemanaging.data.contracts.FavoriteContract;
import com.essentialtcg.magicthemanaging.data.items.PriceItem;
import com.essentialtcg.magicthemanaging.data.loaders.FavoriteLoader;
import com.essentialtcg.magicthemanaging.tasks.GetPriceAsyncTask;
import com.essentialtcg.magicthemanaging.utils.Util;
import com.essentialtcg.magicthemanaging.data.items.CardItem;
import com.essentialtcg.magicthemanaging.utils.CardUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewDetailFragment extends Fragment
        implements GetPriceCallback {//, LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CardViewDetailFragment";

    public static final String ARG_CARD_TO_SHOW = "CARD_TO_SHOW";
    //public static final String ARG_ITEM_ID = "ITEM_ID";
    public static final String ARG_SELECTED_ITEM_ID = "START_ID";

    private static final int LOADER_ID = 0;

    private CoordinatorLayout mCoordinatorLayout;
    private View mRootView;
    private TextView mCardPriceTextView;
    private ViewPager mDetailPager;
    private ImageView mCardImageView;
    private LinearLayout mCardTextVersion;
    private FloatingActionButton mFavoriteButton;

    private long mSelectedItemId;
    private CardItem mCardItem;
    private GetPriceAsyncTask mGetPriceTask;

    private final TabLayout.OnTabSelectedListener mTabSelectedListener =
            new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mDetailPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }

            };

    View.OnClickListener mFavoriteClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            ContentValues values = new ContentValues();

            values.put(FavoriteContract.Favorites.CARD_ID, mCardItem.getId());

            Uri isCardFavorite = FavoriteContract.Favorites.buildFavoriteCardUri(mCardItem.getId());

            Cursor cursor = getActivity().getContentResolver().query(isCardFavorite, null, null, null, null);

            if (cursor != null) {

                if (cursor.getCount() == 0) {
                    getActivity().getContentResolver().insert(FavoriteContract.Favorites.buildDirUri(),
                            values);

                    Snackbar.make(mCoordinatorLayout, "Added to favorites", Snackbar.LENGTH_SHORT).show();
                } else {
                    cursor.moveToFirst();

                    long favoriteId = cursor.getLong(FavoriteLoader.Query._ID);

                    getActivity().getContentResolver().delete(FavoriteContract.Favorites.buildDirUri(),
                            FavoriteContract.Favorites._ID + " = ?",
                            new String[] { String.valueOf(favoriteId) });

                    Snackbar.make(mCoordinatorLayout, "Removed from favorites", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, String.format("onClick: Unable to determine if card already a favorite: ",
                        mCardItem.getId()));
            }

            //getLoaderManager().initLoader(LOADER_ID, null, CardViewDetailFragment.this);
        }

    };

    RequestListener<String, GlideDrawable> mCardImageRequestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            startPostponedEnterTransition();
            mCardImageView.setVisibility(View.GONE);
            mCardTextVersion.setVisibility(View.VISIBLE);

            return false;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            startPostponedEnterTransition();

            return false;
        }

    };

    ViewTreeObserver.OnPreDrawListener mCardImagePreDrawListener = new ViewTreeObserver.OnPreDrawListener() {

        @Override
        public boolean onPreDraw() {
            mCardImageView.getViewTreeObserver().removeOnPreDrawListener(this);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getActivity().startPostponedEnterTransition();
                Log.d("MtMU", "Starting postponed enter transitions");
            }

            return true;
        }

    };

    public static CardViewDetailFragment newInstance(CardItem cardItem, long selectedItemId) {
        CardViewDetailFragment fragment = new CardViewDetailFragment();
        Bundle arguments = new Bundle();

        arguments.putParcelable(ARG_CARD_TO_SHOW, cardItem);
        arguments.putLong(ARG_SELECTED_ITEM_ID, selectedItemId);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_CARD_TO_SHOW)) {
            mCardItem = getArguments().getParcelable(ARG_CARD_TO_SHOW);
        }

        /*if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }*/

        if (getArguments().containsKey(ARG_SELECTED_ITEM_ID)) {
            mSelectedItemId = getArguments().getLong(ARG_SELECTED_ITEM_ID);
        }

        mGetPriceTask = new GetPriceAsyncTask(this);

        String url = null;
        try {
            url = String.format("http://partner.tcgplayer.com/x3/phl.asmx/p?pk=%s&s=%s&p=%s",
                    "TCGTEST",
                    URLEncoder.encode(mCardItem.getSet(), "utf-8"),
                    URLEncoder.encode(mCardItem.getName(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mGetPriceTask.execute(url);
    }

    /*public CardViewActivity getActivityCast() {
        return (CardViewActivity) getActivity();
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isGetPriceTaskRunning()) {
            mGetPriceTask.cancel(true);
        }
    }

    private boolean isGetPriceTaskRunning () {
        return (mGetPriceTask != null) && (mGetPriceTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARG_CARD_TO_SHOW)) {
                mCardItem = savedInstanceState.getParcelable(ARG_CARD_TO_SHOW);
            }

            if (savedInstanceState.containsKey(ARG_SELECTED_ITEM_ID)) {
                mSelectedItemId = savedInstanceState.getLong(ARG_SELECTED_ITEM_ID);
            }
        }

        bindViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_card_view_detail, container, false);

        return mRootView;
    }

    public ImageView getCardImageView() {
        return (ImageView) mRootView.findViewById(R.id.card_detail_image_view);
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        mCoordinatorLayout =
                (CoordinatorLayout) mRootView.findViewById(R.id.card_view_detail_coordinator_layout);
        mCardImageView =
                (ImageView) mRootView.findViewById(R.id.card_detail_image_view);
        mCardTextVersion =
                (LinearLayout) mRootView.findViewById(R.id.card_detail_text_version);
        TextView nameTextView = (TextView) mRootView.findViewById(R.id.card_detail_name_text_view);
        mCardPriceTextView = (TextView) mRootView.findViewById(R.id.card_detail_price_text_view);
        LinearLayout manaCostContainer =
                (LinearLayout) mRootView.findViewById(R.id.card_detail_mana_cost_container_view);
        TextView cardTextView = (TextView) mRootView.findViewById(R.id.card_detail_text_view);
        TextView secondCardTextView =
                (TextView) mRootView.findViewById(R.id.card_detail_secondary_text_view);
        View cardTextSeparator = mRootView.findViewById(R.id.card_detail_text_separator);
        TabLayout tabLayout = (TabLayout) mRootView.findViewById(R.id.card_details_tab_layout);
        mDetailPager = (ViewPager) mRootView.findViewById(R.id.card_detail_view_pager);
        CardDetailPagerAdapter detailPagerAdapter =
                new CardDetailPagerAdapter(getChildFragmentManager());
        mFavoriteButton =
                (FloatingActionButton) mRootView.findViewById(R.id.card_view_detail_favorite_fab);

        if (mCardItem != null) {
            Point size = new Point();

            Display display = getActivity().getWindowManager().getDefaultDisplay();

            display.getSize(size);

            // Clean this up into a function since it will probably be used more than once
            final String imageUrl = "http://www.essentialtcg.com/images/" +
                    mCardItem.getSetCode() + "/" +
                    mCardItem.getImageName().replace(" ", "%20") +
                    ".jpg";//&height=" +
                    //String.valueOf(Util.dpToPx(getActivity(), 100));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mCardImageView.setTransitionName("source_" + String.valueOf(mCardItem.getId()));
            }

            mFavoriteButton.setOnClickListener(mFavoriteClickListener);

            Glide.with(getActivity())
                    .load(imageUrl)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .skipMemoryCache(true)
                    .listener(mCardImageRequestListener)
                    .into(mCardImageView);

            nameTextView.setText(mCardItem.getBothNames(getActivity()));

            if (mCardItem.getManaCost().length() > 0) {
                ArrayList<Integer> icons = CardUtil.parseIcons(mCardItem.getManaCost());

                manaCostContainer.removeAllViews();

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 15),
                                Util.dpToPx(getActivity(), 15));

                if (icons.size() > 5) {
                    params = new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 10),
                            Util.dpToPx(getActivity(), 10));
                }

                params.gravity = Gravity.END;

                manaCostContainer.setVisibility(View.VISIBLE);

                int milIcon = R.drawable.mana_1000000;

                for (Integer iconId : icons) {
                    ImageView manaCostImage = new ImageView(getActivity());

                    try {
                        Drawable manaIconDrawable;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            manaIconDrawable = getResources().getDrawable(iconId, null);
                        } else {
                            manaIconDrawable = getResources().getDrawable(iconId);
                        }

                        manaCostImage.setImageDrawable(manaIconDrawable);

                        if (iconId == milIcon && manaIconDrawable != null) {
                                LinearLayout.LayoutParams paramsOverride =
                                        new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 80),
                                                Util.calculateHeight(
                                                        manaIconDrawable.getIntrinsicWidth(),
                                                        manaIconDrawable.getIntrinsicHeight(),
                                                        Util.dpToPx(getActivity(), 80)
                                                ));

                                manaCostImage.setLayoutParams(paramsOverride);
                        } else {
                            manaCostImage.setLayoutParams(params);
                        }

                        manaCostContainer.addView(manaCostImage);
                    } catch (Exception ex) {
                        Log.e("MtM", mCardItem.getManaCost());
                    }
                }

                manaCostContainer.requestLayout();
            } else {
                manaCostContainer.removeAllViews();
            }

            if (mCardItem.getCardText() != null && mCardItem.getCardText().length() > 0) {
                cardTextView.setText(mCardItem.getCardText());
            }

            if (mCardItem.getSecondName() != null && mCardItem.getSecondName().length() > 0) {
                secondCardTextView.setVisibility(View.VISIBLE);
                cardTextSeparator.setVisibility(View.VISIBLE);

                if (mCardItem.getSecondCardText() != null
                        && mCardItem.getSecondCardText().length() > 0) {
                    secondCardTextView.setText(mCardItem.getSecondCardText());
                }
            }

            mDetailPager.setAdapter(detailPagerAdapter);

            tabLayout.setupWithViewPager(mDetailPager);

            tabLayout.setOnTabSelectedListener(mTabSelectedListener);
        }
    }

    private void startPostponedEnterTransition() {
        if (mCardItem.getId() == mSelectedItemId) {
            mCardImageView.getViewTreeObserver().addOnPreDrawListener(mCardImagePreDrawListener);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ARG_CARD_TO_SHOW, mCardItem);
        outState.putLong(ARG_SELECTED_ITEM_ID, mSelectedItemId);
    }

    @Override
    public void onPriceRetrievedCallback(String priceXml) {
        PriceItem priceItem = CardUtil.parsePrice(priceXml);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        if (mCardPriceTextView != null) {
            mCardPriceTextView.setText(String.format("H: %s L: %s A: %s F: %s",
                    currencyFormat.format(priceItem.getHighPrice()),
                    currencyFormat.format(priceItem.getLowPrice()),
                    currencyFormat.format(priceItem.getAveragePrice()),
                    currencyFormat.format(priceItem.getFoilPrice())
            ));
        }
    }

    /*@Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return FavoriteLoader.newInstanceFavoriteByCardInstance(getActivity(), mCardItem.getId());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        ContentValues values = new ContentValues();

        values.put(FavoriteContract.Favorites.CARD_ID, mCardItem.getId());

        if (cursor.getCount() == 0) {
            getActivity().getContentResolver().insert(FavoriteContract.Favorites.buildDirUri(),
                    values);

            Snackbar.make(mCoordinatorLayout, "Added to favorites", Snackbar.LENGTH_SHORT).show();
        } else {
            Snackbar.make(mCoordinatorLayout, "Removed from favorites", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        cursorLoader.abandon();
    }*/

    private class CardDetailPagerAdapter extends FragmentStatePagerAdapter {

        private final int NUM_TABS = 2;
        private RulesFragment mRulesFragment;
        private PrintingsFragment mPrintingsFragment;

        public CardDetailPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

            mRulesFragment = new RulesFragment();
            mPrintingsFragment = new PrintingsFragment();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mRulesFragment;
            } else if (position == 1) {
                return mPrintingsFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Rulings";
            } else if (position == 1) {
                return "Formats";
            }

            return super.getPageTitle(position);
        }
    }

}
