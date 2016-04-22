package com.essentialtcg.magicthemanaging;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SearchActivity extends AppCompatActivity
    implements ViewPager.OnPageChangeListener{

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

    private int mStartPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerRecycler = (RecyclerView) findViewById(R.id.RecyclerView);
        mDrawerRecycler.setHasFixedSize(true);

        mDrawerAdapter = new DrawerAdapter(MENU_TITLES, MENU_ICONS, NAME, EMAIL, PROFILE);

        mDrawerRecycler.setAdapter(mDrawerAdapter);

        mDrawerLayoutManager = new LinearLayoutManager(this);

        mDrawerRecycler.setLayoutManager(mDrawerLayoutManager);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        SearchFragment searchFragment = new SearchFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, searchFragment)
                .commit();
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

}
