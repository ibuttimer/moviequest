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

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

import java.util.Collections;

import static android.provider.BaseColumns._ID;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.COLUMN_TIMESTAMP;

/**
 * Database contract class
 */
@SuppressWarnings("unused")
public class MovieContract {

    /** Content authority for database content provider */
    static final String AUTHORITY = "ie.ianbuttimer.moviequest";
    /** Base Uri for content provider */
    static final Uri BASE_CONTENT_URI;

    /** Movie lists path for content provider */
    static final String PATH_MOVIE_LIST = "move_lists";

    /** Popular movies path for content provider */
    static final String PATH_POPULAR_MOVIES = "popular";

    /** Top rated movies path for content provider */
    static final String PATH_TOP_RATED_MOVIES = "top_rated";

    /** Movies path for content provider */
    static final String PATH_MOVIES = "movies";
    /** Individual item path for content provider */
    static final String PATH_WITH_ID = "/#";
    /** Individual item reviews path for content provider */
    static final String PATH_WITH_ID_REVIEWS = "/#/reviews";
    /** Individual item videos path for content provider */
    static final String PATH_WITH_ID_VIDEOS = "/#/videos";

    /** Favourites path for content provider */
    static final String PATH_FAVOURITES = "favourites";

    static {
        Uri.Builder builder = new Uri.Builder().
                scheme(ContentResolver.SCHEME_CONTENT).
                encodedAuthority(AUTHORITY);
        BASE_CONTENT_URI = builder.build();
    }

    /**
     * Claas to define the movies table
     */
    public static final class MovieLists {

        /** Movies Uri for content provider */
        public static final Uri MOVIE_LIST_CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE_LIST).build();

        /** Method name to get popular movies list */
        public static final String GET_POPULAR_METHOD = "getPopularList";

        /** Method name to get top rated movies list */
        public static final String GET_TOP_RATED_METHOD = "getTopRatedList";

        /** Method name to get favourite movies list */
        public static final String GET_FAVOURITE_METHOD = "getFavouriteList";
    }

    /**
     * Claas to define the movies table
     */
    public static final class MovieEntry implements BaseColumns {

        /** Movies Uri for content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        // Movies table and column names
        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_JSON = "json";                // json string received from server
        public static final String COLUMN_TIMESTAMP = "timestamp";       // timestamp of server response

        /** Method name to get movies details */
        public static final String GET_DETAILS_METHOD = "getMovieDetails";;
        /** Method name to get movies video details */
        public static final String GET_VIDEOS_METHOD = "getMovieVideos";;
        /** Method name to get movies review details */
        public static final String GET_REVIEWS_METHOD = "getMovieReviews";;

        /** Extra bundle key for append to response details */
        public static final String APPEND_TO_RESPONSE = "appendToResponse";;
    }

    /**
     * Claas to define the movies table
     */
    public static final class FavouriteEntry implements BaseColumns {

        /** Movies Uri for content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();;

        // Movies table and column names
        public static final String TABLE_NAME = "favourites";

        public static final String COLUMN_FAVOURITE = "favourite";  // favourite flag
        public static final String COLUMN_TITLE = "title";          // movie title

    }


    /** String for a selection by id */
    public static final String ID_EQ_SELECTION = columnEqSelection(_ID);
    /** String for a selection by greater than or equal to timestamp */
    public static final String TIMESTAMP_GTEQ_SELECTION = columnGtEqSelection(COLUMN_TIMESTAMP);
    /** String for a selection by less than or equal to timestamp */
    public static final String TIMESTAMP_LTEQ_SELECTION = columnLtEqSelection(COLUMN_TIMESTAMP);

    /**
     * Make a column equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnEqSelection(String column) {
        return column + "=?";
    }

     /**
     * Make a column in selection argument
     * @param column    Column name
     * @param count     Number of items to delete
     * @return  Selection string
     */
    public static String columnInSelection(String column, int count) {
        return String.format(column + " IN (%s)",
                TextUtils.join(",", Collections.nCopies(count, "?")));
    }

    /**
     * Make a column greater than or equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnGtEqSelection(String column) {
        return column + ">=?";
    }

    /**
     * Make a column less than or equal to selection argument
     * @param column    Column name
     * @return  Selection string
     */
    public static String columnLtEqSelection(String column) {
        return column + "<=?";
    }


}
