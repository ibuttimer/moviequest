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

package ie.ianbuttimer.moviequest.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;


import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import ie.ianbuttimer.moviequest.tmdb.MovieDetails;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils;
import ie.ianbuttimer.moviequest.utils.UriUtils;

import static ie.ianbuttimer.moviequest.data.MovieContract.AUTHORITY;
import static ie.ianbuttimer.moviequest.data.MovieContract.BASE_CONTENT_URI;
import static ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry;
import static ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry.COLUMN_FAVOURITE;
import static ie.ianbuttimer.moviequest.data.MovieContract.ID_EQ_SELECTION;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.GET_DETAILS_METHOD;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieLists.GET_FAVOURITE_METHOD;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieLists.GET_POPULAR_METHOD;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieLists.GET_TOP_RATED_METHOD;
import static ie.ianbuttimer.moviequest.data.MovieContract.PATH_FAVOURITES;
import static ie.ianbuttimer.moviequest.data.MovieContract.PATH_MOVIES;
import static ie.ianbuttimer.moviequest.data.MovieContract.PATH_POPULAR_MOVIES;
import static ie.ianbuttimer.moviequest.data.MovieContract.PATH_TOP_RATED_MOVIES;
import static ie.ianbuttimer.moviequest.data.MovieContract.PATH_WITH_ID;
import static ie.ianbuttimer.moviequest.data.MovieContract.columnEqSelection;
import static ie.ianbuttimer.moviequest.tmdb.MovieListResponse.MOVIE_PAGE;
import static ie.ianbuttimer.moviequest.tmdb.MovieListResponse.MOVIE_RESULTS;
import static ie.ianbuttimer.moviequest.tmdb.MovieListResponse.MOVIE_TOTAL_PAGES;
import static ie.ianbuttimer.moviequest.tmdb.MovieListResponse.MOVIE_TOTAL_RESULTS;
import static ie.ianbuttimer.moviequest.tmdb.MovieListResponse.RESULTS_PER_LIST;
import static ie.ianbuttimer.moviequest.tmdb.MovieListResponse.RESULTS_PER_PAGES;
import static ie.ianbuttimer.moviequest.data.ICallback.CONTENT_PROVIDER_RESULT_TYPE;
import static ie.ianbuttimer.moviequest.utils.DbUtils.DB_RAW_BOOLEAN_TRUE;

/**
 * ContentProvider class for movies
 *
 * Android unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieContentProviderTest
 */

public class MovieContentProvider extends ContentProvider {

    private static final String TAG = MovieContentProvider.class.getSimpleName();

    /** Popular movies constant */
    public static final int POPULAR_MOVIES = 100;
    /** Top rated movies constant */
    public static final int TOP_RATED_MOVIES = 101;
    /** Movie directory constant */
    public static final int MOVIES = 200;
    /** Individual movie constant */
    public static final int MOVIE_WITH_ID = MOVIES + 1;
    /** Movie directory constant */
    public static final int FAVOURITES = 300;
    /** Individual movie constant */
    public static final int FAVOURITE_WITH_ID = FAVOURITES + 1;

    /** Movie content provider Uri matcher */
    public static final UriMatcher sUriMatcher = buildUriMatcher();


    /**
     * Build the UriMatcher for use by this ContentProvider
     * @return UriMatcher object
     */
    public static UriMatcher buildUriMatcher() {
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(AUTHORITY, PATH_MOVIES, MOVIES);
        matcher.addURI(AUTHORITY, PATH_MOVIES + PATH_WITH_ID, MOVIE_WITH_ID);
        matcher.addURI(AUTHORITY, PATH_FAVOURITES, FAVOURITES);
        matcher.addURI(AUTHORITY, PATH_FAVOURITES + PATH_WITH_ID, FAVOURITE_WITH_ID);
        matcher.addURI(AUTHORITY, PATH_POPULAR_MOVIES, POPULAR_MOVIES);
        matcher.addURI(AUTHORITY, PATH_TOP_RATED_MOVIES, TOP_RATED_MOVIES);

        return matcher;
    }


    private MovieDbHelper dbHelper; // database helper


    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
            case FAVOURITES:
                cursor = db.query(getTable(match), projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIE_WITH_ID:
            case FAVOURITE_WITH_ID:
                // ignore selection & selectionArgs arguments as have id in uri
                cursor = db.query(getTable(match), projection, ID_EQ_SELECTION,
                                    UriUtils.getIdSelectionArgFromWithIdUri(uri),
                                    null, null, sortOrder);
                break;
            default:
                throwUnsupportedException(uri, "query");
        }

