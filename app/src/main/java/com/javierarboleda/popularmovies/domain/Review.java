package com.javierarboleda.popularmovies.domain;

/**
 * Created by Javier Arboleda on 8/21/15.
 */
public class Review {

    private String mAuthor;
    private String mContent;

    public Review(String author, String content) {
        this.mAuthor = author;
        this.mContent = content;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String mAuthor) {
        this.mAuthor = mAuthor;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String mContent) {
        this.mContent = mContent;
    }
}
