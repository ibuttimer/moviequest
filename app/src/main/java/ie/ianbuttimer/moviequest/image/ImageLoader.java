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
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils;

/**
 * Image loading utility class
 */
@SuppressWarnings("unused")
public abstract class ImageLoader implements Callback {

    private ImageView imageView;
    private ProgressBar progressBar;
    private Callback callback;
    private Context context;
    private String tag;             // tag used in image cache

    /**
     * Default constructor
     */
    public ImageLoader() {
        init();
    }

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
        this.tag = "";
    }

    /**
     * Constructor
     * @param imageView     ImageView to load into
     */
    public ImageLoader(ImageView imageView) {
        this(imageView, null);
    }

    /**
     * Get an image Uri
     * @param context   The current context
     * @param size      Image size to request
     * @return The URI to use to query the server, or <code>null</code> if not able build
     */
    public static @Nullable Uri getImageUri(Context context, String size, String path) {
		return TMDbNetworkUtils.buildGetImageUri(context, size, path);
    }

    /**
     * Get a image Uri
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get Uri for
     * @return The URI to use to query the server, or <code>null</code> if not able build
     */
    public @Nullable Uri getImageUri(Context context, String size, MovieInfo movie) {
        return getImageUri(context, size, getImagePath(movie));
    }

    /**
     * Get a image Uri
     * @param context   The current context
     * @param movie     Movie to get Uri for
     * @return The URI to use to query the server, or <code>null</code> if not able build
     */
    public @Nullable Uri getImageUri(Context context, MovieInfo movie) {
        return getImageUri(context, getImageSize(context, movie), movie);
    }

   /**
     * Get a image size
     * @param context   The current context
     * @param movie     Movie to get size for
     */
    public abstract String getImageSize(Context context, MovieInfo movie);

    /**
     * Get a image path
     * @param movie     Movie to get path for
     */
    public abstract String getImagePath(MovieInfo movie);

    /**
     * Return the ImageView for this object
     * @return  ImageView object
     */
    public ImageView getImageView() {
        return imageView;
    }

    /**
     * Set the ImageView for this object
     * @param imageView ImageView to set
     */
    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    /**
     * Return the ProgressBar for this object
     * @return ProgressBar object
     */
    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * Set the ProgressBarfor this object
     * @param progressBar   ProgressBar to set
     */
    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param movie     Movie to get image for
     * @param callback  Callback to invoke when finished
     * @return Request tag
     */
    public String loadImage(Context context, MovieInfo movie, Callback callback) {
        return loadImage(context, getImageSize(context, movie), movie, callback);
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param movie     Movie to get image for
     * @return Request tag
     */
    public String loadImage(Context context, MovieInfo movie) {
        return loadImage(context, movie, null);
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get image for
     * @param callback  Callback to invoke when finished
     * @return Request tag
     */
    public String loadImage(Context context, String size, MovieInfo movie, Callback callback) {
        Uri uri = getImageUri(context, size, movie);
        if (uri != null) {
            start(context, callback);
            tag = PicassoUtil.loadImage(context, uri, imageView, this);
        } else {
            tag = "";
        }
        return tag;
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get image for
     * @return Request tag
     */
    public String loadImage(Context context, String size, MovieInfo movie) {
        return loadImage(context, size, movie, null);
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param resourceId    Drawable resource ID
     * @param callback  Callback to invoke when finished
     * @return Request tag
     */
    public String loadImage(Context context, @DrawableRes int resourceId, Callback callback) {
        start(context, callback);
        tag = PicassoUtil.loadImage(context, resourceId, imageView, this);
        return tag;
    }

    /**
     * Loads a image into an ImageView
     * @param context   The current context
     * @param resourceId    Drawable resource ID
     * @return Request tag
     */
    public String loadImage(Context context, @DrawableRes int resourceId) {
        return loadImage(context, resourceId, null);
    }

    /**
     * Fetches a image
     * @param context   The current context
     * @param movie     Movie to get image for
     * @param callback  Callback to invoke when finished
     * @return Request tag
     */
    public String fetchImage(Context context, MovieInfo movie, Callback callback) {
        return fetchImage(context, getImageSize(context, movie), movie, callback);
    }

    /**
     * Fetches a image
     * @param context   The current context
     * @param movie     Movie to get image for
     * @return Request tag
     */
    public String fetchImage(Context context, MovieInfo movie) {
        return fetchImage(context, movie, null);
    }

    /**
     * Fetches a image
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get image for
     * @param callback  Callback to invoke when finished
     * @return Request tag
     */
    public String fetchImage(Context context, String size, MovieInfo movie, Callback callback) {
        Uri uri = getImageUri(context, size, movie);
        start(context, callback);
        tag = PicassoUtil.fetchImage(context, uri, this);
        return tag;
    }

    /**
     * Fetches a image
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get image for
     * @return Request tag
     */
    public String fetchImage(Context context, String size, MovieInfo movie) {
        return fetchImage(context, size, movie, null);
    }

    /**
     * Gets an image from cache
     * @param context   The current context
     * @param movie     Movie to get image for
     * @return Request tag
     */
    public Bitmap getImage(Context context, MovieInfo movie) {
        Uri uri = getImageUri(context, movie);
        return PicassoUtil.getImage(uri);
    }

    /**
     * Gets an image from cache
     * @param context   The current context
     * @param size      Image size to request
     * @param movie     Movie to get image for
     * @return Image or <code>null</code> if not available
     */
    public Bitmap getImage(Context context, String size, MovieInfo movie) {
        Uri uri = getImageUri(context, size, movie);
        return PicassoUtil.getImage(uri);
    }

    /**
     * Cancel any existing requests for the target ImageView
     */
    public void cancelImageLoad() {
        PicassoUtil.cancelImageLoad(context, imageView);
        end();
    }

    /**
     * Cancel any existing requests for the request tag
     */
    public void cancel() {
        PicassoUtil.cancelTag(context, tag);
        end();
    }

    /**
     * Pause any existing requests for the request tag
     */
    public void pause() {
        PicassoUtil.pauseTag(context, tag);
    }

    /**
     * Resume any existing requests for the request tag
     */
    public void resume() {
        PicassoUtil.resumeTag(context, tag);
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
        hideProgress();
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

    /**
     * Get the tag for this object
     * @return  tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * Get the tag for this object
     * @param tag   Tag to set
     */
    public void setTag(String tag) {
        this.tag = tag;
    }
}
