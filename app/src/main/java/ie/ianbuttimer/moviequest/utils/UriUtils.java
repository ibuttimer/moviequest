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

package ie.ianbuttimer.moviequest.utils;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import ie.ianbuttimer.moviequest.data.MovieContentProvider;
import ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry;
import ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry;


/**
 * Utility class for Uri functionality
 */

public class UriUtils {

    /**
     * Make a movie with id uri
     * @param id    Id of movie
     * @return  Uri
     */
    public static Uri getMovieWithIdUri(int id) {
        return ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
    }

    /**
     * Make a favourite with id uri
     * @param id    Id of movie
     * @return  Uri
     */
    public static Uri getFavouriteWithIdUri(int id) {
        return ContentUris.withAppendedId(FavouriteEntry.CONTENT_URI, id);
    }

    /**
     * Get the id from a 'with id' uri
     * @param uri   Uri to get id from
     * @return  Id string
     */
    public static String getIdFromWithIdUri(@NonNull Uri uri) {
        String id = "";
        if (uri != null) {
            id = uri.getLastPathSegment();
        }
        return id;
    }

    /**
     * Get a selection args id array from a 'with id' uri
     * @param uri   Uri to get id from
     * @return  Id array
     */
    public static String[] getIdSelectionArgFromWithIdUri(@NonNull Uri uri) {
        String[] array;
        String id = getIdFromWithIdUri(uri);
        if (TextUtils.isEmpty(id)) {
            array = new String[] {};
        } else {
            array = new String[] { id };
        }
        return array;
    }

    /**
     * Match a movie uri
     * @param uri   Uri to match
     * @return  The code for the matched node, or -1 if there is no matched node.
     */
    public static int matchMovieUri(Uri uri) {
        return MovieContentProvider.sUriMatcher.match(uri);
    }


    /**
     * Convert a Url to a uri
     * @param url   Url to convert
     * @return  Equivalent uri
     */
    public static Uri urlToUri(@NonNull URL url) {
        Uri uri = null;
        try {
            URI netUri = url.toURI();
            uri = Uri.parse(netUri.toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * Convert a uri to a Url
     * @param uri   Uri to convert
     * @return  Equivalent Url
     */
    public static URL uriToUrl(@NonNull Uri uri) {
        URL url = null;
        try {
            URI netUri = URI.create(uri.toString());
            url = netUri.toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

}
