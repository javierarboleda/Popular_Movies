package com.javierarboleda.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.javierarboleda.popularmovies.domain.Trailer;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Javier Arboleda on 8/23/15.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer> {

    boolean mFavorite;

    public TrailersAdapter(Activity context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }

    public TrailersAdapter(Activity context, List<Trailer> trailers, boolean favorite) {
        this(context, trailers);
        mFavorite = favorite;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Trailer trailer = getItem(position);

        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.trailer_layout_item, parent, false);
        }

        ImageView posterView =
                (ImageView) convertView.findViewById(R.id.trailer_listview_item_image);

        // if this is a favorite movie, then load image from internal memory
        if (mFavorite) {

            String fileName = trailer.getKey() + ".jpg";
            ContextWrapper cw = new ContextWrapper(getContext());
            // path to /data/data/yourapp/app_data/imageDir
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            // Create imageDir
            File file = new File(directory, fileName);

            Picasso.with(context)
                    .load(file)
                    .into(posterView);
        } else {
            Uri uri = trailer.getThumbnailUrl();
            String url = uri.toString();
            url = url.isEmpty() ? null : url;

            Picasso.with(context)
                    .load(url)
                    .into(posterView);
        }

        TextView titleView = (TextView)
                convertView.findViewById(R.id.trailer_listview_item_trailer_name);
        titleView.setText(trailer.getName());

        return convertView;
    }
}
