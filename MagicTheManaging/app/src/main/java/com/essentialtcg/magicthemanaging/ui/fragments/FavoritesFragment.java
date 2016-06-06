package com.essentialtcg.magicthemanaging.ui.fragments;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.adapters.SearchResultsRecyclerAdapter;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.events.UpdateFavoritesEvent;
import com.essentialtcg.magicthemanaging.events.UpdateRecyclerViewPositionEvent;
import com.essentialtcg.magicthemanaging.events.UpdateRecyclerViewPositionReturnEvent;
import com.essentialtcg.magicthemanaging.ui.activities.MainActivity;
import com.essentialtcg.magicthemanaging.views.EmptyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

public class FavoritesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "FavoritesFragment";

    private static final int FAVORITE_LOADER_ID = 2;

    private SearchResultsRecyclerAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyResultTextView;

    private int mInitialPosition;
    private int mCurrentPosition;
    private boolean mReturning = false;

    private final SharedElementCallback mSharedElementCallback = new SharedElementCallback() {
        @Override
        public void onMapSharedElements(List<String> names,
                                        Map<String, View> sharedElements) {
            if (mReturning) {
                if (mCurrentPosition != mInitialPosition) {
                    long itemId = mRecyclerView.getAdapter().getItemId(mCurrentPosition);

                    String updatedTransitionName =
                            String.format("source_%s", String.valueOf(itemId));

                    SearchResultsRecyclerAdapter.SearchResultViewHolder viewHolder =
                            (SearchResultsRecyclerAdapter.SearchResultViewHolder)
                                    mRecyclerView.findViewHolderForItemId(itemId);

                    View updatedSharedElement = viewHolder.croppedImageView;

                    if (updatedSharedElement != null) {
                        names.clear();
                        names.add(updatedTransitionName);
                        sharedElements.clear();
                        sharedElements.put(updatedTransitionName, updatedSharedElement);
                    }
                }

                mReturning = false;
            }
        }
    };

    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        if (((MainActivity) getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Favorites");
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.favorites_recycler_view);
        mEmptyResultTextView = (TextView) rootView.findViewById(R.id.favorites_empty_reset_text_view);

        getActivity().setExitSharedElementCallback(mSharedElementCallback);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new EmptyRecyclerView());

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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return CardLoader.newCardsByFavorites(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.getCount() == 0) {
            mEmptyResultTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyResultTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mAdapter = SearchResultsRecyclerAdapter.newFavoritesInstance(
                cursor, getActivity(), (MainActivity) getActivity());

        mAdapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private void loadData() {
        if (getLoaderManager().getLoader(FAVORITE_LOADER_ID) == null) {
            getLoaderManager().initLoader(FAVORITE_LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
        }
    }

    @Subscribe
    public void onUpdateRecyclerViewPositionEvent(UpdateRecyclerViewPositionEvent event) {
        mRecyclerView.scrollToPosition(event.currentPosition);
        mAdapter.setCurrentPosition(event.currentPosition);
    }

    @Subscribe
    public void onUpdateFavoritesEvent(UpdateFavoritesEvent event) {
        getLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
    }

    @Subscribe
    public void onUpdateRecyclerViewPositionEvent(UpdateRecyclerViewPositionReturnEvent event) {
        mRecyclerView.scrollToPosition(event.currentPosition);

        mInitialPosition = event.initialPosition;
        mCurrentPosition = event.currentPosition;

        mReturning = true;

        mRecyclerView.invalidate();

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                mRecyclerView.requestLayout();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    getActivity().startPostponedEnterTransition();
                }

                return true;
            }
        });
    }

}
