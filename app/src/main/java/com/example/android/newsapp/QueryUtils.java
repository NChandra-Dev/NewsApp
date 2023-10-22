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


public final class QueryUtils {



    private QueryUtils() {
    }

    private static final String ACCEPT_PROPERTY = "application/geo+json;version=1";
    private static final String USER_AGENT_PROPERTY = "newsapi.org (neerajchandra263@gmail.com)"; //your email id for that site.

    public static final String LOG_TAG = QueryUtils.class.getSimpleName();


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

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


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

                inputStream.close();
            }
        }
        return jsonResponse;
    }


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




    private static List<News> extractFeatureFromJSON(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)){
            return null;
        }

        List<News> news = new ArrayList<>();





        try {
            int i;


            JSONObject baseJsonObject = new JSONObject(newsJSON);

            JSONArray articlesArray = baseJsonObject.getJSONArray("articles");

            for( i = 0; i < articlesArray.length(); i++) {

                JSONObject currentsNews = articlesArray.getJSONObject(i);


                String webTitle = currentsNews.getString("title");
                String url = currentsNews.getString("url");
                String imageUrl = currentsNews.getString("urlToImage");
                Log.v(LOG_TAG, "Url to image"+ imageUrl);
                String date = currentsNews.getString("publishedAt");

                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
                SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date parsedDate = inputFormat.parse(date);
                String formattedDate = outputFormat.format(parsedDate);
                String date2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                Log.v(LOG_TAG, "formattedDate= "+ formattedDate + " date2= " + date2);



                String time = "";
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date d1 = sdf.parse(formattedDate);
                Date d2 = sdf.parse(date2);

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

            Log.e("QueryUtils", "Problem parsing news JSON results", e);
        }

        return news;
    }

}
