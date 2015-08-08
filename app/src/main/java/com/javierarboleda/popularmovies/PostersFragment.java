package com.javierarboleda.popularmovies;

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
import android.widget.GridView;
import android.widget.PopupMenu;

import com.javierarboleda.popularmovies.domain.Movie;
import com.javierarboleda.popularmovies.service.MovieDbService;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by hype on 7/29/15.
 */
public class PostersFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private PostersFragmentImageAdapter mPostersFragmentImageAdapter;
    private View mRootView;
    private Menu mMenu;
    private boolean mDescending;
    private String mSortBy;
    private String mSortOrder;

    public PostersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        mDescending = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_posters, container, false);

        mSortBy = "popularity";
        mSortOrder = "desc";

        populateFragmentImageAdapter(mSortBy, mSortOrder);

        return mRootView;

    }

    private void populateFragmentImageAdapter(String sortBy, String sortOrder) {

        mSortBy = sortBy;
        mSortOrder = sortOrder;

        URL url = MovieDbService.getSortedPosters(sortBy + "." + sortOrder);
        new FetchMovieDbData().execute(url);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_posters, menu);
        mMenu = menu;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

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
                populateFragmentImageAdapter(MovieDbService.POPULARITY, mSortOrder);
                break;
            case R.id.menu_item_highest_rating:
                mMenu.findItem(R.id.sort_by_menu_item).setTitle(R.string.highest_rated);
                populateFragmentImageAdapter(MovieDbService.VOTE_AVERAGE, mSortOrder);
                break;
        }

        return false;
    }

    private void toggleSortOrder() {

        if (mDescending) {
            mDescending = false;
            mMenu.findItem(R.id.ascending_or_descending_menu_item).setIcon(R.drawable.ic_ascending);
            populateFragmentImageAdapter(mSortBy, MovieDbService.ASCENDING);
        }else {
            mDescending = true;
            mMenu.findItem(R.id.ascending_or_descending_menu_item)
                    .setIcon(R.drawable.ic_descending);
            populateFragmentImageAdapter(mSortBy, MovieDbService.DESCENDING);
        }

    }

    public void showPopup(MenuItem item) {
        final View menuItemView = getActivity().findViewById(item.getItemId());

        PopupMenu popup = new PopupMenu(getActivity(), menuItemView);


//        MenuInflater inflater = popup.getMenuInflater();
//        Menu menu = popup.getMenu();
//        inflater.inflate(R.menu.menu_sort_by, menu);

        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.menu_sort_by);

        popup.show();
    }

    public class FetchMovieDbData extends AsyncTask<URL, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieDbData.class.getSimpleName();

        @Override
        protected List<Movie> doInBackground(URL... params) {

            URL movieDbUrl = params[0];

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String responseJsonStr = null;


            try {

                // Create the request to OpenWeatherMap, and open the connection
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

                return MovieDbService.getMoviesFromJson(responseJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {

            }

            return null;

        }

        @Override
        protected void onPostExecute(List<Movie> movies) {

            if (movies != null) {
                mPostersFragmentImageAdapter = new PostersFragmentImageAdapter(getActivity(), movies);
                GridView gridView = (GridView) mRootView.findViewById(R.id.gridview_posters);
                gridView.setAdapter(mPostersFragmentImageAdapter);
            }

        }
    }
}
