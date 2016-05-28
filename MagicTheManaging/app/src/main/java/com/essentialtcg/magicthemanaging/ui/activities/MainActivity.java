package com.essentialtcg.magicthemanaging.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.callback.DrawerAdapterCallback;
import com.essentialtcg.magicthemanaging.callback.LoadCardDetailCallback;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.items.SetItem;
import com.essentialtcg.magicthemanaging.events.UpdateRecyclerViewPositionReturnEvent;
import com.essentialtcg.magicthemanaging.ui.fragments.CardViewFragment;
import com.essentialtcg.magicthemanaging.ui.fragments.FavoritesFragment;
import com.essentialtcg.magicthemanaging.ui.fragments.SearchFragment;
import com.essentialtcg.magicthemanaging.adapters.DrawerAdapter;
import com.essentialtcg.magicthemanaging.utils.SetArrayList;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.greenrobot.eventbus.EventBus;

public class MainActivity extends AppCompatActivity
        implements DrawerAdapterCallback,
        GoogleApiClient.OnConnectionFailedListener,
        LoadCardDetailCallback {

    private String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

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
    private Bundle mReenterState;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem mSignInMenuItem;
    private MenuItem mSignOutMenuItem;

    private boolean mSigningInOut = false;

    SharedPreferences.OnSharedPreferenceChangeListener mPreferenceListener = new SharedPreferences.OnSharedPreferenceChangeListener() {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("PREF_LOGGED_IN")) {
                mDrawerAdapter.notifyDataSetChanged();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        preferences.registerOnSharedPreferenceChangeListener(mPreferenceListener);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerRecycler = (RecyclerView) findViewById(R.id.search_recycler_view);
        mDrawerRecycler.setHasFixedSize(true);

        mDrawerAdapter = new DrawerAdapter(this, MENU_TITLES, MENU_ICONS, NAME, EMAIL, PROFILE, this);

        mDrawerRecycler.setAdapter(mDrawerAdapter);

        mDrawerLayoutManager = new LinearLayoutManager(this);

        mDrawerRecycler.setLayoutManager(mDrawerLayoutManager);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            SearchFragment searchFragment = new SearchFragment();

            mFragment = searchFragment;

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment, "SearchFragment")
                    .commit();

            if (getResources().getBoolean(R.bool.multipane)) {
                // TODO: Pull this from the sharedpreferences instead
                CardSearchParameters cardSearchParameters = new CardSearchParameters();

                SetArrayList setFilter = new SetArrayList();
                SetItem setItem = new SetItem();
                setItem.setName("Oath of the Gatewatch");
                setItem.setCode("OGW");
                setFilter.add(setItem);

                cardSearchParameters.setSetFilter(setFilter);

                CardViewFragment cardViewFragment = CardViewFragment.newInstance(0, 0, cardSearchParameters);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_right, cardViewFragment, "CardViewFragment")
                        .commit();
            }
        } else {
            Fragment currentFragment =
                    getSupportFragmentManager().findFragmentById(R.id.fragment_container);

            if (currentFragment.getClass().equals(SearchFragment.class)) {
                mFragment = currentFragment;
            } else if (currentFragment.getClass().equals(FavoritesFragment.class)) {
                mFragment = currentFragment;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        checkLoginStatus();
    }

    private void checkLoginStatus() {
        GoogleSignInOptions googleSignInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build();

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .build();
        }

        OptionalPendingResult<GoogleSignInResult> optionalPendingResult =
                Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (optionalPendingResult.isDone()) {
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = optionalPendingResult.get();

            handleSignInResult(result);
        } else {
            showProgressDialog();
            optionalPendingResult.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            mSigningInOut = true;

            handleSignInResult(result);
        }
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition();
        }

        mReenterState = new Bundle(data.getExtras());

        int initialPosition = mReenterState.getInt(CardViewActivity.INITIAL_CARD_POSITION);
        int currentPosition = mReenterState.getInt(CardViewActivity.CURRENT_CARD_POSITION);

        EventBus.getDefault().post(
                new UpdateRecyclerViewPositionReturnEvent(initialPosition, currentPosition));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mSignInMenuItem = menu.findItem(R.id.action_sign_in);
        mSignOutMenuItem = menu.findItem(R.id.action_sign_out);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean loggedIn = preferences.getBoolean("PREF_LOGGED_IN", false);

        mSignInMenuItem.setVisible(!loggedIn);
        mSignOutMenuItem.setVisible(loggedIn);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_sign_in) {
            mSigningInOut = true;

            signIn();

            return true;
        }

        if (id == R.id.action_sign_out) {
            mSigningInOut = true;

            signOut();

            return true;
        }

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCallback(int position) {
        mFragment = null;

        Fragment leftFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

        switch (position) {
            case 0:
                if (leftFragment.getClass().equals(SearchFragment.class)) {
                    break;
                }

                mFragment = new SearchFragment();

                if (getResources().getBoolean(R.bool.multipane)) {
                    /*getSupportFragmentManager().beginTransaction()
                            .remove(rightFragment)
                            .commit();*/

                    // TODO: Pull this from the sharedpreferences instead
                    CardSearchParameters cardSearchParameters = new CardSearchParameters();

                    SetArrayList setFilter = new SetArrayList();
                    SetItem setItem = new SetItem();
                    setItem.setName("Oath of the Gatewatch");
                    setItem.setCode("OGW");
                    setFilter.add(setItem);

                    cardSearchParameters.setSetFilter(setFilter);

                    CardViewFragment cardViewFragment = CardViewFragment.newInstance(0, 0, cardSearchParameters);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_right, cardViewFragment, "CardViewFragment")
                            .commit();
                }

                break;
            case 3:
                if (leftFragment.getClass().equals(FavoritesFragment.class)) {
                    break;
                }

                mFragment = new FavoritesFragment();

                if (getResources().getBoolean(R.bool.multipane)) {
                    CardViewFragment cardViewFragment = CardViewFragment.newInstance(0, 0, null);

                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container_right, cardViewFragment, "CardViewFragment")
                            .commit();
                }

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

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        checkLoginStatus();
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult: " + result.isSuccess());

        boolean loggedIn;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();

        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();

            loggedIn = true;

            editor.putBoolean("PREF_LOGGED_IN", true);
            editor.putString("PREF_LOGGED_IN_ID", account.getId());
            editor.putString("PREF_LOGGED_IN_NAME", account.getDisplayName());
            editor.putString("PREF_LOGGED_IN_EMAIL", account.getEmail());

            if (account.getPhotoUrl() != null) {
                editor.putString("PREF_PROFILE_IMAGE_URL", account.getPhotoUrl().toString());
            } else {
                editor.putString("PREF_PROFILE_IMAGE_URL", "");
            }

            if (mSigningInOut) {
                Snackbar.make(findViewById(R.id.main_coord),
                        "Successfully Logged In", Snackbar.LENGTH_SHORT).show();
                mSigningInOut = false;
            }
        } else {
            loggedIn = false;

            editor.putBoolean("PREF_LOGGED_IN", false);
            editor.putString("PREF_LOGGED_IN_ID", "");
            editor.putString("PREF_LOGGED_IN_NAME", "");
            editor.putString("PREF_LOGGED_IN_EMAIL", "");
            editor.putString("PREF_PROFILE_IMAGE_URL", "");

            if (mSigningInOut) {
                Snackbar.make(findViewById(R.id.main_coord),
                        "Successfully Logged Out", Snackbar.LENGTH_SHORT).show();
                mSigningInOut = false;
            }
        }

        if (mSignOutMenuItem != null) {
            mSignOutMenuItem.setVisible(loggedIn);
        }

        if (mSignInMenuItem != null) {
            mSignInMenuItem.setVisible(!loggedIn);
        }

        editor.commit();

        mDrawerAdapter.notifyDataSetChanged();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onLoadCardDetailCallback(int position, long selectedItemId, View transitionView,
                                         CardSearchParameters searchParameters) {
        Intent viewCardIntent = new Intent(this, CardViewActivity.class);

        if (getResources().getBoolean(R.bool.multipane)) {
            if (getSupportFragmentManager().findFragmentByTag("card_view") == null) {
                CardViewFragment cardViewFragment = CardViewFragment.newInstance(
                        position, selectedItemId, searchParameters);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_right, cardViewFragment, "card_view")
                        .commit();
            } else {
                CardViewFragment cardViewFragment = (CardViewFragment) getSupportFragmentManager()
                        .findFragmentByTag("card_view");

                if (cardViewFragment != null) {
                    cardViewFragment.scrollToPosition(position);
                }
            }
        } else {
            Bundle bundle = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                bundle = ActivityOptionsCompat
                        .makeSceneTransitionAnimation(
                                this,
                                transitionView,
                                transitionView.getTransitionName())
                        .toBundle();

                Log.d("MtMT", transitionView.getTransitionName() + " -> " +
                        transitionView.getTransitionName());
            }

            viewCardIntent.putExtra(CardViewActivity.INITIAL_CARD_POSITION, position);
            viewCardIntent.putExtra(CardViewActivity.SELECTED_ITEM_ID,
                    selectedItemId);
            viewCardIntent.putExtra(CardViewActivity.SEARCH_PARAMETERS, searchParameters);

            startActivity(viewCardIntent, bundle);
        }
    }

}
