package com.example.szekelyistvan.popularmovies.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.CONTENT_URI;

/**
 * Loads all favourites movies from the content provider to a RecyclerView.
 */

public class AllFavouritesLoader extends AsyncTaskLoader<Cursor>{

    public AllFavouritesLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
            forceLoad();
    }

    @Override
    public Cursor loadInBackground() {
        try {
            return getContext().getContentResolver().query(CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }
}
