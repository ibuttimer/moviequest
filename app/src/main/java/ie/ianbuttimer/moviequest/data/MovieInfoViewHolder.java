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
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.utils.ImageLoader;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;


/**
 * A RecyclerView.ViewHolder for MovieInfo objects
 */

class MovieInfoViewHolder<T extends MovieInfo> extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected final View mView;
    protected final TextView mIndexTextView;
    protected final ImageView mPosterImageView;
    private MovieInfoAdapter.MovieInfoAdapterOnClickHandler mClickHandler;

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
     * @param movie Movie object to use
     */
    public void setViewInfo(T movie) {
        Context context = getContext();

        ImageLoader imageLoader = new ImageLoader(mPosterImageView);
        imageLoader.loadPosterImage(context, movie);

        boolean show = PreferenceControl.getSharedBooleanPreference(
                context, R.string.pref_show_position_key, R.bool.pref_show_position_dflt_value);
        if (show) {
            // YUCK!!!!!
            try {
                MovieInfoModel model = MovieInfoModel.class.cast(movie);
                mIndexTextView.setText(String.valueOf(model.getIndex()));
                mIndexTextView.setVisibility(View.VISIBLE);
            }
            catch (ClassCastException e) {
                mIndexTextView.setVisibility(View.GONE);
            }
        } else {
            mIndexTextView.setVisibility(View.GONE);
        }

    }

    @Override
    public void onClick(View v) {
        // pass the click onto the click handler
        mClickHandler.onItemClick(v);
    }
}
