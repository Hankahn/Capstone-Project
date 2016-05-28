package com.essentialtcg.magicthemanaging.callback;

import android.view.View;

import com.essentialtcg.magicthemanaging.data.CardSearchParameters;

/**
 * Created by Shawn on 5/22/2016.
 */
public interface LoadCardDetailCallback {

    void onLoadCardDetailCallback(int position, long selectedItemId, View transitionView,
                                  CardSearchParameters searchParameters);

}
