/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

//Implementing the LoaderCallbacks in our activity is a
// little more complex. First we need to say that EarthquakeActivity
// implements the LoaderCallbacks interface, along with a generic parameter
// specifying what the loader will return (in this case an Earthquake).

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
    /**
     * Constant value for the earthquake loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int NEWS_LOADER_ID = 1;
    /** Adapter for the list of earthquakes */
    /**
     * URL for earthquake data from the USGS dataset
     */
    private static final String NEWS_REQUEST_URL =
            "https://newsapi.org/v2/top-headlines?country=in&apiKey=f82a17ce9a23494e832c2471278a3467";
    private static final String IMAGE_URL ="https://c.ndtvimg.com/2022-09/k8dft97_-bharat-jodo-yatra-ani-650_625x300_07_September_22.jpg";
    /**
     * Adapter for the list of earthquakes
     */
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ImageView imageView = (ImageView) findViewById(R.id.image);
        // Find a reference to the {@link ListView} in the layout
        ListView newsListView = (ListView) findViewById(R.id.list);
        // setEmptyView on ListView in Empty state.
        // the line of code written below can be
        // changed to less code by directly calling Id.
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_text_view);
        newsListView.setEmptyView(mEmptyStateTextView);
        //progressBar = (ProgressBar) findViewById(R.id.loading_spinner);
        //progressBar.setVisibility(View.VISIBLE);


        //Picasso.get().load("https://i.imgur.com/DvpvklR.png").into(imageView);
        // Create a new adapter that takes an empty list of earthquakes as input
        //earthquakes, android.R.layout.simple_list_item_1
        mAdapter = new NewsAdapter(this, new ArrayList<News>());
        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsListView.setAdapter(mAdapter);


        newsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                News currentNews = mAdapter.getItem(position);


                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri newsUri = Uri.parse(currentNews.getURL());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }

        });
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo!= null && networkInfo.isConnected()){
            //Finally, to retrieve an earthquake,
            // we need to get the loader manager and tell the loader manager to initialize the loader
            // with the specified ID, the second argument allows us to pass a bundle of additional information,
            // which we'll skip. The third argument is what object should receive the LoaderCallbacks (and therefore,
            // the data when the load is complete!) - which will be this activity. This code goes inside the onCreate()
            // method of the EarthquakeActivity, so that the loader can be initialized as soon as the app opens.
            // Get a reference to the LoaderManager, in order to interact with loaders.
            Log.v(LOG_TAG, "getLoaderManager()");
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            Log.v(LOG_TAG, "what is happening");
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
            Log.v(LOG_TAG, "initLoader");
        }
        else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_spinner);
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText("No Internet Connection");

        }

    }
    //Then we need to override the three methods specified
    // in the LoaderCallbacks interface. We need onCreateLoader(),
    // for when the LoaderManager has determined that the loader with
    // our specified ID isn't running, so we should create a new one.


    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader(this, NEWS_REQUEST_URL);
    }

    /**
     * This method is invoked on the main UI thread after the background work has been
     * completed.
     * <p>
     * It IS okay to modify the UI within this method. We take the {@link News} object
     * (which was returned from the doInBackground() method) and update the views on the screen.
     */
    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> earthquakes) {

        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_spinner);
        loadingIndicator.setVisibility(View.GONE);
        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_data);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();
        Log.v(LOG_TAG, "onLoaderFinished");
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (earthquakes != null && !earthquakes.isEmpty()) {
            mAdapter.addAll(earthquakes);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        // we're being informed that the data from our loader is no longer valid.
        // This isn't actually a case that's going to come up with our simple loader,
        // but the correct thing to do is to remove all the earthquake data
        // from our UI by clearing out the adapter’s data set.
        Log.v(LOG_TAG, "onLoaderReset");
        mAdapter.clear();
    }

}



