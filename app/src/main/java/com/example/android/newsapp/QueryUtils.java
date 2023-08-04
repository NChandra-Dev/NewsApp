package com.example.android.newsapp;



import android.text.TextUtils;
import android.util.Log;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
// importing Date class
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    private static final String ACCEPT_PROPERTY = "application/geo+json;version=1";
    private static final String USER_AGENT_PROPERTY = "newsapi.org (neerajchandra263@gmail.com)"; //your email id for that site.
    /** Tag for the log messages */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Query the USGS dataset and return an {@link News} object to represent a single earthquake.
     */
    public static List<News> fetchNewsTitle(String requestUrl) {
        // Create URL object
        try {
            Thread.sleep(2000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<News> news = extractFeatureFromJSON(jsonResponse);
        Log.v(LOG_TAG, "fetchNewsTitle");
        // Return the {@link Earthquake}
        return news;
    }
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestProperty("Accept", ACCEPT_PROPERTY);  // added
            urlConnection.setRequestProperty("User-Agent", USER_AGENT_PROPERTY); // added
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() ==  200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news titles.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     *
     */


    private static List<News> extractFeatureFromJSON(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }
        // Create an empty ArrayList that we can start adding earthquakes to
        List<News> news = new ArrayList<>();




        // Try to parse the JSON_RESPONSE_STRING. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            int i;

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonObject = new JSONObject(newsJSON);
            // Extract the JSONArray associated with the key called "features",
            // which represents a list of features (or earthquakes).
            //JSONObject newsObject = baseJsonObject.getJSONObject("response");
            JSONArray articlesArray = baseJsonObject.getJSONArray("articles");
            // For each earthquake in the earthquakeArray, create an {@link Earthquake} object
            for( i = 0; i < articlesArray.length(); i++) {

                JSONObject currentsNews = articlesArray.getJSONObject(i);


                String webTitle = currentsNews.getString("title");
                String url = currentsNews.getString("url");
                String imageUrl = currentsNews.getString("urlToImage");
                Log.v(LOG_TAG, "Url to image"+ imageUrl);
                String date = currentsNews.getString("publishedAt");

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                //SimpleDateFormat outputFormat = new SimpleDateFormat("MMM-dd hh:mm a");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = inputFormat.parse(date);
                String formattedDate = outputFormat.format(parsedDate);
                String date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Log.v(LOG_TAG, "formattedDate= "+ formattedDate + " date2= " + date2);

                //String[] parts = formattedDate.split(" ");
                //formattedDate= parts[0];
                //String time  = parts[1]+" "+parts[2];

                String time = "";
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                // parse method is used to parse
                // the text from a string to
                // produce the date
                Date d1 = sdf.parse(formattedDate);
                Date d2 = sdf.parse(date2);
                // Calucalte time difference
                // in milliseconds
                long difference_In_Time
                        = d2.getTime() - d1.getTime();

                long difference_In_Minutes
                        = (difference_In_Time
                        / (1000 * 60))
                        % 60;

                long difference_In_Hours
                        = (difference_In_Time
                        / (1000 * 60 * 60))
                        % 24;
               String formattedTimeDiff;
               if (difference_In_Hours > 1) {
                   formattedTimeDiff = difference_In_Hours + " hours ago";
               }
               else {
                   formattedTimeDiff = difference_In_Hours + " hour ago";
               }
                Log.v(LOG_TAG, "Minutes= "+ difference_In_Minutes + " Hours= " + difference_In_Hours);
                //String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());


                News New = new News( webTitle, formattedTimeDiff, url, time, imageUrl);
                if(imageUrl == "null") {
                    news.remove(New);
                }
                else{
                    news.add(New);
                }
            }
        }
        catch (JSONException | ParseException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing news JSON results", e);
        }
        // Return the list of earthquakes
        return news;
    }

}
