package com.essentialtcg.magicthemanaging.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
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
import com.essentialtcg.magicthemanaging.callback.LoadCardDetailCallback;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.loaders.CardLoader;
import com.essentialtcg.magicthemanaging.events.UpdateViewPagerPositionEvent;
import com.essentialtcg.magicthemanaging.utils.CardUtil;
import com.essentialtcg.magicthemanaging.utils.Util;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * Created by Shawn on 5/19/2016.
 */
public class SearchResultsRecyclerAdapter extends
        RecyclerView.Adapter<SearchResultsRecyclerAdapter.SearchResultViewHolder> {

    private static final String TAG = "SearchResultsAdapter";

    private final Cursor mCursor;
    private int mSelectedPosition;
    private final Context mContext;
    private CardSearchParameters mSearchParameters;
    private final LoadCardDetailCallback mCardDetailCallback;

    private SearchResultsRecyclerAdapter(Cursor cursor, Context context,
                                         LoadCardDetailCallback cardDetailCallback) {
        mCursor = cursor;
        mContext = context;
        mCardDetailCallback = cardDetailCallback;
    }

    public SearchResultsRecyclerAdapter(Cursor cursor, CardSearchParameters searchParameters,
                                        Context context, LoadCardDetailCallback cardDetailCallback) {
        mCursor = cursor;
        mSearchParameters = searchParameters;
        mContext = context;
        mCardDetailCallback = cardDetailCallback;
    }

    public static SearchResultsRecyclerAdapter newFavoritesInstance(Cursor cursor, Context context,
                    LoadCardDetailCallback cardDetailCallback) {

        return new SearchResultsRecyclerAdapter(cursor, context,
                cardDetailCallback);
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

        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchResultViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        if (mContext.getResources().getBoolean(R.bool.multipane)) {
            if (position == mSelectedPosition) {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.gray));
            } else {
                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
            }
        }

        // Clean this up into a function since it will probably be used more than once
        String imageName = mCursor.getString(CardLoader.Query.IMAGE_NAME).replace(" ", "%20");

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

        Glide.with(mContext)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                //.skipMemoryCache(true)
                .dontTransform()
                .dontAnimate()
                .placeholder(R.mipmap.card_back)
                .into(holder.croppedImageView);

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

    public void setCurrentPosition(int position) {
        notifyItemChanged(mSelectedPosition);
        mSelectedPosition = position;
        notifyItemChanged(mSelectedPosition);
    }

    public class SearchResultViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final CardView cardView;
        public final ImageView croppedImageView;
        public final TextView nameTextView;
        public final TextView setRarityTextView;
        public final TextView typeTextView;
        public final LinearLayout rightContainer;
        public final TextView manaCostTextView;
        public final TextView featuredStatTextView;

        public SearchResultViewHolder(View view) {
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_card_view);
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

            if (mContext.getResources().getBoolean(R.bool.multipane)) {
                notifyItemChanged(mSelectedPosition);
                mSelectedPosition = position;
                notifyItemChanged(mSelectedPosition);

                EventBus.getDefault().post(new UpdateViewPagerPositionEvent(position));
            } else {
                View croppedImageView = view.findViewById(R.id.cropped_image_view);

                mCursor.moveToPosition(position);

                long selectedItemId = mCursor.getLong(CardLoader.Query._ID);

                mCardDetailCallback.onLoadCardDetailCallback(position, selectedItemId,
                        croppedImageView, mSearchParameters);
            }
        }

    }

}
