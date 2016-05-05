package com.essentialtcg.magicthemanaging.adapters;

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
import android.transition.Fade;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.ui.activities.CardViewActivity;
import com.essentialtcg.magicthemanaging.ui.viewHolders.FavoritesResultViewHolder;
import com.essentialtcg.magicthemanaging.utils.CardUtil;
import com.essentialtcg.magicthemanaging.utils.Util;

import java.util.ArrayList;

/**
 * Created by Shawn on 5/2/2016.
 */
public class FavoritesResultsRecyclerAdapter extends
        RecyclerView.Adapter<FavoritesResultViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public FavoritesResultsRecyclerAdapter(Cursor cursor) {
        mCursor = cursor;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);
        return mCursor.getLong(CardLoader.Query._ID);
    }

    @Override
    public FavoritesResultViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_search_card, viewGroup, false);

        mContext = viewGroup.getContext();

        return new FavoritesResultViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final FavoritesResultViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        // Clean this up into a function since it will probably be used more than once
        final String imageUrl = "http://www.essentialtcg.com/images/" +
                mCursor.getString(CardLoader.Query.SET_CODE) + "/" +
                mCursor.getString(CardLoader.Query.IMAGE_NAME).replace(" ", "%20") +
                ".jpg?cropyunits=100&cropxunits=100&crop=10,12,90,50&width=" +
                String.valueOf(Util.dpToPx(mContext, 50));

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

        //Picasso.with(getActivity()).setIndicatorsEnabled(true);

            /*Picasso.with(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.sample_card_crop)
                    .into(holder.croppedImageView);*/

            /*PicassoBigCache.INSTANCE.getPicassoBigCache(getActivity())
                    .load(imageUrl)
                    .placeholder(R.mipmap.sample_card_crop)
                    .into(holder.croppedImageView);*/

        // TODO: Try skipMemoryCache
        Glide.with(mContext)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.skipMemoryCache(true)
                .into(holder.croppedImageView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.croppedImageView.setTransitionName(
                    "source_" + String.valueOf(mCursor.getInt(CardLoader.Query._ID)));
            //Log.d("MtMT", String.valueOf(mCursor.getInt(CardLoader.Query._ID)));
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }


}
