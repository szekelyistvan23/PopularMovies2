package com.example.szekelyistvan.popularmovies.loaders;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.example.szekelyistvan.popularmovies.model.LoaderArguments;

import static com.example.szekelyistvan.popularmovies.DetailActivity.LOADER_ARGUMENTS;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.CONTENT_URI;

/**
 * Deletes the selected movie from the content provider.
 */

public class DeleteLoader extends AsyncTaskLoader<Integer>{

    private Bundle mArguments;
    private Integer mFavouriteData;

    public DeleteLoader(Context context, Bundle arguments) {
        super(context);
        this.mArguments = arguments;
    }

    @Override
    protected void onStartLoading() {
        if (mFavouriteData != null) {
            deliverResult(mFavouriteData);
        } else {
            forceLoad();
        }
    }

    @Override
    public Integer loadInBackground() {

        LoaderArguments loaderArguments = mArguments.getParcelable(LOADER_ARGUMENTS);

        try {
            return getContext().getContentResolver().delete(CONTENT_URI,
                    loaderArguments.getSelection(),
                    loaderArguments.getSelectionArgs());

        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(Integer data) {
        mFavouriteData = data;
        super.deliverResult(data);
    }
}
