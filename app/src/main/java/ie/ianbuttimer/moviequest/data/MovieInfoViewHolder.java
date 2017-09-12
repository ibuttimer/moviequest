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
package ie.ianbuttimer.moviequest.data;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.MessageFormat;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.image.PosterImageLoader;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;
import ie.ianbuttimer.moviequest.image.ThumbnailImageLoader;


/**
 * A RecyclerView.ViewHolder for MovieInfo objects
 */
@SuppressWarnings("unused")
class MovieInfoViewHolder<T extends MovieInfoModel> extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final View mView;
    private final TextView mIndexTextView;
    private final TextView mPosterNaTextView;
    private final ImageView mPosterImageView;
    private MovieInfoAdapter.MovieInfoAdapterOnClickHandler mClickHandler;

    private PosterImageLoader mPosterLoader;
    private ThumbnailImageLoader mThumbnailLoader;

    /**
     * Constructor
     * @param view          View to hold
     * @param clickHandler  onClick handler for view
     */
    public MovieInfoViewHolder(View view, MovieInfoAdapter.MovieInfoAdapterOnClickHandler clickHandler) {
        super(view);

        mView = view;
        mClickHandler = clickHandler;

        mPosterImageView = view.findViewById(R.id.iv_poster_movie_info_list_item);
        mPosterNaTextView = view.findViewById(R.id.tv_poster_na_movie_info_list_item);
        mIndexTextView = view.findViewById(R.id.tv_index_movie_info_list_item);

        view.setOnClickListener(this);
    }

    /**
     * Get a context reference
     * @return  context reference
     */
    public Context getContext() {
        return mView.getContext();
    }

    /**
     * Set the movie details to display
     * @param model Movie object to use
     */
    void setViewInfo(T model) {
        Context context = getContext();
        int posterNaVisibility = View.INVISIBLE;

        // request poster image
        mPosterLoader = new PosterImageLoader(mPosterImageView);
        Uri uri = mPosterLoader.getImageUri(context, model);
        if (uri != null) {
            model.setPosterUri(uri);
            mPosterLoader.loadImage(context, model);
        } else {
            String msg = model.getTitle();
            if (!TextUtils.isEmpty(msg)) {
                posterNaVisibility = View.VISIBLE;
                msg = MessageFormat.format(getContext().getString(R.string.movie_info_na), msg);
                mPosterNaTextView.setText(msg);
            }

            mPosterLoader.loadImage(context, R.drawable.no_image_available);
        }
        mPosterNaTextView.setVisibility(posterNaVisibility);

        // set index number
        boolean show = PreferenceControl.getShowPositionPreference(context);
        if (show) {
            mIndexTextView.setText(String.valueOf(model.getIndex()));
            mIndexTextView.setVisibility(View.VISIBLE);
        } else {
            mIndexTextView.setVisibility(View.GONE);
        }

        // request thumbnail image
        mThumbnailLoader = new ThumbnailImageLoader();
        uri = mThumbnailLoader.getImageUri(context, model);
        if (uri != null) {
            model.setThumbnailUri(uri);
            mThumbnailLoader.fetchImage(context, model);
        } else {
            mThumbnailLoader = null;   // can't load without uri
        }
    }

    @Override
    public void onClick(View v) {
        // pass the click onto the click handler
        mClickHandler.onItemClick(v);
    }

    /**
     * Cancel any in progress loading
     */
    public void cancel() {
        if (mPosterLoader != null) {
            mPosterLoader.cancelImageLoad();
        }
        if (mThumbnailLoader != null) {
            mThumbnailLoader.cancelImageLoad();
        }
    }

}
