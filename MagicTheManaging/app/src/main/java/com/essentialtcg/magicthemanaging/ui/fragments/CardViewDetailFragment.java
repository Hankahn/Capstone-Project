package com.essentialtcg.magicthemanaging.ui.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
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
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.callback.GetPriceCallback;
import com.essentialtcg.magicthemanaging.data.items.PriceItem;
import com.essentialtcg.magicthemanaging.tasks.GetPriceAsyncTask;
import com.essentialtcg.magicthemanaging.utils.Util;
import com.essentialtcg.magicthemanaging.data.items.CardItem;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.data.transforms.CardTransform;
import com.essentialtcg.magicthemanaging.ui.activities.CardViewActivity;
import com.essentialtcg.magicthemanaging.utils.CardUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.ArrayList;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewDetailFragment extends Fragment implements GetPriceCallback {

    private static final String TAG = "CardViewDetailFragment";

    public static final String ARG_CARD_TO_SHOW = "CARD_TO_SHOW";
    public static final String ARG_ITEM_ID = "ITEM_ID";
    public static final String ARG_SELECTED_ITEM_ID = "START_ID";

    private TextView mCardPriceTextView;

    private CardItem mCardItem;
    //private long mItemId;
    private long mSelectedItemId;
    private View mRootView;
    private GetPriceAsyncTask mGetPriceTask;

    public static CardViewDetailFragment newInstance(CardItem cardItem, long selectedItemId) {
        CardViewDetailFragment fragment = new CardViewDetailFragment();
        Bundle arguments = new Bundle();

        arguments.putParcelable(ARG_CARD_TO_SHOW, cardItem);
        arguments.putLong(ARG_SELECTED_ITEM_ID, selectedItemId);

        fragment.setArguments(arguments);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_CARD_TO_SHOW)) {
            mCardItem = getArguments().getParcelable(ARG_CARD_TO_SHOW);
        }

        /*if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItemId = getArguments().getLong(ARG_ITEM_ID);
        }*/

        if (getArguments().containsKey(ARG_SELECTED_ITEM_ID)) {
            mSelectedItemId = getArguments().getLong(ARG_SELECTED_ITEM_ID);
        }

        mGetPriceTask = new GetPriceAsyncTask(this);

        String url = null;
        try {
            url = String.format("http://partner.tcgplayer.com/x3/phl.asmx/p?pk=%s&s=%s&p=%s",
                    "TCGTEST",
                    URLEncoder.encode(mCardItem.getSet(), "utf-8"),
                    URLEncoder.encode(mCardItem.getName(), "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        mGetPriceTask.execute(url);
    }

    public CardViewActivity getActivityCast() {
        return (CardViewActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (isGetPriceTaskRunning()) {
            mGetPriceTask.cancel(true);
        }
    }

    private boolean isGetPriceTaskRunning () {
        return (mGetPriceTask != null) && (mGetPriceTask.getStatus() == AsyncTask.Status.RUNNING);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(ARG_CARD_TO_SHOW)) {
                mCardItem = savedInstanceState.getParcelable(ARG_CARD_TO_SHOW);
            }

            if (savedInstanceState.containsKey(ARG_SELECTED_ITEM_ID)) {
                mSelectedItemId = savedInstanceState.getLong(ARG_SELECTED_ITEM_ID);
            }
        }

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
        mCardPriceTextView = (TextView) mRootView.findViewById(R.id.card_detail_price_text_view);
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
                    ".jpg";//&height=" +
                    //String.valueOf(Util.dpToPx(getActivity(), 100));

            Log.d("MtMImageUrl", imageUrl);

            cardImageView.setTransitionName("source_" + String.valueOf(mCardItem.getId()));

            Log.d("MtMT", "source_" + String.valueOf(mCardItem.getId()));

            // TODO: Try skipMemoryCache
            Glide.with(getActivity())
                    .load(imageUrl)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.RESULT)
                    .skipMemoryCache(true)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            startPostponedEnterTransition();
                            cardImageView.setVisibility(View.GONE);
                            cardTextVersion.setVisibility(View.VISIBLE);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            startPostponedEnterTransition();

                            return false;
                        }
                    })
                    .into(cardImageView);

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

    private void startPostponedEnterTransition() {
        if (mCardItem.getId() == mSelectedItemId) {
            final ImageView iv = getCardImageView();

            iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    iv.getViewTreeObserver().removeOnPreDrawListener(this);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getActivity().startPostponedEnterTransition();
                        Log.d("MtMU", "Starting postponed enter transitions");
                    }

                    return true;
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(ARG_CARD_TO_SHOW, mCardItem);
        outState.putLong(ARG_SELECTED_ITEM_ID, mSelectedItemId);
    }

    @Override
    public void onPriceRetrievedCallback(String priceXml) {
        PriceItem priceItem = CardUtil.parsePrice(priceXml);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        if (mCardPriceTextView != null) {
            mCardPriceTextView.setText(String.format("H: %s L: %s A: %s F: %s",
                    currencyFormat.format(priceItem.getHighPrice()),
                    currencyFormat.format(priceItem.getLowPrice()),
                    currencyFormat.format(priceItem.getAveragePrice()),
                    currencyFormat.format(priceItem.getFoilPrice())
            ));
        }
        //Toast.makeText(getActivity(), "hello", Toast.LENGTH_SHORT).show();
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
