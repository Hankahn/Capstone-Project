package com.essentialtcg.magicthemanaging.events;

/**
 * Created by Shawn on 5/27/2016.
 */
public class UpdateRecyclerViewPositionReturnEvent {

    public final int initialPosition;
    public final int currentPosition;

    public UpdateRecyclerViewPositionReturnEvent(int initialPosition, int currentPosition) {
        this.initialPosition = initialPosition;
        this.currentPosition = currentPosition;
    }

}
