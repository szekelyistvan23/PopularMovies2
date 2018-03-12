package com.example.szekelyistvan.popularmovies.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.CREATE_TABLE;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.DB_NAME;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.DB_VERSION;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.TABLE_NAME;

/**
 * Database Helper class.
 */

public class FavouritesDbHelper extends SQLiteOpenHelper{

    public FavouritesDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
