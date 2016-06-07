package com.essentialtcg.magicthemanaging.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.loaders.LegalityLoader;

/**
 * Created by Shawn on 6/7/2016.
 */
public class LegalitiesRecyclerAdapter extends
        RecyclerView.Adapter<LegalitiesRecyclerAdapter.LegalityViewHolder> {

    private static final String TAG = LegalitiesRecyclerAdapter.class.getSimpleName();

    private final Cursor mCursor;
    private final Context mContext;

    public LegalitiesRecyclerAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        mContext = context;
    }

    public static LegalitiesRecyclerAdapter newInstance(Cursor cursor, Context context) {
        return new LegalitiesRecyclerAdapter(cursor, context);
    }

    @Override
    public LegalitiesRecyclerAdapter.LegalityViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_legality, viewGroup, false);

        return new LegalityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LegalitiesRecyclerAdapter.LegalityViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.formatTextView.setText(mCursor.getString(LegalityLoader.Query.FORMAT));
        holder.legalityTextView.setText(mCursor.getString(LegalityLoader.Query.LEGALITY));
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);

        return mCursor.getLong(LegalityLoader.Query._ID);
    }

    public class LegalityViewHolder extends RecyclerView.ViewHolder {

        public final TextView formatTextView;
        public final TextView legalityTextView;

        public LegalityViewHolder(View view) {
            super(view);

            formatTextView = (TextView) view.findViewById(R.id.legality_format_text_view);
            legalityTextView = (TextView) view.findViewById(R.id.legality_text_view);
        }

    }

}
