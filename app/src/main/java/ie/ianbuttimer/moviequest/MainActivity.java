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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.text.MessageFormat;
import java.util.ArrayList;

import ie.ianbuttimer.moviequest.data.MovieInfoAdapter;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.tmdb.MovieListResponse;
import ie.ianbuttimer.moviequest.utils.AsyncCallback;
import ie.ianbuttimer.moviequest.utils.Dialog;
import ie.ianbuttimer.moviequest.utils.ICallback;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;
import ie.ianbuttimer.moviequest.utils.ResponseHandler;
import ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils;
import ie.ianbuttimer.moviequest.utils.Utils;
import okhttp3.Call;
import okhttp3.Response;


import static ie.ianbuttimer.moviequest.Constants.MOVIE_ID;
import static ie.ianbuttimer.moviequest.Constants.MOVIE_OBJ;

/**
 * Main application activity
 */
public class MainActivity extends AppCompatActivity implements
        MovieInfoAdapter.MovieInfoAdapterOnClickHandler, AdapterView.OnItemSelectedListener {

    private MovieInfoAdapter<MovieInfoModel> mMovieAdapter;
    private ArrayList<MovieInfoModel> mMovieList;  // movie data list
    private static String MOVIE_ARRAY = "movie_array";
    private RecyclerView mRecyclerView;

    private GridLayoutManager mLayoutManager;

    private String mListSelection;          // current movie list (popular/top rated)selection
    private String[] mListSelectionArray;   // current movie list selection options

    private TextView mMovieRangeTextView;
    private static String MOVIE_RANGE = "movie_range";

    private ProgressBar mProgressBar;
    private TextView mErrorTextView;
    private Button mRetry;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mListSelection = savedInstanceState.getString(getString(R.string.pref_movie_list_key));
            mMovieList = (ArrayList<MovieInfoModel>)savedInstanceState.getSerializable(MOVIE_ARRAY);
        } else {
            // get the user's last list selection or the app default
            String appDflt = PreferenceControl.getSharedStringPreference(this,
                    R.string.pref_movie_list_key, R.string.pref_movie_list_dlft_value);
            mListSelection = PreferenceControl.getStringPreference(this, R.string.pref_movie_list_key, appDflt);

            mMovieList = new ArrayList<MovieInfoModel>();
        }

        Resources resources = getResources();

        // setup the movie list spinner
        Spinner movieListSpinner = (Spinner) findViewById(R.id.spin_movie_list_mainA);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pref_movie_list_titles, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        movieListSpinner.setAdapter(adapter);

        // get values corresponding to entries in spinner
        mListSelectionArray = resources.getStringArray(R.array.pref_movie_list_values);
        // set the spinner index
        for (int i = 0; i < mListSelectionArray.length; i++) {
            if (mListSelectionArray[i].equals(mListSelection)) {
                movieListSpinner.setSelection(i);
                break;
            }
        }
        movieListSpinner.setOnItemSelectedListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_mainA);
        mErrorTextView = (TextView) findViewById(R.id.tv_error_mainA);

        // setup the recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_movies_mainA);
        mLayoutManager =
                new GridLayoutManager(this, calcNumColumns(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(mLayoutManager);

        /*
         * Use this setting to improve performance if you know that changes in content do not
         * change the child layout size in the RecyclerView
         */
//        mRecyclerView.setHasFixedSize(true);

        // get adapter to responsible for linking data with the Views that display it
        mMovieAdapter = new MovieInfoAdapter<MovieInfoModel>(mMovieList, this);

        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mMovieAdapter);

        mMovieRangeTextView = (TextView) findViewById(R.id.tv_movie_range_MainA);
        if (savedInstanceState != null) {
            mMovieRangeTextView.setText(savedInstanceState .getString(MOVIE_RANGE));
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
        Utils.setBackdropPreference(this);
        PreferenceControl.registerOnSharedPreferenceChangeListener(this, preferenceChangeListener);
    }

    /**
     * Request the movies
     */
    private void requestMovies() {
        // persist to activity preferences
        PreferenceControl.setStringPreference(this, R.string.pref_movie_list_key, mListSelection);

        showInProgress();

        responseHandler.request(TMDbNetworkUtils.buildGetMovieListUrl(this, mListSelection));
    }

    /**
     * Calculate the number of columns to display
     * @return  number of columns
     */
    private int calcNumColumns() {
        int width = Utils.getScreenWidth(this);     //
        String posterSetting = PreferenceControl.getSharedStringPreference(this,
                R.string.pref_poster_size_key, R.string.pref_poster_size_dlft_value);
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
        MovieInfoModel movie = (MovieInfoModel) view.getTag(R.id.movie_id_tag);
        intent.putExtra(MOVIE_ID, movie.getId());   // pass id to request movie details
        intent.putExtra(MOVIE_OBJ, movie);          // pass movie info to at least display what we have

        Utils.startActivity(this, intent);
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
    private ICallback responseHandler = new AsyncCallback() {

        @Override
        public void onFailure(Call call, IOException e) {
            super.onFailure(call, e);
            onListResponse(new MovieListResponse(), getErrorId(call, e));    // default list object will not pass valid range test
        }

        @Override
        public void onResponse(Object result) {
            MovieListResponse response = null;
            if (result != null) {
                response = (MovieListResponse) result;
            }
            onListResponse(response, 0);
        }

        /**
         * Convert the http response into a MovieListResponse object
         * @param response  Response from the server
         * @return
         */
        @Override
        public Object processResponse(Response response) {
            Object result = null;
            try {
                String jsonResponse = response.body().string();
                result = MovieListResponse.getMovieListFromJsonString(jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }
    };

    /**
     * Class to update the ui with response list details
     */
    private class ListResponseHandler extends ResponseHandler<MovieListResponse> implements Runnable {

        public ListResponseHandler(Activity activity, MovieListResponse response, int errorId) {
            super(activity, response, errorId);
        }

        @Override
        public void run() {
            MovieListResponse response = getResponse();
            String range = "";

            super.run();

            if (response != null) {
                if (response.rangeIsValid()) {
                    clearInProgress();

                    range = MessageFormat.format(getResources().getString(R.string.movie_range),
                            response.getRangeStart(), response.getRangeEnd(), response.getTotalResults());
                }
            }
            mMovieRangeTextView.setText(range);

            if (hasDialog()) {
                showError(getErrorId());
            }

            mMovieAdapter.clear();
            if (response != null) {
                mMovieAdapter.addAll(response.getMovies());
            }
            mMovieAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Handle a list response
     * @param response  Response object
     */
    protected void onListResponse(MovieListResponse response, int msgId) {
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
        mRetry.setVisibility(View.VISIBLE);
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
     * Preference change listener to refresh display
     */
    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    boolean request = false;    // request movies flag
                    boolean redraw = false;     // redraw movie cards flag
                    if (key.equals(getString(R.string.pref_poster_size_key))) {
                        // poster size has changed, request again
                        setNumColumns();
                        request = true;
                    } else if (key.equals(getString(R.string.pref_movie_list_key))) {
                        // movie list selection has changed
                        String newValue = sharedPreferences.getString(key, getString(R.string.pref_movie_list_dlft_value));
                        if (!mListSelection.equals(newValue)) {
                            request = true;
                            mListSelection = newValue;
                        }
                    } else if (key.equals(getString(R.string.pref_show_position_key))) {
                        // movie card display option has changed
                        redraw = true;
                    }
                    if (request) {
                        requestMovies();  // request movies again
                    } else if (redraw) {
                        mMovieAdapter.notifyDataSetChanged();   // force redraw
                    }
                }
            };
}
