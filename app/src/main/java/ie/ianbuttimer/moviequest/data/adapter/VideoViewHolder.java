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
import ie.ianbuttimer.moviequest.tmdb.video.Video;

/**
 * A RecyclerView.ViewHolder for Video objects
 */

public class VideoViewHolder extends AbstractTMDbViewHolder<Video> {

    private final TextView mNameTextView;
    private final TextView mSiteTextView;

    /**
     * Constructor
     * @param view          View to hold
     * @param clickHandler  onClick handler for view
     */
    public VideoViewHolder(View view, IAdapterOnClickHandler clickHandler) {
        super(view, clickHandler);

        mNameTextView = view.findViewById(R.id.tv_name_movie_video_list_item);
        mSiteTextView = view.findViewById(R.id.tv_site_movie_video_list_item);
    }


    @Override
    public void setViewInfo(Video info) {
        mNameTextView.setText(info.getName());
        mSiteTextView.setText(info.getSite());
    }
}
