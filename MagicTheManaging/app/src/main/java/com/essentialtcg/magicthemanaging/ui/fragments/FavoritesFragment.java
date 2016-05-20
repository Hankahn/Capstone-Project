package com.essentialtcg.magicthemanaging.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.adapters.FavoritesResultsRecyclerAdapter;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.views.EmptyRecyclerView;

public class FavoritesFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private RecyclerView mRecyclerView;
    private TextView mEmptyResultTextView;

    private int mPosition = 0;

    public FavoritesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.favorites_recycler_view);
        mEmptyResultTextView = (TextView) rootView.findViewById(R.id.favorites_empty_reset_text_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new EmptyRecyclerView());

        LoadData();

        return rootView;
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

        FavoritesResultsRecyclerAdapter adapter = new FavoritesResultsRecyclerAdapter(cursor);

        adapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(mPosition);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void LoadData() {
        getLoaderManager().restartLoader(0, null, this);
    }

}
