package com.essentialtcg.magicthemanaging.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.adapters.RulingsRecyclerAdapter;
import com.essentialtcg.magicthemanaging.data.loaders.RulingLoader;
import com.essentialtcg.magicthemanaging.views.EmptyRecyclerView;

/**
 * Created by Shawn on 4/14/2016.
 */
public class RulingsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = RulingsFragment.class.getSimpleName();

    private static final String CARD_ID_ARG = "CARD_ID_ARG";

    private static final int RULING_LOADER_ID = 10;

    private long mCardId;

    private RecyclerView mRecyclerView;
    private RulingsRecyclerAdapter mAdapter;

    private Cursor mCursor;

    public static RulingsFragment newInstance(long cardId) {
        RulingsFragment rulingsFragment = new RulingsFragment();
        Bundle arguments = new Bundle();

        arguments.putLong(CARD_ID_ARG, cardId);

        rulingsFragment.setArguments(arguments);

        return rulingsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments().containsKey(CARD_ID_ARG)) {
            mCardId = getArguments().getLong(CARD_ID_ARG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_rulings, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.rulings_recycler_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new EmptyRecyclerView());

        loadData();

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return RulingLoader.newRulingsByCardIdInstance(getActivity(), mCardId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mCursor = cursor;

        mAdapter = new RulingsRecyclerAdapter(cursor, getActivity());

        mAdapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.swapAdapter(mAdapter, true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursor = null;

        /*try {
            mPagerAdapter.notifyDataSetChanged();
        } catch (Exception ex) {
            Log.e(TAG, "PagerAdapter null");
        }*/
    }

    private void loadData() {
        if (getLoaderManager().getLoader(RULING_LOADER_ID) == null) {
            getLoaderManager().initLoader(RULING_LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(RULING_LOADER_ID, null, this);
        }
    }

}
