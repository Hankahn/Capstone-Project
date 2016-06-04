package com.essentialtcg.magicthemanaging.ui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.callback.LoadCardDetailCallback;
import com.essentialtcg.magicthemanaging.data.CardSearchParameters;
import com.essentialtcg.magicthemanaging.data.items.SetItem;
import com.essentialtcg.magicthemanaging.events.UpdateRecyclerViewPositionReturnEvent;
import com.essentialtcg.magicthemanaging.transforms.RoundImageTransform;
import com.essentialtcg.magicthemanaging.ui.fragments.CardViewFragment;
import com.essentialtcg.magicthemanaging.ui.fragments.FavoritesFragment;
import com.essentialtcg.magicthemanaging.ui.fragments.SearchFragment;
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
        implements GoogleApiClient.OnConnectionFailedListener,
        LoadCardDetailCallback {

    private String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private View mNavigationHeader;
    private ImageView mHeaderUserPicture;
    private TextView mHeaderUserName;
    private TextView mHeaderUserEmail;
    private Fragment mFragment;
    private Bundle mReenterState;
    private CoordinatorLayout mCoordinatorLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem mSignInMenuItem;
    private MenuItem mSignOutMenuItem;

    private boolean mSigningInOut = false;

    NavigationView.OnNavigationItemSelectedListener mNavigationItemSelectedListener =
            new NavigationView.OnNavigationItemSelectedListener() {

                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    if (item.isChecked()) {
                        item.setChecked(false);
                    } else {
                        item.setChecked(true);
                    }

                    mDrawerLayout.closeDrawers();

                    mFragment = null;

                    Fragment leftFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                    switch (item.getItemId()) {
                        case R.id.drawer_menu_search:
                            if (leftFragment.getClass().equals(SearchFragment.class)) {
                                break;
                            }

                            mFragment = new SearchFragment();

                            if (getResources().getBoolean(R.bool.multipane)) {
                                mToolbar.setSubtitle("Search Results");

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
                        case R.id.drawer_menu_favorites:
                            if (leftFragment.getClass().equals(FavoritesFragment.class)) {
                                break;
                            }

                            mFragment = new FavoritesFragment();

                            if (getResources().getBoolean(R.bool.multipane)) {
                                mToolbar.setSubtitle("Favorites");

                                CardViewFragment cardViewFragment = CardViewFragment.newInstance(0, 0, null);

                                getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.fragment_container_right, cardViewFragment, "CardViewFragment")
                                        .commit();
                            }

                            break;
                    }

                    if (mFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, mFragment, null)
                                .commit();

                        return true;
                    }

                    return false;
                }

            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.search_toolbar);
        setSupportActionBar(mToolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coord);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        mNavigationHeader = mNavigationView.getHeaderView(0);
        mHeaderUserPicture = (ImageView) mNavigationHeader.findViewById(R.id.header_user_picture);
        mHeaderUserName = (TextView) mNavigationHeader.findViewById(R.id.header_user_name);
        mHeaderUserEmail = (TextView) mNavigationHeader.findViewById(R.id.header_user_email);

        mNavigationView.setNavigationItemSelectedListener(mNavigationItemSelectedListener);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {
            mFragment = new SearchFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, mFragment, "SearchFragment")
                    .commit();

            mToolbar.setSubtitle("Search Results");

            mNavigationView.getMenu().getItem(0).setChecked(true);

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

        switch (id) {
            case R.id.action_sign_in:
                mSigningInOut = true;

                signIn();

                return true;
            case R.id.action_sign_out:
                mSigningInOut = true;

                signOut();

                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
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

            mHeaderUserPicture.setVisibility(View.VISIBLE);

            if (account.getPhotoUrl() != null && !account.getPhotoUrl().toString().equals("")) {
                Glide.with(this)
                        .load(account.getPhotoUrl().toString())
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        //.skipMemoryCache(true)
                        .transform(new RoundImageTransform(this))
                        .dontAnimate()
                        .into(mHeaderUserPicture);
            }

            mHeaderUserName.setText(account.getDisplayName());
            mHeaderUserEmail.setText(account.getEmail());
            mHeaderUserName.setVisibility(View.VISIBLE);
            mHeaderUserEmail.setVisibility(View.VISIBLE);

            if (mSigningInOut) {
                Snackbar.make(mCoordinatorLayout,
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

            mHeaderUserPicture.setVisibility(View.INVISIBLE);
            mHeaderUserName.setVisibility(View.INVISIBLE);
            mHeaderUserEmail.setVisibility(View.INVISIBLE);

            if (mSigningInOut) {
                Snackbar.make(mCoordinatorLayout,
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

        editor.apply();
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.sign_in_progress_text));
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
