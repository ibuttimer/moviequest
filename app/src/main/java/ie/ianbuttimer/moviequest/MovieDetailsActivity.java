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

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import ie.ianbuttimer.moviequest.tmdb.CollectionInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieDetails;
import ie.ianbuttimer.moviequest.utils.Dialog;
import ie.ianbuttimer.moviequest.utils.ImageLoader;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;
import ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils;
import ie.ianbuttimer.moviequest.utils.Utils;

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
        R.id.tv_banner_title_movie_detailsA
    };
    // ids of text views that are populated from movie details info returned from server
    private static final int[] mTextViewDetailsIds = new int[] {
        R.id.tv_runningtime_moviedetailsA, R.id.tv_genres_moviedetailsA,
        R.id.tv_homepage_moviedetailsA, R.id.tv_revenue_moviedetailsA,
        R.id.tv_collection_moviedetailsA
    };

    private MovieInfo mMovie;
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

        /* set the backdrop size depending on screen size
            its setup as a preference (but not currently shown in settings), so save as preference
         */
        String[] array = getResources().getStringArray(R.array.pref_backdrop_size_values);
        int sizeIndex = -1;
        if (Utils.isXLargeScreen((this))) {
            sizeIndex = 2;
        } else if (Utils.isLargeScreen((this))) {
            sizeIndex = 1;
        } else {
            sizeIndex = 0;
        }
        if ((sizeIndex >= 0) && (sizeIndex < array.length)) {
            PreferenceControl.setSharedStringPreference(this, R.string.pref_backdrop_size_key, array[sizeIndex]);
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
                    default:
                        text = "";
                        break;
                }
                setTextViewText(id, text);
            }

            RatingBar rb = (RatingBar) findViewById(R.id.rb_rating_moviedetailsA);
            rb.setRating(new Float(movie.getVoteAverage().floatValue()));

            // load background
            backdropLoader = getImageLoader(R.id.iv_background_movie_detailsA, R.id.pb_banner_movie_detailsA);
            backdropLoader.loadBackdropImage(this, movie);

            // load thumbnail
            thumbnailLoader = getImageLoader(R.id.iv_movie_thumbnail_detailsA, R.id.pb_thumbnail_movie_detailsA);
            String size = getString(R.string.pref_thumbnail_size);
            thumbnailLoader.loadPosterImage(this, size, movie);
        }
    }

    /**
     * Display info available only in movie details
     */
    private void setDetails() {
        if (mDetails != null) {
            for (int id : mTextViewDetailsIds) {
                boolean set = true;
                String text = "";
                switch (id) {
                    case R.id.tv_runningtime_moviedetailsA:
                        text = MessageFormat.format(getString(R.string.movie_running_time),
                                mDetails.getRuntime().toString());
                        break;
                    case R.id.tv_genres_moviedetailsA:
                        text = Arrays.toString(mDetails.getGenreNames());
                        text = text.substring(1, text.length() - 1);    // drop braces
                        break;
                    case R.id.tv_homepage_moviedetailsA:
                        text = mDetails.getHomepage();
                        break;
                    case R.id.tv_revenue_moviedetailsA:
                        text = mDetails.getRevenueFormatted();
                        break;
                    case R.id.tv_collection_moviedetailsA:
                        LinearLayout ll = (LinearLayout) findViewById(R.id.ll_collection_moviedetailsA);
                        CollectionInfo collection = mDetails.getCollection();
                        set = !CollectionInfo.isEmpty(collection);
                        if (set) {
                            text = collection.getName();
                            ll.setVisibility(View.VISIBLE);
                        } else {
                            ll.setVisibility(View.GONE);
                        }
                        break;
                    default:
                        text = "";
                        break;
                }
                if (set) {
                    setTextViewText(id, text);
                }
            }
        }
    }

    private void setInfoAndDetails() {
        if (mMovie == null) {
            setInfo(mDetails);
        }
        setDetails();
    }


    private void setTextViewText(int id, String text) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(text);
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
        new GetMovieDetails().execute(TMDbNetworkUtils.buildGetDetailsUrl(this, id));
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

    private ImageLoader getImageLoader(int viewId, int barId) {
        ImageView iv = (ImageView) findViewById(viewId);
        ProgressBar pb = null;
        if (barId > 0) {
            pb = (ProgressBar) findViewById(barId);
        }
        return new ImageLoader(iv, pb);
    }


    /**
     * AsyncTask to request movies
     */
    class GetMovieDetails extends AsyncTask<URL, Void, MovieDetails> {

        @Override
        protected MovieDetails doInBackground(URL... params) {

            /* If there's no url, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(params[0]);

                return MovieDetails.getInstance(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(MovieDetails response) {
            if (response == null) {
                showNoResponse();
            }
            mDetails = response;
            setInfoAndDetails();
        }

    }

    /**
     * Show no response dialog
     */
    private void showNoResponse() {
        Dialog.showNoResponseDialog(this);
    }

}
