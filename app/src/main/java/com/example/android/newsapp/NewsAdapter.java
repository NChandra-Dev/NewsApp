package com.example.android.newsapp;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Activity context, ArrayList<News> news) {
        super(context, 0, news);

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        //String imageUrl = currentWord.getImageURL();

        News currentWord = getItem(position);
        //String section = currentWord.getSectionName();
        //TextView sectionTextView = (TextView) listItemView.findViewById(R.id.section_text_view);
        //sectionTextView.setText(section);

        String output = currentWord.getTitle();
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
        titleTextView.setText(output);

        String date = currentWord.getDate();
        TextView dateTextView = (TextView) listItemView.findViewById(R.id.date_text_view);
        dateTextView.setText(date);

        //String time = currentWord.getTime();
        //TextView timeTextView = (TextView) listItemView.findViewById(R.id.time_text_view);
        //timeTextView.setText(time);

        String imageURL = currentWord.getImageURL();

        ImageView imageView = (ImageView) listItemView.findViewById(R.id.image);
        Glide.with(imageView.getContext()).load(imageURL)
                .diskCacheStrategy(DiskCacheStrategy.ALL).centerCrop().into(imageView);
        return listItemView;

    }


    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }
}

