package com.manish.nanoapp.popularmovies;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private boolean mTwoPane;
    FragmentManager fragmentManager = getFragmentManager();

    private static final String DETAILSFRAGMENT_TAG = "DFTAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (findViewById(R.id.movie_details) != null) {

            mTwoPane = true;

            if (savedInstanceState == null) {

                fragmentManager.beginTransaction()
                        .replace(R.id.movie_details, new MovieDetailsFragment(), DETAILSFRAGMENT_TAG)
                        .commit();
            }
        }

        else {
            mTwoPane = false;

//            fragment = (MovieListFragment) fragmentManager.getFragment(
        //            savedInstanceState, "fragmentContent");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_cat) {
            startActivity(new Intent(this, Settings.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

