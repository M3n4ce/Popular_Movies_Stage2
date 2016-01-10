package com.manish.nanoapp.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by f4898303 on 2015/11/10.
 */
public class OnitemClickListenerTrailerItems implements AdapterView.OnItemClickListener {

    SharedPreferences TrailerList;
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Context context = view.getContext();

            TextView textViewItem = ((TextView) view.findViewById(R.id.txt_trailer));

            TrailerList = context.getSharedPreferences("TrailerList",Context.MODE_PRIVATE);

            // get the clicked item name
            String listItemText = textViewItem.getText().toString();

            // get the clicked item ID
            //get trailer details from sharedpreferences
            String listItemKey = TrailerList.getString(String.valueOf(position),"null");

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + listItemKey));
                context.startActivity(intent);
            }
            catch(ActivityNotFoundException ex) {
                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse("http://www.youtube.com/watch?v=" + listItemKey));
                context.startActivity(intent);
            }


        }
}
