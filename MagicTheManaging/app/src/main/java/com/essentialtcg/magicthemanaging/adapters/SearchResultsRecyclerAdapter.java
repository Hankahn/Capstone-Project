package com.essentialtcg.magicthemanaging.adapters;

import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.ui.activities.CardViewActivity;
import com.essentialtcg.magicthemanaging.ui.activities.MainActivity;
import com.essentialtcg.magicthemanaging.utils.CardUtil;
import com.essentialtcg.magicthemanaging.utils.Util;

import java.util.ArrayList;

/**
 * Created by Shawn on 5/19/2016.
 */
public class SearchResultsRecyclerAdapter extends
        RecyclerView.Adapter<SearchResultsRecyclerAdapter.SearchResultViewHolder> {

    private static final String TAG = "SearchResultsAdapter";

    private int mCursorPosition;
    private Cursor mCursor;
    private static Context mContext;
    private CardSearchParameters mSearchParameters;

    public SearchResultsRecyclerAdapter(Cursor cursor, CardSearchParameters searchParameters,
                                        Context context) {
        mCursor = cursor;
        mSearchParameters = searchParameters;
        mContext = context;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(CardLoader.Query._ID);
    }

    @Override
    public SearchResultViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_search_card, viewGroup, false);
        final SearchResultViewHolder viewHolder = new SearchResultViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
        mCursorPosition = position;
        mCursor.moveToPosition(position);

        // Clean this up into a function since it will probably be used more than once
        String imageName = mCursor.getString(CardLoader.Query.IMAGE_NAME).replace(" ", "%20");

            /*try {
                imageName = URLEncoder.encode(imageName, "UTF-8");
            } catch (Exception ex) {

            }*/

        final String imageUrl = String.format("http://www.essentialtcg.com/images/%s/%s.jpg?height=%s",
                mCursor.getString(CardLoader.Query.SET_CODE),
                imageName,
                Util.dpToPx(mContext, 43));
        Log.d(TAG, "onBindViewHolder: " + imageUrl);
            /*final String imageUrl = "http://www.essentialtcg.com/images/" +
                    mCursor.getString(CardLoader.Query.SET_CODE) + "/" +
                    mCursor.getString(CardLoader.Query.IMAGE_NAME).replace(" ", "%20") +
                    ".jpg" *//*?cropyunits=100&cropxunits=100&crop=10,12,90,50";*//* + "?width=" +
                    String.valueOf(Util.dpToPx(getActivity(), 50));*/

        if (mCursor.getString(CardLoader.Query.NAME2) != null) {
            holder.nameTextView.setText(String.format(
                    mContext.getResources().getString(R.string.CARD_NAME_DOUBLE_SIDED),
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
                Drawable iconDrawable = ContextCompat.getDrawable(mContext, resourceId);
                int imageHeight = Util.dpToPx(mContext, 15);//holder.typeTextView.getLineHeight();
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

            for (Integer iconId : icons) {
                Drawable iconDrawable = ContextCompat.getDrawable(mContext, iconId);
                int imageHeight = Util.dpToPx(mContext, imageHeightdp);
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
                    Drawable iconDrawable = ContextCompat.getDrawable(mContext, iconId);
                    int imageHeight = Util.dpToPx(mContext, imageHeightdp);
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

        if (type.startsWith(mContext.getString(R.string.TYPE_CREATURE_STARTS))) {
            holder.featuredStatTextView.setText(String.format(
                    mContext.getString(R.string.FEATURED_STAT_POWER_TOUGHNESS_FORMAT),
                    mCursor.getString(CardLoader.Query.POWER),
                    mCursor.getString(CardLoader.Query.TOUGHNESS)));
        } else if (type.startsWith(mContext.getString(R.string.TYPE_PLANESWALKER_STARTS))) {
            holder.featuredStatTextView.setText(mCursor.getString(CardLoader.Query.LOYALTY));
        } else {
            holder.featuredStatTextView.setText(R.string.NO_FEATURED_STAT);
        }

            /*if (position == mCurrentPosition) {
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
            } else {*/
        Glide.with(mContext)
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

            croppedImageView = (ImageView) view.findViewById(R.id.cropped_image_view);
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

            Intent viewCardIntent = new Intent(mContext, CardViewActivity.class);

            View croppedImageView = view.findViewById(R.id.cropped_image_view);

            Bundle bundle = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    /*setSharedElementEnterTransition(new ChangeImageTransform());
                    setSharedElementReturnTransition(new ChangeImageTransform());*/
                //setSharedElementEnterTransition(new android.transition.Fade());

                bundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(
                                (MainActivity) mContext,
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

            mContext.startActivity(viewCardIntent, bundle);

            Log.d("MtMT", croppedImageView.getTransitionName() + " -> " +
                    croppedImageView.getTransitionName());

                /*CardViewFragment cardViewFragment = CardViewFragment.newInstance(
                position, mSearchParameters);*/

                /*cardViewFragment.setSharedElementEnterTransition(new DetailTransition());
                setSharedElementEnterTransition(new DetailTransition());
                cardViewFragment.setEnterTransition(new Fade());
                setExitTransition(new Fade());
                setSharedElementReturnTransition(new DetailTransition());*/

                /*FragmentTransitionUtil.getInstance(getFragmentManager())
                        .transition(R.id.fragment_container, this, cardViewFragment, croppedImageView,
                                croppedImageView.getTransitionName());*/

                /*getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        //.addSharedElement(croppedImageView, croppedImageView.getTransitionName())// destinationTransitionName)
                        //.add(R.id.fragment_container, cardViewFragment)
                        .replace(R.id.fragment_container, cardViewFragment)
                        .addToBackStack(null)
                        .commit();*/
        }

    }

    private void startPostponedEnterTransition(ImageView imageView) {
        final ImageView iv = imageView;

        iv.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                iv.getViewTreeObserver().removeOnPreDrawListener(this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((MainActivity) mContext).startPostponedEnterTransition();
                }

                return true;
            }
        });
    }

}