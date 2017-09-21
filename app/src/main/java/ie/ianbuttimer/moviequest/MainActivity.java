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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ie.ianbuttimer.moviequest.data.DbCacheIntentService;
import ie.ianbuttimer.moviequest.data.IAdapterOnClickHandler;
import ie.ianbuttimer.moviequest.data.adapter.MovieInfoAdapter;
import ie.ianbuttimer.moviequest.tmdb.MovieDetails;
import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.tmdb.MovieList;
import ie.ianbuttimer.moviequest.data.AbstractResultWrapper;
import ie.ianbuttimer.moviequest.data.AsyncCallback;
import ie.ianbuttimer.moviequest.utils.Dialog;
import ie.ianbuttimer.moviequest.data.ICallback;
import ie.ianbuttimer.moviequest.utils.ITester;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;
import ie.ianbuttimer.moviequest.utils.ResponseHandler;
import ie.ianbuttimer.moviequest.utils.Tuple;
import ie.ianbuttimer.moviequest.utils.Utils;
import okhttp3.Call;
import okhttp3.Response;


import static ie.ianbuttimer.moviequest.Constants.MOVIE_ID;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_OBJ;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_TITLE;
import static ie.ianbuttimer.moviequest.data.DbCacheIntentService.PURGE_EXPIRED;
import static ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry.COLUMN_FAVOURITE;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieLists.GET_FAVOURITE_METHOD;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieLists.MOVIE_LIST_CONTENT_URI;
import static ie.ianbuttimer.moviequest.tmdb.MovieList.LIST_PAGE;
import static ie.ianbuttimer.moviequest.tmdb.MovieList.RESULTS_PER_LIST;
import static ie.ianbuttimer.moviequest.tmdb.MovieList.RESULTS_PER_PAGE;
import static ie.ianbuttimer.moviequest.utils.PreferenceControl.PreferenceTypes.BOOLEAN;
import static ie.ianbuttimer.moviequest.utils.PreferenceControl.PreferenceTypes.STRING;

/**
 * Main application activity
 */
