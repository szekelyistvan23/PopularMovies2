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
import android.widget.TextView;

import com.example.szekelyistvan.popularmovies.Model.Trailer;
import com.example.szekelyistvan.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is a custom adapter for a RecyclerView, it is displaying only an ImageView and a TextView.
 * Implements an OnClickListener interface to open the selected trailer with an intent.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder>{

    private List<Trailer> mTrailers;
    private OnItemClickListener mListener;

    public TrailerAdapter(List<Trailer> trailers, OnItemClickListener listener) {
        this.mTrailers = trailers;
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Trailer trailer);
    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.trailer_item, parent, false);
        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, final int position) {
        Picasso.with(holder.mTrailerImage.getContext())
                .load(mTrailers.get(position).getKey())
                .placeholder(R.drawable.trailer_default)
                .error(R.drawable.error185)
                .into(holder.mTrailerImage);
        holder.mTrailerName.setText(mTrailers.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(mTrailers.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.trailerImageView)
        ImageView mTrailerImage;
        @BindView(R.id.trailerName)
        TextView mTrailerName;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
