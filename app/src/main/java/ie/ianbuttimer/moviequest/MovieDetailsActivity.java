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
package ie.ianbuttimer.moviequest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import ie.ianbuttimer.moviequest.data.DbCacheIntentService;
import ie.ianbuttimer.moviequest.data.IEmpty;
import ie.ianbuttimer.moviequest.data.adapter.ReviewAdapter;
import ie.ianbuttimer.moviequest.data.adapter.VideoAdapter;
import ie.ianbuttimer.moviequest.tmdb.AbstractList;
import ie.ianbuttimer.moviequest.tmdb.CollectionInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieDetails;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.data.AbstractResultWrapper;
import ie.ianbuttimer.moviequest.data.AsyncCallback;
import ie.ianbuttimer.moviequest.image.BackdropImageLoader;
import ie.ianbuttimer.moviequest.data.DbContentValues;
import ie.ianbuttimer.moviequest.tmdb.TMDbObject;
import ie.ianbuttimer.moviequest.tmdb.review.AppendedReviewList;
import ie.ianbuttimer.moviequest.tmdb.review.BaseReview;
import ie.ianbuttimer.moviequest.tmdb.review.MovieReviewList;
import ie.ianbuttimer.moviequest.tmdb.video.MovieVideoList;
import ie.ianbuttimer.moviequest.tmdb.video.Video;
import ie.ianbuttimer.moviequest.utils.AbstractRecyclerViewController;
import ie.ianbuttimer.moviequest.utils.DbUtils;
import ie.ianbuttimer.moviequest.data.FavouritesContentValues;
import ie.ianbuttimer.moviequest.data.ICallback;
import ie.ianbuttimer.moviequest.image.ImageLoader;
import ie.ianbuttimer.moviequest.data.MovieContentValues;
import ie.ianbuttimer.moviequest.utils.Dialog;
import ie.ianbuttimer.moviequest.utils.ITester;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import ie.ianbuttimer.moviequest.image.PicassoUtil;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;
import ie.ianbuttimer.moviequest.utils.ResponseHandler;
import ie.ianbuttimer.moviequest.image.ThumbnailImageLoader;
import ie.ianbuttimer.moviequest.utils.UriUtils;
import ie.ianbuttimer.moviequest.utils.Utils;
import ie.ianbuttimer.moviequest.widgets.TitledProgressBar;
import okhttp3.Call;
import okhttp3.Response;

import static ie.ianbuttimer.moviequest.Constants.INVALID_DATE;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_DETAIL_OBJ;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_ID;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_OBJ;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_TITLE;
import static ie.ianbuttimer.moviequest.data.DbCacheIntentService.CV_EXTRA;
import static ie.ianbuttimer.moviequest.data.DbCacheIntentService.INSERT_OR_UPDATE_FAVOURITE;
import static ie.ianbuttimer.moviequest.data.DbCacheIntentService.INSERT_OR_UPDATE_MOVIE;
import static ie.ianbuttimer.moviequest.data.MovieContentProvider.FAVOURITE_WITH_ID;
import static ie.ianbuttimer.moviequest.data.MovieContentProvider.MOVIE_WITH_ID;
import static ie.ianbuttimer.moviequest.data.MovieContentProvider.MOVIE_WITH_REVIEWS;
import static ie.ianbuttimer.moviequest.data.MovieContentProvider.MOVIE_WITH_VIDEOS;
import static ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry.COLUMN_FAVOURITE;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.APPEND_TO_RESPONSE;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.COLUMN_JSON;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.GET_REVIEWS_METHOD;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.GET_VIDEOS_METHOD;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry._ID;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.GET_DETAILS_METHOD;
import static ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils.APPEND_REVIEWS_VIDEOS;
import static ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils.REVIEW_DETAILS;
import static ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils.VIDEO_DETAILS;
import static ie.ianbuttimer.moviequest.utils.UriUtils.matchMovieUri;

/**
 * Activity to display the details for a movie
 *
 * TODO schedule job to update details cached time
 * TODO update movie details layout; ExpandableListView?, compound views to reduce view count
 */
public class MovieDetailsActivity extends AppCompatActivity /*implements IAdapterOnClickHandler*/ {

    private static final String TAG = MovieDetailsActivity.class.getSimpleName();

    /* Its more efficient to request video/review details appended to movie details response but
        don't do for now as rubric requires separate requests for videos & reviews
    */
    private static final boolean REQUEST_APPENDED_INFO = false;

    private static final int TV_IDX = 0;        // text view id index
    private static final int PORTRAIT_IDX = 1;  // portrait orientation container id index
    private static final int LANDSCAPE_IDX = 2; // landscape orientation container id index

    private int orientationIdx;     // current orientation; PORTRAIT_IDX or LANDSCAPE_IDX

    // ids of items that can be populated from a placeholder movie info object
    private static int[][] mPlaceholderViewContainerIds;
    // ids of items that can be populated from movie info provided in intent
    private static int[][] mInfoViewContainerIds;
    // ids of items that are populated from movie details info returned from server
    private static int[][] mDetailsViewContainerIds;

