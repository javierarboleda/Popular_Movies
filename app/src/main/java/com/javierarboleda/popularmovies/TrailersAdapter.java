package com.javierarboleda.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.javierarboleda.popularmovies.domain.Trailer;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Javier Arboleda on 8/23/15.
 */
public class TrailersAdapter extends ArrayAdapter<Trailer> implements View.OnClickListener {

    public TrailersAdapter(Activity context, List<Trailer> trailers) {
        super(context, 0, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Trailer trailer = getItem(position);

        Context context = getContext();

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.trailer_listview_item, parent, false);
        }

        ImageView posterView =
                (ImageView) convertView.findViewById(R.id.trailer_listview_item_image);

        Uri uri = trailer.getThumbnailUrl();
        String url = uri.toString();
        url = url.isEmpty() ? null : url;

        Picasso.with(context)
                .load(url)
                        // todo: create and add placeholder and error images for thumbnail
//                .placeholder(R.drawable.poster_placeholder_w342)
//                .error(R.drawable.poster_error_w342)
                .into(posterView);

        TextView titleView = (TextView)
                convertView.findViewById(R.id.trailer_listview_item_trailer_name);
        titleView.setText(trailer.getName());

        return convertView;
    }


    @Override
    public void onClick(View v) {

    }
}
