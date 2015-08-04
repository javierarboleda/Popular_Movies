package com.javierarboleda.popularmovies.service;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.javierarboleda.popularmovies.domain.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hype on 8/3/15.
 */
public class MovieDbService {


    private static final String mApiKey = "";
    public final static String BASE_API_URL = "http://api.themoviedb.org/3/discover/movie?";
    public final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public final static String SORT_BY_PARAM = "sort_by";
    public final static String API_KEY_PARAM = "api_key";



    private void getMovieDbJson() {


        return;
    }

    public static List<Movie> getSortedPosters(String sortBy) {

        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, sortBy)
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (url == null) {
            // todo: throw an exception, eh
            return null;
        }

        new FetchMovieDbData().execute(url);

        return null;
    }



    private static class FetchMovieDbData extends AsyncTask<URL, Void, List<Movie>> {

        private final String LOG_TAG = FetchMovieDbData.class.getSimpleName();

        private final String BACKDROP_PATH = "backdrop_path";
        private final String TITLE = "title";
        private final String OVERVIEW = "overview";
        private final String RELEASE_DATE = "release_date";
        private final String POSTER_PATH = "poster_path";

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

                return getMoviesFromJson(responseJsonStr);

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

        private List<Movie> getMoviesFromJson(String moviesJsonStr) throws JSONException {

            List<Movie> movies = new ArrayList<Movie>();

            try {
                JSONObject moviesJson = new JSONObject(moviesJsonStr);
                JSONArray moviesJsonArray = moviesJson.getJSONArray("results");

                for (int i = 0; i < moviesJsonArray.length(); i++) {

                    JSONObject movie = moviesJsonArray.getJSONObject(i);

                    String title = movie.getString(TITLE);
                    String releaseDate = movie.getString(RELEASE_DATE);
                    String posterPath = movie.getString(POSTER_PATH);
                    String overview = movie.getString(OVERVIEW);
                    String backdropPath = movie.getString(BACKDROP_PATH);

                    movies.add(new Movie(title, releaseDate, overview, posterPath, backdropPath));
                }

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
            }

            return movies;
        }


    }

}
