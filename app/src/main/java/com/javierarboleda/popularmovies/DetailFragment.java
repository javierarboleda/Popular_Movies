package com.javierarboleda.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.javierarboleda.popularmovies.data.MovieContract;
import com.javierarboleda.popularmovies.domain.Movie;
import com.javierarboleda.popularmovies.domain.Review;
import com.javierarboleda.popularmovies.domain.Trailer;
import com.javierarboleda.popularmovies.util.Constants;
import com.javierarboleda.popularmovies.util.MovieDbUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
    private ReviewsAdapter mReviewsAdapter;
    private LinearLayout mTrailersLinearLayout;
    private LinearLayout mReviewsLinearLayout;
    private Movie mMovie;
    private View mRootView;
    private Menu mMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mIntent = getActivity().getIntent();
        mApiKey = mIntent.getStringExtra(MovieDbUtil.API_KEY_PARAM);
        mMovie = mIntent.getParcelableExtra(MovieDbUtil.MOVIE);

        if(mMovie == null)
            return;

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if (args != null) {
            mMovie = args.getParcelable(MovieDbUtil.MOVIE);
            mApiKey = args.getString(MovieDbUtil.API_KEY_PARAM);
        }

        if(mMovie == null)
            return null;

        checkIfIsFavorite();

        mRootView = inflater.inflate(R.layout.fragment_details, container, false);

        injectFragmentViews(mRootView);

        return mRootView;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_details, menu);
        mMenu = menu;

        if (mMovie.isFavorite()) {
            setAsFavoriteIcon();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.favorite_item:
                if (!mMovie.isFavorite())
                    toggleFavorite();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * When a movie is added to favorites, this method will add movie, trailer, and review
     * details to the database using ContentProvider
     */
    private void addFavoriteToDb(Movie movie) {

        insertMovieIntoDb(movie);

        insertTrailersIntoDb(movie);

        insertReviewsIntoDb(movie);
    }

    private void insertReviewsIntoDb(Movie movie) {
        ContentValues reviewValues = new ContentValues();
        for (Review review : mReviews) {
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie.getMovieId());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, review.getAuthor());
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, review.getContent());

            getActivity().getContentResolver()
                    .insert(MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
        }
    }

    private void insertTrailersIntoDb(Movie movie) {

        ContentValues trailerValues = new ContentValues();

        String fileName;

        for (int i = 0; i < mTrailers.size(); i++) {
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie.getMovieId());
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, mTrailers.get(i).getKey());
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, mTrailers.get(i).getName());

            getActivity().getContentResolver()
                    .insert(MovieContract.TrailerEntry.CONTENT_URI, trailerValues);

            // save trailer thumbnail to internal memory
            // example to save imageView image to directory from:
            // http://stackoverflow.com/questions/17674634/
            // saving-and-reading-bitmaps-images-from-internal-memory-in-android
            fileName = mTrailers.get(i).getKey() + ".jpg";

            ImageView view = (ImageView) mTrailersAdapter
                    .getView(i, null, null).findViewById(R.id.trailer_listview_item_image);

            // saving images
            view.buildDrawingCache();
            BitmapDrawable bitmapDrawable = (BitmapDrawable) view.getDrawable();
            Bitmap bitmap = bitmapDrawable.getBitmap();

            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File myPath = new File(directory, fileName);

            FileOutputStream fos;
            try {

                fos = new FileOutputStream(myPath);

                // Use the compress method on the BitMap object to write image to the OutputStream
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                File f = new File(directory, fileName);
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                System.out.print(b.toString());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private void insertMovieIntoDb(Movie movie) {

        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getMovieId());
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_BACKGROUND_PATH, movie.getBackdropPath());
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_COUNT, movie.getVoteCount());
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, "true");

        getActivity().getContentResolver()
                .insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
    }

    /**
     * Takes all the values passed through intent and populates the layout views
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
        String posterPath = mMovie.getPosterPath();
        String voteCount = mMovie.getVoteCount();
        String voteAverage = mMovie.getVoteAverage();
        String backdropPath = mMovie.getBackdropPath();
        String overview = mMovie.getOverview();
        String releaseDate = mMovie.getHumanReadableReleaseDate();
        String title = mMovie.getTitle();
        int movieId = mMovie.getMovieId();

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

    public void populateTrailersAdapter() {
        if (mTrailersAdapter != null) {
            mTrailersLinearLayout =
                    (LinearLayout) mRootView.findViewById(R.id.trailers_linearlayout);

            for(int i = 0; i < mTrailersAdapter.getCount(); i++) {
                final int position = i;
                View view = mTrailersAdapter.getView(i, null, null);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Trailer trailer = (Trailer) mTrailersAdapter.getItem(position);
                        startYoutubeActivity(trailer.getYoutubeUri());
                    }
                });
                mTrailersLinearLayout.addView(view);
            }
        }
    }

    private void populateReviewsAdapter() {
        if (mReviewsAdapter != null) {
            mReviewsLinearLayout = (LinearLayout) mRootView.findViewById(R.id.reviews_linearlayout);

            for(int i = 0; i < mReviewsAdapter.getCount(); i++) {
                final int position = i;
                View view = mReviewsAdapter.getView(i, null, null);
                mReviewsLinearLayout.addView(view);
            }

        }
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
                mReviewsAdapter = new ReviewsAdapter(getActivity(), mReviews);
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
            populateReviewsAdapter();
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
                mTrailersAdapter = new TrailersAdapter(getActivity(), mTrailers,
                        mMovie.isFavorite());
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
            populateTrailersAdapter();
        }
    }

    private void toggleFavorite() {
        addFavoriteToDb(mMovie);
        mMovie.setFavorite(true);
        setAsFavoriteIcon();
    }

    /**
     * Used for setting favorite when using master/detail tablet layout
     */
    public void toggleFavoriteTwoPane() {
        addFavoriteToDb(mMovie);
        mMovie.setFavorite(true);
    }

    private void setAsFavoriteIcon() {
        mMenu.findItem(R.id.favorite_item).setIcon(R.drawable.ic_action_content_heart_circle);
        mMovie.setFavorite(true);
    }

    private void startYoutubeActivity(Uri youtubeUri) {
        startActivity(new Intent(Intent.ACTION_VIEW, youtubeUri));
    }

    /**
     * Checks the local database to determine if this movie exists as a favorite, if so, will set
     * member movie object favorite attribute to true
     */
    private void checkIfIsFavorite() {
        Cursor c =
                getActivity().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(mMovie.getMovieId())},
                        null);
        if (c.getCount() > 0){
            mMovie.setFavorite(true);
        }
    }

    public boolean isFavorite() {
        return mMovie.isFavorite();
    }
}
