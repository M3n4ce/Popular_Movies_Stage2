package com.manish.nanoapp.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by f4898303 on 2015/11/10.
 */
public class TrailerArrayAdapter extends ArrayAdapter<String> {

    Context context;
    int layoutId,textViewId;
    //TrailObjectItem data[] = null;

    public TrailerArrayAdapter(Context context, int layoutId, int textViewId, ArrayList<String> data) {
        super(context, layoutId, data);

        this.context = context;
        this.layoutId = layoutId;
        this.context = context;
        this.textViewId = textViewId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String vid;

        if (convertView == null) {

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutId, parent, false);
        }

        TextView txtView = (TextView)convertView.findViewById(textViewId);

        vid = getItem(position);
        TextView txtTrailer = (TextView)convertView.findViewById(R.id.txt_trailer);

        txtTrailer.setText(vid);
      //  txtTrailer.setTag(trailerObjectitem.key);

        return convertView;
    }

}
