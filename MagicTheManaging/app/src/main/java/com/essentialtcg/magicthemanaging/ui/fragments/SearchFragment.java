package com.essentialtcg.magicthemanaging.ui.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.transition.ChangeImageTransform;
import android.transition.Fade;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.essentialtcg.magicthemanaging.adapters.SearchResultsRecyclerAdapter;
import com.essentialtcg.magicthemanaging.applications.AnalyticsApplication;
import com.essentialtcg.magicthemanaging.behaviors.ScrollFabBehavior;
import com.essentialtcg.magicthemanaging.callback.UpdateRecyclerViewCallback;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.items.SetItem;
import com.essentialtcg.magicthemanaging.views.EmptyRecyclerView;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.utils.Util;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.ui.activities.CardViewActivity;
import com.essentialtcg.magicthemanaging.utils.CardUtil;
import com.essentialtcg.magicthemanaging.utils.SetArrayList;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        UpdateRecyclerViewCallback,
        SetPickerDialogFragment.SetPickerDialogFragmentListener {

    private static final String TAG = "SearchFragment";

    private static final String SEARCH_PARAMETERS_TAG = "SEARCH_PARAMETERS";
    private static final String SEARCH_RESULTS_POSITION_TAG = "SEARCH_RESULTS_POSITION";
    private final int SET_PICKER_RESULTS = 0;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final int LOADER_ID = 1;

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
    //private Button mSearchButton;
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
                    /*mRecyclerView.scrollToPosition(mCurrentPosition);
                    mRecyclerView.requestLayout();*/

                    long itemId = mRecyclerView.getAdapter().getItemId(mCurrentPosition);

                    String updatedTransitionName =
                            String.format("source_%s", String.valueOf(itemId));

                    Log.d(TAG, "onMapSharedElements: 1 " + updatedTransitionName);

                    SearchResultsRecyclerAdapter.SearchResultViewHolder viewHolder =
                            (SearchResultsRecyclerAdapter.SearchResultViewHolder)
                                    mRecyclerView.findViewHolderForItemId(itemId);

                    View itemView = mRecyclerView.getLayoutManager().findViewByPosition(mCurrentPosition);

                    View updatedSharedElement = viewHolder.croppedImageView;

                    /*ImageView croppedImageView = (ImageView) mRecyclerView.findViewWithTag(
                        updatedTransitionName);

                    View updatedSharedElement = croppedImageView;*/

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
                if(actionId == EditorInfo.IME_ACTION_DONE) {
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

                mNameFilterEditText.clearFocus();

                Util.hideSoftKeyboard(getActivity());

                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                mBottomSheet.setVisibility(View.INVISIBLE);

                mPosition = 0;

                LoadData();
            }
        });

        //getActivity().setEnterSharedElementCallback(mSharedElementCallback);
        getActivity().setExitSharedElementCallback(mSharedElementCallback);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new EmptyRecyclerView());

        LoadData();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);

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

        mAdapter = new SearchResultsRecyclerAdapter(cursor, mSearchParameters, getActivity());
        //SearchResultsRecyclerAdapter adapter = new SearchResultsRecyclerAdapter(cursor);

        mAdapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(mPosition);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.swapAdapter(mAdapter, true);
        //mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private void LoadData() {
        if (getLoaderManager().getLoader(LOADER_ID) == null) {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().restartLoader(0, null, this);
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

    @Override
    public void onUpdateRecyclerViewCallback(int initialPosition, int currentPosition) {
        if (initialPosition != currentPosition) {
            mRecyclerView.scrollToPosition(currentPosition);
        }

        mInitialPosition = initialPosition;
        mCurrentPosition = currentPosition;

        mReturning = true;

        mRecyclerView.invalidate();
        /*if (mRecyclerView.()) {
            getActivity().startPostponedEnterTransition();

            return;
        }*/

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);
                // TODO: figure out why it is necessary to request layout here in order to get a smooth transition.
                mRecyclerView.requestLayout();
                Toast.makeText(getActivity(), "starting postponed transitions", Toast.LENGTH_SHORT).show();
                getActivity().startPostponedEnterTransition();
                //startPostponedEnterTransition();
                return true;
            }
        });
    }

    private void startPostponedEnterTransition() {
        if (mRecyclerView.isAttachedToWindow()) {
            getActivity().startPostponedEnterTransition();

            return;
        }

        mRecyclerView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mRecyclerView.getViewTreeObserver().removeOnPreDrawListener(this);

                mRecyclerView.requestLayout();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Toast.makeText(getActivity(), "starting postponed transitions", Toast.LENGTH_SHORT).show();
                    getActivity().startPostponedEnterTransition();
                }

                return true;
            }
        });
    }

    /*private class SearchResultsRecyclerAdapter extends
            RecyclerView.Adapter<SearchResultsRecyclerAdapter.SearchResultViewHolder> {

        private Cursor mCursor;

        public SearchResultsRecyclerAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(CardLoader.Query._ID);
        }

        @Override
        public SearchResultViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(
                    R.layout.list_item_search_card, parent, false);
            final SearchResultViewHolder viewHolder = new SearchResultViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewCardIntent = new Intent(getActivity(), CardViewActivity.class);

                    viewCardIntent.putExtra(CardViewActivity.INITIAL_CARD_POSITION, 0);

                    startActivity(viewCardIntent);
                }
            });

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
            mCursorPosition = position;
            mCursor.moveToPosition(position);

            // Clean this up into a function since it will probably be used more than once
            String imageName = mCursor.getString(CardLoader.Query.IMAGE_NAME).replace(" ", "%20");

            try {
                imageName = URLEncoder.encode(imageName, "UTF-8");
            } catch (Exception ex) {

            }

            final String imageUrl = String.format("http://www.essentialtcg.com/images/%s/%s.jpg?height=%s",
                    mCursor.getString(CardLoader.Query.SET_CODE),
                    imageName,
                    Util.dpToPx(getActivity(), 43));
            Log.d(TAG, "onBindViewHolder: " + imageUrl);
            final String imageUrl = "http://www.essentialtcg.com/images/" +
                    mCursor.getString(CardLoader.Query.SET_CODE) + "/" +
                    mCursor.getString(CardLoader.Query.IMAGE_NAME).replace(" ", "%20") +
                    ".jpg" ?cropyunits=100&cropxunits=100&crop=10,12,90,50"; + "?width=" +
                    String.valueOf(Util.dpToPx(getActivity(), 50));

            if (mCursor.getString(CardLoader.Query.NAME2) != null) {
                holder.nameTextView.setText(String.format(
                        getActivity().getResources().getString(R.string.CARD_NAME_DOUBLE_SIDED),
                        mCursor.getString(CardLoader.Query.NAME),
                        mCursor.getString(CardLoader.Query.NAME2)));
            } else {
                holder.nameTextView.setText(mCursor.getString(CardLoader.Query.NAME));
            }

            try {
                int resourceId = CardUtil.parseSetRarity(
                        mCursor.getString(CardLoader.Query.SET_CODE),
                        mCursor.getString(CardLoader.Query.RARITY));

                try {
                    Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), resourceId);
                    int imageHeight = Util.dpToPx(getActivity(), 15);//holder.typeTextView.getLineHeight();
                    int imageWidth = Util.calculateWidth(iconDrawable.getIntrinsicWidth(),
                            iconDrawable.getIntrinsicHeight(), imageHeight);

                    iconDrawable.setBounds(0, 0, imageWidth, imageHeight);

                    SpannableStringBuilder builder = new SpannableStringBuilder();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder.append(" ", new ImageSpan(iconDrawable), 0);
                    } else {
                        builder.append(" ");
                        builder.setSpan(new ImageSpan(iconDrawable),
                                builder.length() - 1, builder.length(), 0);
                    }

                    holder.setRarityTextView.setText(builder);
                } catch (Exception ex) {
                    Log.e("MtM", ex.toString());
                    holder.setRarityTextView.setText(String.format("%s %s",
                            mCursor.getString(CardLoader.Query.SET_CODE),
                            mCursor.getString(CardLoader.Query.RARITY).substring(0, 1)));
                }
            } catch (Exception ex) {
                Log.d("MtM", mCursor.getString(CardLoader.Query.SET_CODE)
                        + "_" + mCursor.getString(CardLoader.Query.RARITY));
            }

            holder.typeTextView.setText(mCursor.getString(CardLoader.Query.TYPE));

            String manaCost = mCursor.getString(CardLoader.Query.MANA_COST);
            String secondManaCost = mCursor.getString(CardLoader.Query.MANA_COST2);

            //LinearLayout manaCostContainer = holder.manaCostContainerView;

            if (manaCost.length() > 0) {
                ArrayList<Integer> icons = CardUtil.parseIcons(manaCost);

                ArrayList<Integer> secondIcons = null;

                if (secondManaCost != null && secondManaCost.length() > 0) {
                    secondIcons = CardUtil.parseIcons(secondManaCost);
                }

                SpannableStringBuilder builder = new SpannableStringBuilder();

                int imageHeightdp = 15;//icons.size() > 5 || (secondIcons != null && secondIcons.size() > 0) ?
                //10 : 15;

                for(Integer iconId : icons) {
                    Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), iconId);
                    int imageHeight = Util.dpToPx(getActivity(), imageHeightdp);
                    int imageWidth = Util.calculateWidth(iconDrawable.getIntrinsicWidth(),
                            iconDrawable.getIntrinsicHeight(), imageHeight);

                    iconDrawable.setBounds(0, 0, imageWidth, imageHeight);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder.append(" ", new ImageSpan(iconDrawable), 0);
                    } else {
                        builder.append(" ");
                        builder.setSpan(new ImageSpan(iconDrawable),
                                builder.length() - 1, builder.length(), 0);
                    }
                }

                if (secondIcons != null && secondIcons.size() > 0) {
                    builder.append("/");
                    for (Integer iconId : secondIcons) {
                        Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), iconId);
                        int imageHeight = Util.dpToPx(getActivity(), imageHeightdp);
                        int imageWidth = Util.calculateWidth(iconDrawable.getIntrinsicWidth(),
                                iconDrawable.getIntrinsicHeight(), imageHeight);

                        iconDrawable.setBounds(0, 0, imageWidth, imageHeight);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder.append(" ", new ImageSpan(iconDrawable), 0);
                        } else {
                            builder.append(" ");
                            builder.setSpan(new ImageSpan(iconDrawable),
                                    builder.length() - 1, builder.length(), 0);
                        }
                    }
                }

                holder.manaCostTextView.setText(builder);

            } else {
                holder.manaCostTextView.setText("");
            }

            String type = mCursor.getString(CardLoader.Query.TYPE).toLowerCase();

            if (type.startsWith(getActivity().getString(R.string.TYPE_CREATURE_STARTS))) {
                holder.featuredStatTextView.setText(String.format(
                        getActivity().getString(R.string.FEATURED_STAT_POWER_TOUGHNESS_FORMAT),
                        mCursor.getString(CardLoader.Query.POWER),
                        mCursor.getString(CardLoader.Query.TOUGHNESS)));
            } else if (type.startsWith(getActivity().getString(R.string.TYPE_PLANESWALKER_STARTS))) {
                holder.featuredStatTextView.setText(mCursor.getString(CardLoader.Query.LOYALTY));
            } else {
                holder.featuredStatTextView.setText(R.string.NO_FEATURED_STAT);
            }

            if (position == mCurrentPosition) {
                // TODO: Try skipMemoryCache
                Glide.with(getActivity())
                        .load(imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .skipMemoryCache(true)
                        .listener(new RequestListener<String, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                                //getActivity().startPostponedEnterTransition();
                                //startPostponedEnterTransition(holder.croppedImageView);

                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                //startPostponedEnterTransition(holder.croppedImageView);

                                return false;
                            }
                        })
                        .into(holder.croppedImageView);
            } else {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    //.skipMemoryCache(true)
                    .dontTransform()
                    .dontAnimate()
                    .placeholder(R.mipmap.card_back)
                    .into(holder.croppedImageView);
            //}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String transitionName = String.format("source_%s",
                        mCursor.getInt(CardLoader.Query._ID));

                holder.croppedImageView.setTransitionName(transitionName);
            }
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }

        public class SearchResultViewHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener {

            public ImageView croppedImageView;
            public TextView nameTextView;
            public TextView setRarityTextView;
            public TextView typeTextView;
            public LinearLayout rightContainer;
            public TextView manaCostTextView;
            public TextView featuredStatTextView;

            public SearchResultViewHolder(View view) {
                super(view);

                croppedImageView = (ImageView)view.findViewById(R.id.cropped_image_view);
                nameTextView = (TextView) view.findViewById(R.id.card_name_text_view);
                setRarityTextView = (TextView) view.findViewById(R.id.set_rarity_text_view);
                typeTextView = (TextView) view.findViewById(R.id.type_text_view);
                rightContainer = (LinearLayout) view.findViewById(R.id.right_container);
                manaCostTextView = (TextView) view.findViewById(R.id.mana_cost_text_view);
                featuredStatTextView = (TextView) view.findViewById(R.id.featured_stat_text_view);

                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                int position = this.getLayoutPosition();

                Intent viewCardIntent = new Intent(getActivity(), CardViewActivity.class);

                View croppedImageView = view.findViewById(R.id.cropped_image_view);

                Bundle bundle = null;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    setSharedElementEnterTransition(new ChangeImageTransform());
                    setSharedElementReturnTransition(new ChangeImageTransform());
                    //setSharedElementEnterTransition(new android.transition.Fade());

                    bundle = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(
                                    getActivity(),
                                    croppedImageView,
                                    croppedImageView.getTransitionName())
                            .toBundle();
                }

                mCursor.moveToPosition(position);

                viewCardIntent.putExtra(CardViewActivity.INITIAL_CARD_POSITION, position);
                viewCardIntent.putExtra(CardViewActivity.SELECTED_ITEM_ID,
                        mCursor.getLong(CardLoader.Query._ID));
                viewCardIntent.putExtra(CardViewActivity.SEARCH_PARAMETERS, mSearchParameters);

                //croppedImageAnimateOut((ImageView) croppedImageView);

                startActivity(viewCardIntent, bundle);

                Log.d("MtMT", croppedImageView.getTransitionName() + " -> " +
                        croppedImageView.getTransitionName());

                CardViewFragment cardViewFragment = CardViewFragment.newInstance(
                position, mSearchParameters);

                cardViewFragment.setSharedElementEnterTransition(new DetailTransition());
                setSharedElementEnterTransition(new DetailTransition());
                cardViewFragment.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                setSharedElementReturnTransition(new DetailTransition());

                FragmentTransitionUtil.getInstance(getFragmentManager())
                        .transition(R.id.fragment_container, this, cardViewFragment, croppedImageView,
                                croppedImageView.getTransitionName());

                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        //.addSharedElement(croppedImageView, croppedImageView.getTransitionName())// destinationTransitionName)
                        //.add(R.id.fragment_container, cardViewFragment)
                        .replace(R.id.fragment_container, cardViewFragment)
                        .addToBackStack(null)
                        .commit();
            }

        }

        private void startPostponedEnterTransition(ImageView imageView) {
            final ImageView iv = imageView;

            iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    iv.getViewTreeObserver().removeOnPreDrawListener(this);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getActivity().startPostponedEnterTransition();
                    }

                    return true;
                }
            });
        }

    }*/

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
