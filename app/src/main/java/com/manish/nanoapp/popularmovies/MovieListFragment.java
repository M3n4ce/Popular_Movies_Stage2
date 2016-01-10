package com.manish.nanoapp.popularmovies;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by f4898303 on 2015/11/07.
 */
public class MovieListFragment extends Fragment {

    private GridViewAdapter gridviewMoviePoster;
    List<MovieInfo> movies = new ArrayList<MovieInfo>();
    private final String STORED_MOVIES = "stored_movies";
    private final String MOVIE_POSTER_BASE = "http://image.tmdb.org/t/p/";
    private final String MOVIE_POSTER_SIZE = "w185";
    private Context globalContext = null;

    SharedPreferences mainPrefs,MovieID,MovieName,MoviePlot,MoviePoster,MovieYear,MovieRating;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        IniPrefs();

        if(savedInstanceState != null) {
            ArrayList<MovieInfo> storedMovies = new ArrayList<MovieInfo>();
            storedMovies = savedInstanceState.<MovieInfo>getParcelableArrayList(STORED_MOVIES);
            movies.clear();
            movies.addAll(storedMovies);
        }

        globalContext = this.getActivity();

    }

    @Override
    public void onStart() {
        super.onStart();

        if(movies.size() > 0 ) {
            updatePosterAdapter();
            //getMovies();
        }else{
           String sortOrder = mainPrefs.getString("sort_by","popularity.desc");
            if(sortOrder.equals("favorite")) {

                movies.clear();
                movies.addAll(FavPrefs());
                updatePosterAdapter();
            }
            else {
                getMovies(sortOrder);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        gridviewMoviePoster = new GridViewAdapter(getActivity(), R.layout.movie_poster,
                R.id.movie_list_imageview,
                new ArrayList<String>());

        View View = inflater.inflate(R.layout.movie_list_fragment,container,false);
        GridView gridView = (GridView) View.findViewById(R.id.movie_grid);
        gridView.setAdapter(gridviewMoviePoster);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieInfo details = movies.get(position);
                Intent intent = new Intent(getActivity(), MovieDetails.class)
                        .putExtra("details", details);
                startActivity(intent);
            }

        });

        return View;
    }

    private void getMovies(String SortBy) {

        CheckConnection checkCon = new CheckConnection(globalContext);
        if (checkCon.isConnectingToInternet()) {
            FetchMovieInfo fetchInfo = new FetchMovieInfo();
            fetchInfo.execute(SortBy);
        }
        else{
            Log.e("Connection Error","Check network connection" );
        }
    }

    private class FetchMovieInfo extends AsyncTask<String,Void,List<MovieInfo>> {
        @Override
        protected List<MovieInfo> doInBackground(String... params) {

            String BaseUrl = "https://api.themoviedb.org/3/discover/movie?";
            String KEY = "api_key";
            String ApiKey= getResources().getString(R.string.picasso_api_key);
            String SortBy = "sort_by";
            String sortBy = params[0];
            String MovieDbJson = "";

            try {

                Uri builtUri = Uri.parse(BaseUrl).buildUpon()
                        .appendQueryParameter(SortBy, sortBy)
                        .appendQueryParameter(KEY, ApiKey)
                        .build();

                URL url = new URL(builtUri.toString());

                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                //con.setRequestProperty("Content-Type", "application/json");

                BufferedReader br = new BufferedReader(new InputStreamReader((con.getInputStream())));

                String output;
                StringBuffer buffer = new StringBuffer();
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    buffer.append(output + "\n");
                }

                MovieDbJson = buffer.toString();
            }
            catch(Exception ex){
                Log.e("Error",ex.toString());
            }

            try {
               return extractData(MovieDbJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movieInfos) {
            super.onPostExecute(movieInfos);
            movies.clear();
            movies.addAll(movieInfos);
            updatePosterAdapter();
        }

    }

    private void updatePosterAdapter() {
        gridviewMoviePoster.clear();
        gridviewMoviePoster.notifyDataSetChanged();

        for(MovieInfo movie : movies) {
            gridviewMoviePoster.add(movie.getMoviePoster());
        }
    }

    private List<MovieInfo> extractData(String moviesJsonStr) throws JSONException {

        // Items to extract
        final String ARRAY_OF_MOVIES = "results";
        final String ORIGINAL_TITLE = "original_title";
        final String POSTER_PATH = "poster_path";
        final String OVERVIEW = "overview";
        final String VOTE_AVERAGE = "vote_average";
        final String RELEASE_DATE = "release_date";
        final String MOVIE_ID = "id";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_OF_MOVIES);
        int moviesLength =  moviesArray.length();
        List<MovieInfo> movies = new ArrayList<MovieInfo>();

        for(int i = 0; i < moviesLength; ++i) {

            JSONObject movie = moviesArray.getJSONObject(i);
            String title = movie.getString(ORIGINAL_TITLE);
            String poster = MOVIE_POSTER_BASE + MOVIE_POSTER_SIZE + movie.getString(POSTER_PATH);
            String plot = movie.getString(OVERVIEW);
            String voteAverage = movie.getString(VOTE_AVERAGE);
            String releaseDate = getYear(movie.getString(RELEASE_DATE));
            String ID = movie.getString(MOVIE_ID);

            movies.add(new MovieInfo(title, releaseDate,poster, voteAverage , plot,ID));

        }

        return movies;

    }


    private List<MovieInfo> FavPrefs(){

        int prefSize = 0;
        //size of sharedprefs
        if (MovieID.getAll()!=null) {
            prefSize = MovieID.getAll().size();
        }

        List<MovieInfo> movies = new ArrayList<MovieInfo>();

        for (int i = 0; i < prefSize; i++) {
            String ID = MovieID.getString(String.valueOf(i),"blank");
            String title = MovieName.getString(ID,"blank");
            String releaseDate = MovieYear.getString(ID,"blank");
            String poster = MoviePoster.getString(ID,"blank");
            String voteAverage = MovieRating.getString(ID, "blank");
            String plot = MoviePlot.getString(ID,"blank");

            movies.add(new MovieInfo(title, releaseDate, poster, voteAverage, plot, ID));
        }

        return movies;
    }

    private String getYear(String date){
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        final Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(df.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return Integer.toString(cal.get(Calendar.YEAR));
    }

    //Initialise Shared preferences
    public void IniPrefs() {
        //Global Settings for app
        mainPrefs = getActivity().getSharedPreferences("mainPrefs", Context.MODE_PRIVATE);

        //local data for favorites
        MovieID = getActivity().getSharedPreferences("MovieID", Context.MODE_PRIVATE);
        MovieName = getActivity().getSharedPreferences("MovieName", Context.MODE_PRIVATE);
        MoviePlot = getActivity().getSharedPreferences("MoviePlot", Context.MODE_PRIVATE);
        MoviePoster = getActivity().getSharedPreferences("MoviePoster", Context.MODE_PRIVATE);
        MovieRating = getActivity().getSharedPreferences("MovieRating", Context.MODE_PRIVATE);
        MovieYear = getActivity().getSharedPreferences("MovieYear", Context.MODE_PRIVATE);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<MovieInfo> storedMovies = new ArrayList<MovieInfo>();
        storedMovies.addAll(movies);
        outState.putParcelableArrayList(STORED_MOVIES, storedMovies);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }
}
