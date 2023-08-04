package com.example.android.newsapp;


public class News {

    private String mTitle;
    private String mDate;
    private String mUrl;
    private String  mTime;
    //private String mSectionName;
    private String mImageUrl;


    public News( String Title, String Date, String Url, String time, String imageUrl) {
       mTitle = Title;
       mDate = Date;
        mUrl = Url;
        mTime = time;
        mImageUrl = imageUrl;
    }
    //public String getSectionName() {
       // return mSectionName;
    //}
    public String getTitle() {
        return mTitle;
    }

    public String getDate() {
        return mDate;
    }
    /**
     * Get the URL of the news.
     */
    public String getURL() {
        return mUrl;
    }
    public String getTime() {
        return mTime;
    }
    public String getImageURL() {
        return mImageUrl;
    }




}