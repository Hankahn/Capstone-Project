package com.essentialtcg.magicthemanaging.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.ui.fragments.FavoritesFragment;
import com.essentialtcg.magicthemanaging.ui.fragments.SearchFragment;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        FavoritesFragment favoritesFragment = new FavoritesFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.favorites_fragment_container, favoritesFragment, "FavoritesFragment")
                .commit();
    }
}
