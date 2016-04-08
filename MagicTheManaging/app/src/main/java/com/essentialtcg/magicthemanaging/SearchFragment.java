package com.essentialtcg.magicthemanaging;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
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
import android.view.Gravity;
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

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        SetPickerDialogFragment.SetPickerDialogFragmentListener {

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

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.card_recycler_view);
        mEmptyResultTextView = (TextView) rootView.findViewById(R.id.empty_reset_text_view);
        mBottomSheet = rootView.findViewById(R.id.bottom_sheet);
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
            mPosition = 0;
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

        /*mNameFilterEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)
                        getActivity().getSystemService(((AppCompatActivity) getActivity()).INPUT_METHOD_SERVICE);

                imm.showSoftInput(mNameFilterEditText, InputMethodManager.SHOW_IMPLICIT);
            }
        });*/

        mSetFilterEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetPickerDialogFragment setPickerFragment = new SetPickerDialogFragment();
                setPickerFragment.setTargetFragment(mFragment, SET_PICKER_RESULTS);
                setPickerFragment.show(getFragmentManager(), "Set Picker");
            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchParameters.setNameFilter(mNameFilterEditText.getText().toString());

                mPosition = 0;

                LoadData();
            }
        });

        LoadData();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int position = 0;

        try {
            ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                    .findFirstVisibleItemPosition();

        } catch (Exception ex) {
            position = 0;
        }

        outState.putInt(SEARCH_RESULTS_POSITION_TAG, position);
        outState.putParcelable(SEARCH_PARAMETERS_TAG, mSearchParameters);
    }

    @Override
    public void onReturnValue(ArrayList<SetItem> selectedSets) {
        mSearchParameters.setSetFilter(selectedSets);

        StringBuilder selectedSetsText = new StringBuilder();

        for (SetItem set : selectedSets) {
            if(selectedSetsText.length() > 0) {
                selectedSetsText.append(" or ");
            }

            selectedSetsText.append(set.getName());
        }

        mSetFilterEditText.setText(selectedSetsText.toString());

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
        RecyclerAdapter adapter = new RecyclerAdapter(cursor);

        adapter.setHasStableIds(true);
        mRecyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.scrollToPosition(mPosition);
        mRecyclerView.setLayoutManager(layoutManager);
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

    private class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        private Cursor mCursor;

        public RecyclerAdapter(Cursor cursor) {
            mCursor = cursor;
        }

        @Override
        public long getItemId(int position) {
            mCursor.moveToPosition(position);
            return mCursor.getLong(CardLoader.Query._ID);
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(
                    R.layout.list_item_search_card, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            mCursor.moveToPosition(position);

            // Clean this up into a function since it will probably be used more than once
            String imageUrl = "http://www.essentialtcg.com/images/ogw/crops/" +
                    mCursor.getString(CardLoader.Query.NAME).replace(" ", "%20") +
                    ".crop.jpg?width=" + String.valueOf(Util.dpToPx(getActivity(), 50));

            if (mCursor.getString(CardLoader.Query.NAME2) != null) {
                holder.nameTextView.setText(mCursor.getString(CardLoader.Query.NAME) +
                        " / " + mCursor.getString(CardLoader.Query.NAME2));
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
                            mCursor.getString(CardLoader.Query.RARITY)));
                }
            } catch (Exception ex) {
                Log.d("MtM", mCursor.getString(CardLoader.Query.SET_CODE)
                        + "_" + mCursor.getString(CardLoader.Query.RARITY));
            }

            holder.typeTextView.setText(mCursor.getString(CardLoader.Query.TYPE));

            String manaCost = mCursor.getString(CardLoader.Query.MANA_COST);

            LinearLayout manaCostContainer = holder.manaCostContainerView;

            if (manaCost.length() > 0) {
                ArrayList<Integer> icons = CardUtil.parseIcons(
                        mCursor.getString(CardLoader.Query.MANA_COST));

                manaCostContainer.removeAllViews();

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 15),
                                Util.dpToPx(getActivity(), 15));

                if (icons.size() > 5) {
                    params = new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 10),
                            Util.dpToPx(getActivity(), 10));
                }

                params.gravity = Gravity.RIGHT;

                manaCostContainer.setVisibility(View.VISIBLE);

                int milIcon = R.drawable.mana_1000000;

                for (Integer iconId : icons) {
                    ImageView manaCostImage = new ImageView(getActivity());

                    try {
                        Drawable manaIconDrawable = getResources().getDrawable(iconId);

                        manaCostImage.setImageDrawable(manaIconDrawable);

                        if (iconId == milIcon) {
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
                        Log.e("MtM", mCursor.getString(CardLoader.Query.MANA_COST));
                    }
                }

                manaCostContainer.requestLayout();
            } else {
                manaCostContainer.removeAllViews();
            }

            String type = mCursor.getString(CardLoader.Query.TYPE).toLowerCase();

            if (type.startsWith("creature")) {
                holder.featuredStatTextView.setText(String.format("%s/%s",
                        mCursor.getString(CardLoader.Query.POWER),
                        mCursor.getString(CardLoader.Query.TOUGHNESS)));
            } else if (type.startsWith("planeswalker")) {
                holder.featuredStatTextView.setText(mCursor.getString(CardLoader.Query.LOYALTY));
            } else {
                holder.featuredStatTextView.setText("");
            }

            PicassoBigCache.INSTANCE.getPicassoBigCache(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.sample_card_crop)
                    .into(holder.cropppedImageView);
        }

        @Override
        public int getItemCount() {
            return mCursor.getCount();
        }
    }

    /*private class RecyclerAdapter extends RecyclerView.Adapter<ViewHolder> {

        private ArrayList<CardItem> mCardList;

        public RecyclerAdapter(ArrayList<CardItem> cardList) {
            mCardList = cardList;
        }

        @Override
        public long getItemId(int position) {
            return mCardList.get(position).getId();
        }

        @Override
        public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
            View view = getActivity().getLayoutInflater().inflate(
                    R.layout.list_item_search_card, parent, false);
            final ViewHolder vh = new ViewHolder(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            return vh;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            CardItem cardItem = mCardList.get(position);

            // Clean this up into a function since it will probably be used more than once
            String imageUrl = "http://www.essentialtcg.com/images/ogw/crops/" +
                    cardItem.getName().replace(" ", "%20") +
                    ".crop.jpg?width=" + String.valueOf(Util.dpToPx(getActivity(), 50));

            if (cardItem.getSecondName() != null) {
                holder.nameTextView.setText(
                        String.format("%s / %s", cardItem.getName(), cardItem.getSecondName()));
            } else {
                holder.nameTextView.setText(cardItem.getName());
            }

            try {
                int resourceId = CardUtil.parseSetRarity(
                        cardItem.getSetCode(), cardItem.getRarity());

                try {
                    Drawable iconDrawable = ContextCompat.getDrawable(getActivity(), resourceId);
                    int imageHeight = holder.typeTextView.getLineHeight();
                    int imageWidth = Util.calculateWidth(iconDrawable.getIntrinsicWidth(),
                            iconDrawable.getIntrinsicHeight(), imageHeight);

                    iconDrawable.setBounds(0, 0, imageHeight, imageWidth);

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
                            cardItem.getSetCode(), cardItem.getRarity()));
                }
            } catch (Exception ex) {
                Log.d("MtM", cardItem.getSetCode() + "_" + cardItem.getRarity());
            }

            holder.typeTextView.setText(cardItem.getType());

            String manaCost = cardItem.getManaCost();

            LinearLayout manaCostContainer = holder.manaCostContainerView;

            if (manaCost.length() > 0) {
                ArrayList<Integer> icons = CardUtil.parseIcons(cardItem.getManaCost());

                manaCostContainer.removeAllViews();

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 15),
                                Util.dpToPx(getActivity(), 15));

                if (icons.size() > 5) {
                    params = new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 10),
                            Util.dpToPx(getActivity(), 10));
                }

                params.gravity = Gravity.RIGHT;

                manaCostContainer.setVisibility(View.VISIBLE);

                int milIcon = R.drawable.mana_1000000;

                for (Integer iconId : icons) {
                    ImageView manaCostImage = new ImageView(getActivity());

                    try {
                        Drawable manaIconDrawable = getResources().getDrawable(iconId);

                        manaCostImage.setImageDrawable(manaIconDrawable);

                        if (iconId == milIcon) {
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
                        Log.e("MtM", cardItem.getManaCost());
                    }
                }

                manaCostContainer.requestLayout();
            } else {
                manaCostContainer.removeAllViews();
            }

            String type = cardItem.getType().toLowerCase();

            if (type.startsWith("creature")) {
                holder.featuredStatTextView.setText(String.format("%s/%s",
                        cardItem.getPower(), cardItem.getToughness()));
            } else if (type.startsWith("planeswalker")) {
                holder.featuredStatTextView.setText(cardItem.getLoyalty());
            } else {
                holder.featuredStatTextView.setText("");
            }

            PicassoBigCache.INSTANCE.getPicassoBigCache(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.sample_card_crop)
                    .into(holder.cropppedImageView);
        }

        @Override
        public int getItemCount() {
            return mCardList.size();
        }
    }*/

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView cropppedImageView;
        public TextView nameTextView;
        public TextView setRarityTextView;
        public TextView typeTextView;
        public LinearLayout rightContainer;
        public LinearLayout manaCostContainerView;
        public TextView featuredStatTextView;

        public ViewHolder(View view) {
            super(view);
            cropppedImageView = (ImageView)view.findViewById(R.id.cropped_image_view);
            nameTextView = (TextView) view.findViewById(R.id.card_name_text_view);
            setRarityTextView = (TextView) view.findViewById(R.id.set_rarity_text_view);
            typeTextView = (TextView) view.findViewById(R.id.type_text_view);
            rightContainer = (LinearLayout) view.findViewById(R.id.right_container);
            manaCostContainerView = (LinearLayout) view.findViewById(R.id.mana_cost_container_view);
            featuredStatTextView = (TextView) view.findViewById(R.id.featured_stat_text_view);
        }
    }

}
