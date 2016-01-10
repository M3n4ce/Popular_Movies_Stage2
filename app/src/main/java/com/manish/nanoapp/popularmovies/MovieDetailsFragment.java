package com.manish.nanoapp.popularmovies;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by f4898303 on 2015/11/07.
 */
public class MovieDetailsFragment extends Fragment{

    private final String LOG_TAG = MovieDetailsFragment.class.getSimpleName();

    private TrailerArrayAdapter trailerArrayAdapter;
    List<TrailObjectItem> trailers= new ArrayList<TrailObjectItem>();
    MovieInfo movie;
    TrailObjectItem trailObjectItem;
    Button btnFavorites;
    ImageView favorites;
    public final String storedID = "";
    private Context globalContext = null;
    ListView listViewItems;

    SharedPreferences MovieID,MovieName,MoviePlot,MoviePoster,MovieYear,MovieRating,TrailerList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        IniPrefs();

        globalContext = this.getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        trailerArrayAdapter = new TrailerArrayAdapter(getActivity(), R.layout.trailer_list_view,R.id.txt_trailer, new ArrayList<String>());

        View view = inflater.inflate(R.layout.movie_details_fragment, container, false);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linearlayout_MovieDescription);
        listViewItems = new ListView(getActivity());
        listViewItems.setAdapter(trailerArrayAdapter);
        listViewItems.setOnItemClickListener(new OnitemClickListenerTrailerItems());

        linearLayout.addView(listViewItems);

        btnFavorites = (Button)view.findViewById(R.id.btnFavourite);
        favorites =(ImageView)view.findViewById(R.id.img_favorite);

        Log.d(LOG_TAG, "In onCreateLoader");
        Intent intent = getActivity().getIntent();
        if (intent != null || intent.hasExtra("details")) {

            movie = (MovieInfo)intent.getParcelableExtra("details");

            TextView title = (TextView) view.findViewById(R.id.movie_title);
            ImageView poster = (ImageView) view.findViewById(R.id.poster_image);
            TextView releaseDate = (TextView) view.findViewById(R.id.release_date);
            TextView ratings = (TextView) view.findViewById(R.id.ratings);
            TextView overview = (TextView) view.findViewById(R.id.plot);

            title.setText(movie.getTitle());
            Picasso.with(getActivity()).load(movie.getMoviePoster()).into(poster);
            releaseDate.setText(movie.getReleaseDate());
            ratings.setText(" ( " + movie.getVoteAverage() + "/10 )");
            overview.setText(movie.getPlot());

            ManageFavourites();

            GetTrailersReviews();

        }

        return view;

    }

    //Manage the favourites
    private void ManageFavourites() {
        //if not saved as favourite
        if (!((MovieName.getString(movie.getID(),"movie does not exist")).equals(movie.getTitle()))) {
            btnFavorites.setVisibility(View.VISIBLE);

            //Save favorite movies locally to shared prefs
            btnFavorites.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int prefSize=0;
                    //Size of sharedprefs
                    if(MovieID.getAll()!=null) {
                        prefSize = MovieID.getAll().size();
                    }

                    SharedPreferences.Editor MID = MovieID.edit();
                    SharedPreferences.Editor MName = MovieName.edit();
                    SharedPreferences.Editor MPlot = MoviePlot.edit();
                    SharedPreferences.Editor MPoster = MoviePoster.edit();
                    SharedPreferences.Editor MRating = MovieRating.edit();
                    SharedPreferences.Editor MYear = MovieYear.edit();

                    //Write to shared prefs
                    MID.putString(String.valueOf(prefSize), movie.getID());
                    MName.putString(movie.getID(), movie.getTitle());
                    MPlot.putString(movie.getID(), movie.getPlot());
                    MPoster.putString(movie.getID(), movie.getMoviePoster());
                    MRating.putString(movie.getID(), movie.getVoteAverage());
                    MYear.putString(movie.getID(), movie.getReleaseDate());

                    //commit sharedprefs
                    MID.apply();
                    MName.apply();
                    MPlot.apply();
                    MPoster.apply();
                    MRating.apply();
                    MYear.apply();

                    btnFavorites.setVisibility(View.INVISIBLE);

                    Snackbar.make(getView(),"Movie saved to favorites.",Snackbar.LENGTH_SHORT).show();
                }
            });
        }
        else {
            favorites.setVisibility(View.VISIBLE);
        }
    }

    //Initialise Shared preferences
    public void IniPrefs() {
        MovieID = getActivity().getSharedPreferences("MovieID", Context.MODE_PRIVATE);
        MovieName = getActivity().getSharedPreferences("MovieName", Context.MODE_PRIVATE);
        MoviePlot = getActivity().getSharedPreferences("MoviePlot", Context.MODE_PRIVATE);
        MoviePoster = getActivity().getSharedPreferences("MoviePoster", Context.MODE_PRIVATE);
        MovieRating = getActivity().getSharedPreferences("MovieRating", Context.MODE_PRIVATE);
        MovieYear = getActivity().getSharedPreferences("MovieYear", Context.MODE_PRIVATE);
        TrailerList = getActivity().getSharedPreferences("TrailerList",Context.MODE_PRIVATE);
    }

    //Get trailers and reviews
    private void GetTrailersReviews() {
        CheckConnection checkCon = new CheckConnection(globalContext);
        if (checkCon.isConnectingToInternet()) {
            FetchMovieInfo fetchInfo = new FetchMovieInfo();
            fetchInfo.execute(movie.getID());
        }
        else{
            Log.d("Connection Error","Check network connection" );
        }

    }

    private class FetchMovieInfo extends AsyncTask<String,Void,List<TrailObjectItem>> {
        @Override
        protected List<TrailObjectItem> doInBackground(String... params) {

            String BaseUrl = "https://api.themoviedb.org/3/movie?";
            String KEY = "api_key";
            String ApiKey= "74696b98a7c2c21e494c86e2da61f2f6";
            String Movie_Id = "id";
            String movie_id = params[0];
            String MovieDbJson = "";

            try {

                Uri builtUri = Uri.parse(BaseUrl).buildUpon()
                        .appendEncodedPath(movie_id)
                        .appendEncodedPath("videos")
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
        protected void onPostExecute(List<TrailObjectItem> trailerInfo) {
            super.onPostExecute(trailerInfo);

            trailers.clear();
            trailers.addAll(trailerInfo);

            updatePosterAdapter();

            //updatePosterAdapter();
            /*movies.clear();
            movies.addAll(movieInfos);
            updatePosterAdapter();*/
        }

    }

    private List<TrailObjectItem> extractData(String moviesJsonStr) throws JSONException {

        // Items to extract
        final String ARRAY_OF_TRAILERS = "results";
        final String ORIGINAL_NAME = "name";
        final String TRAILER_KEY = "key";
        final String TRAILER_ID = "id";

        JSONObject moviesJson = new JSONObject(moviesJsonStr);
        JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_OF_TRAILERS);
        int numOfTrailers =  moviesArray.length();
        List<TrailObjectItem> trailers = new ArrayList<TrailObjectItem>();

        //add trailer details to sharedpreferences
        SharedPreferences.Editor trailerEditor = TrailerList.edit();
        trailerEditor.clear();

        for(int i = 0; i < numOfTrailers; ++i) {

            JSONObject trailer = moviesArray.getJSONObject(i);
            String title = trailer.getString(ORIGINAL_NAME);
            String trailer_key = trailer.getString(TRAILER_KEY);

            trailers.add(new TrailObjectItem(title,trailer_key));

            //Shared Prefs for trailer key
            trailerEditor.putString(String.valueOf(i),trailer_key);

        }

        trailerEditor.commit();

        return trailers;

    }

    private void updatePosterAdapter() {
        trailerArrayAdapter.clear();
        trailerArrayAdapter.notifyDataSetChanged();
        for(TrailObjectItem trailer : trailers) {
            trailerArrayAdapter.add(trailer.getTitle());
        }

    }
}
