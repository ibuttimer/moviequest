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
package ie.ianbuttimer.moviequest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import ie.ianbuttimer.moviequest.tmdb.CollectionInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieDetails;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.utils.AsyncCallback;
import ie.ianbuttimer.moviequest.utils.BackdropImageLoader;
import ie.ianbuttimer.moviequest.utils.ICallback;
import ie.ianbuttimer.moviequest.utils.ImageLoader;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import ie.ianbuttimer.moviequest.utils.PicassoUtil;
import ie.ianbuttimer.moviequest.utils.ResponseHandler;
import ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils;
import ie.ianbuttimer.moviequest.utils.ThumbnailImageLoader;
import okhttp3.Call;
import okhttp3.Response;

import static ie.ianbuttimer.moviequest.Constants.MOVIE_DETAIL_OBJ;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_ID;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_OBJ;

/**
 * Activity to display the details for a movie
 */
public class MovieDetailsActivity extends AppCompatActivity {

    // ids of text views that can be populated from movie info provided in intent
    private static final int[] mTextViewIds = new int[] {
        R.id.tv_year_moviedetailsA, R.id.tv_plot_moviedetailsA,
        R.id.tv_releasedate_moviedetailsA, R.id.tv_rating_moviedetailsA,
        R.id.tv_banner_title_movie_detailsA, R.id.tv_originaltitle_moviedetailsA

    };
    // ids of text views that are populated from movie details info returned from server
    private static final int[] mTextViewDetailsIds = new int[] {
        R.id.tv_runningtime_moviedetailsA, R.id.tv_genres_moviedetailsA,
        R.id.tv_homepage_moviedetailsA, R.id.tv_revenue_moviedetailsA,
        R.id.tv_collection_moviedetailsA, R.id.tv_originallang_moviedetailsA,
        R.id.tv_budget_moviedetailsA, R.id.tv_languages_moviedetailsA,
        R.id.tv_companies_moviedetailsA, R.id.tv_countries_moviedetailsA,
        R.id.tv_tagline_movie_detailsA
    };

    private MovieInfoModel mMovie;
    private ImageLoader backdropLoader;
    private ImageLoader thumbnailLoader;

    private MovieDetails mDetails = null;
    private int movieId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        if (savedInstanceState != null) {
            // get movie object from saved bundle
            mMovie = savedInstanceState.getParcelable(MOVIE_OBJ);
            mDetails = savedInstanceState.getParcelable(MOVIE_DETAIL_OBJ);
        } else {
            // get movie object from intent
            Intent intent = getIntent();
            if (intent.hasExtra(MOVIE_OBJ)) {
                mMovie = intent.getParcelableExtra(MOVIE_OBJ);
            }
            if (intent.hasExtra(MOVIE_DETAIL_OBJ)) {
                mDetails = intent.getParcelableExtra(MOVIE_DETAIL_OBJ);
            }
            if (intent.hasExtra(MOVIE_ID)) {
                movieId = intent.getIntExtra(MOVIE_ID, 0);
            }
        }

