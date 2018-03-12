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

package com.example.szekelyistvan.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.szekelyistvan.popularmovies.model.Comment;
import com.example.szekelyistvan.popularmovies.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * This is a custom adapter for a RecyclerView, it is displaying two TextViews.
 * Implements an OnClickListener interface to open the full text of the comment in a browser.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder>{
    private List<Comment> mComments;
    private CommentAdapter.OnItemClickListener mListener;

    public CommentAdapter(List<Comment> comments, CommentAdapter.OnItemClickListener listener) {
        this.mComments = comments;
        this.mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Comment comment);
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.comment_item, parent, false);
        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.CommentViewHolder holder, final int position) {
        holder.mCommentAuthor.setText(mComments.get(position).getAuthor());
        holder.mCommentText.setText(mComments.get(position).getContent());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(mComments.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void changeCommentData(List<Comment> newComments) {
        mComments = newComments;
        notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.comment_author)
        TextView mCommentAuthor;
        @BindView(R.id.comment_content)
        TextView mCommentText;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
