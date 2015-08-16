package com.javierarboleda.popularmovies.domain;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by hype on 8/3/15.
 */
public class Movie {
    private String mTitle;
    private String mReleaseDate;
    private String mOverview;
    private String mPosterPath;
    private String mBackdropPath;
    private String mVoteAverage;
    private String mVoteCount;

    public Movie(String title, String releaseDate, String overview, String posterPath,
                 String backdropPath, String voteAverage, String voteCount) {

        this.mTitle =title;
        this.mReleaseDate = releaseDate;
        this.mOverview = overview;
        this.mPosterPath = posterPath;
        this.mBackdropPath = backdropPath;
        this.mVoteAverage = voteAverage;
        this.mVoteCount = voteCount;

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
}
