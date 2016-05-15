package com.essentialtcg.magicthemanaging.data.loaders;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import com.essentialtcg.magicthemanaging.data.contracts.SetContract;

/**
 * Created by Shawn on 4/4/2016.
 */
public class SetLoader extends CursorLoader {

    public static SetLoader newAllSetsInstance(Context context) {
        return new SetLoader(context, SetContract.Sets.buildDirUri());
    }

    public static SetLoader newInstanceForSetId(Context context, long setId) {
        return new SetLoader(context, SetContract.Sets.buildSetUri(setId));
    }

    private SetLoader(Context context, Uri uri) {
        super(context, uri, Query.PROJECTION, null, null, SetContract.Sets.DEFAULT_SORT);
    }

    public interface Query {
        String[] PROJECTION = {
                SetContract.Sets._ID,
                SetContract.Sets.NAME,
                SetContract.Sets.CODE,
                SetContract.Sets.GATHERER_CODE,
                SetContract.Sets.OLD_CODE,
                SetContract.Sets.MAGIC_CARDS_INFO_CODE,
                SetContract.Sets.RELEASE_DATE,
                SetContract.Sets.BORDER,
                SetContract.Sets.SET_TYPE,
                SetContract.Sets.BLOCK,
                SetContract.Sets.ONLINE_ONLY,
                SetContract.Sets.BOOSTER
        };

        int _ID = 0;
        int NAME = 1;
        int CODE = 2;
        int GATHERER_CODE = 3;
        int OLD_CODE = 4;
        int MAGIC_CARDS_INFO_CODE = 5;
        int RELEASE_DATE = 6;
        int BORDER = 7;
        int SET_TYPE = 8;
        int BLOCK = 9;
        int ONLINE_ONLY = 10;
        int BOOSTER = 11;

    }

}
