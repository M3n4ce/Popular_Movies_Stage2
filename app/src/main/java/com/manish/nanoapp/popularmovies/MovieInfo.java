package com.manish.nanoapp.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by f4898303 on 2015/11/08.
 */
public class MovieInfo implements Parcelable {

    private String ID;
    private String Title;
    private String ReleaseDate;
    private String MoviePoster;
    private String VoteAverage;
    private String Plot;

    public MovieInfo(String Title,String ReleaseDate, String MoviePoster, String VoteAverage,String Plot,String ID) {
        this.Title = Title;
        this.ReleaseDate = ReleaseDate;
        this.MoviePoster = MoviePoster;
        this.VoteAverage = VoteAverage;
        this.Plot = Plot;
        this.ID = ID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(Title);
        dest.writeString(MoviePoster);
        dest.writeString(VoteAverage);
        dest.writeString(ReleaseDate);
        dest.writeString(Plot);
        dest.writeString(ID);
    }

    private MovieInfo(Parcel in) {
        Title = in.readString();
        MoviePoster = in.readString();
        VoteAverage = in.readString();
        ReleaseDate = in.readString();
        Plot = in.readString();
        ID = in.readString();
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR = new Parcelable.Creator<MovieInfo>() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    public String getTitle() {
        return Title;
    }

    public String getReleaseDate() {
        return ReleaseDate;
    }

    public String getMoviePoster() {
        return MoviePoster;
    }

    public String getVoteAverage() {
        return VoteAverage;
    }

    public String getPlot() {
        return Plot;
    }

    public String getID() { return ID;
    }
}
