package com.javierarboleda.popularmovies.service;

import android.net.Uri;

import com.javierarboleda.popularmovies.domain.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hype on 8/3/15.
 */
public class MovieDbService {


    private static final String mApiKey = "e06175091bf067955402417761ce5904";
    public final static String BASE_API_URL = "http://api.themoviedb.org/3/discover/movie?";
    public final static String BASE_IMAGE_URL = "http://image.tmdb.org/t/p/";
    public final static String SORT_BY_PARAM = "sort_by";
    public final static String API_KEY_PARAM = "api_key";
    public final static String VOTE_COUNT_PARAM = "vote_count.gte";
    public final static String VOTE_COUNT_MIN = "100";
    public final static String POPULARITY = "popularity";
    public final static String VOTE_AVERAGE = "vote_average";
    public final static String ASCENDING = "asc";
    public final static String DESCENDING = "desc";

    public final static String BACKDROP_PATH = "backdrop_path";
    public final static String TITLE = "title";
    public final static String OVERVIEW = "overview";
    public final static String RELEASE_DATE = "release_date";
    public final static String POSTER_PATH = "poster_path";




    private void getMovieDbJson() {
        return;
    }

    public static URL getSortedPosters(String sortBy) {

        Uri builtUri = Uri.parse(BASE_API_URL).buildUpon()
                .appendQueryParameter(SORT_BY_PARAM, sortBy)
                .appendQueryParameter(API_KEY_PARAM, mApiKey)
                .appendQueryParameter(VOTE_COUNT_PARAM, VOTE_COUNT_MIN)
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

        return url;
    }


    public static List<Movie> getMoviesFromJson(String moviesJsonStr) throws JSONException {

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
            throw e;
        }

        return movies;
    }


}
