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
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;

import ie.ianbuttimer.moviequest.tmdb.MovieInfo;
import ie.ianbuttimer.moviequest.data.MovieInfoAdapter;
import ie.ianbuttimer.moviequest.tmdb.MovieInfoModel;
import ie.ianbuttimer.moviequest.tmdb.MovieListResponse;
import ie.ianbuttimer.moviequest.utils.Dialog;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import ie.ianbuttimer.moviequest.utils.PreferenceControl;
import ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils;
import ie.ianbuttimer.moviequest.utils.Utils;


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

        // request the movie info
        if (mMovieList.size() == 0) {
            requestMovies();
        }

        // watch for preference changes
        PreferenceControl.registerOnSharedPreferenceChangeListener(this, preferenceChangeListener);
    }

    /**
     * Request the movies
     */
    private void requestMovies() {
        // persist to activity preferences
        PreferenceControl.setStringPreference(this, R.string.pref_movie_list_key, mListSelection);

        showInProgress();

        new GetMovies().execute(TMDbNetworkUtils.buildGetMovieListUrl(this, mListSelection));
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
        if (NetworkUtils.isInternetAvailable(this)) {
            Intent intent = new Intent(this, MovieDetailsActivity.class);
            // add the movie object to the intent so it doesn't have to be requested
            MovieInfo movie = (MovieInfo) view.getTag(R.id.movie_id_tag);
            intent.putExtra(MOVIE_ID, movie.getId());   // pass id to request movie details
            intent.putExtra(MOVIE_OBJ, movie);          // pass movie info to at least display what we have

            Utils.startActivity(this, intent);
        } else {
            Dialog.showNoNetworkDialog(this);
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
     * AsyncTask to request movies
     */
    class GetMovies extends AsyncTask<URL, Void, MovieListResponse> {

        @Override
        protected MovieListResponse doInBackground(URL... params) {

            /* If there's no url, there's nothing to look up. */
            if (params.length == 0) {
                return null;
            }

            try {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(params[0]);

                return MovieListResponse.getMovieInfoFromJsonString(jsonResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onPostExecute(MovieListResponse response) {
            boolean rangeIsValid = false;
            if (response == null) {
                // no response received
                showNoResponse();
            } else {
                rangeIsValid = response.rangeIsValid();
            }
            if (rangeIsValid) {
                clearInProgress();

                String range = MessageFormat.format(getResources().getString(R.string.movie_range),
                        response.getRangeStart(), response.getRangeEnd(), response.getTotalResults());
                mMovieRangeTextView.setText(range);
            } else {
                if (response != null) {
                    showError(R.string.invalid_response);
                }

                mMovieRangeTextView.setText("");
            }

            mMovieAdapter.clear();
            if (response != null) {
                mMovieAdapter.addAll(response.getMovies());
            }
            mMovieAdapter.notifyDataSetChanged();
        }

    }

    /**
     * Show no response dialog
     */
    private void showInProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Clear in progress indicator
     */
    private void clearInProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * Show error message
     */
    private void showError(int msgResId) {
        mErrorTextView.setText(getString(msgResId));
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Show no response dialog
     */
    private void showNoResponse() {
        Dialog.showNoResponseDialog(this);
        showError(R.string.no_response);
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
