package com.essentialtcg.magicthemanaging;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.data.CardLoader;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.SetItem;
import com.essentialtcg.magicthemanaging.utils.SetArrayList;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        SetPickerDialogFragment.SetPickerDialogFragmentListener,
        RecyclerViewClickListener {

    private final String SEARCH_PARAMETERS_TAG = "SEARCH_PARAMETERS";
    private final String SEARCH_RESULTS_POSITION_TAG = "SEARCH_RESULTS_POSITION";
    private final int SET_PICKER_RESULTS = 0;

    private RecyclerView mRecyclerView;
    private TextView mEmptyResultTextView;
    private View mBottomSheet;
    private EditText mNameFilterEditText;
    private EditText mSetFilterEditText;
    private BottomSheetBehavior mBottomSheetBehavior;
    private FloatingActionButton mFab;
    private Button mSearchButton;
    private SearchFragment mFragment;

    private CardSearchParameters mSearchParameters = new CardSearchParameters();
    private int mPosition = 0;

    public SearchFragment() {
        mFragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.search_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        mEmptyResultTextView = (TextView) rootView.findViewById(R.id.empty_reset_text_view);
        mBottomSheet = rootView.findViewById(R.id.searc_bottom_sheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        mNameFilterEditText = (EditText) rootView.findViewById(R.id.name_filter_edit_text);
        mSetFilterEditText = (EditText) rootView.findViewById(R.id.set_filter_edit_text);
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mSearchButton = (Button) rootView.findViewById(R.id.search_button);

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
        }

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new EmptyRecyclerView());

        LoadData();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setExitSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onMapSharedElements(List<String> names,
                                                Map<String, View> sharedElements) {
                    /*if(mReenterState != null) {
                        int startPosition = mReenterState.getInt(STARTING_ARTICLE_POSITION);
                        int currentPosition = mReenterState.getInt(CURRENT_ARTICLE_POSITION);

                        if(startPosition != currentPosition) {
                            String updatedTransitionName =
                                    String.valueOf(
                                            mRecyclerView.getAdapter().getItemId(currentPosition));
                            View updatedSharedElement =
                                    mRecyclerView.findViewWithTag(updatedTransitionName);

                            Log.d("Heck4", "Updated transition name " + updatedTransitionName);

                            if(updatedSharedElement != null) {
                                Log.d("Heck4", "Shared element found " + updatedTransitionName);
                                names.clear();
                                names.add(updatedTransitionName);
                                sharedElements.clear();
                                sharedElements.put(updatedTransitionName, updatedSharedElement);
                            }
                        }

                        mReenterState = null;
                    }*/
                }
            });
        }


        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int position = 0;

        try {
            position = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();

        } catch (Exception ex) {
            position = 0;
        }

        outState.putInt(SEARCH_RESULTS_POSITION_TAG, position);
        outState.putParcelable(SEARCH_PARAMETERS_TAG, mSearchParameters);
    }

    @Override
    public void onReturnValue(SetArrayList selectedSets) {
        mSearchParameters.setSetFilter(selectedSets);
        mSetFilterEditText.setText(selectedSets.toString());
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

        /*ArrayList<CardItem> cardItems = CardTransform.transform(cursor);

        RecyclerAdapter adapter = new RecyclerAdapter(cardItems);*/
        SearchResultsRecyclerAdapter adapter = new SearchResultsRecyclerAdapter(cursor, this);

        adapter.setHasStableIds(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(mPosition);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private void LoadData() {
        getLoaderManager().restartLoader(0, null, this);
        //getLoaderManager().initLoader(0, null, this);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback =
        new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mFab.setVisibility(View.VISIBLE);
                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    mFab.setVisibility(View.INVISIBLE);
                    mBottomSheet.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {

            }
        };

    @Override
    public void recyclerViewItemClicked(View view, int position) {
        Intent viewCardIntent = new Intent(getActivity(), CardViewActivity.class);

        View croppedImageView = view.findViewById(R.id.cropped_image_view);

        Bundle bundle = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String transitionName = croppedImageView.getTransitionName();
            bundle = ActivityOptionsCompat
                    .makeSceneTransitionAnimation(
                            getActivity(),
                            croppedImageView,
                            transitionName)
                    .toBundle();
        }

        viewCardIntent.putExtra(CardViewActivity.INITIAL_CARD_POSITION, position);
        viewCardIntent.putExtra(CardViewActivity.SEARCH_PARAMETERS, mSearchParameters);

        startActivity(viewCardIntent, bundle);
    }

    private class SearchResultsRecyclerAdapter
            extends RecyclerView.Adapter<SearchResultsRecyclerAdapter.SearchResultViewHolder> {

        private Cursor mCursor;

        private RecyclerViewClickListener mCardClickListener;

        public SearchResultsRecyclerAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        public SearchResultsRecyclerAdapter(
                Cursor cursor, RecyclerViewClickListener cardClickListener) {
            mCursor = cursor;
            mCardClickListener = cardClickListener;
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
            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewCardIntent = new Intent(getActivity(), CardViewActivity.class);

                    viewCardIntent.putExtra(CardViewActivity.INITIAL_CARD_POSITION, 0);

                    startActivity(viewCardIntent);
                }
            });*/

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
            mCursor.moveToPosition(position);

            // Clean this up into a function since it will probably be used more than once
            final String imageUrl = "http://www.essentialtcg.com/images/" +
                    mCursor.getString(CardLoader.Query.SET_CODE) + "/" +
                    mCursor.getString(CardLoader.Query.IMAGE_NAME).replace(" ", "%20") +
                    ".jpg?cropyunits=100&cropxunits=100&crop=10,12,90,50&width=" +
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

            //Picasso.with(getActivity()).setIndicatorsEnabled(true);

            /*Picasso.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.sample_card_crop)
                    .into(holder.croppedImageView);*/

            PicassoBigCache.INSTANCE.getPicassoBigCache(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.sample_card_crop)
                    .into(holder.croppedImageView);

            holder.croppedImageView.setTag(String.valueOf(mCursor.getInt(CardLoader.Query._ID)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.croppedImageView.setTransitionName(
                        String.valueOf(mCursor.getInt(CardLoader.Query._ID)));
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
                mCardClickListener.recyclerViewItemClicked(view, this.getLayoutPosition());
            }

        }

    }

}
