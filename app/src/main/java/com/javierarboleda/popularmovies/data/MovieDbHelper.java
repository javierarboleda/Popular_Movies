package com.javierarboleda.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.javierarboleda.popularmovies.data.MovieContract.MovieEntry;
import com.javierarboleda.popularmovies.data.MovieContract.TrailerEntry;
import com.javierarboleda.popularmovies.data.MovieContract.ReviewEntry;

/**
 * Created by Javier Arboleda on 8/21/15.
 */
public class MovieDbHelper extends SQLiteOpenHelper {


    //name & version
    private static final String DATABASE_NAME = "favorite_movie.db";
    private static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE =
                "CREATE TABLE " + MovieEntry.TABLE_NAME +
                "(" + MovieEntry.COLUMN_MOVIE_ID + " TEXT PRIMARY KEY, " +
                MovieEntry.COLUMN_TITLE + " TEXT, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_POSTER_PATH + " TEXT, " +
                MovieEntry.COLUMN_BACKGROUND_PATH + " TEXT, " +
                MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT, " +
                MovieEntry.COLUMN_VOTE_COUNT + " TEXT, " +
                MovieEntry.COLUMN_FAVORITE + " TEXT, " +

                // ensure uniqueness of entry
                " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_TRAILER_TABLE =
                "CREATE TABLE " + TrailerEntry.TABLE_NAME +
                        "(" + TrailerEntry.COLUMN_MOVIE_ID + " TEXT, " +
                        TrailerEntry.COLUMN_KEY + " TEXT, " +
                        TrailerEntry.COLUMN_NAME + " TEXT, " +
                
                // Set up foreign key
                " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                // ensure uniqueness of entry
                " UNIQUE (" + TrailerEntry.COLUMN_KEY + ", " +
                        TrailerEntry.COLUMN_NAME + ") ON CONFLICT REPLACE);";


        final String SQL_CREATE_REVIEW_TABLE =
                "CREATE TABLE " + ReviewEntry.TABLE_NAME +
                        "(" + ReviewEntry.COLUMN_MOVIE_ID + " TEXT, " +
                        ReviewEntry.COLUMN_AUTHOR + " TEXT, " +
                        ReviewEntry.COLUMN_CONTENT + " TEXT, " +

                        // Set up foreign key
                        " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
                        MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + "), " +

                        // ensure uniqueness of entry
                        " UNIQUE (" + ReviewEntry.COLUMN_AUTHOR + ", " +
                        ReviewEntry.COLUMN_CONTENT + ") ON CONFLICT REPLACE);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        onCreate(db);
    }
}