        if (mDetails == null) {
            if (mMovie != null) {
                requestDetails(mMovie);     // request using movie info
                setInfo(mMovie);    // set the info available
            } else if (movieId != 0) {
                requestDetails(movieId);    // request using id
            } else {
                // no movie info so clear text view texts
                for (int id : mTextViewIds) {
                    setTextViewText(id, "");
                }
                for (int id : mTextViewDetailsIds) {
                    setTextViewText(id, "");
                }
            }
        } else {
            // set text view texts
            setInfoAndDetails();
        }
    }

    /**
     * Display info available in both movie info & details
     */
    private void setInfo(MovieInfo movie) {
        if (movie != null) {
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");

            for (int id : mTextViewIds) {
                String text;
                switch (id) {
                    case R.id.tv_year_moviedetailsA:
                        text = sdf.format(movie.getReleaseDate());
                        break;
                    case R.id.tv_banner_title_movie_detailsA:
                        text = movie.getTitle();
                        break;
                    case R.id.tv_plot_moviedetailsA:
                        text = movie.getOverview();
                        break;
                    case R.id.tv_releasedate_moviedetailsA:
                        text = df.format(movie.getReleaseDate());
                        break;
                    case R.id.tv_rating_moviedetailsA:
                        text = MessageFormat.format(getString(R.string.movie_num_stars),
                                movie.getVoteAverage().toString());
                        break;
                    case R.id.tv_originaltitle_moviedetailsA:
                        text = movie.getOriginalTitle();
                        break;
                    default:
                        text = "";
                        break;
                }
                setTextViewText(id, text);
            }

            RatingBar rb = (RatingBar) findViewById(R.id.rb_rating_moviedetailsA);
            rb.setRating(new Float(movie.getVoteAverage().floatValue()));

            getImages(movie);
        }
    }

    /**
     * Download & display images
     */
    private void getImages(MovieInfo movie) {
        if (movie != null) {
            boolean haveNetwork = NetworkUtils.isInternetAvailable(this);
            Bitmap image;

            // load background
            image = PicassoUtil.getImage(mMovie.getBackdropUri());
            if (image == null) {
                if (haveNetwork) {
                    backdropLoader = setImageLoader(new BackdropImageLoader(), R.id.iv_background_movie_detailsA, R.id.pb_banner_movie_detailsA);
                    backdropLoader.loadImage(this, movie);
                }
            } else {
                setImageViewImage(R.id.iv_background_movie_detailsA, image);
            }
            // load thumbnail
            image = PicassoUtil.getImage(mMovie.getThumbnailUri());
            if (image == null) {
                if (haveNetwork) {
                    thumbnailLoader = setImageLoader(new ThumbnailImageLoader(), R.id.iv_movie_thumbnail_detailsA, R.id.pb_thumbnail_movie_detailsA);
                    String size = getString(R.string.pref_thumbnail_size_dlft_value);
                    thumbnailLoader.loadImage(this, size, movie);
                }
            } else {
                setImageViewImage(R.id.iv_movie_thumbnail_detailsA, image);
            }
        }
    }

    /**
     * Display info available only in movie details
     */
    private void setDetails() {
        if ((mDetails != null) && !mDetails.isEmpty()) {
            for (int id : mTextViewDetailsIds) {
                boolean set = true;
                boolean hide = false;
                String text = "";
                switch (id) {
                    case R.id.tv_runningtime_moviedetailsA:
                        text = MessageFormat.format(getString(R.string.movie_running_time),
                                mDetails.getRuntime().toString());
                        break;
                    case R.id.tv_genres_moviedetailsA:
                        text = listString(mDetails.getGenreNames(), null);
                        break;
                    case R.id.tv_languages_moviedetailsA:
                        text = listString(mDetails.getSpokenLanguageNames(), null);
                        break;
                    case R.id.tv_companies_moviedetailsA:
                        text = listString(mDetails.getProductionCompaniesNames(), "\n");
                        break;
                    case R.id.tv_countries_moviedetailsA:
                        text = listString(mDetails.getProductionCountriesNames(), "\n");
                        break;
                    case R.id.tv_homepage_moviedetailsA:
                        text = mDetails.getHomepage();
                        break;
                    case R.id.tv_revenue_moviedetailsA:
                        text = monetaryString(mDetails.getRevenue(), mDetails.getRevenueFormatted());
                        break;
                    case R.id.tv_budget_moviedetailsA:
                        text = monetaryString(mDetails.getBudget(), mDetails.getBudgetFormatted());
                        break;
                    case R.id.tv_collection_moviedetailsA:
                        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_collection_moviedetailsA);
                        CollectionInfo collection = mDetails.getCollection();
                        set = !CollectionInfo.isEmpty(collection);
                        if (set) {
                            text = collection.getName();
                            ll.setVisibility(View.VISIBLE);
                        } else {
                            ll.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case R.id.tv_originallang_moviedetailsA:
                        text = mDetails.getOriginalLanguageName();
                        break;
                    case R.id.tv_tagline_movie_detailsA:
                        text = mDetails.getTagline();
                        hide = text.equals("");
                        set = !hide;
                        break;
                    default:
                        text = "";
                        break;
                }
                if (set) {
                    setTextViewText(id, text);
                } else if (hide) {
                    setTextViewVisibility(id, View.GONE);
                }
            }
        }
    }

    /**
     * Return formatted monetary amount or unavailable text
     * @param amount    Monetary amount
     * @param formatted Formatted amount
     * @return  Monetary text to display
     */
    private String monetaryString(int amount, String formatted) {
        String text;
        if (amount > 0) {
            text = formatted;
        } else {
            text = getString(R.string.unavailable);
        }
        return text;
    }

    /**
     * Convert a String array to text
     * @param list      Array to convert
     * @param separator Item separator
     * @return  Text to display
     */
    private String listString(String[] list, String separator) {
        String text = "";
        if (separator == null) {
            // default array to string comma separated list
            text = Arrays.toString(list);
            text = text.substring(1, text.length() - 1);    // drop braces
        } else {
            int limit = list.length - 1;
            for (int i = 0; i < limit; i++) {
                text += list[i] + separator;
            }
            if (limit >= 0) {
                text += list[list.length - 1];
            }
        }
        return text;
    }

    /**
     * Set the basic movie info and details
     */
    private void setInfoAndDetails() {
        if (mMovie == null) {
            setInfo(mDetails);
        } else {
            setInfo(mMovie);
        }
        setDetails();
    }

    /**
     * Set the text for a TextView
     * @param id    Id of TextView
     * @param text  Text to display
     */
    private void setTextViewText(int id, String text) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(text);
    }

    /**
     * Set a TextView visibility
     * @param id            Id of TextView
     * @param visibility    View visibility
     */
    private void setTextViewVisibility(int id, int visibility) {
        TextView tv = (TextView) findViewById(id);
        tv.setVisibility(visibility);
    }

    /**
     * Set the image for an ImageView
     * @param id    Id of TextView
     * @param image Image to display
     */
    private void setImageViewImage(int id, Bitmap image) {
        if (image != null) {
            ImageView iv = (ImageView) findViewById(R.id.iv_movie_thumbnail_detailsA);
            iv.setImageBitmap(image);
        }
    }

    /**
     * Request the movies
     */
    private void requestDetails(MovieInfo mMovie) {
        requestDetails(mMovie.getId());
    }

    /**
     * Request the movies
     */
    private void requestDetails(int id) {
        responseHandler.request(TMDbNetworkUtils.buildGetDetailsUrl(this, id));
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(MOVIE_OBJ, mMovie);
        outState.putParcelable(MOVIE_DETAIL_OBJ, mDetails);
    }


    @Override
    protected void onDestroy() {
        if (backdropLoader != null) {
            backdropLoader.cancelImageLoad();
        }
        if (thumbnailLoader != null) {
            thumbnailLoader.cancelImageLoad();
        }
        super.onDestroy();
    }

    /**
     * Set the ImageView and ProgressBar for the specified loader
     * @param loader    Loader to update
     * @param viewId    Resource id of ImageView
     * @param barId     Resource id of ProgressBar or <code>0</code> to not set
     * @return  Loader
     */
    private ImageLoader setImageLoader(ImageLoader loader, int viewId, int barId) {
        loader.setImageView((ImageView) findViewById(viewId));
        if (barId > 0) {
            loader.setProgressBar((ProgressBar) findViewById(barId));
        }
        return loader;
    }

    /**
     * Asynchronous request and response handler
     */
    private ICallback responseHandler = new AsyncCallback() {

        @Override
        public void onFailure(Call call, IOException e) {
            super.onFailure(call, e);
            onMovieResponse(new MovieDetails(), getErrorId(call, e));
        }

        @Override
        public void onResponse(Object result) {
            MovieDetails response = null;
            if (result != null) {
                response = (MovieDetails) result;
            }
            onMovieResponse(response, 0);
        }

        /**
         * Convert the http response into a MovieDetails object
         * @param response  Response from the server
         * @return
         */
        @Override
        public Object processResponse(Response response) {
            Object result = null;
            try {
                String jsonResponse = response.body().string();
                result = MovieDetails.getInstance(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    };

    /**
     * Class to update the ui with response details
     */
    private class MovieResponseHandler extends ResponseHandler<MovieDetails> implements Runnable {

        public MovieResponseHandler(Activity activity, MovieDetails response, int errorId) {
            super(activity, response, errorId);
        }

        @Override
        public void run() {
            super.run();

            mDetails = getResponse();
            setInfoAndDetails();
        }
    }

    /**
     * Handle a list response
     * @param response  Response object
     */
    protected void onMovieResponse(MovieDetails response, int msgId) {
        // ui updates need to be on ui thread
        MovieDetailsActivity.this.runOnUiThread(new MovieResponseHandler(MovieDetailsActivity.this, response, msgId));
    }

}
