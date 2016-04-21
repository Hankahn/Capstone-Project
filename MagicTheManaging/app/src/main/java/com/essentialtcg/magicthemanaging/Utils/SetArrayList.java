package com.essentialtcg.magicthemanaging.utils;

import com.essentialtcg.magicthemanaging.R;
import com.essentialtcg.magicthemanaging.data.SetItem;

import java.util.ArrayList;

/**
 * Created by Shawn on 4/16/2016.
 */
public class SetArrayList extends ArrayList<SetItem> {

    public boolean containsSetItem(SetItem setItem) {
        if (this.size() == 0) {
            return false;
        }

        for(SetItem item : this) {
            if (item.getCode().equals(setItem.getCode())) {
                return true;
            }
        }

        return false;
    }

    public void removeSetItem(SetItem setItem) {
        for (SetItem item : this) {
            if (item.getCode().equals(setItem.getCode())) {
                remove(item);
                return;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder selectedSetsText = new StringBuilder();

        for (SetItem set : this) {
            if(selectedSetsText.length() > 0) {
                selectedSetsText.append(" or ");
            }

            selectedSetsText.append(set.getName());
        }

        return selectedSetsText.toString();
    }
}
