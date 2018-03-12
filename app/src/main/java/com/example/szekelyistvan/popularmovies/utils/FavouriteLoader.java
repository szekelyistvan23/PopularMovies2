package com.example.szekelyistvan.popularmovies.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

import com.example.szekelyistvan.popularmovies.loaders.LoaderArguments;

import static com.example.szekelyistvan.popularmovies.DetailActivity.LOADER_ARGUMENTS;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.CONTENT_URI;

/**
 * Created by morpheus on 10.03.2018.
 */

public class FavouriteLoader extends AsyncTaskLoader<Cursor> {

    private Bundle mArguments;
    private Cursor mFavouriteData = null;

    public FavouriteLoader(@NonNull Context context, Bundle args) {
        super(context);
        this.mArguments = args;
    }

    @Override
    protected void onStartLoading() {
        if (mFavouriteData != null) {
            deliverResult(mFavouriteData);
        } else {
            forceLoad();
        }
    }

    @Nullable
    @Override
    public Cursor loadInBackground() {

        LoaderArguments loaderArguments = mArguments.getParcelable(LOADER_ARGUMENTS);

        try {
            return getContext().getContentResolver().query(CONTENT_URI,
                    null,
                    loaderArguments.getSelection(),
                    loaderArguments.getSelectionArgs(),
                    null);

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(@Nullable Cursor data) {
        mFavouriteData = data;
        super.deliverResult(data);
    }
}
