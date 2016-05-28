package com.essentialtcg.magicthemanaging.events;

/**
 * Created by Shawn on 5/27/2016.
 */
public class UpdateRecyclerViewPositionEvent {

    public final int currentPosition;

    public UpdateRecyclerViewPositionEvent(int currentPosition) {
        this.currentPosition = currentPosition;
    }

}
