package com.javierarboleda.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.javierarboleda.popularmovies.domain.Movie;
import com.javierarboleda.popularmovies.domain.Review;
import com.javierarboleda.popularmovies.domain.Trailer;
import com.javierarboleda.popularmovies.util.Constants;
import com.javierarboleda.popularmovies.util.MovieDbUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Javier Arboleda on 8/10/15.
 *
 * Fragment for the movie details activity
 *
 */
public class DetailFragment extends Fragment {

    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;
    private Intent mIntent;
    private String mApiKey;
    private TrailersAdapter mTrailersAdapter;
    private ListView mTrailersListView;
    private ListView mReviewsListView;
    private View mRootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getActivity().getIntent();
        mApiKey = mIntent.getStringExtra(MovieDbUtil.API_KEY_PARAM);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_details, container, false);

        injectFragmentViews(mRootView);

        return mRootView;

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

        // get values passed through intent and set to local variables
        Movie movie = mIntent.getParcelableExtra(MovieDbUtil.MOVIE);
        String posterPath = movie.getPosterPath();
        String voteCount = movie.getVoteCount();
        String voteAverage = movie.getVoteAverage();
        String backdropPath = movie.getBackdropPath();
        String overview = movie.getOverview();
        String releaseDate = movie.getHumanReadableReleaseDate();
        String title = movie.getTitle();
        int movieId = movie.getMovieId();

        Uri posterUri = Uri.parse(
                MovieDbUtil.BASE_IMAGE_URL + Constants.SIZE_W342 + posterPath);
        String posterUrl = posterUri.toString();
        posterUrl = posterUrl.isEmpty() ? null : posterUrl;

        Uri backdropUri = Uri.parse(
                MovieDbUtil.BASE_IMAGE_URL + Constants.SIZE_W780 + backdropPath);
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

        // get and set trailers
        URL trailersUrl = MovieDbUtil.getVideosUrl(movieId, mApiKey);
        new FetchMovieTrailers().execute(trailersUrl);

        // get and set reviews

        URL reviewsUrl = MovieDbUtil.getReviewsUrl(movieId, mApiKey);
        new FetchMovieReviews().execute(reviewsUrl);

    }

    public void populateTrailersAdapter(List<Trailer> trailers) {
        if (trailers != null) {
            mTrailersAdapter = new TrailersAdapter(getActivity(), trailers);
            mTrailersListView = (ListView) mRootView.findViewById(R.id.trailers_listview);
            mTrailersListView.setAdapter(mTrailersAdapter);
        }

        mTrailersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                // todo: add click logic to send to youtube vid

            }
        });
    }

    public class FetchMovieReviews extends AsyncTask<URL, Void, ArrayList<Review>> {

        private final String LOG_TAG = FetchMovieReviews.class.getSimpleName();

        @Override
        protected ArrayList<Review> doInBackground(URL... params) {

            URL reviewUrl = params[0];

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            String responseJsonStr = null;

            try {

                connection = (HttpURLConnection) reviewUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Read the input stream into a String
                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                responseJsonStr = buffer.toString();

                mReviews = MovieDbUtil.getReviewsFromJson(responseJsonStr);

                return mReviews;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> reviews) {
            super.onPostExecute(reviews);
        }
    }


    public class FetchMovieTrailers extends AsyncTask<URL, Void, ArrayList<Trailer>> {
        private final String LOG_TAG = FetchMovieReviews.class.getSimpleName();

        @Override
        protected ArrayList<Trailer> doInBackground(URL... params) {

            URL trailerUrl = params[0];

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            String responseJsonStr = null;

            try {

                connection = (HttpURLConnection) trailerUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                // Read the input stream into a String
                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                responseJsonStr = buffer.toString();

                mTrailers = MovieDbUtil.getTrailersFromJson(responseJsonStr);

                return mTrailers;

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailer> trailers) {

            populateTrailersAdapter(trailers);
            setListViewHeightBasedOnChildren(mTrailersListView);
        }
    }

    /**
     * a hack that measures and sets the height of the given listview
     *
     * I found online that it's a no-no to put a ListView in a ScrollView, which I have done.
     * This method allows you to get around the listview collapsing to one item.
     *
     * I took this helper method code from:
     * http://nex-otaku-en.blogspot.com/2010/12/android-put-listview-in-scrollview.html
     *
     * I plan to find a better solution to using a listview within a scrollview if I have the time
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
