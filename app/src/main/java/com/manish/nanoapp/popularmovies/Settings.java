package com.manish.nanoapp.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class Settings extends AppCompatActivity {

    SharedPreferences mainPrefs,MovieID,MovieName,MoviePlot,MoviePoster,MovieYear,MovieRating;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainPrefs = getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);
    }

    public void CatPopularity(View view){

        String sortByPop = "popularity.desc";
        //Add prefs to shared preferences globally

        SharedPreferences.Editor edt = mainPrefs.edit();
        edt.putString("sort_by", sortByPop);
        edt.commit();

        Log.d("check here", sortByPop);

        startActivity(new Intent(Settings.this,MainActivity.class));
    }

    public void CatHighestRated(View view){

        String sortByRating = "vote_average.desc";
        //Add prefs to shared preferences globally

        SharedPreferences.Editor edt2 = mainPrefs.edit();
        edt2.putString("sort_by", sortByRating);
        edt2.commit();

        Log.d("check here", sortByRating);

        startActivity(new Intent(Settings.this, MainActivity.class));
    }

    public void sortFavorites(View view) {
        String sortByRating = "favorite";
        //Add prefs to shared preferences globally

        SharedPreferences.Editor edt2 = mainPrefs.edit();
        edt2.putString("sort_by", sortByRating);
        edt2.commit();

        Log.d("check here", sortByRating);

        startActivity(new Intent(Settings.this, MainActivity.class));
    }

    public void ResetFavorites(View view){
        IniPrefs();
        SharedPreferences.Editor MID = MovieID.edit();
        SharedPreferences.Editor MName = MovieName.edit();
        SharedPreferences.Editor MPlot = MoviePlot.edit();
        SharedPreferences.Editor MPoster = MoviePoster.edit();
        SharedPreferences.Editor MYear = MovieYear.edit();
        SharedPreferences.Editor MRating = MovieRating.edit();

        MID.clear();
        MName.clear();
        MPlot.clear();
        MPoster.clear();
        MYear.clear();
        MRating.clear();

        MID.apply();
        MName.apply();
        MPlot.apply();
        MPoster.apply();
        MYear.apply();
        MRating.apply();
    }


    //Initialise Shared preferences
    public void IniPrefs() {
        MovieID = getSharedPreferences("MovieID", Context.MODE_PRIVATE);
        MovieName = getSharedPreferences("MovieName", Context.MODE_PRIVATE);
        MoviePlot = getSharedPreferences("MoviePlot", Context.MODE_PRIVATE);
        MoviePoster = getSharedPreferences("MoviePoster", Context.MODE_PRIVATE);
        MovieRating = getSharedPreferences("MovieRating", Context.MODE_PRIVATE);
        MovieYear = getSharedPreferences("MovieYear", Context.MODE_PRIVATE);
    }
}
