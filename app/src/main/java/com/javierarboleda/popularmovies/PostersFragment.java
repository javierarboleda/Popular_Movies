package com.javierarboleda.popularmovies;

import android.content.Context;
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

    // Below fields super necessary for handling master/detail tablet layout
    private boolean mTwoPane;
    private boolean mFavorite;
    private boolean mFavoriteVisible;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        /**
         * Callback for when an item has been selected from posters GridView.
         */
        boolean onItemSelected(Movie movie, String apiKey);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mDescending = true;

        mGridView = (GridView) getActivity().findViewById(R.id.gridview_posters);

        setApiKeyFromProperties();
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
            mTwoPane = savedInstanceState.getBoolean(MovieDbUtil.IS_TWO_PANE);
            mFavorite = savedInstanceState.getBoolean(MovieDbUtil.IS_FAVORITE);
            mFavoriteVisible = savedInstanceState.getBoolean(MovieDbUtil.IS_FAVORITE_VISIBLE);
            setImageAdapter(mMovies);
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
        outState.putBoolean(MovieDbUtil.IS_TWO_PANE, mTwoPane);
        outState.putBoolean(MovieDbUtil.IS_FAVORITE, mFavorite);
        outState.putBoolean(MovieDbUtil.IS_FAVORITE_VISIBLE, mFavoriteVisible);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_posters, menu);
        mMenu = menu;

        if (!mDescending) {
            mMenu.findItem(R.id.ascending_or_descending_menu_item)
                    .setIcon(R.drawable.ic_ascending);
        }
        // Below is necessary for setting correct favorite icon on screen rotate
        if (mFavoriteVisible) {
            mMenu.findItem(R.id.favorite_item).setVisible(true);
            if (mTwoPane && mFavorite) {
                mMenu.findItem(R.id.favorite_item)
                        .setIcon(R.drawable.ic_action_content_heart_circle);
            } else {
                mMenu.findItem(R.id.favorite_item)
                        .setIcon(R.drawable.ic_action_content_add_circle);
            }
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
            case R.id.favorite_item:
                favoriteMenuItemClicked();
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

    /**
     * Calls AsyncTask for populating FragmentImageAdapter. Will make a call to either
     * FetchMoviesFromMovieDb for fetching movies from local database, or FetchMovieDbData for
     * fetching movies from TheMovieDB
     */
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
        } else {
            URL url = MovieDbUtil.getApiUrl(sortBy + "." + sortOrder, mApiKey);
            new FetchMovieDbData().execute(url);
        }


    }

    /**
     * Function for toggling ascending/descending sort order.
     */
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

    /**
     * Function for showing popup menu that contains sort by menu items.
     */
    public void showPopup(MenuItem item) {
        final View menuItemView = getActivity().findViewById(item.getItemId());

        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);

        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_sort_by);

        popup.show();
    }

    /**
     * Function for instantiating the PostersFragmentImageAdapter and setting onItemClickListener.
     */
    public void setImageAdapter(List<Movie> movies) {

        if (movies != null) {
            mPostersFragmentImageAdapter = new PostersFragmentImageAdapter(getActivity(), movies);
            mGridView = (GridView) mRootView.findViewById(R.id.gridview_posters);
            mGridView.setAdapter(mPostersFragmentImageAdapter);
        }

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {

                Movie movie = (Movie) parent.getAdapter().getItem(position);

                mTwoPane = ((Callback) getActivity()).onItemSelected(movie, mApiKey);

                if (mTwoPane) {

                    mMenu.findItem(R.id.favorite_item).setVisible(true);
                    mFavoriteVisible = true;

                    if (isMovieFavorite(movie)) {
                        mMenu.findItem(R.id.favorite_item)
                                .setIcon(R.drawable.ic_action_content_heart_circle);
                        mFavorite = true;
                    } else {
                        mMenu.findItem(R.id.favorite_item)
                                .setIcon(R.drawable.ic_action_content_add_circle);
                        mFavorite = false;
                    }

                }

            }
        });
    }

    /**
     * Called when favorite menu icon is clicked. This is necessary for master/detail tablet
     * layout. If movie is added to favorites from this layout, this fragment will call
     * DetailFragment object's add to favortie logic
     */
    public void favoriteMenuItemClicked() {
        DetailFragment df = (DetailFragment) getActivity().getSupportFragmentManager()
                .findFragmentByTag(PostersActivity.DETAIL_FRAGMENT_TAG);

        if (df != null && !df.isFavorite()) {
            df.toggleFavoriteTwoPane();
            mMenu.findItem(R.id.favorite_item).setIcon(R.drawable.ic_action_content_heart_circle);
            mFavorite = true;
        }
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

    private boolean isMovieFavorite(Movie movie) {
        Cursor c =
                getActivity().getContentResolver().query(
                        MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(movie.getMovieId())},
                        null);
        if (c.getCount() > 0){
            return true;
        }
        return false;
    }

    /**
     * Sets TheMovieDB API key from properties file
     */
    private void setApiKeyFromProperties() {
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
            setImageAdapter(movies);
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

            return mMovies = movies;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            setImageAdapter(movies);
        }
    }
}
