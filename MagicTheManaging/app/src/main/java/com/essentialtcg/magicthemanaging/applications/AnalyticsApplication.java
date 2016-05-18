package com.essentialtcg.magicthemanaging.applications;

import android.app.Application;

import com.essentialtcg.magicthemanaging.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Shawn on 5/15/2016.
 */
public class AnalyticsApplication extends Application {

    private Tracker mTracker;

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            mTracker = analytics.newTracker(R.xml.global_tracker);
        }

        return mTracker;
    }

}
