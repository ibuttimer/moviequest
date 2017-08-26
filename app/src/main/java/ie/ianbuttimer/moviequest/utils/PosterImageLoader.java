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
package ie.ianbuttimer.moviequest.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;

/**
 * Image loading utility class
 */
public class PosterImageLoader extends ImageLoader implements Callback {

    /**
     * Default constructor
     */
    public PosterImageLoader() {
        super();
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     * @param progressBar   In progress bar
     */
    public PosterImageLoader(ImageView imageView, ProgressBar progressBar) {
        super(imageView, progressBar);
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     */
    public PosterImageLoader(ImageView imageView) {
        super(imageView);
    }

    @Override
    public String getImageSize(Context context, MovieInfo movie) {
        return PreferenceControl.getSharedStringPreference(context,
                R.string.pref_poster_size_key, R.string.pref_poster_size_dlft_value);
    }

    @Override
    public String getImagePath(MovieInfo movie) {
        return movie.getPosterPath();
    }

}
