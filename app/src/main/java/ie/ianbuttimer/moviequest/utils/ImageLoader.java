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
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;

/**
 * Image loading utility class
 */
public class ImageLoader implements Callback {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Callback callback;
    private Context context;

    /**
     * Constructor
     * @param imageView     ImageView to load into
     * @param progressBar   In progress bar
     */
    public ImageLoader(ImageView imageView, ProgressBar progressBar) {
        init();
        this.imageView = imageView;
        this.progressBar = progressBar;
    }

    /**
     * Initialise internal state
     */
    private void init() {
        this.imageView = null;
        this.progressBar = null;
        this.callback = null;
        this.context = null;
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     */
    public ImageLoader(ImageView imageView) {
        this(imageView, null);
    }

    /**
     * Loads a poster image into an ImageView
     * @param context   The current context
     * @param movie     Movie to get poster for
     * @param callback  Callback to invoke when finished
     */
    public void loadPosterImage(Context context, MovieInfo movie, Callback callback) {
        String size = PreferenceControl.getSharedStringPreference(context,
                R.string.pref_poster_size_key, R.string.pref_poster_size_dlft_value);
        loadPosterImage(context, size, movie, callback);
    }

    /**
     * Loads a poster image into an ImageView
     * @param context   The current context
     * @param movie     Movie to get poster for
     */
    public void loadPosterImage(Context context, MovieInfo movie) {
        loadPosterImage(context, movie, null);
    }

    /**
     * Loads a poster image into an ImageView
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get poster for
     * @param callback  Callback to invoke when finished
     */
    public void loadPosterImage(Context context, String size, MovieInfo movie, Callback callback) {
        Uri uri = TMDbNetworkUtils.buildGetImageUri(context, size, movie.getPosterPath());
        start(context, callback);
        Utils.loadImage(context, uri, imageView, this);
    }

    /**
     * Loads a poster image into an ImageView
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get poster for
     */
    public void loadPosterImage(Context context, String size, MovieInfo movie) {
        loadPosterImage(context, size, movie, null);
    }

    /**
     * Loads a backdrop image into an ImageView
     * @param context   The current context
     * @param movie     Movie to get backdrop for
     * @param callback  Callback to invoke when finished
     */
    public void loadBackdropImage(Context context, MovieInfo movie, Callback callback) {
        String size = PreferenceControl.getSharedStringPreference(context,
                R.string.pref_backdrop_size_key, R.string.pref_backdrop_size_dlft_value);
        loadBackdropImage(context, size, movie, callback);
    }

    /**
     * Loads a backdrop image into an ImageView
     * @param context   The current context
     * @param movie     Movie to get backdrop for
     */
    public void loadBackdropImage(Context context, MovieInfo movie) {
        loadBackdropImage(context, movie, null);
    }

    /**
     * Loads a backdrop image into an ImageView
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get backdrop for
     * @param callback  Callback to invoke when finished
     */
    public void loadBackdropImage(Context context, String size, MovieInfo movie, Callback callback) {
        Uri uri = TMDbNetworkUtils.buildGetImageUri(context, size, movie.getBackdropPath());
        start(context, callback);
        Utils.loadImage(context, uri, imageView, this);
    }

    /**
     * Cancel any existing requests for the target ImageView
     */
    public void cancelImageLoad() {
        Utils.cancelImageLoad(context, imageView);
        end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onSuccess() {
        if (callback != null) {
            callback.onSuccess();
        }
        end();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onError() {
        if (callback != null) {
            callback.onError();
        }
        end();
    }

    /**
     * Start download
     * @param context   The current context
     * @param callback  Picasso callback
     */
    private void start(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        showProgress();
    }

    /**
     * End download
     */
    private void end() {
        hideProgress();;
        init();
    }

    /**
     * Show progress indicator
     */
    private void showProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide progress indicator
     */
    private void hideProgress() {
        if (progressBar != null) {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
