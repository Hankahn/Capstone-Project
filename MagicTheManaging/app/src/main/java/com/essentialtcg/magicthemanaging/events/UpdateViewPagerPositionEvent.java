package com.essentialtcg.magicthemanaging.events;

/**
 * Created by Shawn on 5/27/2016.
 */
public class UpdateViewPagerPositionEvent {

    public final int currentPosition;

    public UpdateViewPagerPositionEvent(int currentPosition) {
        this.currentPosition = currentPosition;
    }

}
