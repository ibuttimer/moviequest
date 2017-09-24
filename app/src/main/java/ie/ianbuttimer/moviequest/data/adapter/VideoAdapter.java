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


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.List;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.data.IAdapterOnClickHandler;
import ie.ianbuttimer.moviequest.tmdb.TMDbObject;
import ie.ianbuttimer.moviequest.tmdb.video.Video;

/**
 * Adapter class for a RecyclerView of videos
 */

public class VideoAdapter extends AbstractTMDbRecycleViewAdapter<Video> {

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     * @param clickHandler  Click handler for the views in this adapter
     */
    public VideoAdapter(@NonNull List<Video> objects, @Nullable IAdapterOnClickHandler clickHandler) {
        super(objects, clickHandler, R.layout.movie_video_list_item);
    }

    /**
     * Constructor
     * @param objects       The objects to represent in the list.
     */
    public VideoAdapter(@NonNull List<Video> objects) {
        this(objects, null);
    }


    @Override
    public AbstractTMDbViewHolder<? extends TMDbObject> getNewViewHolder(View view, IAdapterOnClickHandler clickHandler) {
        return new VideoViewHolder(view, mClickHandler);
    }
}
