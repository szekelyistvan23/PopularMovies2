package com.example.szekelyistvan.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Szekely Istvan on 10.03.2018.
 */

public class LoaderArguments implements Parcelable {
    private String mSelection;
    private String [] mSelectionArgs;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSelection);
        dest.writeStringArray(this.mSelectionArgs);
    }

    public LoaderArguments() {
    }

    protected LoaderArguments(Parcel in) {
        this.mSelection = in.readString();
        this.mSelectionArgs = in.createStringArray();
    }

    public static final Parcelable.Creator<LoaderArguments> CREATOR = new Parcelable.Creator<LoaderArguments>() {
        @Override
        public LoaderArguments createFromParcel(Parcel source) {
            return new LoaderArguments(source);
        }

        @Override
        public LoaderArguments[] newArray(int size) {
            return new LoaderArguments[size];
        }
    };
}
