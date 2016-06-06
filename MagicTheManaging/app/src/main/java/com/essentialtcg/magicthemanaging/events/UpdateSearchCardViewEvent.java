package com.essentialtcg.magicthemanaging.events;

import com.essentialtcg.magicthemanaging.data.CardSearchParameters;

/**
 * Created by Shawn on 5/28/2016.
 */
public class UpdateSearchCardViewEvent {

    public final CardSearchParameters searchParameters;

    public UpdateSearchCardViewEvent(CardSearchParameters searchParameters) {
        this.searchParameters = searchParameters;
    }

}
