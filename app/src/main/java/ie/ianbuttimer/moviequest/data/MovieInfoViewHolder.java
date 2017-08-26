/**
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
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.utils.PosterImageLoader;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;
import ie.ianbuttimer.moviequest.utils.ThumbnailImageLoader;


/**
 * A RecyclerView.ViewHolder for MovieInfo objects
 */

class MovieInfoViewHolder<T extends MovieInfoModel> extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected final View mView;
    protected final TextView mIndexTextView;
    protected final ImageView mPosterImageView;
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

        mPosterImageView = (ImageView) view.findViewById(R.id.iv_poster_movie_info_list_item);
        mIndexTextView = (TextView) view.findViewById(R.id.tv_index_movie_info_list_item);

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
    public void setViewInfo(T model) {
        Context context = getContext();

        // request poster image
        mPosterLoader = new PosterImageLoader(mPosterImageView);
        model.setPosterUri(mPosterLoader.getImageUri(context, model));
        mPosterLoader.loadImage(context, model);

        // set index number
        boolean show = PreferenceControl.getSharedBooleanPreference(
                context, R.string.pref_show_position_key, R.bool.pref_show_position_dflt_value);
        if (show) {
            mIndexTextView.setText(String.valueOf(model.getIndex()));
            mIndexTextView.setVisibility(View.VISIBLE);
        } else {
            mIndexTextView.setVisibility(View.GONE);
        }

        // request thumbnail image
        mThumbnailLoader = new ThumbnailImageLoader();
        model.setThumbnailUri(mThumbnailLoader.getImageUri(context, model));
        mThumbnailLoader.fetchImage(context, model);
    }

    @Override
    public void onClick(View v) {
        // pass the click onto the click handler
        mClickHandler.onItemClick(v);
    }

    public void cancel() {
        if (mThumbnailLoader != null) {
            mThumbnailLoader.cancelImageLoad();
        }
    }

}
