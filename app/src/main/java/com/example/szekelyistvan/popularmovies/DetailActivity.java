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

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.example.szekelyistvan.popularmovies.Adapter.MovieAdapter;
import com.example.szekelyistvan.popularmovies.Model.Comment;
import com.example.szekelyistvan.popularmovies.Model.Movie;
import com.example.szekelyistvan.popularmovies.Model.Trailer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    private Movie mMovieDetail;
    @BindView(R.id.user_rating) TextView mVoteAverage;
    @BindView(R.id.poster_image) ImageView mPosterPath;
    @BindView(R.id.original_title) TextView mOriginalTitle;
    @BindView(R.id.background_image) ImageView mBackdropPath;
    @BindView(R.id.overview) TextView mOverview;
    @BindView(R.id.release_date) TextView mReleaseDate;
    @BindView(R.id.action_bar_title) TextView mTextViewActionBar;
    @BindView(R.id.detailProgressBar) ProgressBar mDetailProgressBar;
    private List<Comment> mCommentsArray;
    private List <Trailer> mTrailersArray;
    public static final String DETAIL_BASE_URL = "https://api.themoviedb.org/3/movie/";
    public static final String DETAIL_URL_LAST_PART = "?api_key=" + BuildConfig.API_KEY +
            "&language=en-US&page=1&append_to_response=reviews,videos&language=en-US";
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
    public static final String JSON_TRAILER = "Trailer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                mMovieDetail = extras.getParcelable(MovieAdapter.MOVIE_OBJECT);
            } else {
                finish();
                Toast.makeText(DetailActivity.this, "No data available", Toast.LENGTH_SHORT).show();
            }

            setupActionBar();
            setUpAndLoadDataToUi();
            downloadCommentsTrailersData();


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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.action_bar_title_layout);
        }
        ButterKnife.bind(this);
        mTextViewActionBar.setText(mMovieDetail.getTitle());
    }

    /** Downloads JSON data from the Internet. */
    private void downloadCommentsTrailersData(){
//        ButterKnife.bind(this);
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

            if (trailerResult.getType().equals(JSON_TRAILER)) {
                resultArray.add(trailerResult);
            }
            trailerResult = new Trailer();
        }
        return resultArray;
    }
}
