package com.javierarboleda.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

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
public class PostersFragment extends Fragment{

    private PostersFragmentImageAdapter mPostersFragmentImageAdapter;
    private View mRootView;
    private List<Movie> movies;

    public PostersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        URL url = MovieDbService.getSortedPosters("popularity.desc");

        mRootView = inflater.inflate(R.layout.fragment_posters, container, false);

        new FetchMovieDbData().execute(url);

        return mRootView;

    }

//    private List<Movie> movies = {
//            new Movie("Ant-Man", "2015", null, "/7SGGUiTE6oc2fh9MjIk5M00dsQd.jpg", null),
//            new Movie("Minions", "2015", null, "/s5uMY8ooGRZOL0oe4sIvnlTsYQO.jpg", null),
//            new Movie("Jurassic World", null, "2015", "/uXZYawqUsChGSj54wcuBtEdUJbh.jpg", null),
//            new Movie("Terminator: Genisys", null, "2015", "/5JU9ytZJyR3zmClGmVm9q4Geqbd.jpg", null),
//            new Movie("Mad Max: Fury Road", null, "2015", "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg", null),
//            new Movie("Insurgent", "2014", null, "/aBBQSC8ZECGn6Wh92gKDOakSC8p.jpg", null),
//    };








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

            mPostersFragmentImageAdapter = new PostersFragmentImageAdapter(getActivity(), movies);

            GridView gridView = (GridView) mRootView.findViewById(R.id.gridview_posters);
            gridView.setAdapter(mPostersFragmentImageAdapter);
        }

    }

}
