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

    public Movie(String mTitle, String mReleaseDate, String mOverview, String mPosterPath, String mBackdropPath) {
        this.mTitle = mTitle;
        this.mReleaseDate = mReleaseDate;
        this.mOverview = mOverview;
        this.mPosterPath = mPosterPath;
        this.mBackdropPath = mBackdropPath;
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

    public String getHumanReadableReleaseDate() {

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
