package com.javierarboleda.popularmovies.util;

import android.net.Uri;

import com.javierarboleda.popularmovies.domain.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Javier Arboleda on 8/3/15.
 *
 * Util util class for MovieDb API
 *
 */
public class MovieDbUtil {

    public final static String BASE_API_URL = "http://api.themoviedb.org/3/discover/movie?";
    public final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public final static String SORT_BY_PARAM = "sort_by";
    public final static String API_KEY_PARAM = "api_key";
    public final static String VOTE_COUNT_PARAM = "vote_count.gte";
    public final static String VOTE_COUNT_MIN = "150";
    public final static String POPULARITY = "popularity";
    public final static String VOTE_AVERAGE = "vote_average";
    public final static String ASCENDING = "asc";
    public final static String DESCENDING = "desc";

    public final static String BACKDROP_PATH = "backdrop_path";
    public final static String TITLE = "title";
    public final static String OVERVIEW = "overview";
    public final static String RELEASE_DATE = "release_date";
    public final static String POSTER_PATH = "poster_path";
    public final static String VOTE_COUNT = "vote_count";

    public static URL getApiUrl(String sortBy, String apiKey) {

        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, sortBy)
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .appendQueryParameter(VOTE_COUNT_PARAM, VOTE_COUNT_MIN)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static ArrayList<Movie> getMoviesFromJson(String moviesJsonStr) throws JSONException {

        ArrayList<Movie> movies = new ArrayList<Movie>();

        try {
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesJsonArray = moviesJson.getJSONArray("results");

            for (int i = 0; i < moviesJsonArray.length(); i++) {

                JSONObject movieJsonObject = moviesJsonArray.getJSONObject(i);

                String title = movieJsonObject.getString(TITLE);
                String releaseDate = movieJsonObject.getString(RELEASE_DATE);
                String posterPath = movieJsonObject.getString(POSTER_PATH);
                String overview = movieJsonObject.getString(OVERVIEW);
                String backdropPath = movieJsonObject.getString(BACKDROP_PATH);
                String voteAverage = movieJsonObject.getString(VOTE_AVERAGE);
                String voteCount = movieJsonObject.getString(VOTE_COUNT);

                movies.add(new Movie(title, releaseDate, overview, posterPath, backdropPath,
                        voteAverage, voteCount));
            }

        } catch (JSONException e) {
            throw e;
        }

        return movies;
    }


}
