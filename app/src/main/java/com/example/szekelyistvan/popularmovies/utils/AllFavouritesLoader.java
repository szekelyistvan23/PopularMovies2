package com.example.szekelyistvan.popularmovies.utils;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.CONTENT_URI;

/**
 * Created by Szekely Istvan on 09.03.2018.
 */

public class AllFavouritesLoader extends AsyncTaskLoader<Cursor>{

    private Cursor mFavouritesData = null;

    public AllFavouritesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mFavouritesData != null) {
            deliverResult(mFavouritesData);
        } else {
            forceLoad();
        }
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return getContext().getContentResolver().query(CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(Cursor data) {
        mFavouritesData = data;
        super.deliverResult(data);
    }
}
