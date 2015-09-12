package com.javierarboleda.popularmovies.util;

/**
 * Created by Javier Arboleda on 8/4/15.
 *
 * constants that can be shared across app
 *
 */
public class Constants {

    public static final String SIZE_W500 = "w500";
    public static final String SIZE_W185 = "w185";
    public static final String SIZE_W342 = "w342";
    public static final String SIZE_W780 = "w780";

    public static final String DESCENDING = "descending";
    public static final String SORT_BY = "sort_by";
    public static final String SORT_ORDER = "sort_order";

    public static final String APP_PROPERTIES = "app.properties";
    public static final String TMDB_API_KEY = "TMDB_API_KEY";
    public static final String MOVIE_LIST = "movie_list";
    public static final String NO_NETWORK_TOAST_MESSAGE = "No network connectivity detected!";
    public static final String API_KEY_EMPTY = "API Key was not detected. Did you add it to " +
            "the app.properties file?";
    public static final String MOVIE_DB_CONNECTION_ERROR_TOAST = "There was an error connecting " +
            "to TheMovieDB API. Are you sure you entered the correct key into app.properties file?";
}
