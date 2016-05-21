package com.essentialtcg.magicthemanaging.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.callback.DrawerAdapterCallback;
import com.essentialtcg.magicthemanaging.ui.activities.SignInActivity;

/**
 * Created by Shawn on 4/18/2016.
 */
public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private final Context mContext;

    private String mNavTitles[];
    private int mIcons[];

    private String mName;
    private int mProfile;
    private String mEmail;

    private DrawerAdapterCallback mDrawerAdapterCallback;

    public static class DrawerViewHolder extends RecyclerView.ViewHolder {

        int HolderId;

        Button mSignInButton;
        TextView mTextView;
        ImageView mImageView;
        ImageView mProfile;
        TextView mName;
        TextView mEmail;

        public DrawerViewHolder(View itemView, int viewType) {
            super(itemView);

            if (viewType == TYPE_ITEM) {
                mTextView = (TextView) itemView.findViewById(R.id.rowText);
                mImageView = (ImageView) itemView.findViewById(R.id.rowIcon);
                HolderId = 1;
            } else {
                mSignInButton = (Button) itemView.findViewById(R.id.sign_in_button);
                mName = (TextView) itemView.findViewById(R.id.name);
                mEmail = (TextView) itemView.findViewById(R.id.email);
                mProfile = (ImageView) itemView.findViewById(R.id.profile_picture);
                HolderId = 0;
            }
        }

    }

    public DrawerAdapter(Context context, String titles[], int icons[], String name, String email, int profile,
                         DrawerAdapterCallback drawerAdapterCallback) {
        mContext = context;
        mNavTitles = titles;
        mIcons = icons;
        mName = name;
        mEmail = email;
        mProfile = profile;
        mDrawerAdapterCallback = drawerAdapterCallback;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_drawer, parent, false);

            DrawerViewHolder viewHolderItem = new DrawerViewHolder(view, viewType);

            return viewHolderItem;
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.header, parent, false);

            DrawerViewHolder viewHolderHeader = new DrawerViewHolder(view, viewType);

            return viewHolderHeader;
        }

        return null;
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, final int position) {
        if (holder.HolderId == 1) {
            holder.mTextView.setText(mNavTitles[position - 1]);
            holder.mImageView.setImageResource(mIcons[position - 1]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerAdapterCallback.onCallback(position - 1);
                }
            });
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            boolean isLoggedIn = preferences.getBoolean("PREF_LOGGED_IN", false);
            String displayName = preferences.getString("PREF_LOGGED_IN_NAME", "");
            String email = preferences.getString("PREF_LOGGED_IN_EMAIL", "");

            if (isLoggedIn) {
                holder.mProfile.setVisibility(View.VISIBLE);
                holder.mProfile.setImageResource(mProfile);
                holder.mName.setVisibility(View.VISIBLE);
                holder.mName.setText(displayName);
                holder.mEmail.setVisibility(View.VISIBLE);
                holder.mEmail.setText(email);
                holder.mSignInButton.setVisibility(View.GONE);
            } else {
                holder.mProfile.setVisibility(View.INVISIBLE);
                holder.mName.setVisibility(View.INVISIBLE);
                holder.mEmail.setVisibility(View.INVISIBLE);
                holder.mSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent signInIntent = new Intent(mContext, SignInActivity.class);
                        mContext.startActivity(signInIntent);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return mNavTitles.length + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