        // Set a notification URI on the Cursor
        Context context = getContext();
        if (context != null) {
            cursor.setNotificationUri(context.getContentResolver(), uri);
        }

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri resultUri = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
            case FAVOURITES:
                long id = db.insert(getTable(match), null, contentValues);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(uri, id);
                }
                break;
            default:
                throwUnsupportedException(uri, "insert");
        }

        // Notify the resolver if the uri has been changed
        if (resultUri != null) {
            notifyChange(uri, null);
        }

        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
            case FAVOURITES:
                count = db.delete(getTable(match), selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
            case FAVOURITE_WITH_ID:
                // ignore selection & selectionArgs arguments as have id in uri
                count = db.delete(getTable(match), ID_EQ_SELECTION,
                                    UriUtils.getIdSelectionArgFromWithIdUri(uri));
                break;
            default:
                throwUnsupportedException(uri, "delete");
        }

        // Notify the resolver of a change
        if (count > 0) {
            notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = 0;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES:
            case FAVOURITES:
                count = db.update(getTable(match), contentValues, selection, selectionArgs);
                break;
            case MOVIE_WITH_ID:
            case FAVOURITE_WITH_ID:
                // ignore selection & selectionArgs arguments as have id
                count = db.update(getTable(match), contentValues, ID_EQ_SELECTION,
                                    UriUtils.getIdSelectionArgFromWithIdUri(uri));
                break;
            default:
                throwUnsupportedException(uri, "update");
        }

        // Notify the resolver of a change
        if (count > 0) {
            notifyChange(uri, null);
        }

        return count;
    }

    @Nullable
    @Override
    public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle bundle = new Bundle();
        Context context = getContext();
        URL url = null;
        Bundle bundleResult = null;

        switch (method) {
            case GET_POPULAR_METHOD:    // request the popular movies list
                url = TMDbNetworkUtils.buildGetPopularUrl(context);
                break;
            case GET_TOP_RATED_METHOD:  // request the top rated movies list
                url = TMDbNetworkUtils.buildGetTopRatedUrl(context);
                break;
            case GET_FAVOURITE_METHOD:
                bundleResult = getFavouritesList(extras);
                break;
            case GET_DETAILS_METHOD:    // request individual movie details
                int id = 0;
                if (arg != null) {
                    try {
                        id = Integer.parseInt(arg);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (id > 0) {
                    url = TMDbNetworkUtils.buildGetDetailsUrl(context, id);
                }
                break;
            default:
                throwUnsupportedException(null, method);
        }
        if (url != null) {
            // put the response from the url into the bundle
            bundle.putInt(CONTENT_PROVIDER_RESULT_TYPE, AbstractResultWrapper.ResultType.STRING.ordinal());
            bundle.putString(method, getHttpResponseStringSync(url));
        } else if (bundleResult != null) {
            // put list into the bundle
            bundle.putInt(CONTENT_PROVIDER_RESULT_TYPE, AbstractResultWrapper.ResultType.BUNDLE.ordinal());
            bundle.putBundle(method, bundleResult);
        }
        return bundle;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        Uri contentUri = getContentUri(match);
        String type = contentUri.toString().substring(BASE_CONTENT_URI.toString().length());

        switch (match) {
            case MOVIES:
            case FAVOURITES:
                type = "vnd.android.cursor.dir" + type;
                break;
            case MOVIE_WITH_ID:
            case FAVOURITE_WITH_ID:
                type = "vnd.android.cursor.item" + type;
                break;
            default:
                throwUnsupportedException(uri, "getType");
        }
        return type;
    }

    /**
     * Get the table name corresponding to the specified match id
     * @param match     Match id
     * @return  Table name
     */
    private String getTable(int match) {
        String table;
        switch (match) {
            case MOVIES:
            case MOVIE_WITH_ID:
                table = MovieContract.MovieEntry.TABLE_NAME;
                break;
            case FAVOURITES:
            case FAVOURITE_WITH_ID:
                table = MovieContract.FavouriteEntry.TABLE_NAME;
                break;
            default:
                throw new UnsupportedOperationException("No table for unknown match: " + match);
        }
        return table;
    }

    /**
     * Get the Content Uri corresponding to the specified match id
     * @param match     Match id
     * @return  Content Uri
     */
    private Uri getContentUri(int match) {
        Uri uri;
        switch (match) {
            case MOVIES:
            case MOVIE_WITH_ID:
                uri = MovieEntry.CONTENT_URI;
                break;
            case FAVOURITES:
            case FAVOURITE_WITH_ID:
                uri = FavouriteEntry.CONTENT_URI;
                break;
            default:
                throw new UnsupportedOperationException("No uri for unknown match: " + match);
        }
        return uri;
    }


    /**
     * This method synchronously returns the entire result from a HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, or <code>null</code>
     * @see NetworkUtils#getHttpResponseStringSync
     */
    private String getHttpResponseStringSync(URL url) {
        String result = "";
        try {
            result = NetworkUtils.getHttpResponseStringSync(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Notify registered observers that a row was updated and attempt to sync changes to the network.
     * @param uri       The uri of the content that was changed.
     * @param observer  The observer that originated the change
     */
    private void notifyChange(@NonNull Uri uri, ContentObserver observer) {
        Context context = getContext();
        if (context != null) {
            context.getContentResolver().notifyChange(uri, observer);
        }
    }

    /**
     * Throw a new UnsupportedOperationException
     * @param uri       Uri which caused exception
     * @param method    Method uri was involked on
     */
    private void throwUnsupportedException(Uri uri, String method) {
        String message = "Unknown ";
        if (uri != null) {
            message += " uri " + uri;
        }
        if (!TextUtils.isEmpty(method)) {
            if (uri == null) {
                message += " method " + method;
            } else {
                message += " for " + method;
            }
        }
        throw new UnsupportedOperationException(message);
    }

    /**
     * Get a list of favourite movies
     * @param extras    Arguments bundle
     * @return  Favourite result bundle
     */
    private Bundle getFavouritesList(Bundle extras) {

        Bundle result = new Bundle();

        // get all favourites
        Cursor cursor = query(FavouriteEntry.CONTENT_URI, null, columnEqSelection(COLUMN_FAVOURITE), new String[] {
            DB_RAW_BOOLEAN_TRUE
        }, null);

        if (cursor != null) {
            int count = cursor.getCount();
            int perPage = extras.getInt(RESULTS_PER_PAGES, RESULTS_PER_LIST);
            int page = extras.getInt(MOVIE_PAGE, 0);

            if (count > 0) {
                int idIndex = cursor.getColumnIndex(FavouriteEntry._ID);
                int titleIndex = cursor.getColumnIndex(FavouriteEntry.COLUMN_TITLE);
                int start = (page * perPage);                   // start position
                int limit = Math.min((start + perPage), count); // end position
                int noResponseLimit = 1;    // number of time to get no response before aborting network requests

                if (start < limit) {
                    String[] list = new String[limit - start];
                    for (int i = start; i < limit; ++i) {

                        cursor.moveToPosition(i);
                        String id = cursor.getString(idIndex);  // movie id

                        // see if movie in db
                        Cursor movieCursor = query(MovieEntry.CONTENT_URI, null, ID_EQ_SELECTION, new String[]{
                                id
                        }, null);

                        if (movieCursor.moveToNext()) {
                            // add cached info
                            int jsonIndex = cursor.getColumnIndex(FavouriteEntry.COLUMN_FAVOURITE);
                            list[i] = movieCursor.getString(jsonIndex);
                        } else {
                            // request info from server
                            String json;
                            if (noResponseLimit > 0) {
                                Bundle movieBundle = call(GET_DETAILS_METHOD, id, null);
                                json = movieBundle.getString(GET_DETAILS_METHOD, "");
                            } else {
                                // don't request as not getting responses
                                json = null;
                                Log.i(TAG, "No response limit exceeded, no requesting");
                            }
                            if (TextUtils.isEmpty(json)) {
                                /** make placeholder object with id & title
                                    @see MovieInfo#isPlaceHolder()
                                 */
                                MovieDetails movie = new MovieDetails(Integer.valueOf(id), cursor.getString(titleIndex));
                                json = new Gson().toJson(movie);

                                --noResponseLimit;
                                Log.i(TAG, "Received no response generated placeholder");
                            }
                            list[i] = json;
                        }
                        movieCursor.close();
                    }

                    // mimic the field in a TMDb server list response
                    result.putInt(MOVIE_PAGE, page + 1);    // sever is 1-based
                    result.putInt(MOVIE_TOTAL_RESULTS, count);
                    result.putInt(MOVIE_TOTAL_PAGES, ((count + (perPage - 1)) / perPage));
                    result.putStringArray(MOVIE_RESULTS, list);
                }
            }
            cursor.close();
        }
        return result;
    }


}
