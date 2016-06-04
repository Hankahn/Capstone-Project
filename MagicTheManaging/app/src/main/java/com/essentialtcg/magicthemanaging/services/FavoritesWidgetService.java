package com.essentialtcg.magicthemanaging.services;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.essentialtcg.magicthemanaging.factories.FavoritesViewsFactory;

/**
 * Created by Shawn on 5/31/2016.
 */
public class FavoritesWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoritesViewsFactory(this.getApplicationContext(), intent);
    }

}
