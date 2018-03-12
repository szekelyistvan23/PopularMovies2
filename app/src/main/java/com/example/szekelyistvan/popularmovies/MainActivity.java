/**Copyright 2018 Szekely Istvan
*
*        Licensed under the Apache License, Version 2.0 (the "License");
*        you may not use this file except in compliance with the License.
*        You may obtain a copy of the License at
*
*        http://www.apache.org/licenses/LICENSE-2.0
*
*        Unless required by applicable law or agreed to in writing, software
*        distributed under the License is distributed on an "AS IS" BASIS,
*        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*        See the License for the specific language governing permissions and
*        limitations under the License
 */

package com.example.szekelyistvan.popularmovies;

/**
 * Displays a RecyclerView with popular, top rated or favourite movies.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.szekelyistvan.popularmovies.adapters.MovieAdapter;
import com.example.szekelyistvan.popularmovies.model.Movie;
import com.example.szekelyistvan.popularmovies.loaders.AllFavouritesLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.szekelyistvan.popularmovies.adapters.MovieAdapter.MOVIE_OBJECT;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_BACKDROP_PATH;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_ID;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_ORIGINAL_TITLE;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_OVERVIEW;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_POSTER_PATH;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_RELEASE_DATE;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_TITLE;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_VOTE_AVERAGE;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Put your own api key from themoviedb.org to gradle.properties in the following form
    API_KEY = "your api key goes here" */
    private static final String API_KEY = BuildConfig.API_KEY;
    public static final String JSON_ARRAY_RESULTS = "results";
    public static final String JSON_ID = "id";
    public static final String JSON_VOTE_AVERAGE = "vote_average";
    public static final String JSON_TITLE = "title";
    public static final String JSON_POSTER_PATH = "poster_path";
    public static final String JSON_ORIGINAL_TITLE = "original_title";
    public static final String JSON_BACKDROP_PATH = "backdrop_path";
    public static final String JSON_OVERVIEW = "overview";
    public static final String JSON_RELEASE_DATE = "release_date";
    public static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE_W185 = "w185/";
    public static final String IMAGE_SIZE_W500 = "w500/";
    public static final String NO_IMAGE = "NO IMAGE";
    public static final String JSON_REQUEST_BASE_URL = "https://api.themoviedb.org/3/movie";
    public static final String POPULAR = "popular";
    public static final String TOP_RATED = "top_rated";
    public static final String FAVOURITE = "favourite";
    public static final String API_KEY_QUERY = "api_key";
    public static final String LANGUAGE_QUERY = "language";
    public static final String LANGUAGE_QUERY_VALUE = "en-US";
    public static final String PAGE_QUERY = "page";
    public static final String PAGE_QUERY_VALUE = "1";
    public static final String DEFAULT_QUERY = "default_query";
    public static final String ACTIVITY_TITLE = "title";
    public static final double AVERAGE_VOTE_FALLBACK = 0.0;
    public static final int DEFAULT_IMAGE_WIDTH = 185;
    public static final int FAVOURITES_LOADER_ID = 22;
    public static final int FIRST_POSITION = 0;
    public static final int NO_FAVOURITES = 0;
    @BindView(R.id.recycler_view_main) RecyclerView mRecyclerView;
    @BindView(R.id.main_progress_bar) ProgressBar mMainProgressBar;
    private MovieAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Movie> moviesArray;
    private @MovieListType String defaultQuery;
    private Toast sortToast;


    @StringDef({POPULAR, TOP_RATED, FAVOURITE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MovieListType {
    }

    @StringDef({IMAGE_SIZE_W185, IMAGE_SIZE_W185})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MovieImageSize {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readSharedPreferences();
        setupRecyclerView();
        if (haveNetworkConnection()) {
                downloadData(defaultQuery);
        } else {
            defaultQuery = FAVOURITE;
            downloadData(defaultQuery);
        }
    }

    /** Sets up a RecyclerView. */
    private void setupRecyclerView(){
    // Butterknife is distributed under Apache License, Version 2.0

        ButterKnife.bind(this);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, autoSpan());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new MovieAdapter(new ArrayList<Movie>(), new MovieAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Movie movie) {
                Bundle args = new Bundle();
                args.putParcelable(MOVIE_OBJECT, movie);
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }
    /** Downloads JSON data from the Internet. */
    private void downloadData(@MovieListType String query){
        ButterKnife.bind(this);
        if (query.equals(POPULAR) || query.equals(TOP_RATED)) {

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

            String url = buildStringForRequest(query);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                moviesArray = jsonToMovieArray(response);
                                mAdapter.changeMovieData(moviesArray);
                                mMainProgressBar.setVisibility(View.INVISIBLE);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    finish();
                    Toast.makeText(MainActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
                }
            });
            queue.add(stringRequest);
        } else {
            getSupportLoaderManager().restartLoader(FAVOURITES_LOADER_ID, null, this);
            mMainProgressBar.setVisibility(View.INVISIBLE);
            setTitle(getString(R.string.favourite_title));
        }
    }
    /** Loads the custom menu. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return true;
    }

    /** Implements custom menu with three elements. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.sortPopular:
                if (haveNetworkConnection()) {
                    sortPopular();
                } else {
                    showToast(getString(R.string.no_internet));
                }
                return true;
            case R.id.sortTopRated:
                if (haveNetworkConnection()) {
                    sortTopRated();
                } else {
                    showToast(getString(R.string.no_internet));
                }
                return true;
            case R.id.sortFavourite:
                    sortFavourite();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        }

    /** Builds a String for an Internet request based on the user's choice. */
    private String buildStringForRequest(@MovieListType String queryType){

        if (queryType != null) {
            Uri uri = Uri.parse(JSON_REQUEST_BASE_URL).buildUpon()
                    .appendPath(queryType)
                    .appendQueryParameter(API_KEY_QUERY, API_KEY)
                    .appendQueryParameter(LANGUAGE_QUERY, LANGUAGE_QUERY_VALUE)
                    .appendQueryParameter(PAGE_QUERY, PAGE_QUERY_VALUE)
                    .build();
            return uri.toString();
        } else {
            return "";
        }
    }

    /** Checks the state of the network connection. */
    public boolean haveNetworkConnection() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
                return true;
            } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                return true;
            }
        }
        }catch (NullPointerException e){
            e.printStackTrace();
            }
        return false;
    }

    /** Displays the popular movies list. */
    private void sortPopular(){
        if (defaultQuery.equals(POPULAR)) {
            showToast(getString(R.string.already_popular));
        } else {
            defaultQuery = POPULAR;
            setTitle(getString(R.string.popular_movies_title));
            saveSharedPreferences();
            mMainProgressBar.setVisibility(View.VISIBLE);
            mLayoutManager.scrollToPosition(FIRST_POSITION);
            downloadData(defaultQuery);
        }
    }

    /** Displays the top rated movies list. */
    private void sortTopRated(){
        if (defaultQuery.equals(TOP_RATED)) {
            showToast(getString(R.string.already_top));
        } else {
            defaultQuery = TOP_RATED;
            setTitle(getString(R.string.highest_rated_title));
            saveSharedPreferences();
            mMainProgressBar.setVisibility(View.VISIBLE);
            mLayoutManager.scrollToPosition(FIRST_POSITION);
            downloadData(defaultQuery);
        }
    }

    /** Displays the favourite movies' list. */
    private void sortFavourite(){
        if (defaultQuery.equals(FAVOURITE)) {
            showToast(getString(R.string.already_favourite));
        } else {
            defaultQuery = FAVOURITE;
            setTitle(getString(R.string.favourite_title));
            saveSharedPreferences();
            mMainProgressBar.setVisibility(View.VISIBLE);
            mLayoutManager.scrollToPosition(FIRST_POSITION);
            downloadData(defaultQuery);


        }
    }

    /** Displays a toast message after canceling the previous one. */
    public void showToast(String text){
        if (sortToast != null){
            sortToast.cancel();
        }
        sortToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        sortToast.show();
    }

    /** Parses JSONArray to a Movie Array. */
    private List<Movie> jsonToMovieArray(String jsonResponse) throws JSONException{
        List<Movie> resultArray = new ArrayList<>();
        Movie movieResult = new Movie();
        JSONObject extractMovieData;

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray jsonArray = jsonObject.getJSONArray(JSON_ARRAY_RESULTS);

        for (int i=0; i < jsonArray.length(); i++){
            extractMovieData = (JSONObject) jsonArray.get(i);

            movieResult.setId(extractMovieData.optInt(JSON_ID));
            movieResult.setVoteAverage(extractMovieData.optDouble(JSON_VOTE_AVERAGE, AVERAGE_VOTE_FALLBACK));
            movieResult.setTitle(extractMovieData.optString(JSON_TITLE));
            movieResult.setPosterPath(setImageSize(IMAGE_SIZE_W185, extractMovieData.optString(JSON_POSTER_PATH)));
            movieResult.setOriginalTitle(extractMovieData.optString(JSON_ORIGINAL_TITLE));
            movieResult.setBackdropPath(setImageSize((String)IMAGE_SIZE_W500, extractMovieData.optString(JSON_BACKDROP_PATH)));
            movieResult.setOverview(extractMovieData.optString(JSON_OVERVIEW));
            movieResult.setReleaseDate(extractMovieData.optString(JSON_RELEASE_DATE));

            resultArray.add(movieResult);
            movieResult = new Movie();
        }
        return resultArray;
    }

    /** Calculates the number of pictures to be displayed in a row based on the screen width. */
    private int autoSpan(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.widthPixels;

        int orientation = this.getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return height / DEFAULT_IMAGE_WIDTH;
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
            return width / DEFAULT_IMAGE_WIDTH;
        }
        return 1;
    }

    /** Sets the image size for an image to be downloaded with Picasso. */
    private String setImageSize (@MovieImageSize String imageSize, String imagePath){
        StringBuilder stringBuilder = new StringBuilder();

        if (imageSize!=null && imagePath !=null){
            stringBuilder.append(IMAGE_BASE_URL);
            stringBuilder.append(imageSize);
            stringBuilder.append(imagePath);
        } else {
            stringBuilder.append(NO_IMAGE);
        }
        return stringBuilder.toString();
    }
    /** Using loader to load favourite movies from content provider. */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AllFavouritesLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && defaultQuery.equals(FAVOURITE)) {
            mAdapter.changeMovieData(cursorToArrayList(data));
        }

        try {
            if (data.getCount() == NO_FAVOURITES && defaultQuery.equals(FAVOURITE)) {
                showToast(getString(R.string.no_favourite_movies));
            }
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    /** Transforms cursor object to an array. */
    private List<Movie> cursorToArrayList (Cursor cursor){
        List<Movie> resultArrayList = new ArrayList<>();
            while (cursor.moveToNext()){
                Integer id = cursor.getInt(cursor.getColumnIndex(FAVOURITES_COLUMN_ID));
                Double voteAverage = cursor.getDouble(cursor.getColumnIndex(FAVOURITES_COLUMN_VOTE_AVERAGE));
                String title = cursor.getString(cursor.getColumnIndex(FAVOURITES_COLUMN_TITLE));
                String posterPath = cursor.getString(cursor.getColumnIndex(FAVOURITES_COLUMN_POSTER_PATH));
                String originalTitle = cursor.getString(cursor.getColumnIndex(FAVOURITES_COLUMN_ORIGINAL_TITLE));
                String backdropPath = cursor.getString(cursor.getColumnIndex(FAVOURITES_COLUMN_BACKDROP_PATH));
                String overview = cursor.getString(cursor.getColumnIndex(FAVOURITES_COLUMN_OVERVIEW));
                String releaseDate = cursor.getString(cursor.getColumnIndex(FAVOURITES_COLUMN_RELEASE_DATE));

                resultArrayList.add(new Movie(id, voteAverage, title, posterPath, originalTitle, backdropPath, overview,releaseDate));
            }
        return resultArrayList;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
            saveSharedPreferences();
    }
    /** Saves data to shared preferences. */
    private void saveSharedPreferences(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DEFAULT_QUERY, defaultQuery);
        editor.putString(ACTIVITY_TITLE, getTitle().toString());
        editor.apply();
    }

    /** Reads data from shared preferences. */
    private void readSharedPreferences(){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        defaultQuery = sharedPreferences.getString(DEFAULT_QUERY, POPULAR);
        setTitle(sharedPreferences.getString(ACTIVITY_TITLE, getString(R.string.popular_movies_title)));
    }
}
