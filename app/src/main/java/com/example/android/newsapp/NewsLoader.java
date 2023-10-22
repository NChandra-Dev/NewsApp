package com.example.android.newsapp;



import android.app.Activity;
import android.content.Context;


import androidx.annotation.Nullable;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;




public class NewsLoader extends AsyncTaskLoader<List<News>> {
    private static final String LOG_TAG = MainActivity.class.getName();
    /** Query URL */
    private String mUrl;
    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://content.guardianapis.com/search?api-key=880bca8a-fea6-4a57-9deb-eafb832aef73";

    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
        Log.v(LOG_TAG, "NewsLoader");
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG, "onStartLoading method");
        forceLoad();
    }



    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.v(LOG_TAG, "loadInBackground method");
        List<News> news = QueryUtils.fetchNewsTitle(mUrl);
        return news;
    }



}
