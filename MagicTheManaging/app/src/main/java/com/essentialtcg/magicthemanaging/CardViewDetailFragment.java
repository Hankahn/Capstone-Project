package com.essentialtcg.magicthemanaging;

import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.data.CardItem;
import com.essentialtcg.magicthemanaging.data.CardLoader;
import com.essentialtcg.magicthemanaging.data.CardTransform;
import com.squareup.picasso.Callback;

import java.util.ArrayList;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewDetailFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "CardViewDetailFragment";

    public static final String ARG_CARD_TO_SHOW = "CARD_TO_SHOW";
    public static final String ARG_ITEM_ID = "ITEM_ID";
    public static final String ARG_START_ID = "START_ID";

    private CardItem mCardItem;
    private long mItemId;
    private long mStartId;
    private View mRootView;

    public static CardViewDetailFragment newInstance(long itemId, long startId) {
        CardViewDetailFragment fragment = new CardViewDetailFragment();
        Bundle arguments = new Bundle();

        arguments.putLong(ARG_ITEM_ID, itemId);
        arguments.putLong(ARG_START_ID, startId);

        fragment.setArguments(arguments);

        return fragment;
    }

    public static CardViewDetailFragment newInstance(CardItem cardItem, long startId) {
        CardViewDetailFragment fragment = new CardViewDetailFragment();
        Bundle arguments = new Bundle();

        arguments.putParcelable(ARG_CARD_TO_SHOW, cardItem);
        arguments.putLong(ARG_START_ID, startId);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_CARD_TO_SHOW)) {
            mCardItem = getArguments().getParcelable(ARG_CARD_TO_SHOW);
        }

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }

        if (getArguments().containsKey(ARG_START_ID)) {
            mStartId = getArguments().getLong(ARG_START_ID);
        }
    }

    public CardViewActivity getActivityCast() {
        return (CardViewActivity) getActivity();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        bindViews();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_card_view_detail, container, false);

        return mRootView;
    }

    public ImageView getCardImageView() {
        return (ImageView) mRootView.findViewById(R.id.card_detail_image_view);
    }

    private void bindViews() {
        if (mRootView == null) {
            return;
        }

        final ImageView cardImageView =
                (ImageView) mRootView.findViewById(R.id.card_detail_image_view);
        final LinearLayout cardTextVersion =
                (LinearLayout) mRootView.findViewById(R.id.card_detail_text_version);
        TextView nameTextView = (TextView) mRootView.findViewById(R.id.card_detail_name_text_view);
        LinearLayout manaCostContainer =
                (LinearLayout) mRootView.findViewById(R.id.card_detail_mana_cost_container_view);
        TextView cardTextView = (TextView) mRootView.findViewById(R.id.card_detail_text_view);
        TextView secondCardTextView =
                (TextView) mRootView.findViewById(R.id.card_detail_secondary_text_view);
        View cardTextSeparator = mRootView.findViewById(R.id.card_detail_text_separator);
        TabLayout tabLayout = (TabLayout) mRootView.findViewById(R.id.card_details_tab_layout);
        final ViewPager detailPager = (ViewPager) mRootView.findViewById(R.id.card_detail_view_pager);
        CardDetailPagerAdapter detailPagerAdapter =
                new CardDetailPagerAdapter(getChildFragmentManager());

        if (mCardItem != null) {
            Point size = new Point();

            Display display = getActivity().getWindowManager().getDefaultDisplay();

            display.getSize(size);

            // Clean this up into a function since it will probably be used more than once
            final String imageUrl = "http://www.essentialtcg.com/images/" +
                    mCardItem.getSetCode() + "/" +
                    mCardItem.getImageName().replace(" ", "%20") +
                    ".jpg";//?width=" +
                    //String.valueOf(Util.dpToPx(getActivity(), 600));

            Log.d("MtMImageUrl", imageUrl);

            PicassoBigCache.INSTANCE.getPicassoBigCache(getActivity())
                    .load(imageUrl)
                    .centerInside()
                    .fit()
                    .into(cardImageView, new Callback() {

                        @Override
                        public void onSuccess() { }

                        @Override
                        public void onError() {
                            cardImageView.setVisibility(View.GONE);
                            cardTextVersion.setVisibility(View.VISIBLE);
                            Log.d(TAG, "onError() called with: " + imageUrl);
                        }

                    });

            nameTextView.setText(mCardItem.getBothNames(getActivity()));

            if (mCardItem.getManaCost().length() > 0) {
                ArrayList<Integer> icons = CardUtil.parseIcons(mCardItem.getManaCost());

                manaCostContainer.removeAllViews();

                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 15),
                                Util.dpToPx(getActivity(), 15));

                if (icons.size() > 5) {
                    params = new LinearLayout.LayoutParams(Util.dpToPx(getActivity(), 10),
                            Util.dpToPx(getActivity(), 10));
                }

                params.gravity = Gravity.END;

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
                        Log.e("MtM", mCardItem.getManaCost());
                    }
                }

                manaCostContainer.requestLayout();
            } else {
                manaCostContainer.removeAllViews();
            }

            if (mCardItem.getCardText() != null && mCardItem.getCardText().length() > 0) {
                cardTextView.setText(mCardItem.getCardText());
            }

            if (mCardItem.getSecondName() != null && mCardItem.getSecondName().length() > 0) {
                secondCardTextView.setVisibility(View.VISIBLE);
                cardTextSeparator.setVisibility(View.VISIBLE);

                if (mCardItem.getSecondCardText() != null
                        && mCardItem.getSecondCardText().length() > 0) {
                    secondCardTextView.setText(mCardItem.getSecondCardText());
                }
            }

            detailPager.setAdapter(detailPagerAdapter);

            tabLayout.setupWithViewPager(detailPager);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    detailPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "ItemId: " + String.valueOf(mItemId));
        return CardLoader.newInstanceForCardId(getActivity(), mItemId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        Log.d(TAG, "Cursor Length: " + cursor.getCount());

        if (!isAdded()) {
            cursor.close();

            return;
        }

        ArrayList<CardItem> cardItems = CardTransform.transform(cursor);

        mCardItem = cardItems.get(0);

        bindViews();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCardItem = null;
        bindViews();
    }

    private class CardDetailPagerAdapter extends FragmentStatePagerAdapter {

        private final int NUM_TABS = 2;
        private RulesFragment mRulesFragment;
        private PrintingsFragment mPrintingsFragment;

        public CardDetailPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);

            mRulesFragment = new RulesFragment();
            mPrintingsFragment = new PrintingsFragment();
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return mRulesFragment;
            } else if (position == 1) {
                return mPrintingsFragment;
            }

            return null;
        }

        @Override
        public int getCount() {
            return NUM_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "Rulings";
            } else if (position == 1) {
                return "Printings";
            }

            return super.getPageTitle(position);
        }
    }

}
