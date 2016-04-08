package com.essentialtcg.magicthemanaging.data;

import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by Shawn on 4/4/2016.
 */
public class SetTransform {

    public static ArrayList<SetItem> transform(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        ArrayList<SetItem> setItems = new ArrayList<>();

        String group = "";

        while (cursor.moveToNext()) {
            SetItem setItem = new SetItem();

            setItem.setId(cursor.getInt(SetLoader.Query._ID));
            setItem.setName(cursor.getString(SetLoader.Query.NAME));
            setItem.setCode(cursor.getString(SetLoader.Query.CODE));
            setItem.setGathererCode(cursor.getString(SetLoader.Query.GATHERER_CODE));
            setItem.setOldCode(cursor.getString(SetLoader.Query.OLD_CODE));
            setItem.setMagicCardsInfoCode(cursor.getString(SetLoader.Query.MAGIC_CARDS_INFO_CODE));
            setItem.setReleaseDate(cursor.getString(SetLoader.Query.RELEASE_DATE));
            setItem.setBorder(cursor.getString(SetLoader.Query.BORDER));
            setItem.setSetType(cursor.getString(SetLoader.Query.SET_TYPE));
            setItem.setBlock(cursor.getString(SetLoader.Query.BLOCK));
            setItem.setOnlineOnly(cursor.getInt(SetLoader.Query.ONLINE_ONLY) > 0);
            setItem.setBooster(cursor.getString(SetLoader.Query.BOOSTER));

            String groupName = setItem.getBlock().length() > 0 ?
                    setItem.getBlock() : setItem.getSetType();

            if (group.length() == 0 ||
                    !group.equals(groupName)) {

                setItem.setFirstSet(true);

                group = groupName;
            } else {
                setItem.setFirstSet(false);
            }

            setItems.add(setItem);
        }

        return setItems;
    }

}
