package com.manish.nanoapp.popularmovies;

/**
 * Created by f4898303 on 2015/11/10.
 */
public class TrailObjectItem {
    public String Title;
    public String key;

    public TrailObjectItem(String Title,String Key){

        this.Title = Title;
        this.key = Key;
    }


    public String getTitle() {
        return Title;
    }

    public String getKey() {
        return key;
    }
}
