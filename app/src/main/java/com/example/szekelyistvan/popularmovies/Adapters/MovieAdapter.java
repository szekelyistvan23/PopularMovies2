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
package com.example.szekelyistvan.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.szekelyistvan.popularmovies.Model.Movie;
import com.example.szekelyistvan.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is a custom adapter for a RecyclerView, it is displaying only an ImageView.
 * Implements an OnClickListener interface to open a DetailActivity for the selected item.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {


    private List<Movie> mMovies;
    private OnItemClickListener mListener;
    public static final String MOVIE_OBJECT = "movie_object";

    public MovieAdapter(List<Movie> movies, OnItemClickListener listener) {
        this.mMovies = movies;
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Movie movie);
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_thumbnail, parent, false);
        return new MovieAdapter.MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, final int position) {
        Picasso.with(holder.mMoviePoster.getContext())
                .load(mMovies.get(position).getPosterPath())
                .placeholder(R.drawable.blank185)
                .error(R.drawable.error185)
                .into(holder.mMoviePoster);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(mMovies.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public void changeMovieData(List<Movie> newMovies) {
        mMovies = newMovies;
        notifyDataSetChanged();
    }
    class MovieViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.movie_thumbnail_image)
        ImageView mMoviePoster;

        public MovieViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
