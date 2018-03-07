package com.example.szekelyistvan.popularmovies.utils;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Szekely Istvan on 07.03.2018.
 */

public class FavouritesContract {

    public static final String AUTHORITY = "com.example.szekelyistvan.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_FAVOURITES = "favourites";

    public static final class FavouritesEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        public static final String DB_NAME = "favourites.db";
        public static final int DB_VERSION = 1;
        public static final String TABLE_NAME = "favourites";
        public static final String FAVOURITES_COLUMN_ID = "MOVIE_ID";
        public static final String FAVOURITES_COLUMN_VOTE_AVERAGE = "VOTE_AVERAGE";
        public static final String FAVOURITES_COLUMN_TITLE = "TITLE";
        public static final String FAVOURITES_COLUMN_POSTER_PATH = "POSTER_PATH";
        public static final String FAVOURITES_COLUMN_ORIGINAL_TITLE = "ORIGINAL_TITLE";
        public static final String FAVOURITES_COLUMN_BACKDROP_PATH = "BACKDROP_PATH";
        public static final String FAVOURITES_COLUMN_OVERVIEW = "OVERVIEW";
        public static final String FAVOURITES_COLUMN_RELEASE_DATE = "RELEASE_DATE";
        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FAVOURITES_COLUMN_ID +" TEXT, "
                + FAVOURITES_COLUMN_VOTE_AVERAGE + " TEXT, "
                + FAVOURITES_COLUMN_TITLE + " TEXT, "
                + FAVOURITES_COLUMN_POSTER_PATH + " TEXT, "
                + FAVOURITES_COLUMN_ORIGINAL_TITLE + " TEXT, "
                + FAVOURITES_COLUMN_BACKDROP_PATH +" TEXT, "
                + FAVOURITES_COLUMN_OVERVIEW +" TEXT, "
                + FAVOURITES_COLUMN_RELEASE_DATE + " TEXT);";
    }
}
