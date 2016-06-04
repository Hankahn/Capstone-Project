package com.essentialtcg.magicthemanaging.ui.fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.adapters.SearchResultsRecyclerAdapter;
import com.essentialtcg.magicthemanaging.behaviors.ScrollFabBehavior;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.items.SetItem;
import com.essentialtcg.magicthemanaging.events.UpdateSearchCardViewEvent;
import com.essentialtcg.magicthemanaging.events.UpdateRecyclerViewPositionEvent;
import com.essentialtcg.magicthemanaging.events.UpdateRecyclerViewPositionReturnEvent;
import com.essentialtcg.magicthemanaging.ui.activities.MainActivity;
import com.essentialtcg.magicthemanaging.views.EmptyRecyclerView;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.utils.Util;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.utils.SetArrayList;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SetPickerDialogFragment.SetPickerDialogFragmentListener {

    private static final String TAG = "SearchFragment";

    private static final String SEARCH_PARAMETERS_TAG = "SEARCH_PARAMETERS";
    private static final String SEARCH_RESULTS_POSITION_TAG = "SEARCH_RESULTS_POSITION";
    private final int SET_PICKER_RESULTS = 0;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final int SEARCH_LOADER_ID = 1;

    private RecyclerView mRecyclerView;
    private SearchResultsRecyclerAdapter mAdapter;
    private TextView mEmptyResultTextView;
    private View mBottomSheet;
    private EditText mNameFilterEditText;
    private EditText mSetFilterEditText;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FloatingActionButton mFab;
    private CoordinatorLayout.LayoutParams mFabLayoutParams;
    private FloatingActionButton mSearchButton;
    private SearchFragment mFragment;
    private boolean mReturning = false;

    private CardSearchParameters mSearchParameters = new CardSearchParameters();
    private int mPosition = 0;
    private int mCursorPosition;
    private int mInitialPosition;
    private int mCurrentPosition;
    private boolean mIsAnimatingOut = false;

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

    public SearchFragment() {
        mFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        if (((MainActivity) getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setSubtitle("Search Results");
        }

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        mEmptyResultTextView = (TextView) rootView.findViewById(R.id.empty_reset_text_view);
        mBottomSheet = rootView.findViewById(R.id.search_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mNameFilterEditText = (EditText) rootView.findViewById(R.id.name_filter_edit_text);
        mSetFilterEditText = (EditText) rootView.findViewById(R.id.set_filter_edit_text);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFabLayoutParams = (CoordinatorLayout.LayoutParams) mFab.getLayoutParams();
        mSearchButton = (FloatingActionButton) rootView.findViewById(R.id.search_button);

        mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);

        if (savedInstanceState != null) {
            mPosition = savedInstanceState.getInt(SEARCH_RESULTS_POSITION_TAG);
            mSearchParameters = savedInstanceState.getParcelable(SEARCH_PARAMETERS_TAG);
        } else {
            mSearchParameters = new CardSearchParameters();

            SetArrayList setFilter = new SetArrayList();
            SetItem setItem = new SetItem();
            setItem.setName("Oath of the Gatewatch");
            setItem.setCode("OGW");
            setFilter.add(setItem);

            mSearchParameters.setSetFilter(setFilter);

            mPosition = 0;
        }

        mNameFilterEditText.setText(mSearchParameters.getNameFilter());

        if (mSearchParameters.getSetFilter() != null &&
                mSearchParameters.getSetFilter().size() > 0) {
            mSetFilterEditText.setText(mSearchParameters.getSetFilter().toString());
        } else {
            mSetFilterEditText.setText("All");
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                fabAnimateOut(mFab);
                mBottomSheet.setVisibility(View.VISIBLE);
            }
        });

        mNameFilterEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Util.hideSoftKeyboard(getActivity());
                }

                return false;
            }
        });

        mSetFilterEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPickerDialogFragment setPickerFragment =
                        SetPickerDialogFragment.newInstance(mSearchParameters);
                setPickerFragment.setTargetFragment(mFragment, SET_PICKER_RESULTS);
                setPickerFragment.show(getFragmentManager(), "Set Picker");
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchParameters.setNameFilter(mNameFilterEditText.getText().toString());

                writeSearchPreferences();

                mNameFilterEditText.clearFocus();

                Util.hideSoftKeyboard(getActivity());

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheet.setVisibility(View.INVISIBLE);

                mPosition = 0;

                loadData();

                EventBus.getDefault().post(new UpdateSearchCardViewEvent(mSearchParameters));
            }
        });

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

    private void writeSearchPreferences() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("PREF_SEARCH_NAME", mSearchParameters.getNameFilter());
        editor.putString("PREF_SEARCH_SETS", mSearchParameters.getSetFilter().toPrefString());

        editor.apply();
    }

    private void readSearchParameters() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        String nameFilter = preferences.getString("PREF_SEARCH_NAME", "");
        String setFilter = preferences.getString("PREF_SEARCH_SETS", "");

        mSearchParameters.setNameFilter(nameFilter);
        mSearchParameters.setSetFilter(new SetArrayList(setFilter));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int position;

        try {
            position = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();

        } catch (Exception ex) {
            position = 0;
        }

        outState.putInt(SEARCH_RESULTS_POSITION_TAG, position);
        outState.putParcelable(SEARCH_PARAMETERS_TAG, mSearchParameters);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onReturnValue(SetArrayList selectedSets) {
        mSearchParameters.setSetFilter(selectedSets);
        String fieldText = selectedSets.toString();
        mSetFilterEditText.setText(fieldText.equals("") ? "ALL" : fieldText);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return CardLoader.newCardsByCriteria(getActivity(), mSearchParameters);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if (cursor.getCount() == 0) {
            mEmptyResultTextView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmptyResultTextView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mAdapter = new SearchResultsRecyclerAdapter(cursor, mSearchParameters, getActivity(),
                (MainActivity) getActivity());

        mAdapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(mPosition);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.swapAdapter(mAdapter, true);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private void loadData() {
        if (getLoaderManager().getLoader(SEARCH_LOADER_ID) == null) {
            getLoaderManager().initLoader(SEARCH_LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(SEARCH_LOADER_ID, null, this);
        }
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback =
            new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        fabAnimateIn(mFab);
                        mFabLayoutParams.setBehavior(new ScrollFabBehavior(getActivity(), null));
                        //mFab.setLayoutParams(mFabLayoutParams);
                    } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        mBottomSheet.setVisibility(View.VISIBLE);
                        mFabLayoutParams.setBehavior(null);
                        mFab.setLayoutParams(mFabLayoutParams);
                    }
                }

                @Override
                public void onSlide(View bottomSheet, float slideOffset) {

                }
            };

    @Subscribe
    public void onUpdateRecyclerViewPositionEvent(UpdateRecyclerViewPositionEvent event) {
        mRecyclerView.scrollToPosition(event.currentPosition);
        mAdapter.setCurrentPosition(event.currentPosition);
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

    // Same animation that FloatingActionButton.Behavior uses to hide the FAB when the AppBarLayout exits
    private void fabAnimateOut(final FloatingActionButton button) {
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(0.0F).scaleY(0.0F).alpha(0.0F).setInterpolator(INTERPOLATOR).withLayer()
                    .setListener(new ViewPropertyAnimatorListener() {
                        public void onAnimationStart(View view) {
                            mIsAnimatingOut = true;
                        }

                        public void onAnimationCancel(View view) {
                            mIsAnimatingOut = false;
                        }

                        public void onAnimationEnd(View view) {
                            mIsAnimatingOut = false;
                            view.setVisibility(View.GONE);
                        }
                    }).start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.design_fab_out);
            anim.setInterpolator(INTERPOLATOR);
            anim.setDuration(200L);
            anim.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {
                    mIsAnimatingOut = true;
                }

                public void onAnimationEnd(Animation animation) {
                    mIsAnimatingOut = false;
                    button.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(final Animation animation) {
                }
            });
            button.startAnimation(anim);
        }
    }

    // Same animation that FloatingActionButton.Behavior uses to show the FAB when the AppBarLayout enters
    private void fabAnimateIn(FloatingActionButton button) {
        button.setVisibility(View.VISIBLE);
        if (Build.VERSION.SDK_INT >= 14) {
            ViewCompat.animate(button).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
        } else {
            Animation anim = AnimationUtils.loadAnimation(button.getContext(), R.anim.design_fab_in);
            anim.setDuration(200L);
            anim.setInterpolator(INTERPOLATOR);
            button.startAnimation(anim);
        }
    }

}
