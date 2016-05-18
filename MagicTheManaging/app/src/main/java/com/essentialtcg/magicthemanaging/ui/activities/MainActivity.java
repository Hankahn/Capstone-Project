package com.essentialtcg.magicthemanaging.ui.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.SharedElementCallback;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.callback.DrawerAdapterCallback;
import com.essentialtcg.magicthemanaging.callback.UpdateRecyclerViewCallback;
import com.essentialtcg.magicthemanaging.ui.fragments.FavoritesFragment;
import com.essentialtcg.magicthemanaging.ui.fragments.SearchFragment;
import com.essentialtcg.magicthemanaging.adapters.DrawerAdapter;

import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements DrawerAdapterCallback {
    //implements ViewPager.OnPageChangeListener {

    private String TAG = "MainActivity";

    private String MENU_TITLES[] = { "Search", "Decks", "Collection", "Favorites" };
    private int MENU_ICONS[] = { R.mipmap.ic_search_black_18, R.mipmap.ic_content_copy_black_18,
            R.mipmap.ic_check_box_black_24, R.mipmap.ic_star_rate_black_18 };
    private String NAME = "Shawn M. Sullivan";
    private String EMAIL = "hankahn@gmail.com";
    private int PROFILE = R.mipmap.soi_m;
    private DrawerLayout mDrawerLayout;
    private RecyclerView mDrawerRecycler;
    private RecyclerView.Adapter mDrawerAdapter;
    private RecyclerView.LayoutManager mDrawerLayoutManager;
    private Fragment mFragment;
    private UpdateRecyclerViewCallback mUpdateRecyclerViewCallback;
    private Bundle mReenterState;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerRecycler = (RecyclerView) findViewById(R.id.search_recycler_view);
        mDrawerRecycler.setHasFixedSize(true);

        mDrawerAdapter = new DrawerAdapter(MENU_TITLES, MENU_ICONS, NAME, EMAIL, PROFILE, this);

        mDrawerRecycler.setAdapter(mDrawerAdapter);

        mDrawerLayoutManager = new LinearLayoutManager(this);

        mDrawerRecycler.setLayoutManager(mDrawerLayoutManager);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SearchFragment searchFragment;

        if (savedInstanceState == null) {
            searchFragment = new SearchFragment();

            mFragment = searchFragment;

            mUpdateRecyclerViewCallback = searchFragment;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment, "SearchFragment")
                    .commit();
        } else {
            searchFragment = (SearchFragment) getSupportFragmentManager().findFragmentByTag("SearchFragment");
            mFragment = searchFragment;
            mUpdateRecyclerViewCallback = searchFragment;
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        postponeEnterTransition();
        //Toast.makeText(this, "postponed transitions", Toast.LENGTH_SHORT).show();

        mReenterState = new Bundle(data.getExtras());

        int initialPosition = mReenterState.getInt(CardViewActivity.INITIAL_CARD_POSITION);
        int currentPosition = mReenterState.getInt(CardViewActivity.CURRENT_CARD_POSITION);

        mUpdateRecyclerViewCallback.onUpdateRecyclerViewCallback(initialPosition, currentPosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCallback(int position) {
        mFragment = null;
        switch (position) {
            case 0:
                mFragment = new SearchFragment();
                mUpdateRecyclerViewCallback = (SearchFragment) mFragment;

                break;
            case 3:
                mFragment = new FavoritesFragment();

                break;
            default:
                Log.d(TAG, String.format(
                        "onCallback: Attempted to load unimplemented Fragment at position (%s)",
                        String.valueOf(position)));
        }

        if (mFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment, null)
                    .commit();
        }

        mDrawerLayout.closeDrawers();
    }

}
