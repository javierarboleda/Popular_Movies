package com.javierarboleda.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.javierarboleda.popularmovies.domain.Movie;
import com.javierarboleda.popularmovies.util.MovieDbUtil;
import com.javierarboleda.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Javier Arboleda on 8/1/15.
 *
 * Some code taken from http://developer.android.com/guide/topics/ui/layout/gridview.html
 *
 * Even more code taken from https://github.com/udacity/android-custom-arrayadapter/blob/master/app/src/main/java/demo/example/com/customarrayadapter/AndroidFlavorAdapter.java
 *   Copied some comments from above sample code for studying purposes
 *
 */
class PostersFragmentImageAdapter extends ArrayAdapter<Movie> implements View.OnClickListener {



    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context       The current context. Used to inflate the layout file.
     * @param movies  A List of Movie objects to display in a list
     */
    public PostersFragmentImageAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }

    /**
     * Provides a view for an AdapterView
     *
     * @param position  The AdapterView position that is requesting a view
     * @param convertView   The recycled view to populate. (google "android view recycling")
     * @param parent    The parent ViewGroup that is used for inflation
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Movie movie = getItem(position);

        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.grid_item_poster, parent, false);
        }

        ImageView posterView =
                (ImageView) convertView.findViewById(R.id.grid_item_poster_image);

        Uri uri = Uri.parse(
                MovieDbUtil.BASE_IMAGE_URL + Constants.SIZE_W342 + movie.getPosterPath());
        String url = uri.toString();
        url = url.isEmpty() ? null : url;

        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.poster_placeholder_w342)
                .error(R.drawable.poster_error_w342)
                .into(posterView);

        TextView titleView = (TextView) convertView.findViewById(R.id.grid_item_poster_title);
        titleView.setText(movie.getTitle());

        TextView releaseYearView = (TextView) convertView.findViewById(R.id.grid_item_poster_release_year);
        releaseYearView.setText(movie.getHumanReadableReleaseDate());

        return convertView;
    }

    @Override
    public void onClick(View v) {

    }
}
