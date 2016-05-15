package com.essentialtcg.magicthemanaging.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.ui.fragments.CardViewFragment;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener {

    public static final String INITIAL_CARD_POSITION = "INITIAL_CARD_POSITION";
    public static final String CURRENT_CARD_POSITION = "CURRENT_CARD_POSITION";
    public static final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    public static final String SEARCH_PARAMETERS = "SEARCH_PARAMETERS";

    private int mStartPosition;
    private int mCurrentPosition;
    private long mSelectedItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();

            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mStartPosition = 0;
        mCurrentPosition = 0;
        CardSearchParameters searchParameters = new CardSearchParameters();

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                searchParameters = getIntent().getExtras().getParcelable(SEARCH_PARAMETERS);
                mSelectedItemId = getIntent().getExtras().getLong(SELECTED_ITEM_ID, 0);
                mStartPosition = getIntent().getExtras().getInt(INITIAL_CARD_POSITION, 0);
                mCurrentPosition = mStartPosition;
            }
        } else if (savedInstanceState.containsKey(INITIAL_CARD_POSITION)) {
            mStartPosition = savedInstanceState.getInt(INITIAL_CARD_POSITION);
            mCurrentPosition = savedInstanceState.getInt(CURRENT_CARD_POSITION);
        }

        if (savedInstanceState == null) {
            CardViewFragment cardViewFragment = CardViewFragment.newInstance(
                    mStartPosition, mSelectedItemId, searchParameters);

            if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(android.R.id.content, cardViewFragment, "CardViewFragment")
                        .commit();
            }
        } else {
            CardViewFragment cardViewFragment = (CardViewFragment) getSupportFragmentManager()
                    .findFragmentByTag("CardViewFragment");

            //finishAfterTransition();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INITIAL_CARD_POSITION, mStartPosition);
        outState.putInt(CURRENT_CARD_POSITION, mCurrentPosition);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mCurrentPosition = position;
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void finishAfterTransition() {
        Intent data = new Intent();
        data.putExtra(INITIAL_CARD_POSITION, mStartPosition);
        data.putExtra(CURRENT_CARD_POSITION, mCurrentPosition);
        setResult(RESULT_OK, data);
        super.finishAfterTransition();
    }

    /*@Override
    public void onBackPressed() {
        Intent data = new Intent();

        data.putExtra(INITIAL_CARD_POSITION, mStartPosition);
        data.putExtra(CURRENT_CARD_POSITION, mCurrentPosition);

        setResult(RESULT_OK, data);

        super.onBackPressed();
    }*/
}
