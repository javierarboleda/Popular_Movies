package com.javierarboleda.popularmovies.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Javier Arboleda on 8/3/15.
 */
public class Movie implements Parcelable {
    private String mTitle;
    private String mReleaseDate;
    private String mOverview;
    private String mPosterPath;
    private String mBackdropPath;
    private String mVoteAverage;
    private String mVoteCount;
    private int mMovieId;

    public Movie(String title, String releaseDate, String overview, String posterPath,
                 String backdropPath, String voteAverage, String voteCount, int movieId) {

        this.mTitle =title;
        this.mReleaseDate = releaseDate;
        this.mOverview = overview;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
        this.mVoteAverage = voteAverage;
        this.mVoteCount = voteCount;
        this.mMovieId = movieId;

    }

    public Movie(Parcel in) {
        mTitle = in.readString();
        mReleaseDate = in.readString();
        mOverview = in.readString();
        mPosterPath = in.readString();
        mBackdropPath = in.readString();
        mVoteAverage = in.readString();
        mVoteCount = in.readString();
        mMovieId = in.readInt();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mReleaseDate);
        dest.writeString(mOverview);
        dest.writeString(mPosterPath);
        dest.writeString(mBackdropPath);
        dest.writeString(mVoteAverage);
        dest.writeString(mVoteCount);
        dest.writeInt(mMovieId);
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseYear) {
        this.mReleaseDate = mReleaseYear;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String mOverview) {
        this.mOverview = mOverview;
    }

    public String getPosterPath() {
        return mPosterPath;
    }

    public void setPosterPath(String mPosterPath) {
        this.mPosterPath = mPosterPath;
    }

    public String getBackdropPath() {
        return mBackdropPath;
    }

    public void setBackdropPath(String mBackdropPath) {
        this.mBackdropPath = mBackdropPath;
    }

    public String getVoteAverage() {
        return mVoteAverage;
    }

    public void setVoteAverage(String mVoteAverage) {
        this.mVoteAverage = mVoteAverage;
    }

    public String getVoteCount() { return mVoteCount; }

    public void setVoteCount(String mVoteCount) { this.mVoteCount = mVoteCount;  }



    public String getHumanReadableReleaseDate() {

        if (mReleaseDate == null || mReleaseDate.trim().isEmpty() || mReleaseDate.equals("null")) {
            return "";
        }

        Calendar cal = Calendar.getInstance();
        String[] yearMonthDay = mReleaseDate.split("-");

        int year = Integer.parseInt(yearMonthDay[0]);
        int month = Integer.parseInt(yearMonthDay[1]) - 1;
        int day = Integer.parseInt(yearMonthDay[2]);

        cal.set(year, month, day);
        SimpleDateFormat monthDayYearFormat = new SimpleDateFormat("MMMM d, yyyy");

        return monthDayYearFormat.format(cal.getTime());
    }

    public int getMovieId() {
        return mMovieId;
    }

    public void setMovieId(int mMovieId) {
        this.mMovieId = mMovieId;
    }
}
