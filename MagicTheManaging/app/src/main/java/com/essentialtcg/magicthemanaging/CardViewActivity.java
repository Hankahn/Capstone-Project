package com.essentialtcg.magicthemanaging;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.essentialtcg.magicthemanaging.data.CardSearchParameters;

/**
 * Created by Shawn on 4/9/2016.
 */
public class CardViewActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener {

    public static final String INITIAL_CARD_POSITION = "INITIAL_CARD_POSITION";
    public static final String SELECTED_ITEM_ID = "SELECTED_ITEM_ID";
    public static final String SEARCH_PARAMETERS = "SEARCH_PARAMETERS";

    private int mStartPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_card_view);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mStartPosition = 0;
        CardSearchParameters searchParameters = new CardSearchParameters();

        if (savedInstanceState == null) {
            if (getIntent() != null) {
                searchParameters = getIntent().getExtras().getParcelable(SEARCH_PARAMETERS);
                mStartPosition = getIntent().getExtras().getInt(INITIAL_CARD_POSITION, 0);
            }
        } else if (savedInstanceState.containsKey(INITIAL_CARD_POSITION)) {
            mStartPosition = savedInstanceState.getInt(INITIAL_CARD_POSITION);
        }

        CardViewFragment cardViewFragment = CardViewFragment.newInstance(
                mStartPosition, searchParameters);

        if (getSupportFragmentManager().findFragmentById(android.R.id.content) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(android.R.id.content, cardViewFragment)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INITIAL_CARD_POSITION, mStartPosition);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mStartPosition = position;
    }

    @Override
    public void onPageSelected(int position) {
        mStartPosition = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
