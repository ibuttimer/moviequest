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
package ie.ianbuttimer.moviequest.image;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;

/**
 * Image loading utility class
 */
public class BackdropImageLoader extends ImageLoader implements Callback {

    /**
     * Default constructor
     */
    public BackdropImageLoader() {
        super();
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     * @param progressBar   In progress bar
     */
    @SuppressWarnings("unused")
    public BackdropImageLoader(ImageView imageView, ProgressBar progressBar) {
        super(imageView, progressBar);
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     */
    @SuppressWarnings("unused")
    public BackdropImageLoader(ImageView imageView) {
        super(imageView);
    }

    @Override
    public String getImageSize(Context context, MovieInfo movie) {
        return PreferenceControl.getBackdropSizePreference(context);
    }

    @Override
    public String getImagePath(MovieInfo movie) {
        return movie.getBackdropPath();
    }

}
