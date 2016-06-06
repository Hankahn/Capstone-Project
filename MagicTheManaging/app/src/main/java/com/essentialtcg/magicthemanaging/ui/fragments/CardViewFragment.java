package com.essentialtcg.magicthemanaging.ui.fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.Loader;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.essentialtcg.magicthemanaging.applications.AnalyticsApplication;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.items.CardItem;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.data.transforms.CardTransform;
import com.essentialtcg.magicthemanaging.events.UpdateFavoritesEvent;
import com.essentialtcg.magicthemanaging.events.UpdateRecyclerViewPositionEvent;
import com.essentialtcg.magicthemanaging.events.UpdateSearchCardViewEvent;
import com.essentialtcg.magicthemanaging.events.UpdateViewPagerPositionEvent;
import com.essentialtcg.magicthemanaging.ui.activities.CardViewActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CardViewFragment";

    private static final int CARD_VIEW_LOADER_ID = 4;

    private ViewPager mPager;
    private CardViewPagerAdapter mPagerAdapter;

    private CardViewDetailFragment mCardViewDetailFragment;

    private CardSearchParameters mSearchParameters;
    private Cursor mCursor;
    private int mStartPosition;
    private int mCurrentPosition;
    //private long mStartItemId;
    private long mSelectedItemId;
    private Tracker mTracker;

    private final SharedElementCallback mSharedElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mCardViewDetailFragment == null) {
                return;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ImageView sharedElement = mCardViewDetailFragment.getCardImageView();

                if (sharedElement == null) {
                    names.clear();
                    sharedElements.clear();
                } else if (mCurrentPosition != mStartPosition) {
                    names.clear();
                    names.add(sharedElement.getTransitionName());
                    sharedElements.clear();
                    sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                }
            }
        }
    };

    public static CardViewFragment newInstance(
            int startPosition, long selectedItemId, CardSearchParameters searchParameters) {
        Bundle arguments = new Bundle();
        CardViewFragment fragment = new CardViewFragment();

        arguments.putInt(CardViewActivity.INITIAL_CARD_POSITION, startPosition);
        arguments.putInt(CardViewActivity.CURRENT_CARD_POSITION, startPosition);
        arguments.putLong(CardViewActivity.SELECTED_ITEM_ID, selectedItemId);
        arguments.putParcelable(CardViewActivity.SEARCH_PARAMETERS, searchParameters);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsApplication application = (AnalyticsApplication) getActivity().getApplication();

        mTracker = application.getDefaultTracker();

        getActivity().setEnterSharedElementCallback(mSharedElementCallback);

        if (savedInstanceState == null) {
            if (getArguments().containsKey(CardViewActivity.INITIAL_CARD_POSITION)) {
                mStartPosition = getArguments().getInt(CardViewActivity.INITIAL_CARD_POSITION);
            }
        } else {
            mStartPosition = savedInstanceState.getInt(CardViewActivity.INITIAL_CARD_POSITION);
        }

        if (savedInstanceState == null) {
            if (getArguments().containsKey(CardViewActivity.CURRENT_CARD_POSITION)) {
                mCurrentPosition = getArguments().getInt(CardViewActivity.CURRENT_CARD_POSITION);
            }
        } else {
            mCurrentPosition = savedInstanceState.getInt(CardViewActivity.CURRENT_CARD_POSITION);
        }

        if (savedInstanceState == null) {
            if (getArguments().containsKey(CardViewActivity.SELECTED_ITEM_ID)) {
                mSelectedItemId = getArguments().getLong(CardViewActivity.SELECTED_ITEM_ID);
            }
        }

        if (savedInstanceState == null) {
            if (getArguments().containsKey(CardViewActivity.SEARCH_PARAMETERS)) {
                mSearchParameters = getArguments().getParcelable(CardViewActivity.SEARCH_PARAMETERS);
            }
        } else {
            mSearchParameters = savedInstanceState.getParcelable(CardViewActivity.SEARCH_PARAMETERS);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_view, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.card_view_toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (getActivity().getClass().equals(CardViewActivity.class)) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        }

        mPager = (ViewPager) rootView.findViewById(R.id.card_view_pager);
        mPagerAdapter = new CardViewPagerAdapter(getChildFragmentManager());

        mPagerAdapter.setCardViewFragment(this);

        mPager.setAdapter(mPagerAdapter);

        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mCursor.moveToPosition(position);

                EventBus.getDefault().post(new UpdateRecyclerViewPositionEvent(position));

                mSelectedItemId = mCursor.getLong(CardLoader.Query._ID);
                mCurrentPosition = position;
                mCardViewDetailFragment =
                        (CardViewDetailFragment) mPagerAdapter.getItem(position);

                sendViewedCardName();
            }
        });

        loadData();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CardViewActivity.INITIAL_CARD_POSITION, mStartPosition);
        outState.putInt(CardViewActivity.CURRENT_CARD_POSITION, mCurrentPosition);
        outState.putLong(CardViewActivity.SELECTED_ITEM_ID, mSelectedItemId);
        outState.putParcelable(CardViewActivity.SEARCH_PARAMETERS, mSearchParameters);

        super.onSaveInstanceState(outState);
    }

    private void loadData() {
        if (getLoaderManager().getLoader(CARD_VIEW_LOADER_ID) == null) {
            getLoaderManager().initLoader(CARD_VIEW_LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(CARD_VIEW_LOADER_ID, null, this);
        }
    }

    @Subscribe
    public void onUpdateViewPostionEvent(UpdateViewPagerPositionEvent event) {
        mPager.setCurrentItem(event.currentPosition);
    }

    @Subscribe
    public void onUpdateFavoritesEvent(UpdateFavoritesEvent event) {
        if (mSearchParameters == null) {
            getLoaderManager().restartLoader(CARD_VIEW_LOADER_ID, null, this);
        }
    }

    @Subscribe
    public void onUpdateSearchCardViewEvent(UpdateSearchCardViewEvent event) {
        mSearchParameters = event.searchParameters;

        getLoaderManager().restartLoader(CARD_VIEW_LOADER_ID, null, this);
    }

    private void sendViewedCardName() {
        String name = mCursor.getString(CardLoader.Query.NAME);

        mTracker.setScreenName("Card~" + name);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mSearchParameters != null) {
            return CardLoader.newCardsByCriteria(getActivity(), mSearchParameters);
        } else {
            return CardLoader.newCardsByFavorites(getActivity());
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;
        mPagerAdapter.notifyDataSetChanged();

        if (mStartPosition > 0) {
            mPager.setCurrentItem(mCurrentPosition, false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;

        try {
            mPagerAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Log.e(TAG, "PagerAdapter null");
        }
    }

    public void scrollToPosition(int position) {
        mCurrentPosition = position;
        mPager.setCurrentItem(position);
    }

    private class CardViewPagerAdapter extends FragmentStatePagerAdapter {

        private CardViewFragment mCardViewFragment;

        public CardViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            mCursor.moveToPosition(position);

            CardItem cardItem = CardTransform.transformInstance(mCursor);

            return CardViewDetailFragment.newInstance(cardItem, mSelectedItemId);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);

            mCardViewDetailFragment = (CardViewDetailFragment) object;
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            mCursor.moveToPosition(position);

            return mCursor.getString(CardLoader.Query.NAME);
        }

        public void setCardViewFragment(CardViewFragment cardViewFragment) {
            mCardViewFragment = cardViewFragment;
        }

    }

}
