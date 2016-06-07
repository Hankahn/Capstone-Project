package com.essentialtcg.magicthemanaging.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.loaders.RulingLoader;

/**
 * Created by Shawn on 6/6/2016.
 */
public class RulingsRecyclerAdapter extends
        RecyclerView.Adapter<RulingsRecyclerAdapter.RulingViewHolder> {

    private static final String TAG = RulingsRecyclerAdapter.class.getSimpleName();

    private final Cursor mCursor;
    private final Context mContext;

    public RulingsRecyclerAdapter(Cursor cursor, Context context) {
        mCursor = cursor;
        mContext = context;
    }

    public static RulingsRecyclerAdapter newInstance(Cursor cursor, Context context) {
        return new RulingsRecyclerAdapter(cursor, context);
    }

    @Override
    public RulingsRecyclerAdapter.RulingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.list_item_ruling, viewGroup, false);

        return new RulingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RulingsRecyclerAdapter.RulingViewHolder holder, int position) {
        mCursor.moveToPosition(position);

        holder.rulingTextView.setText(mCursor.getString(RulingLoader.Query.TEXT));
    }

    @Override
    public int getItemCount() {
        return mCursor != null ? mCursor.getCount() : 0;
    }

    @Override
    public long getItemId(int position) {
        mCursor.moveToPosition(position);

        return mCursor.getLong(RulingLoader.Query._ID);
    }

    public class RulingViewHolder extends RecyclerView.ViewHolder {

        public final TextView rulingTextView;

        public RulingViewHolder(View view) {
            super(view);

            rulingTextView = (TextView) view.findViewById(R.id.ruling_text_view);
        }

    }

}
