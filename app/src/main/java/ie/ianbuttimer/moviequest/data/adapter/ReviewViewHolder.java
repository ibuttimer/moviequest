/*
 * Copyright (C) 2017  Ian Buttimer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ie.ianbuttimer.moviequest.data.adapter;

import android.view.View;
import android.widget.TextView;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.data.IAdapterOnClickHandler;
import ie.ianbuttimer.moviequest.tmdb.review.BaseReview;

/**
 * A RecyclerView.ViewHolder for Review objects
 */

public class ReviewViewHolder extends AbstractTMDbViewHolder<BaseReview> {

    private final TextView mReviewTextView;
    private final TextView mAuthorTextView;

    /**
     * Constructor
     * @param view          View to hold
     * @param clickHandler  onClick handler for view
     */
    public ReviewViewHolder(View view, IAdapterOnClickHandler clickHandler) {
        super(view, clickHandler);

        mReviewTextView = view.findViewById(R.id.tv_review_movie_video_list_item);
        mAuthorTextView = view.findViewById(R.id.tv_author_movie_video_list_item);
    }


    @Override
    public void setViewInfo(BaseReview info) {
        mReviewTextView.setText(info.getContent());
        mAuthorTextView.setText(info.getAuthor());
    }
}