    static {
        // ids of text views that can be populated from movie info placeholder
        mPlaceholderViewContainerIds = new int[][]{
            // view id                              portrait container id               landscape contained id
            {R.id.tv_banner_title_movie_detailsA, R.id.tv_banner_title_movie_detailsA, R.id.tv_banner_title_movie_detailsA}   // no container
        };

        // ids of text views that can be populated from movie info provided in intent excluding placeholder fields
        int[][] infoNonPlaceholderIds = new int[][]{
            // view id                              portrait container id               landscape contained id
            {R.id.tv_year_moviedetailsA, R.id.tv_year_moviedetailsA, R.id.tv_year_moviedetailsA},   // no container
            {R.id.tv_plot_moviedetailsA, R.id.tv_plot_moviedetailsA, R.id.tv_plot_moviedetailsA},   // no container
            {R.id.tv_releasedate_moviedetailsA, R.id.tr_releasedate_moviedetailsA, R.id.ll_releasedate_moviedetailsA},
            {R.id.tv_rating_moviedetailsA, R.id.tv_rating_moviedetailsA, R.id.tv_rating_moviedetailsA}, // no container
            {R.id.tv_originaltitle_moviedetailsA, R.id.tr_originaltitle_moviedetailsA, R.id.ll_originaltitle_moviedetailsA}
        };

        // ids of text views that can be populated from movie info provided in intent
        mInfoViewContainerIds = new int[mPlaceholderViewContainerIds.length + infoNonPlaceholderIds.length][];
        System.arraycopy(mPlaceholderViewContainerIds, 0, mInfoViewContainerIds, 0, mPlaceholderViewContainerIds.length);
        System.arraycopy(infoNonPlaceholderIds, 0, mInfoViewContainerIds, mPlaceholderViewContainerIds.length, infoNonPlaceholderIds.length);

        // ids of text views that are populated from movie details info returned from server
        mDetailsViewContainerIds = new int[][]{
            // view id                              portrait container id               landscape contained id
            {R.id.tv_runningtime_moviedetailsA, R.id.tv_runningtime_moviedetailsA, R.id.tv_runningtime_moviedetailsA},    // no container
            {R.id.tv_genres_moviedetailsA, R.id.tr_genres_moviedetailsA, R.id.ll_genres_moviedetailsA},
            {R.id.tv_homepage_moviedetailsA, R.id.tr_homepage_moviedetailsA, R.id.ll_homepage_moviedetailsA},
            {R.id.tv_revenue_moviedetailsA, R.id.tr_revenue_moviedetailsA, R.id.ll_revenue_moviedetailsA},
            {R.id.tv_collection_moviedetailsA, R.id.tr_collection_moviedetailsA, R.id.ll_collection_moviedetailsA},
            {R.id.tv_originallang_moviedetailsA, R.id.tr_originallang_moviedetailsA, R.id.ll_originallang_moviedetailsA},
            {R.id.tv_budget_moviedetailsA, R.id.tr_budget_moviedetailsA, R.id.ll_budget_moviedetailsA},
            {R.id.tv_languages_moviedetailsA, R.id.tr_languages_moviedetailsA, R.id.ll_languages_moviedetailsA},
            {R.id.tv_companies_moviedetailsA, R.id.tr_companies_moviedetailsA, R.id.ll_companies_moviedetailsA},
            {R.id.tv_countries_moviedetailsA, R.id.tr_countries_moviedetailsA, R.id.ll_countries_moviedetailsA},
            {R.id.tv_tagline_movie_detailsA, R.id.tv_tagline_movie_detailsA, R.id.tv_tagline_movie_detailsA},   // no container
            {R.id.tv_cache_moviedetailsA, R.id.ll_cache_moviedetailsA, R.id.ll_cache_moviedetailsA}
        };

        // all arrays need to be sorted in ascending order based on view id
        Comparator comparator = new java.util.Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                // return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
                return (a[0] - b[0]);
            }
        };
        Arrays.sort(mPlaceholderViewContainerIds, comparator);
        Arrays.sort(mInfoViewContainerIds, comparator);
        Arrays.sort(mDetailsViewContainerIds, comparator);
    }

    // movie related variables
    private MovieInfoModel mMovie;          // info for movie being displayed
    private MovieDetails mDetails = null;   // detailed for movie being displayed
    private int movieId = 0;                // id of movie being displayed
    private ImageLoader backdropLoader;     // loader for banner backdrop image
    private ImageView backdropImageView;    // banner backdrop image
    private ImageLoader thumbnailLoader;    // loader for thumbnail image
    private boolean mFavourite = false;     // movie favourite status

    // favourite related variables
    private Button mFavouriteButton;        // favourite button
    private ImageView mFavouriteBadge;      // favourite badge

    // refresh related variables
    private ProgressBar mRefreshProgress;   // refresh progress bar
    private Button mRefreshButton;          // refresh button

    // video related variables
    private VideoRecyclerViewController[] videoControllers;
    private TitledProgressBar mVideosProgress;   // video progress bar

    // review related variables
    private ReviewRecyclerViewController reviewController;
    private TitledProgressBar mReviewProgress;   // review progress bar

    private DateFormat mediumDF = DateFormat.getDateInstance(DateFormat.MEDIUM);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String title = null;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        orientationIdx = (Utils.isPotraitScreen(this) ? PORTRAIT_IDX : LANDSCAPE_IDX);

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
            if (intent.hasExtra(MOVIE_TITLE)) {
                title = intent.getStringExtra(MOVIE_TITLE);
            }
        }

        // setup the video recycler view
        int[][] videos = new int[][] {
            // viewID                           containerId
            { R.id.rv_trailer_movies_detailsA, R.id.ll_trailer_moviedetailsA },
            { R.id.rv_teaser_movies_detailsA, R.id.ll_teaser_moviedetailsA },
            { R.id.rv_clip_movies_detailsA, R.id.ll_clip_moviedetailsA },
            { R.id.rv_featurette_movies_detailsA, R.id.ll_featurette_moviedetailsA }
        };
        videoControllers = new VideoRecyclerViewController[videos.length];
        for (int i = 0; i < videos.length; ++i) {
            ITester<Video> tester;
            switch (videos[i][0]) {
                case R.id.rv_trailer_movies_detailsA:
                    tester = Video.IS_TRAILER;
                    break;
                case R.id.rv_teaser_movies_detailsA:
                    tester = Video.IS_TEASER;
                    break;
                case R.id.rv_clip_movies_detailsA:
                    tester = Video.IS_CLIP;
                    break;
                case R.id.rv_featurette_movies_detailsA:
                    tester = Video.IS_FEATURETTE;
                    break;
                default:
                    tester = null;
                    break;
            }
            videoControllers[i] = new VideoRecyclerViewController(this, videos[i][0], videos[i][1], tester);
            videoControllers[i].setAdapter(new VideoAdapter(new ArrayList<Video>()));
        }

        // setup the review recycler view
        reviewController = new ReviewRecyclerViewController(this);
        reviewController.setAdapter(new ReviewAdapter(new ArrayList<BaseReview>()));

        // setup progress bars
        mVideosProgress = (TitledProgressBar) findViewById(R.id.tpb_videos_movie_detailsA);
        mReviewProgress = (TitledProgressBar) findViewById(R.id.tpb_reviews_movie_detailsA);

        if ((mDetails == null) && (mMovie != null)) {
            mDetails = mMovie.getDetails();
        }

        if (mDetails == null) {
            if (mMovie != null) {
                requestDetails(mMovie);     // request using movie info
                setInfo(mMovie);            // set the info available
            } else if (movieId != 0) {
                requestDetails(movieId);    // request using id

                mMovie = new MovieInfoModel(movieId, title);
                setInfo(mMovie);            // set the placeholder info available
            } else {
                // no movie info so clear text view texts
                for (int id[] : mInfoViewContainerIds) {
                    setTextViewText(id[TV_IDX], "");
                }
                for (int id[] : mDetailsViewContainerIds) {
                    setTextViewText(id[TV_IDX], "");
                }
            }
        } else {
            // set text view texts
            setInfoAndDetails();
        }

        // setup refresh button
        mRefreshProgress = (ProgressBar) findViewById(R.id.pb_refresh_moviedetailsA);
        mRefreshButton = (Button) findViewById(R.id.b_refresh_moviedetailsA);
        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestDetailsFromServer(mMovie.getId());
            }
        });

        // setup favourite button
        mFavouriteButton = (Button) findViewById(R.id.b_favourite_moviedetailsA);
        mFavouriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = mMovie.getId();
                boolean toSet = !mMovie.isFavourite();  // toggle current setting
                setFavouriteButton(toSet);

                startDbCacheIntentService(INSERT_OR_UPDATE_FAVOURITE,
                        FavouritesContentValues.builder()
                                .setTitle(mMovie.getTitle())
                                .setFavourite(toSet),
                        id);
            }
        });
        mFavouriteBadge = (ImageView) findViewById(R.id.iv_favourite_moviedetailsA);
    }

    /**
     * Display info available in both movie info & details
     */
    private void setInfo(MovieInfo movie) {
        if (movie != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.US);
            boolean isPlaceholder = movie.isPlaceHolder();
            int[][] ids;

            if (isPlaceholder) {
                ids = mPlaceholderViewContainerIds;
            } else {
                ids = mInfoViewContainerIds;
            }

            for (int[] id : ids) {
                String text;
                switch (id[TV_IDX]) {
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
                        text = mediumDF.format(movie.getReleaseDate());
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
                setTextViewText(id[TV_IDX], text);
            }

            RatingBar rb = (RatingBar) findViewById(R.id.rb_rating_moviedetailsA);
            rb.setRating(movie.getVoteAverage().floatValue());

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
                    backdropImageView = backdropLoader.getImageView();
                    backdropLoader.loadImage(this, movie, new Callback() {
                        @Override
                        public void onSuccess() {
                            backdropImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                            backdropImageView.setAdjustViewBounds(true);
                        }

                        @Override
                        public void onError() {
                        }
                    });
                } else {
                    backdropImageView = (ImageView) findViewById(R.id.iv_background_movie_detailsA);
                }
            } else {
                backdropImageView = setImageViewImage(R.id.iv_background_movie_detailsA, image);
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
            for (int[] row : mDetailsViewContainerIds) {
                int id = row[TV_IDX];
                int containerId = row[orientationIdx];
                boolean set = true;
                boolean hide = false;
                int visibility;
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
                        CollectionInfo collection = mDetails.getCollection();
                        set = !CollectionInfo.isEmpty(collection);
                        if (set) {
                            text = collection.getName();
                            visibility = View.VISIBLE;
                        } else {
                            visibility = View.INVISIBLE;
                        }
                        setViewVisibility(R.id.ll_collection_moviedetailsA, visibility);
                        break;
                    case R.id.tv_originallang_moviedetailsA:
                        text = mDetails.getOriginalLanguageName();
                        break;
                    case R.id.tv_tagline_movie_detailsA:
                        text = mDetails.getTagline();
                        hide = text.equals("");
                        set = !hide;
                        break;
                    case R.id.tv_cache_moviedetailsA:
                        Date cacheDate = mMovie.getCacheDate();
                        hide = Utils.isEmpty(cacheDate);
                        if (!hide) {
                            long cache = cacheDate.getTime();
                            long now = new Date().getTime();
                            if (cache < now) {
                                String elapsed = Utils.getRelativeTimeSpanString(this, cache, now).toLowerCase();
                                text = MessageFormat.format(getString(R.string.movie_cache_details), elapsed);
                            } else {
                                hide = true;    // should never happen but if cached date is in future, don't have reliable info so show nothing
                            }
                        }
                        set = !hide;
                        break;
                    default:
                        text = "";
                        break;
                }
                if (set) {
                    setViewOpacity(containerId, 1f);
                    setTextViewText(id, text);
                } else if (hide) {
                    setViewVisibility(containerId, View.GONE);
                }
            }

            // update video list
            if (!setVideoDetails(mDetails.getMovieVideoList())) {
                // no valid info, request from server
                requestVideosFromServer(mDetails.getId());
            }

            // update review list
            if (!setReviewDetails(mDetails.getReviewList())) {
                // no valid info, request from server
                requestReviewsFromServer(mDetails.getId());
            }
        } else {
            dimUnknown();
        }
    }

    /**
     * Update Video details
     * @param list  List of videos
     */
    private boolean setVideoDetails(MovieVideoList list) {
        boolean valid = false;
        for (VideoRecyclerViewController controller : videoControllers) {
            valid = controller.updateDataSet(list);
        }
        return valid;
    }

    /**
     * Update Review details
     * @param list  List of reviewss
     */
    private boolean setReviewDetails(AppendedReviewList list) {
        return reviewController.updateDataSet(list);
    }

    /**
     * Return formatted monetary amount or unavailable text
     *
     * @param amount    Monetary amount
     * @param formatted Formatted amount
     * @return Monetary text to display
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
     *
     * @param list      Array to convert
     * @param separator Item separator
     * @return Text to display
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
     *
     * @param id   Id of TextView
     * @param text Text to display
     */
    private void setTextViewText(int id, String text) {
        TextView tv = (TextView) findViewById(id);
        tv.setText(text);
        tv.setVisibility(View.VISIBLE);
    }

    /**
     * Set a View's visibility
     *
     * @param id         Id of View
     * @param visibility View visibility
     */
    private void setViewVisibility(int id, int visibility) {
        View view = findViewById(id);
        view.setVisibility(visibility);
    }

    /**
     * Set View's opacity
     *
     * @param ids        Array of view ids
     * @param visibility View visibility
     */
    private void setViewVisibility(int[] ids, int visibility) {
        for (int id : ids) {
            if (id > 0) {
                setViewVisibility(id, visibility);
            }
        }
    }

    /**
     * Set View's opacity
     *
     * @param ids        Array of view ids
     * @param visibility View visibility
     */
    @SuppressWarnings("unused")
    private void setViewVisibility(int[][] ids, int visibility) {
        setViewVisibility(Utils.getArrayColumn(ids, orientationIdx), visibility);
    }

    /**
     * Set a View's opacity
     *
     * @param id    Id of View
     * @param alpha View opacity
     */
    private void setViewOpacity(int id, float alpha) {
        View view = findViewById(id);
        view.setAlpha(alpha);
    }

    /**
     * Set View's opacity
     *
     * @param ids     Array of view ids
     * @param alpha   View opacity
     * @param exclude Array of view ids to exclude
     */
    private void setViewOpacity(int[] ids, float alpha, int[] exclude) {
        int[] sortedExclude = Utils.getSortedArray(exclude);
        for (int id : ids) {
            if (id > 0) {
                if (Arrays.binarySearch(sortedExclude, id) < 0) {
                    setViewOpacity(id, alpha);
                }
            }
        }
    }

    /**
     * Set View's opacity
     *
     * @param ids     Array of view ids
     * @param alpha   View opacity
     * @param exclude Array of view ids to exclude. <b>NOTE:</b> must be TV_IDX index ids!
     */
    private void setViewOpacity(int[][] ids, float alpha, int[] exclude) {
        int[] sortedExclude = Utils.getSortedArray(exclude);
        int[] excludedContainer = null;
        int length = sortedExclude.length;
        if (length > 0) {
            excludedContainer = new int[length];
            Arrays.fill(excludedContainer, 0);
            for (int i = 0; i < length; i++) {
                int index = Utils.binarySearch(ids, TV_IDX, sortedExclude[i]);
                if (index >= 0) {
                    excludedContainer[i] = ids[index][orientationIdx];
                }
            }
        }
        setViewOpacity(Utils.getArrayColumn(ids, orientationIdx), alpha, excludedContainer);
    }

    /**
     * Set the image for an ImageView
     *
     * @param id    Id of ImageView
     * @param image Image to display
     */
    private ImageView setImageViewImage(int id, Bitmap image) {
        ImageView iv = (ImageView) findViewById(id);
        if (image != null) {
            iv.setImageBitmap(image);
        }
        return iv;
    }

    /**
     * Setup favourite button
     *
     * @param isFavourite Favourite flag
     */
    private void setFavouriteButton(boolean isFavourite) {
        @StringRes int textId;
        @DrawableRes int imageId;
        int visibility;
        if (isFavourite) {
            textId = R.string.del_from_favourites;
            imageId = R.drawable.ic_remove_circle_white_18dp;
            visibility = View.VISIBLE;
        } else {
            textId = R.string.add_to_favourites;
            imageId = R.drawable.ic_favorite_white_18dp;
            visibility = View.INVISIBLE;
        }
        mFavouriteButton.setText(textId);
        mFavouriteButton.setCompoundDrawablesWithIntrinsicBounds(imageId, 0, 0, 0);

        mFavouriteBadge.setVisibility(visibility);

        if (mMovie != null) {
            mMovie.setFavourite(isFavourite);
        }
    }

    /**
     * Request the movies
     *
     * @param mMovie MovieInfo object
     */
    private void requestDetails(MovieInfo mMovie) {
        requestDetails(mMovie.getId());
    }

    /**
     * Request the movies
     *
     * @param id Id of movie to request
     */
    private void requestDetails(int id) {
        dimUnknown();

        requestFavouriteDetails(id);

        if (PreferenceControl.getCachePreference(this)) {
            // caching enabled, try database first
            Uri uri = UriUtils.getMovieWithIdUri(id);
            movieDetailsResponseHandler.query(this, matchMovieUri(uri), uri);
        } else {
            // no cache request from server
            requestDetailsFromServer(id);
        }
    }

    /**
     * Dim details currently not available
     */
    private void dimUnknown() {
        setViewOpacity(mDetailsViewContainerIds, 0.5f, new int[]{
                R.id.tv_cache_moviedetailsA     // don't dim cache info
        });  // dim details currently n/a
    }

    /**
     * Show refresh in progress
     */
    private void showRefreshInProgress() {
        mRefreshButton.setEnabled(false);
        mRefreshProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Hide refresh in progress
     */
    private void hideRefreshInProgress() {
        mRefreshButton.setEnabled(true);
        mRefreshProgress.setVisibility(View.INVISIBLE);
    }

    /**
     * Show/hide videos refresh in progress
     */
    private void setVideosInProgress(boolean inProgress) {
        mVideosProgress.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

    /**
     * Show/hide reviews refresh in progress
     */
    private void setReviewsInProgress(boolean inProgress) {
        mReviewProgress.setVisibility(inProgress ? View.VISIBLE : View.GONE);
    }

    /**
     * Request the movie details from the server
     * @param id Id of movie to request
     */
    private void requestDetailsFromServer(int id) {
        dimUnknown();
        showRefreshInProgress();

        Uri uri = UriUtils.getMovieWithIdUri(id);
        Bundle extras = null;
        if (REQUEST_APPENDED_INFO) {
            extras = new Bundle();
            extras.putStringArray(APPEND_TO_RESPONSE, APPEND_REVIEWS_VIDEOS);
        }
        requestFromServer(movieDetailsResponseHandler, id, uri, GET_DETAILS_METHOD, extras);
        // direct http request alternative is
//        movieDetailsResponseHandler.request(TMDbNetworkUtils.buildGetDetailsUrl(this, id));
    }

    /**
     * Request the movie details from the server
     * @param handler   Handler to make request & process response
     * @param id        Id of movie to request
     * @param uri       Request uri
     * @param method    Method to request
     */
    private void requestFromServer(AsyncCallback handler, int id, Uri uri, String method, Bundle extras) {
        if (NetworkUtils.isInternetAvailable(this)) {
            // matcher result is used as loader id
            handler.call(this, matchMovieUri(uri), uri, method, String.valueOf(id), extras);
        }
    }

    /**
     * Request the movie video details from the server
     * @param id Id of movie to request
     */
    private void requestVideosFromServer(int id) {
        setVideosInProgress(true);
        Uri uri = UriUtils.getMovieWithIdAdditionalInfoUri(id, VIDEO_DETAILS);
        requestFromServer(videoListResponseHandler, id, uri, GET_VIDEOS_METHOD, null);
    }

    /**
     * Request the movie review details from the server
     * @param id Id of movie to request
     */
    private void requestReviewsFromServer(int id) {
        setReviewsInProgress(true);
        Uri uri = UriUtils.getMovieWithIdAdditionalInfoUri(id, REVIEW_DETAILS);
        requestFromServer(reviewListResponseHandler, id, uri, GET_REVIEWS_METHOD, null);
    }

    /**
     * Request the movie favourite details
     * @param id Id of movie to request
     */
    private void requestFavouriteDetails(int id) {
        Uri uri = UriUtils.getFavouriteWithIdUri(id);
        movieDetailsResponseHandler.query(this, matchMovieUri(uri), uri);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(MOVIE_OBJ, mMovie);
        outState.putParcelable(MOVIE_DETAIL_OBJ, mDetails);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK, setReturnIntent(new Intent()));
        finish();
    }

    /**
     * Add any required return info to the intent
     *
     * @param intent Intent to add to
     * @return Intent
     */
    private Intent setReturnIntent(@NonNull Intent intent) {
        // add favourite status change info if necessary
        if (mMovie.isFavourite() != mFavourite) {
            intent.putExtra(MOVIE_ID, mMovie.getId());
            intent.putExtra(COLUMN_FAVOURITE, mMovie.isFavourite());
        }
        if ((mDetails != null) && !mDetails.isEmpty()) {
            intent.putExtra(MOVIE_OBJ, mDetails);   // return details
        }
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent upIntent;
        boolean result;
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                upIntent = NavUtils.getParentActivityIntent(this);
                setReturnIntent(upIntent);

                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    // This activity is part of this app's task, so simply
                    // navigate up to the logical parent activity.
//                    NavUtils.navigateUpTo(this, upIntent);

                    /* NavUtils.navigateUpTo() causes the onActivityResult() in the parent to receive RESULT_CANCELED and no data intent.
                        FIXME This is ok for this app for now, as the parent activity is the only activity calling this activity, but needs to be reviewed.
                     */
                    upIntent = NavUtils.getParentActivityIntent(this);
                    setReturnIntent(upIntent);

                    // this is basically what NavUtils.navigateUpTo() does except it uses startActivity() rather than setResult()
                    upIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    setResult(RESULT_OK, upIntent);
                    finish();
                }
                result = true;
                break;
            default:
                result = super.onOptionsItemSelected(item);
                break;
        }
        return result;
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
     *
     * @param loader Loader to update
     * @param viewId Resource id of ImageView
     * @param barId  Resource id of ProgressBar or <code>0</code> to not set
     * @return Loader
     */
    private ImageLoader setImageLoader(ImageLoader loader, int viewId, int barId) {
        loader.setImageView((ImageView) findViewById(viewId));
        if (barId > 0) {
            loader.setProgressBar((ProgressBar) findViewById(barId));
        }
        return loader;
    }

    /**
     * Start the DbCacheIntentService
     *
     * @param action  Service action to perform
     * @param builder ContentValues builder to create ContentValues
     * @param id      Id to set in builder
     */
    private void startDbCacheIntentService(@NonNull String action, DbContentValues.Builder builder, int id) {
        Intent intent = DbCacheIntentService.getLaunchIntent(this, action);

        ContentValues cv = builder
                .setId(id)
                .build();

        intent.putExtra(_ID, id);
        intent.putExtra(CV_EXTRA, cv);
        this.startService(intent);
    }

    /* Movie details related classes
     * ----------------------------- */

    /**
     * Abstract base class for asynchronous request and response handling
     */
    private abstract class AsyncResponseHandler<T> extends AsyncCallback<T> {

        @Override
        public void onFailure(Call call, IOException e) {
            super.onFailure(call, e);
            onResponseResult(newResponseResult(), getErrorId(call, e));
        }

        @Override
        public void onResponse(T result) {
            int msgId = 0;
            boolean empty = false;
            if (result instanceof IEmpty) {
                empty = ((IEmpty)result).isEmpty();
            }
            if ((result == null) || empty) {
                msgId = R.string.movie_no_response;
            }
            onResponseResult(result, msgId);
        }

        @Override
        public void onFailure(int code, String message) {
            onResponseResult(newResponseResult(), getErrorId(code));
        }

        public abstract void onResponseResult(T result, int msgId);

        public abstract T newResponseResult();

        @Override
        public T processUrlResponse(@NonNull URL request, @NonNull Response response) {
            String jsonResponse = NetworkUtils.getResponseBodyString(response);
            return processUriResponse(new UrlProviderResultWrapper(request, jsonResponse));
        }

        /**
         * Process the response from a {@link ICallback#call(AppCompatActivity, int, Uri, String, String, Bundle)} call
         * @param response  Response from the content provider
         * @return Response object or <code>null</code>
         */
        @Override
        public T processUriResponse(@Nullable AbstractResultWrapper response) {
            T result = null;
            if (response != null) {
                if (response.isString()) {
                    String stringResult = response.getStringResult();
                    if (!TextUtils.isEmpty(stringResult)) {
                        int match = matchMovieUri(response.getUriRequest());
                        result = processUriResponse(response, stringResult, match);
                    }
                }
            }
            return result;
        }

        /**
         * Process the response from a {@link ICallback#call(AppCompatActivity, int, Uri, String, String, Bundle)} call
         * @param response      Response from the content provider
         * @param stringResult  Response content
         * @param match         Uri matcher result
         * @return Response object or <code>null</code>
         */
        public abstract T processUriResponse(@Nullable AbstractResultWrapper response, String stringResult, int match);

        @Override
        public void processQueryResponse(@Nullable QueryResultWrapper response) {
            if (response != null) {
                Uri uriRequest = response.getUriRequest();
                int match = matchMovieUri(uriRequest);
                Cursor cursor = response.getCursorResult();

                processQueryResponse(response, uriRequest, match, cursor);
            }
        }

        /**
         * Process the response from a query request
         * @param response      Response from the content provider
         * @param uriRequest    Uri used for request
         * @param match         Uri matcher result
         * @param cursor        Result of query
         */
        public abstract void processQueryResponse(@Nullable QueryResultWrapper response, Uri uriRequest, int match, Cursor cursor);

        @Override
        public Context getContext() {
            return MovieDetailsActivity.this;
        }
    }

    /**
     * Asynchronous request and response handler
     */
    private AsyncCallback<MovieDetails> movieDetailsResponseHandler = new AsyncResponseHandler<MovieDetails>() {

        @Override
        public void onResponseResult(MovieDetails result, int msgId) {
            onMovieResponse(result, msgId);
        }

        @Override
        public MovieDetails newResponseResult() {
            return new MovieDetails();
        }

        /**
         * Process the response from a {@link ICallback#call(AppCompatActivity, int, Uri, String, String, Bundle)} call
         * @param response  Response from the content provider
         * @return Response object or <code>null</code>
         */
        @Override
        public MovieDetails processUriResponse(@Nullable AbstractResultWrapper response, String stringResult, int match) {
            MovieDetails movieDetails = null;
            switch (match) {
                case MOVIE_WITH_ID:
                    movieDetails = MovieDetails.getInstance(stringResult);

                    // if caching is enabled save details to db
                    boolean cache = PreferenceControl.getCachePreference(getContext());
                    if ((movieDetails != null) && cache) {
                        int id = movieDetails.getId();
                        if (id > 0) {
                            startDbCacheIntentService(INSERT_OR_UPDATE_MOVIE,
                                    MovieContentValues.builder()
                                            .setJson(stringResult)
                                            .setTimestamp(),
                                    id);
                        }
                    }
                    break;
            }
            return movieDetails;
        }

        @Override
        public void processQueryResponse(@Nullable QueryResultWrapper response, Uri uriRequest, int match, Cursor cursor) {
            switch (match) {
                case MOVIE_WITH_ID:
                    if (cursor.moveToNext()) {
                        // got result
                        int idxJson = cursor.getColumnIndex(COLUMN_JSON);
                        setMovieDetails(MovieDetails.getInstance(cursor.getString(idxJson)), DbUtils.timestampToDate(cursor));
                    } else {
                        // details n/a in database
                        int id;
                        try {
                            // request from server
                            id = Integer.parseInt(UriUtils.getIdFromWithIdUri(uriRequest));
                            requestDetailsFromServer(id);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case FAVOURITE_WITH_ID:
                    mFavourite = false;
                    if (cursor.moveToNext()) {
                        // got result
                        int idxFav = cursor.getColumnIndex(COLUMN_FAVOURITE);
                        mFavourite = (cursor.getInt(idxFav) == 1);
                    }
                    setFavouriteButton(mFavourite);
                    break;
            }
        }
    };

    /**
     * Class to update the ui with response details
     */
    private class MovieResponseHandler extends ResponseHandler<MovieDetails> implements Runnable {

        MovieResponseHandler(Activity activity, MovieDetails response, int errorId, Date cacheDate) {
            super(activity, response, errorId, null, cacheDate);
            if (response == null) {
                // set custom error message
                setErrorMsg(MessageFormat.format(getString(R.string.no_type_response), getString(R.string.movie_details)));
            }
        }

        @Override
        public void run() {
            super.run();
            setMovieDetails(getResponse(), getCacheDate());
        }
    }

    /**
     * Set details from a request
     *
     * @param details Details to set
     */
    private void setMovieDetails(MovieDetails details, Date cacheDate) {
        mDetails = details;
        if (mMovie.isPlaceHolder()) {
            mMovie.copy(details, null);
        }
        mMovie.setDetails(mDetails);
        mMovie.setCacheDate(cacheDate);
        setInfoAndDetails();
    }

    /**
     * Handle a movie details response
     * @param response Response object
     */
    protected void onMovieResponse(MovieDetails response, int msgId) {
        hideRefreshInProgress();

        Date cacheDate = getCacheDate(response);
        // ui updates need to be on ui thread
        MovieDetailsActivity.this.runOnUiThread(new MovieResponseHandler(MovieDetailsActivity.this, response, msgId, cacheDate));
    }

    /**
     * Get the cache date
     * @param response Response object
     * @return  Cache date
     */
    protected Date getCacheDate(IEmpty response) {
        Date cacheDate = INVALID_DATE;
        if ((response != null) && !response.isEmpty()) {
            if (PreferenceControl.getCachePreference(this)) {
                // if caching id enabled, the DbCacheIntentService will be saving response in the background
                cacheDate = new Date(); // assume successful
            }
        }
        return cacheDate;
    }

    /* Video/Review related classes
     * ---------------------------- */

    /**
     * Asynchronous request and response handler
     */
    private AsyncCallback<AbstractList> videoListResponseHandler = new AsyncResponseHandler<AbstractList>() {

        @Override
        public void onResponseResult(AbstractList result, int msgId) {
            onVideoListResponse((MovieVideoList)result, msgId);
        }

        @Override
        public AbstractList newResponseResult() {
            return new MovieVideoList();
        }

        @Override
        public AbstractList processUriResponse(@Nullable AbstractResultWrapper response, String stringResult, int match) {
            AbstractList listDetails = null;
            switch (match) {
                case MOVIE_WITH_VIDEOS:
                    listDetails = MovieVideoList.getListFromJsonString(stringResult);
                    break;
            }
            return listDetails;
        }

        @Override
        public void processQueryResponse(@Nullable QueryResultWrapper response, Uri uriRequest, int match, Cursor cursor) {
            // noop
        }
    };

    /**
     * Asynchronous request and response handler
     */
    private AsyncCallback<AbstractList> reviewListResponseHandler = new AsyncResponseHandler<AbstractList>() {

        @Override
        public void onResponseResult(AbstractList result, int msgId) {
            onReviewListResponse(result, msgId);
        }

        @Override
        public AbstractList newResponseResult() {
            return new MovieReviewList();
        }

        @Override
        public AbstractList processUriResponse(@Nullable AbstractResultWrapper response, String stringResult, int match) {
            AbstractList listDetails = null;
            switch (match) {
                case MOVIE_WITH_REVIEWS:
                    listDetails = MovieReviewList.getListFromJsonString(stringResult);
                    break;
            }
            return listDetails;
        }

        @Override
        public void processQueryResponse(@Nullable QueryResultWrapper response, Uri uriRequest, int match, Cursor cursor) {
            // noop
        }
    };

    /**
     * Class to update the ui with response details
     */
    private class ListResponseHandler extends ResponseHandler<AbstractList> implements Runnable {

        int type;

        ListResponseHandler(Activity activity, int type, AbstractList response, int errorId, Date cacheDate) {
            super(activity, response, errorId, null, cacheDate);
            this.type = type;
            if (response == null) {
                // set custom error message
                @StringRes int typeId = 0;
                switch (type) {
                    case MOVIE_WITH_VIDEOS:
                        typeId = R.string.video_details;
                        break;
                    case MOVIE_WITH_REVIEWS:
                        typeId = R.string.review_details;
                        break;
                }
                if (typeId > 0) {
                    setErrorMsg(MessageFormat.format(getString(R.string.no_type_response), getString(typeId)));
                }
            }
        }

        @Override
        public void run() {
            super.run();
            switch (type) {
                case MOVIE_WITH_VIDEOS:
                    setVideoListDetails((MovieVideoList)getResponse());
                    break;
                case MOVIE_WITH_REVIEWS:
                    setReviewListDetails((MovieReviewList)getResponse());
                    break;
            }
        }
    }

    /**
     * Set video details from a request
     * @param list Details to set
     */
    private void setVideoListDetails(MovieVideoList list) {
//        // update video list
        setVideoDetails(list);

        mDetails.setMovieVideoList(list);
    }

    /**
     * Set video details from a request
     * @param list Details to set
     */
    private void setReviewListDetails(MovieReviewList list) {
        // update review list
        setReviewDetails(list);

        mDetails.setReviewList(list);
    }

    /**
     * Handle a video list response
     * @param response Response object
     */
    protected void onVideoListResponse(MovieVideoList response, int msgId) {
        setVideosInProgress(false);
        Date cacheDate = getCacheDate(response);
        // ui updates need to be on ui thread
        MovieDetailsActivity.this.runOnUiThread(new ListResponseHandler(MovieDetailsActivity.this, MOVIE_WITH_VIDEOS, response, msgId, cacheDate));
    }

    /**
     * Handle a review list response
     * @param response Response object
     */
    protected void onReviewListResponse(AbstractList response, int msgId) {
        setReviewsInProgress(false);
        Date cacheDate = getCacheDate(response);
        // ui updates need to be on ui thread
        MovieDetailsActivity.this.runOnUiThread(new ListResponseHandler(MovieDetailsActivity.this, MOVIE_WITH_REVIEWS, response, msgId, cacheDate));
    }


    /* RecyclerView related classes
     * ---------------------------- */

    /**
     * Abstract base class to handle the RecyclerView for AbstractList
     */
    private abstract class AbstractListRecyclerViewController<T extends TMDbObject> extends AbstractRecyclerViewController<T>
            implements AbstractRecyclerViewController.IRecyclerViewHost {

        View container;
        boolean hideOnEmpty;

        /**
         * Constructor
         * @param activity      The current activity
         * @param viewId        Resource id of layout for the RecyclerView
         * @param containerId   Resource id of container for the RecyclerView
         * @param layout        Type of layout to use
         * @param hideOnEmpty   <code>true</code> hide when empty, <code>false</code> show always
         */
        AbstractListRecyclerViewController(Activity activity, @IdRes int viewId, @IdRes int containerId, IRecyclerViewHost.LAYOUT_TYPE layout, boolean hideOnEmpty) {
            super(activity, viewId, layout);
            setHost(this);

            container = activity.findViewById(containerId);
            this.hideOnEmpty = hideOnEmpty;
            setContainerVisibility();
        }

        public boolean updateDataSet(@NonNull AbstractList<T> list, ITester<T> tester, @Nullable Comparator<Object> comparator) {
            boolean valid = false;
            if (list != null) {
                valid = list.rangeIsValid();
            }
            if (valid) {
                addAndNotify(list.getResults(), tester, comparator);
            } else {
                clearAndNotify();
            }
            setContainerVisibility();
            return valid;
        }

        public boolean updateDataSet(@NonNull AbstractList<T> list, ITester<T> tester) {
            return updateDataSet(list, tester, null);
        }

        public boolean updateDataSet(@NonNull AbstractList<T> list) {
            return updateDataSet(list, null);
        }

        private void setContainerVisibility() {
            int visibility;
            if ((getItemCount() == 0) && hideOnEmpty) {
                visibility = View.GONE;
            } else {
                visibility = View.VISIBLE;
            }
            container.setVisibility(visibility);
        }

        @Override
        public int calcNumColumns() {
            return 1;   // not required for LAYOUT_TYPE.LINEAR
        }
    }

    /**
     * Class to handle the RecyclerView for videos
     */
    private class VideoRecyclerViewController extends AbstractListRecyclerViewController<Video> implements AbstractRecyclerViewController.IRecyclerViewHost {

        ITester<Video> tester;

        VideoRecyclerViewController(Activity activity, @IdRes int viewId, @IdRes int containerId, ITester<Video> tester) {
            super(activity, viewId, containerId, LAYOUT_TYPE.LINEAR, true);
            this.tester = tester;
        }

        @Override
        public boolean updateDataSet(@NonNull AbstractList<Video> list) {
            return super.updateDataSet(list, tester, Video.COMPARATOR);
        }

        @Override
        public <T extends TMDbObject> void onItemClick(T obj) {
            Video video = (Video) obj;
            if (video != null) {
                Intent[] intents = video.getViewIntents();
                if (intents != null) {
                    Utils.startActivity(MovieDetailsActivity.this, intents);
                } else {
                    Dialog.showAlertDialog(MovieDetailsActivity.this, R.string.unable_to_play_moviedetailsA);
                }
            }
        }
    }

    /**
     * Class to handle the RecyclerView for reviews
     */
    private class ReviewRecyclerViewController extends AbstractListRecyclerViewController<BaseReview> implements AbstractRecyclerViewController.IRecyclerViewHost {

        ReviewRecyclerViewController(Activity activity) {
            super(activity, R.id.rv_review_movies_detailsA, R.id.ll_reviews_moviedetailsA, LAYOUT_TYPE.LINEAR, true);
        }

        @Override
        public <T extends TMDbObject> void onItemClick(T obj) {
            BaseReview review = (BaseReview) obj;
            if (review != null) {
                Intent[] intents = review.getViewIntents(MovieDetailsActivity.this);
                if (intents != null) {
                    Utils.startActivity(MovieDetailsActivity.this, intents);
                } else {
                    Dialog.showAlertDialog(MovieDetailsActivity.this, R.string.unable_to_view_review_moviedetailsA);
                }
            }
        }
    }
}
