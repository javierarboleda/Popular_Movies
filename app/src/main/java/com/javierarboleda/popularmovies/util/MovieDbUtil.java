package com.javierarboleda.popularmovies.util;

import android.net.Uri;

import com.javierarboleda.popularmovies.data.MovieContract;
import com.javierarboleda.popularmovies.domain.Movie;
import com.javierarboleda.popularmovies.domain.Review;
import com.javierarboleda.popularmovies.domain.Trailer;

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
    public final static String BASE_MOVIE_URL = "http://api.themoviedb.org/3/movie/";
    public final static String REVIEWS_PATH = "/reviews?";
    public final static String VIDEOS_PATH = "/videos?";
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
    public final static String ID = "id";
    public static final String MOVIE = "movie";
    public static final String RESULTS = "results";
    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String AUTHOR = "author";
    public static final String CONTENT = "content";
    public static final String TYPE = "type";
    public static final String TRAILER = "Trailer";
    public static final String FAVORITE_MOVIES = "favorite_movies";
    public static final String IS_TWO_PANE = "is_two_pane";
    public static final String IS_FAVORITE = "is_favorite";
    public static final String IS_FAVORITE_VISIBLE = "is_favorite_visible";

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

    public static Uri getMovieQueryUrl() {
            return MovieContract.MovieEntry.CONTENT_URI;
    }

    public static URL getReviewsUrl(int movieId, String apiKey) {

        Uri builtUri = Uri.parse(BASE_MOVIE_URL + movieId + REVIEWS_PATH).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static URL getVideosUrl(int movieId, String apiKey) {

        Uri builtUri = Uri.parse(BASE_MOVIE_URL + movieId + VIDEOS_PATH).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
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
                int movieId = movieJsonObject.getInt(ID);

                movies.add(new Movie(title, releaseDate, overview, posterPath, backdropPath,
                        voteAverage, voteCount, movieId));
            }

        } catch (JSONException e) {
            throw e;
        }

        return movies;
    }

    public static ArrayList<Trailer> getTrailersFromJson(String trailerJsonStr) throws JSONException {

        ArrayList<Trailer> trailers = new ArrayList<Trailer>();

        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerJsonArray = trailerJson.getJSONArray(RESULTS);

            for (int i = 0; i < trailerJsonArray.length(); i++) {

                JSONObject trailerJsonObject = trailerJsonArray.getJSONObject(i);

                // we only want videos that are type=Trailer
                if (!trailerJsonObject.getString(TYPE).equalsIgnoreCase(TRAILER)) {
                    continue;
                }

                String key = trailerJsonObject.getString(KEY);
                String name = trailerJsonObject.getString(NAME);

                trailers.add(new Trailer(key, name));
            }

        } catch (JSONException e) {
            throw e;
        }

        return trailers;
    }

    public static ArrayList<Review> getReviewsFromJson(String reviewJsonStr) throws JSONException {

        ArrayList<Review> reviews = new ArrayList<Review>();

        try {
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewJsonArray = reviewJson.getJSONArray(RESULTS);

            for (int i = 0; i < reviewJsonArray.length(); i++) {

                JSONObject reviewJsonObject = reviewJsonArray.getJSONObject(i);

                String author = reviewJsonObject.getString(AUTHOR);
                String content = reviewJsonObject.getString(CONTENT);

                reviews.add(new Review(author, content));
            }

        } catch (JSONException e) {
            throw e;
        }

        return reviews;
    }


}
