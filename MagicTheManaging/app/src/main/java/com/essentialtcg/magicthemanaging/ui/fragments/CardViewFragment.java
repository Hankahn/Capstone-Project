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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.items.CardItem;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.data.transforms.CardTransform;
import com.essentialtcg.magicthemanaging.ui.activities.CardViewActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CardViewFragment";

    private ViewPager mPager;

    private CardViewDetailFragment mCardViewDetailFragment;

    private CardSearchParameters mSearchParameters;
    private Cursor mCursor;
    private CardViewPagerAdapter mPagerAdapter;
    private int mStartPosition;
    private int mCurrentPosition;
    private long mStartItemId;
    private long mSelectedItemId;

    private final SharedElementCallback mSharedElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
            if (mCardViewDetailFragment == null) {
                return;
            }

            ImageView sharedElement = mCardViewDetailFragment.getCardImageView();

            if (sharedElement == null) {
                names.clear();
                sharedElements.clear();
            } else if (mCurrentPosition != mStartPosition) {
                /*sharedElement.setTransitionName(String.format("source_%s",
                        mSelectedItemId));*/
                Log.d(TAG, "onMapSharedElements: 2 " + sharedElement.getTransitionName());
                names.clear();
                names.add(sharedElement.getTransitionName());
                sharedElements.clear();
                sharedElements.put(sharedElement.getTransitionName(), sharedElement);
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

        //getActivity().postponeEnterTransition();

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

        if (getArguments().containsKey(CardViewActivity.SELECTED_ITEM_ID)) {
            mSelectedItemId = getArguments().getLong(CardViewActivity.SELECTED_ITEM_ID);
        }

        if (getArguments().containsKey(CardViewActivity.SEARCH_PARAMETERS)) {
            mSearchParameters = getArguments().getParcelable(CardViewActivity.SEARCH_PARAMETERS);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_view, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.card_view_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (mSearchParameters == null) {
            mSearchParameters = new CardSearchParameters();
        }

        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new CardViewPagerAdapter(getActivity().getSupportFragmentManager());
        //mPagerAdapter = new CardViewPagerAdapter(getChildFragmentManager());
        mPager = (ViewPager) rootView.findViewById(R.id.card_view_pager);
        mPager.setAdapter(mPagerAdapter);
        //mPager.addOnPageChangeListener((SearchActivity) getActivity());
        mPager.addOnPageChangeListener((CardViewActivity)getActivity());
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mCursor.moveToPosition(position);

                mSelectedItemId = mCursor.getLong(CardLoader.Query._ID);
                mCurrentPosition = position;
                mCardViewDetailFragment =
                        (CardViewDetailFragment) mPagerAdapter.getItem(position);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(CardViewActivity.INITIAL_CARD_POSITION, mStartPosition);
        outState.putInt(CardViewActivity.CURRENT_CARD_POSITION, mCurrentPosition);
    }

    public Fragment getCurrentPagerFragment(int position) {
        FragmentStatePagerAdapter a = (FragmentStatePagerAdapter) mPager.getAdapter();
        return (Fragment) a.instantiateItem(mPager, position);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return CardLoader.newCardsByCriteria(getActivity(), mSearchParameters);
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
        mPagerAdapter.notifyDataSetChanged();
    }

    private class CardViewPagerAdapter extends FragmentStatePagerAdapter {

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
    }

}
