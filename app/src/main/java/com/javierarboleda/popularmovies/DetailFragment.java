package com.javierarboleda.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.javierarboleda.popularmovies.util.MovieDbUtil;
import com.javierarboleda.popularmovies.util.Constants;
import com.squareup.picasso.Picasso;

/**
 * Created by Javier Arboleda on 8/10/15.
 *
 * Fragment for the movie details activity
 *
 */
public class DetailFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_details, container, false);

        injectFragmentViews(rootView);

        return rootView;

    }

    /**
     * Takes all the values passed through intent and populates the layout views
     *
     * @param rootView
     */
    private void injectFragmentViews(View rootView) {

        // get references to all of the layout views
        ImageView backdropImageView = (ImageView) rootView.findViewById(R.id.backdrop_imageview);
        RatingBar ratingBar = (RatingBar) rootView.findViewById(R.id.rating_bar);
        ImageView posterImageView = (ImageView) rootView
                .findViewById(R.id.poster_details_imageview);
        TextView titleTextView = (TextView) rootView.findViewById(R.id.title_details_textview);
        TextView releaseDateTextView = (TextView) rootView
                .findViewById(R.id.release_date_details_textview);
        TextView ratingTextView = (TextView) rootView.findViewById(R.id.rating_textview);
        TextView overviewTextView = (TextView) rootView.findViewById(R.id.overview_textview);

        Intent intent = getActivity().getIntent();

        // get values passed through intent and set to local variables
        String posterPath = intent.getStringExtra(MovieDbUtil.POSTER_PATH);
        String voteCount = intent.getStringExtra(MovieDbUtil.VOTE_COUNT);
        String voteAverage = intent.getStringExtra(MovieDbUtil.VOTE_AVERAGE);
        String backdropPath = intent.getStringExtra(MovieDbUtil.BACKDROP_PATH);
        String overview = intent.getStringExtra(MovieDbUtil.OVERVIEW);
        String releaseDate = intent.getStringExtra(MovieDbUtil.RELEASE_DATE);
        String title = intent.getStringExtra(MovieDbUtil.TITLE);

        Uri posterUri = Uri.parse(
                MovieDbUtil.BASE_IMAGE_URL + Constants.SIZE_W342 + posterPath);
        String posterUrl = posterUri.toString();
        posterUrl = posterUrl.isEmpty() ? null : posterUrl;

        Uri backdropUri = Uri.parse(
                MovieDbUtil.BASE_IMAGE_URL + Constants.SIZE_W342 + backdropPath);
        String backdropUrl = backdropUri.toString();
        backdropUrl = backdropUrl.isEmpty() ? null : backdropUrl;

        // set layout views values
        Picasso.with(getActivity())
                .load(posterUrl)
                .placeholder(R.drawable.poster_placeholder_w342)
                .error(R.drawable.poster_error_w342)
                .into(posterImageView);
        Picasso.with(getActivity())
                .load(backdropUrl)
                .placeholder(R.drawable.backdrop_placeholder_w780)
                .error(R.drawable.backdrop_error_w780)
                .into(backdropImageView);
        ratingBar.setRating(Float.parseFloat(voteAverage));
        titleTextView.setText(title);
        releaseDateTextView.setText(releaseDate);
        ratingTextView.setText(voteAverage + "/10 from " + voteCount + " users");
        overviewTextView.setText(overview);

    }
}
