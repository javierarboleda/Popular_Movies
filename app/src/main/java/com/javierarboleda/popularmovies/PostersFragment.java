package com.javierarboleda.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.javierarboleda.popularmovies.data.MovieContract;
import com.javierarboleda.popularmovies.domain.Movie;
import com.javierarboleda.popularmovies.util.Constants;
import com.javierarboleda.popularmovies.util.MovieDbUtil;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Javier Arboleda on 7/29/15.
 *
 * Fragment for the Posters activity
 *
 */
public class PostersFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private String mApiKey;
    private PostersFragmentImageAdapter mPostersFragmentImageAdapter;
    private View mRootView;
    private Menu mMenu;
    private boolean mDescending;
    private String mSortBy;
    private String mSortOrder;
    private GridView mGridView;
    ArrayList<Movie> mMovies;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mDescending = true;

        mGridView = (GridView) getActivity().findViewById(R.id.gridview_posters);

        // help with loading a property comes from :
        // >> http://myossdevblog.blogspot.com/2010/02/reading-properties-files-on-android.html
        Resources resources = this.getResources();
        AssetManager assetManager = resources.getAssets();

        try {
            InputStream inputStream = assetManager.open(Constants.APP_PROPERTIES);
            Properties properties = new Properties();
            properties.load(inputStream);
            mApiKey = properties.getProperty(Constants.TMDB_API_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_posters, container, false);

        if (savedInstanceState != null) {
            mDescending = savedInstanceState.getBoolean(Constants.DESCENDING);
            mSortBy = savedInstanceState.getString(Constants.SORT_BY);
            mSortOrder = savedInstanceState.getString(Constants.SORT_ORDER);
            mMovies = (ArrayList<Movie>) savedInstanceState.get(Constants.MOVIE_LIST);
            populateImageAdapter(mMovies);
        }
        else {
            mSortBy = "popularity";
            mSortOrder = "desc";
            populateFragmentImageAdapter(mSortBy, mSortOrder);
        }


        return mRootView;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(Constants.DESCENDING, mDescending);
        outState.putString(Constants.SORT_BY, mSortBy);
        outState.putString(Constants.SORT_ORDER, mSortOrder);
        outState.putParcelableArrayList(Constants.MOVIE_LIST, mMovies);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_posters, menu);
        mMenu = menu;

        if (!mDescending) {
            mMenu.findItem(R.id.ascending_or_descending_menu_item)
                    .setIcon(R.drawable.ic_ascending);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.sort_by_menu_item:
                showPopup(item);
                break;
            case R.id.ascending_or_descending_menu_item:
                toggleSortOrder();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.menu_item_popularity:
                mMenu.findItem(R.id.sort_by_menu_item).setTitle(R.string.popularity);
                populateFragmentImageAdapter(MovieDbUtil.POPULARITY, mSortOrder);
                break;
            case R.id.menu_item_highest_rating:
                mMenu.findItem(R.id.sort_by_menu_item).setTitle(R.string.highest_rated);
                populateFragmentImageAdapter(MovieDbUtil.VOTE_AVERAGE, mSortOrder);
                break;
            case R.id.menu_item_favorite_movies:
                mMenu.findItem(R.id.sort_by_menu_item).setTitle(R.string.favorite_movies);
                populateFragmentImageAdapter(MovieDbUtil.FAVORITE_MOVIES, mSortOrder);
                break;
        }

        return false;
    }

    private void populateFragmentImageAdapter(String sortBy, String sortOrder) {

        if (!isNetworkAvailable()) {
            Toast.makeText(getActivity(), Constants.NO_NETWORK_TOAST_MESSAGE, Toast.LENGTH_SHORT)
                .show();
            return;
        }

        mSortBy = sortBy;
        mSortOrder = sortOrder;

        if (mSortBy.equalsIgnoreCase(MovieDbUtil.FAVORITE_MOVIES)) {
            new FetchMoviesFromMovieDb().execute(MovieDbUtil.getMovieQueryUrl());
        }else {
            URL url = MovieDbUtil.getApiUrl(sortBy + "." + sortOrder, mApiKey);
            new FetchMovieDbData().execute(url);
        }


    }

    private void toggleSortOrder() {

        if (mDescending) {
            mDescending = false;
            mMenu.findItem(R.id.ascending_or_descending_menu_item).setIcon(R.drawable.ic_ascending);
            populateFragmentImageAdapter(mSortBy, MovieDbUtil.ASCENDING);
        }else {
            mDescending = true;
            mMenu.findItem(R.id.ascending_or_descending_menu_item)
                    .setIcon(R.drawable.ic_descending);
            populateFragmentImageAdapter(mSortBy, MovieDbUtil.DESCENDING);
        }

    }

    public void showPopup(MenuItem item) {
        final View menuItemView = getActivity().findViewById(item.getItemId());

        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);

        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_sort_by);

        popup.show();
    }

    public void populateImageAdapter(List<Movie> movies) {

        if (movies != null) {
            mPostersFragmentImageAdapter = new PostersFragmentImageAdapter(getActivity(), movies);
            mGridView = (GridView) mRootView.findViewById(R.id.gridview_posters);
            mGridView.setAdapter(mPostersFragmentImageAdapter);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Movie movie = (Movie) parent.getAdapter().getItem(position);

                Intent intent = createDetailActivityIntent(movie);
                startActivity(intent);

            }
        });
    }

    private Intent createDetailActivityIntent(Movie movie) {

        Intent intent = new Intent(getActivity(), DetailActivity.class);

        intent.putExtra(MovieDbUtil.MOVIE, movie);
        intent.putExtra(MovieDbUtil.API_KEY_PARAM, mApiKey);

        return intent;
    }

    /**
     *
     * Method checks for network connectivity. Code is from review example Udacity Project 1
     * reviewer provided
     *
     * @return
     */
    private boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    /**
     *
     * AsyncTask class that runs MovieDB API call on background thread
     *
     * Some code used from Udacity Sunshine App
     *
     */
    public class FetchMovieDbData extends AsyncTask<URL, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieDbData.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(URL... params) {

            URL movieDbUrl = params[0];

            HttpURLConnection connection = null;
            BufferedReader reader = null;

            String responseJsonStr = null;

            try {

                connection = (HttpURLConnection) movieDbUrl.openConnection();
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                responseJsonStr = buffer.toString();

                mMovies = MovieDbUtil.getMoviesFromJson(responseJsonStr);

                return mMovies;

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
        protected void onPostExecute(List<Movie> movies) {

            populateImageAdapter(movies);
        }
    }

    /**
     *
     * AsyncTask class that fetches movie data from SQLite Movie database
     *
     * Some code used from Udacity Sunshine App
     *
     */
    public class FetchMoviesFromMovieDb extends AsyncTask<Uri, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieDbData.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(Uri... params) {

            Uri uri = params[0];

            Cursor c =
                    getActivity().getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null);

            ArrayList<Movie> movies = new ArrayList<Movie>();

            while (c.moveToNext()) {
                String title = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE));
                String releaseDate = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE));
                String overview = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW));
                String posterPath = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH));
                String backgroundPath = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKGROUND_PATH));
                String voteAverage = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE));
                String voteCount = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_COUNT));
                String movieId = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                String favorite = c.getString(c.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE));

                movies.add(new Movie(title, releaseDate, overview, posterPath, backgroundPath,
                        voteAverage, voteCount, Integer.valueOf(movieId),
                        Boolean.valueOf(favorite)));
            }

            return movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {

            populateImageAdapter(movies);
        }
    }
}
