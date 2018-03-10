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
 * Displays data from an object received trough an intent.
 */

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.szekelyistvan.popularmovies.Adapters.CommentAdapter;
import com.example.szekelyistvan.popularmovies.Adapters.MovieAdapter;
import com.example.szekelyistvan.popularmovies.Adapters.TrailerAdapter;
import com.example.szekelyistvan.popularmovies.model.Comment;
import com.example.szekelyistvan.popularmovies.model.LoaderArguments;
import com.example.szekelyistvan.popularmovies.model.Movie;
import com.example.szekelyistvan.popularmovies.model.Trailer;
import com.example.szekelyistvan.popularmovies.utils.FavouriteLoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.CONTENT_URI;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_BACKDROP_PATH;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_ID;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_ORIGINAL_TITLE;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_OVERVIEW;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_POSTER_PATH;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_RELEASE_DATE;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_TITLE;
import static com.example.szekelyistvan.popularmovies.utils.FavouritesContract.FavouritesEntry.FAVOURITES_COLUMN_VOTE_AVERAGE;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private Movie mMovieDetail;
    @BindView(R.id.user_rating)
    TextView mVoteAverage;
    @BindView(R.id.poster_image)
    ImageView mPosterPath;
    @BindView(R.id.original_title)
    TextView mOriginalTitle;
    @BindView(R.id.background_image)
    ImageView mBackdropPath;
    @BindView(R.id.overview)
    TextView mOverview;
    @BindView(R.id.release_date)
    TextView mReleaseDate;
    @BindView(R.id.action_bar_title)
    TextView mTextViewActionBar;
    @BindView(R.id.detailProgressBar)
    ProgressBar mDetailProgressBar;
    @BindView(R.id.rv_videos)
    RecyclerView mTrailersRecyclerView;
    @BindView(R.id.trailer_header)
    TextView mTrailerHeader;
    @BindView(R.id.rv_comments)
    RecyclerView mCommentsRecyclerView;
    @BindView(R.id.comment_header)
    TextView mCommentsHeader;
    @BindView(R.id.favouriteImage)
    ImageView mFavouriteImage;
    private List<Comment> mCommentsArray;
    private List <Trailer> mTrailersArray;
    private TrailerAdapter mTrailerAdapter;
    private CommentAdapter mCommentAdapter;
    private boolean mFavorite;
    public static final String DETAIL_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String DETAIL_URL_LAST_PART = "?api_key=" + BuildConfig.API_KEY +
            "&language=en-US&page=1&append_to_response=reviews,videos&language=en-US";
    public static final int QUERY_LOADER_ID = 11;
    public static final String LOADER_ARGUMENTS = "args";
    public static final String JSON_REVIEWS = "reviews";
    public static final String JSON_VIDEOS = "videos";
    public static final String JSON_RESULTS = "results";
    public static final String JSON_ID = "id";
    public static final String JSON_AUTHOR = "author";
    public static final String JSON_CONTENT = "content";
    public static final String JSON_URL = "url";
    public static final String JSON_KEY = "key";
    public static final String JSON_NAME = "name";
    public static final String JSON_TYPE = "type";
    public static final String YOUTUBE_VIDEO_LINK = "https://m.youtube.com/watch?v=";
    public static final String LOADER_SELECTION = "MOVIE_ID=?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_title_layout);
        }

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mMovieDetail = extras.getParcelable(MovieAdapter.MOVIE_OBJECT);
            } else {
                finish();
                Toast.makeText(DetailActivity.this, "No data available", Toast.LENGTH_SHORT).show();
            }

            setupRecyclerViews();
            setupActionBar();
            setUpAndLoadDataToUi();
            downloadCommentsTrailersData();
            queryFavouriteState();
            onClickManageFavourite();


    }
    /** Sets up the views and populates it with data from a Movie object. */
    private void setUpAndLoadDataToUi(){
        ButterKnife.bind(this);
        // Picasso is distributed under Apache License, Version 2.0
        Picasso.with(this)
                .load(mMovieDetail.getPosterPath())
                .placeholder(R.drawable.blank185)
                .error(R.drawable.error185)
                .into(mPosterPath);

        Picasso.with(this)
                .load(mMovieDetail.getBackdropPath())
                .placeholder(R.drawable.blank500)
                .error(R.drawable.error500)
                .into(mBackdropPath, new Callback() {
                    @Override
                    public void onSuccess() {
                        mDetailProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        mDetailProgressBar.setVisibility(View.INVISIBLE);
                    }
                });

        mVoteAverage.setText(String.valueOf(mMovieDetail.getVoteAverage()));
        mOriginalTitle.setText(mMovieDetail.getOriginalTitle());
        mOverview.setText(mMovieDetail.getOverview());
        mReleaseDate.setText(mMovieDetail.getReleaseDate());

    }

    /** Sets up a custom action bar, for correct display of movies' titles. */
    private void setupActionBar() {
        ButterKnife.bind(this);
        mTextViewActionBar.setText(mMovieDetail.getTitle());
    }

    /** Downloads JSON data from the Internet. */
    private void downloadCommentsTrailersData(){
        ButterKnife.bind(this);
        RequestQueue queue = Volley.newRequestQueue(DetailActivity.this);

        String url = detailBuildStringForRequest();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            mCommentsArray = jsonToCommentArray(response);
                            mTrailersArray = jsonToTrailerArray(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        trailersRecyclerViewSettings();
                        commentsRecyclerViewSettings();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(DetailActivity.this, R.string.wrong, Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);

    }

    private String detailBuildStringForRequest(){
        return DETAIL_BASE_URL + mMovieDetail.getId() + DETAIL_URL_LAST_PART;
    }

    /** Converts JSONArray to a Comment Array. */
    private List<Comment> jsonToCommentArray(String jsonResponse) throws JSONException{
        List<Comment> resultArray = new ArrayList<>();
        Comment commentResult = new Comment();
        JSONObject extractedCommentData;

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject baseCommentArray = jsonObject.getJSONObject(JSON_REVIEWS);
        JSONArray jsonArray = baseCommentArray.getJSONArray(JSON_RESULTS);


        for (int i=0; i < jsonArray.length(); i++){
            extractedCommentData = jsonArray.getJSONObject(i);

            commentResult.setId(extractedCommentData.optString(JSON_ID));
            commentResult.setAuthor(extractedCommentData.optString(JSON_AUTHOR));
            commentResult.setContent(extractedCommentData.optString(JSON_CONTENT));
            commentResult.setUrl(extractedCommentData.optString(JSON_URL));

            resultArray.add(commentResult);
            commentResult = new Comment();
        }
        return resultArray;
    }

    /** Converts JSONArray to a Trailer Array. */
    private List<Trailer> jsonToTrailerArray(String jsonResponse) throws JSONException{
        List<Trailer> resultArray = new ArrayList<>();
        Trailer trailerResult = new Trailer();
        JSONObject extractedTrailerData;

        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONObject baseTrailerArray = jsonObject.getJSONObject(JSON_VIDEOS);
        JSONArray jsonArray = baseTrailerArray.getJSONArray(JSON_RESULTS);


        for (int i=0; i < jsonArray.length(); i++){
            extractedTrailerData = jsonArray.getJSONObject(i);

            trailerResult.setId(extractedTrailerData.optString(JSON_ID));
            trailerResult.setKey(extractedTrailerData.optString(JSON_KEY));
            trailerResult.setName(extractedTrailerData.optString(JSON_NAME));
            trailerResult.setType(extractedTrailerData.optString(JSON_TYPE));

            resultArray.add(trailerResult);

            trailerResult = new Trailer();
        }
        return resultArray;
    }

    /** Sets up a RecyclerViews for trailers and comments. */
    private void setupRecyclerViews(){

        ButterKnife.bind(this);
        mTrailersRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager trailerLayoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mTrailersRecyclerView.setLayoutManager(trailerLayoutManager);

        mTrailerAdapter = new TrailerAdapter(new ArrayList<Trailer>(), new TrailerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Trailer trailer) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(YOUTUBE_VIDEO_LINK + trailer.getKey()));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        mTrailersRecyclerView.setAdapter(mTrailerAdapter);

        mCommentsRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager commentLayoutManager= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);
        mCommentsRecyclerView.setLayoutManager(commentLayoutManager);

        mCommentAdapter = new CommentAdapter(new ArrayList<Comment>(), new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Comment comment) {
                Uri uri = Uri.parse(comment.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }

            }
        });

        mCommentsRecyclerView.setAdapter(mCommentAdapter);
    }

    private void trailersRecyclerViewSettings(){
        ButterKnife.bind(this);
        if (mTrailersArray != null && !mTrailersArray.isEmpty()){
            mTrailerAdapter.changeTrailerData(mTrailersArray);

            if (mTrailersArray.size() == 1){
                mTrailerHeader.setText(R.string.trailer);
            }
        }
        if (mTrailersArray == null || mTrailersArray.size() == 0){
            mTrailerHeader.setVisibility(View.GONE);
            mTrailersRecyclerView.setVisibility(View.GONE);
        }
    }

    private void commentsRecyclerViewSettings(){
        ButterKnife.bind(this);
        if (mCommentsArray != null && !mCommentsArray.isEmpty()){
        mCommentAdapter.changeCommentData(mCommentsArray);

            if (mCommentsArray.size() == 1){
                mCommentsHeader.setText(R.string.comment);
            }
        }
        if (mCommentsArray == null || mCommentsArray.size()==0){
            mCommentsHeader.setVisibility(View.GONE);
            mCommentsRecyclerView.setVisibility(View.GONE);
        }


    }

    private void onClickManageFavourite (){
        ButterKnife.bind(this);

        final ContentValues contentValues = new ContentValues();

        mFavouriteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contentValues.put(FAVOURITES_COLUMN_ID, mMovieDetail.getId());
                contentValues.put(FAVOURITES_COLUMN_VOTE_AVERAGE, mMovieDetail.getVoteAverage());
                contentValues.put(FAVOURITES_COLUMN_TITLE, mMovieDetail.getTitle());
                contentValues.put(FAVOURITES_COLUMN_POSTER_PATH, mMovieDetail.getPosterPath());
                contentValues.put(FAVOURITES_COLUMN_ORIGINAL_TITLE, mMovieDetail.getOriginalTitle());
                contentValues.put(FAVOURITES_COLUMN_BACKDROP_PATH, mMovieDetail.getBackdropPath());
                contentValues.put(FAVOURITES_COLUMN_OVERVIEW, mMovieDetail.getOverview());
                contentValues.put(FAVOURITES_COLUMN_RELEASE_DATE, mMovieDetail.getReleaseDate());

                Uri uri = getContentResolver().insert(CONTENT_URI, contentValues);

                if (uri != null ){
                    mFavouriteImage.setImageResource(R.drawable.favourite);
                    Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void queryFavouriteState(){
        Bundle bundle = new Bundle();
        String [] loaderSelectionArgs  = {mMovieDetail.getId().toString()};

        LoaderArguments loaderArguments = new LoaderArguments(LOADER_SELECTION, loaderSelectionArgs);
        bundle.putParcelable(LOADER_ARGUMENTS, loaderArguments);

        getSupportLoaderManager().initLoader(QUERY_LOADER_ID, bundle, this);

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new FavouriteLoader(this, args);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() > 0){
            mFavouriteImage.setImageResource(R.drawable.favourite);
            mFavorite = true;
        } else {
            mFavouriteImage.setImageResource(R.drawable.not_favourite);
            mFavorite = false;
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
