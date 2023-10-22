
package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.Loader;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ContentLoadingProgressBar;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;



public class MainActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    private String url;
    private ImageView imageView;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;
    ProgressBar progressBar;
    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int NEWS_LOADER_ID = 1;

    private static final String NEWS_REQUEST_URL =
            "https://newsapi.org/v2/top-headlines?country=in&apiKey=f82a17ce9a23494e832c2471278a3467";
    private static final String IMAGE_URL ="https://c.ndtvimg.com/2022-09/k8dft97_-bharat-jodo-yatra-ani-650_625x300_07_September_22.jpg";

    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView newsListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_text_view);
        newsListView.setEmptyView(mEmptyStateTextView);




        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        newsListView.setAdapter(mAdapter);


        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News currentNews = mAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentNews.getURL());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(websiteIntent);
            }

        });

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo!= null && networkInfo.isConnected()){
            Log.v(LOG_TAG, "getLoaderManager()");
            LoaderManager loaderManager = getLoaderManager();
            Log.v(LOG_TAG, "what is happening");
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            Log.v(LOG_TAG, "initLoader");
        }
        else {

            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText("No Internet Connection");

        }

    }



    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, NEWS_REQUEST_URL);
    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> earthquakes) {


        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);

        mEmptyStateTextView.setText(R.string.no_data);


        mAdapter.clear();
        Log.v(LOG_TAG, "onLoaderFinished");

        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        Log.v(LOG_TAG, "onLoaderReset");
        mAdapter.clear();
    }

}



