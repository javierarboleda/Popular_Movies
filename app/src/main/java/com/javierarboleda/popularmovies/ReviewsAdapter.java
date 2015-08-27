package com.javierarboleda.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.javierarboleda.popularmovies.domain.Review;

import java.util.List;

/**
 * Created by Javier Arboleda on 8/26/15.
 */
public class ReviewsAdapter extends ArrayAdapter<Review> {

    public ReviewsAdapter(Activity context, List<Review> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Review review = getItem(position);

        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.review_layout_item, parent, false);
        }

        TextView contentTextView = (TextView)
                convertView.findViewById(R.id.review_content_textview);
        contentTextView.setText(review.getContent());

        TextView authorTextView = (TextView)
                convertView.findViewById(R.id.review_author_textview);
        authorTextView.setText("by " + review.getAuthor());

        return convertView;
    }

}
