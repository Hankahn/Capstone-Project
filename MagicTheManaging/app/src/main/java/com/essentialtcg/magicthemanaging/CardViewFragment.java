package com.essentialtcg.magicthemanaging;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.essentialtcg.magicthemanaging.data.CardItem;
import com.essentialtcg.magicthemanaging.data.CardLoader;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.CardTransform;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private ViewPager mPager;

    private CardViewDetailFragment mCurrentArticleDetailFragment;

    private CardSearchParameters mSearchParameters;
    private Cursor mCursor;
    private CardViewPagerAdapter mPagerAdapter;
    private int mStartPosition;
    private long mStartId;
    private long mSelectedItemId;

    public static CardViewFragment newInstance(
            int startPosition, CardSearchParameters searchParameters) {
        Bundle arguments = new Bundle();
        CardViewFragment fragment = new CardViewFragment();

        arguments.putInt(CardViewActivity.INITIAL_CARD_POSITION, startPosition);
        arguments.putParcelable(CardViewActivity.SEARCH_PARAMETERS, searchParameters);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActivity().postponeEnterTransition();

        if (savedInstanceState == null) {
            if (getArguments().containsKey(CardViewActivity.INITIAL_CARD_POSITION)) {
                mStartPosition = getArguments().getInt(CardViewActivity.INITIAL_CARD_POSITION);
            }
        } else {
            mStartPosition = savedInstanceState.getInt(CardViewActivity.INITIAL_CARD_POSITION);
        }

        if (getArguments().containsKey(CardViewActivity.SEARCH_PARAMETERS)) {
            mSearchParameters = getArguments().getParcelable(CardViewActivity.SEARCH_PARAMETERS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //getActivity().postponeEnterTransition();
            //getActivity().supportPostponeEnterTransition();
            //ActivityCompat.postponeEnterTransition(getActivity());
            //getActivity().getSupportFragmentManager().pos

            /*setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
                    if (mCurrentArticleDetailFragment == null) {
                        return;
                    }

                    ImageView sharedElement = mCurrentArticleDetailFragment.getCardImageView();

                    if (sharedElement == null) {
                        names.clear();
                        sharedElements.clear();
                    } else if (sharedElement != null && mSelectedItemId != mStartId) {
                        sharedElement.setTransitionName(String.valueOf(mSelectedItemId));
                        names.clear();
                        names.add(sharedElement.getTransitionName());
                        sharedElements.clear();
                        sharedElements.put(sharedElement.getTransitionName(), sharedElement);
                    }
                }
            });*/
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card_view, container, false);

        //ActivityCompat.postponeEnterTransition(getActivity());

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.card_view_toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_launcher);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        if (mSearchParameters == null) {
            mSearchParameters = new CardSearchParameters();
        }

        getLoaderManager().initLoader(0, null, this);

        mPagerAdapter = new CardViewPagerAdapter(getActivity().getSupportFragmentManager());
        //mPagerAdapter = new CardViewPagerAdapter(getChildFragmentManager());
        mPager = (ViewPager) rootView.findViewById(R.id.card_view_pager);
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener((SearchActivity) getActivity());
        //mPager.addOnPageChangeListener((CardViewActivity)getActivity());
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mCursor.moveToPosition(position);

                mSelectedItemId = mCursor.getLong(CardLoader.Query._ID);
                //mCurrentPosition = position;
                mCurrentArticleDetailFragment =
                        (CardViewDetailFragment) mPagerAdapter.getItem(position);
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CardViewActivity.INITIAL_CARD_POSITION, mPager.getCurrentItem());
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
            mPager.setCurrentItem(mStartPosition, false);
        }

        getActivity().startPostponedEnterTransition();
        //getActivity().supportPostponeEnterTransition();
        //getActivity().getSupportFragmentManager().executePendingTransactions();
        //ActivityCompat.startPostponedEnterTransition(getActivity());
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

            mCurrentArticleDetailFragment = (CardViewDetailFragment) object;
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