public class MainActivity extends AppCompatActivity implements
        IAdapterOnClickHandler, AdapterView.OnItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private MovieInfoAdapter mMovieAdapter;
    private ArrayList<MovieInfoModel> mMovieList;  // movie data list
    private static String MOVIE_ARRAY = "movie_array";

    private GridLayoutManager mLayoutManager;

    private Spinner mMovieListSpinner;
    private String mListSelection;          // current movie list (popular/top rated) selection
    private String[] mListSelectionArray;   // current movie list selection options

    private TextView mMovieRangeTextView;
    private static String MOVIE_RANGE = "movie_range";
    private int mRangeStart;
    private int mRangeEnd;
    private int mRangeTotal;

    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private Button mRetry;

    private static boolean PREFERENCE_UPDATED = false;  // flag to indicate that preferences have changed
    private static HashMap<String, Object> PREFERENCES; // map of preferences and current values

    @SuppressWarnings("unchecked")
    private static final Tuple<Integer, Integer, PreferenceControl.PreferenceTypes>[] PREFERENCE_LIST =
        new Tuple[] {
            new Tuple<>(R.string.pref_poster_size_key, R.string.pref_poster_size_dlft_value, STRING),
            new Tuple<>(R.string.pref_movie_list_key, R.string.pref_movie_list_dlft_value, STRING),     // Note this is the default NOT the currently displayed (mListSelection)
            new Tuple<>(R.string.pref_show_position_key, R.bool.pref_show_position_dflt_value, BOOLEAN),
        };

    /** Loader id for movie lists */
    private static final int MOVIE_LIST_LOADER_ID = 1;

    /** Request code for movie details activity */
    private static final int MOVIE_DETAILS_REQ_CODE = 1;

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mListSelection = savedInstanceState.getString(getString(R.string.pref_movie_list_key));
            Object obj = savedInstanceState.getSerializable(MOVIE_ARRAY);
            if (obj != null) {
                if (obj instanceof ArrayList) {
                    mMovieList = (ArrayList<MovieInfoModel>) obj;
                } else {
                    Log.e(TAG, MOVIE_ARRAY + " from bundle is incorrect type; " + obj.getClass().getName());
                    obj = null;
                }
            }
            if (obj == null) {
                mMovieList = new ArrayList<>();
            }
        } else {
            // get the user's last list selection or the app default
            String appDflt = PreferenceControl.getMovieListPreference(this);
            mListSelection = PreferenceControl.getStringPreference(this, R.string.pref_movie_list_key, appDflt);
            mMovieList = new ArrayList<>();
        }

        setupPreferenceMap();

        Resources resources = getResources();

        // setup the movie list spinner
        mMovieListSpinner = (Spinner) findViewById(R.id.spin_movie_list_mainA);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pref_movie_list_titles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mMovieListSpinner.setAdapter(adapter);

        // get values corresponding to entries in spinner
        mListSelectionArray = resources.getStringArray(R.array.pref_movie_list_values);
        // set the spinner index
        setSpinnerIndex(mListSelection);
        mMovieListSpinner.setOnItemSelectedListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_mainA);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_mainA);

        // setup the recycler view
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_mainA);
        mLayoutManager =
                new GridLayoutManager(this, calcNumColumns(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
//        mRecyclerView.setHasFixedSize(true);

        // get adapter to responsible for linking data with the Views that display it
        mMovieAdapter = new MovieInfoAdapter(mMovieList, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        mMovieRangeTextView = (TextView) findViewById(R.id.tv_movie_range_MainA);
        if (savedInstanceState != null) {
            mMovieRangeTextView.setText(savedInstanceState.getString(MOVIE_RANGE));
        }

        mRetry = (Button) findViewById(R.id.button_retry_mainA);
        mRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestMovies();
            }
        });


        // request the movie info
        if (mMovieList.size() == 0) {
            requestMovies();
        }

        // watch for preference changes
        PreferenceControl.registerOnSharedPreferenceChangeListener(this, preferenceChangeListener);

        // misc activity
        Utils.setBackdropPreference(this);  // set backdrop preference as currently not in settings

        startService(DbCacheIntentService.getLaunchIntent(this, PURGE_EXPIRED)); // purge expired entries from db
    }

    /**
     * Request the movies
     */
    private void requestMovies() {
        // persist to activity preferences
        PreferenceControl.setStringPreference(this, R.string.pref_movie_list_key, mListSelection);

        clearCurrentList();
        showInProgress();

        // request current movie list via ContentProvider
        Bundle extras = null;
        if (isFavouritesList()) {
            extras = new Bundle();
            extras.putInt(RESULTS_PER_PAGE, RESULTS_PER_LIST);
            extras.putInt(LIST_PAGE, 0);   // TODO always requesting page 0
        }
        responseHandler.call(this, MOVIE_LIST_LOADER_ID, MOVIE_LIST_CONTENT_URI, mListSelection, null, extras);
    }

    /**
     * Clear existing list details
     */
    private void clearCurrentList() {
        // clear existing
        mMovieAdapter.clear();
        mMovieAdapter.notifyDataSetChanged();

        mRangeStart = 1;  // default
        mRangeEnd = 1;
        mRangeTotal = 0;
        setRange();
    }

    /**
     * Check if viewing favourites list
     * @return  <code>true</code> if viewing favourites list, <code>false</code> otherwise
     */
    private boolean isFavouritesList() {
        return GET_FAVOURITE_METHOD.equals(mListSelection);
    }

    /**
     * Calculate the number of columns to display
     * @return  number of columns
     */
    private int calcNumColumns() {
        int width = Utils.getScreenWidth(this);     //
        String posterSetting = PreferenceControl.getPosterSizePreference(this);
        int posterSize = Integer.valueOf(posterSetting);
        return Math.max(1, (width / posterSize));
    }

    /**
     * Set the number of columns displayed
     */
    private void setNumColumns() {
        mLayoutManager.setSpanCount(calcNumColumns());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(View view) {
        Intent intent = new Intent(this, MovieDetailsActivity.class);
        // add the movie object to the intent so it doesn't have to be requested
        MovieInfoModel movie = (MovieInfoModel) view.getTag(R.id.tmdb_obj_tag);
        intent.putExtra(MOVIE_ID, movie.getId());   // pass id to request movie details
        if (!movie.isPlaceHolder()) {
            intent.putExtra(MOVIE_OBJ, movie);          // pass movie info to at least display what we have
        } else {
            intent.putExtra(MOVIE_TITLE, movie.getTitle());   // pass title from placeholder
        }

        Utils.startActivityForResult(this, intent, MOVIE_DETAILS_REQ_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case MOVIE_DETAILS_REQ_CODE:
                if (isFavouritesList()) {
                    if ((resultCode == RESULT_OK) && (data != null)) {
                        boolean dataChange = false;
                        if (data.hasExtra(COLUMN_FAVOURITE)) {
                            // favourite status change has occurred
                            final int id = data.getIntExtra(MOVIE_ID, 0);
                            boolean favourite = data.getBooleanExtra(COLUMN_FAVOURITE, false);

                            if (!favourite) {
                                int index = mMovieAdapter.findItemIndex(new ITester<MovieInfoModel>() {
                                    @Override
                                    public boolean test(MovieInfoModel obj) {
                                        return (obj.getId() == id);
                                    }
                                });
                                if (index >= 0) {
                                    mMovieAdapter.remove(index);

                                    // adjust indices & range display
                                    Iterator<MovieInfoModel> iterator = mMovieAdapter.iterator();
                                    index = mRangeStart;
                                    while (iterator.hasNext()) {
                                        MovieInfoModel movie = iterator.next();
                                        movie.setIndex(index);
                                        ++index;
                                    }
                                    --mRangeEnd;    // one less favourite
                                    --mRangeTotal;
                                    setRange();
                                    if (isFavouritesList() && (mRangeTotal <= 0)) {
                                        showError(R.string.no_movies_favourites);
                                    }

                                    dataChange = true;
                                }
                            }
                        }
                        if (data.hasExtra(MOVIE_OBJ)) {
                            MovieDetails details = data.getParcelableExtra(MOVIE_OBJ);
                            final int id = details.getId();
                            int index = mMovieAdapter.findItemIndex(new ITester<MovieInfoModel>() {
                                @Override
                                public boolean test(MovieInfoModel obj) {
                                    return (obj.getId() == id);
                                }
                            });
                            if (index >= 0) {
                                // if tile is a placeholder update it with the info
                                MovieInfoModel model = mMovieAdapter.getItem(index);
                                if (model.isPlaceHolder()) {
                                    model.copy(details, new MovieInfo().getFieldIds()); // YUCK

                                    dataChange = true;
                                }
                            }
                        }
                        if (dataChange) {
                            // something has changed
                            mMovieAdapter.notifyDataSetChanged();
                        }
                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set intent for about activity
        MenuItem menuItem = menu.findItem(R.id.action_about);
        menuItem.setIntent(new Intent(this, AboutActivity.class));
        // set intent for settings activity
        menuItem = menu.findItem(R.id.action_settings);
        menuItem.setIntent(new Intent(this, SettingsActivity.class));

        return true;
    }

    /**
     * Asynchronous request and response handler
     */
    private ICallback<MovieList> responseHandler = new AsyncCallback<MovieList>() {

        @Override
        public void onFailure(Call call, IOException e) {
            super.onFailure(call, e);
            onListResponse(new MovieList(), getErrorId(call, e));    // default list object will not pass valid range test
        }

        @Override
        public void onResponse(MovieList result) {
            int msgId = 0;
            if (result != null) {
                if (result.getResultCount() == 0) {
                    if (isFavouritesList()) {
                        msgId = R.string.no_movies_favourites;
                    } else if (result.isNonResponse()) {
                        msgId = R.string.no_response;
                    } else {
                        msgId = R.string.no_movies_in_list;
                    }
                }
            }
            onListResponse(result, msgId);
        }

        /**
         * Convert the http response into a MovieList object
         * @param response  Response from the server
         * @return Response object or <code>null</code>
         */
        @Override
        public MovieList processUrlResponse(@NonNull URL request, @NonNull Response response) {
            String jsonResponse = NetworkUtils.getResponseBodyString(response);
            return processUriResponse(new UrlProviderResultWrapper(request, jsonResponse));
        }

        /**
         * Process the response from a {@link ICallback#call(AppCompatActivity, int, Uri, String, String, Bundle)} call
         * @param response  Response from the content provider
         * @return Response object or <code>null</code>
         */
        @Override
        public MovieList processUriResponse(@Nullable AbstractResultWrapper response) {
            MovieList movieList = null;
            if (response != null) {
                if (response.isResultType(AbstractResultWrapper.ResultType.STRING)) {
                    movieList = MovieList.getListFromJsonString(response.getStringResult());
                } else if (response.isResultType(AbstractResultWrapper.ResultType.BUNDLE)) {
                    movieList = MovieList.getListFromBundle(response.getBundleResult());
                }
            }
            return movieList;
        }

        @Override
        public Context getContext() {
            return MainActivity.this;
        }
    };

    /**
     * Class to update the ui with response list details
     */
    private class ListResponseHandler extends ResponseHandler<MovieList> implements Runnable {

        ListResponseHandler(Activity activity, MovieList response, int errorId) {
            super(activity, response, errorId);
        }

        @Override
        public void run() {
            MovieList response = getResponse();

            super.run();

            clearInProgress();
            mRangeStart = 1;  // default
            mRangeEnd = 1;
            mRangeTotal = 0;
            if (response != null) {
                if (response.rangeIsValid()) {
                    mRangeStart = response.getRangeStart();
                    mRangeEnd = response.getRangeEnd();
                    mRangeTotal = response.getTotalResults();
                }
            }
            setRange();

            if (hasDialog()) {
                showError(getErrorId());
            }

            mMovieAdapter.clear();
            if (response != null) {
                mMovieAdapter.addAll(response.getResults());
            }
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Set the range display
     */
    private void setRange() {
        String range;
        if ((mRangeStart <= mRangeEnd) && (mRangeTotal > 0)) {
            range = MessageFormat.format(getResources().getString(R.string.movie_range),
                    mRangeStart, mRangeEnd, mRangeTotal);
        } else {
            range = "";
        }
        mMovieRangeTextView.setText(range);
    }

    /**
     * Handle a list response
     * @param response  Response object
     */
    protected void onListResponse(MovieList response, int msgId) {
        // ui updates need to be on ui thread
        MainActivity.this.runOnUiThread(new ListResponseHandler(MainActivity.this, response, msgId));
    }

    /**
     * Show no response dialog
     */
    private void showInProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRetry.setVisibility(View.INVISIBLE);
    }

    /**
     * Clear in progress indicator
     */
    private void clearInProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
        mRetry.setVisibility(View.INVISIBLE);
    }

    /**
     * Show error message
     */
    private void showError(int msgResId) {
        mErrorTextView.setText(getString(msgResId));
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
        mRetry.setVisibility(isFavouritesList() ? View.INVISIBLE : View.VISIBLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStart() {
        super.onStart();
        if (PREFERENCE_UPDATED) {
            processPreferenceChange();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        // save list user is currently viewing
        PreferenceControl.unregisterOnSharedPreferenceChangeListener(this, preferenceChangeListener);
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(MOVIE_ARRAY, mMovieList);
        outState.putString(getString(R.string.pref_movie_list_key), mListSelection);
        outState.putString(MOVIE_RANGE, mMovieRangeTextView.getText().toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        // handle changes in the movie list spinner
        if (NetworkUtils.isInternetAvailable(this)) {
            String newValue = mListSelectionArray[Long.valueOf(id).intValue()];
            boolean request = !mListSelection.equals(newValue); // new list?
            if (!request) {
                // same list, but check error
                request = (mErrorTextView.getVisibility() == View.VISIBLE);
            }
            if (request) {
                mListSelection = newValue;
                requestMovies();
            }
        } else {
            Dialog.showNoNetworkDialog(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // noop
    }

    /**
     * Set the movie list spinner to match the setting
     * @param setting   Setting value
     */
    private void setSpinnerIndex(String setting) {
        // set the spinner index
        for (int i = 0; i < mListSelectionArray.length; i++) {
            if (mListSelectionArray[i].equals(setting)) {
                mMovieListSpinner.setSelection(i);
                break;
            }
        }
    }
    
    /**
     * Set the movie list spinner to match the setting
     * @param setting   Setting value
     */
    private void setSpinnerIndexSuppressListener(String setting) {
        // clear the listener before setting to avoid double request
        mMovieListSpinner.setOnItemSelectedListener(null);
        setSpinnerIndex(setting);
        mMovieListSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Preference change listener to refresh display
     */
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    PREFERENCE_UPDATED = true;  // flag preference change
                }
            };

    /**
     * Initialise the map of current preference settings
     */
    private void setupPreferenceMap() {
        PREFERENCES = new HashMap<>();
        for (Tuple<Integer, Integer, PreferenceControl.PreferenceTypes> entry : PREFERENCE_LIST) {
            int keyId = entry.getT1();
            String key = getString(keyId);
            Object setting = PreferenceControl.getSharedPreference(this, entry.getT3(), keyId, entry.getT2());
            if (setting != null) {
                PREFERENCES.put(key, setting);
            }
        }
    }

    /**
     * Process a preference change
     */
    private void processPreferenceChange() {

        boolean request = false;    // request movies flag
        boolean redraw = false;     // redraw movie cards flag

        for (Tuple<Integer, Integer, PreferenceControl.PreferenceTypes> entry : PREFERENCE_LIST) {
            int keyId = entry.getT1();
            String key = getString(keyId);
            Object setting = PreferenceControl.getSharedPreference(this, entry.getT3(), keyId, entry.getT2());
            Object current = PREFERENCES.get(key);

            if ((setting != null) && !setting.equals(current)) {
                if (key.equals(getString(R.string.pref_poster_size_key))) {
                    // poster size has changed, request again
                    setNumColumns();
                    request = true;
                } else if (key.equals(getString(R.string.pref_movie_list_key))) {
                    // default movie list selection has changed
                    if (!mListSelection.equals(setting)) {
                        request = true;
                        mListSelection = (String)setting;
                        setSpinnerIndexSuppressListener(mListSelection);
                    }
                } else if (key.equals(getString(R.string.pref_show_position_key))) {
                    // movie card display option has changed
                    redraw = true;
                }

                PREFERENCES.put(key, setting);
            }
        }
        PREFERENCE_UPDATED = false;
        if (request) {
            requestMovies();  // request movies again
        } else if (redraw) {
            mMovieAdapter.notifyDataSetChanged();   // force redraw
        }
    }
}
