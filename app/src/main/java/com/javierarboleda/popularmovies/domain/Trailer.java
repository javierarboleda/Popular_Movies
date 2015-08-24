package com.javierarboleda.popularmovies.domain;

import android.net.Uri;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Javier Arboleda on 8/21/15.
 */
public class Trailer {

    private String mKey;
    private String mName;
    private final String VIDEO_BASE_URL = "https://www.youtube.com/watch?";
    private final String THUMBNAIL_BASE_URL = "http://i3.ytimg.com/vi/";
    private final String MQ_DEFAULT_PATH = "/mqdefault.jpg";
    private final String V = "v";

    public Trailer(String key, String name) {
        this.mKey = key;
        this.mName = name;
    }
    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * method that returns the thumbnail of this trailer in medium quality
     *
     * @return URL of the thumbnail
     */
    public Uri getThumbnailUrl() {

        return Uri.parse(THUMBNAIL_BASE_URL + mKey + MQ_DEFAULT_PATH);

    }

    public URL getYoutubeUrl() {

        Uri builtUri = Uri.parse(VIDEO_BASE_URL).buildUpon()
                .appendQueryParameter(V, mKey)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return url;

    }

}
