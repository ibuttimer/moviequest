/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ie.ianbuttimer.moviequest.utils;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.net.URL;
import java.util.HashMap;

import ie.ianbuttimer.moviequest.R;

/**
 * This is a modified copy of the file of the same name provided in the
 * Android Developer Nanodegree Program from Udacity
 * (https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801)
 */
@SuppressWarnings("unused")
public class TMDbNetworkUtils {

    private static final String TAG = TMDbNetworkUtils.class.getSimpleName();

    // Url of TMDb API.
    public static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";

    // query arguments for all endpoints of the TMDb API
    final static String API_KEY_PARAM = "api_key";
    final static String APPEND_TO_RESP_PARAM = "append_to_response";

    final static String APPEND_VIDEOS = "videos";
    final static String APPEND_REVIEWS = "reviews";

    /* API endpoint for Get Movie Details on TMDb.
        See https://developers.themoviedb.org/3/movies/get-movie-details */
    private static final String GET_DETAILS = "movie/";
    /* API endpoint for Get Top Rated movies on TMDb.
        See https://developers.themoviedb.org/3/movies/get-top-rated-movies */
    private static final String GET_TOP_RATED = "movie/top_rated";
    /* API endpoint for Get Popular movies on TMDb.
        See https://developers.themoviedb.org/3/movies/get-popular-movies */
    private static final String GET_POPULAR = "movie/popular";

    // query arguments for movie endpoint of the TMDb API
    final static String LANGUAGE_PARAM = "language";
    final static String PAGE_PARAM = "page";
    final static String REGION_PARAM = "region";

    /* API endpoint for Get API Configuration on TMDb.
        https://developers.themoviedb.org/3/configuration/get-api-configuration */
    private static final String GET_CONFIGURATION = "configuration";

    // Url of TMDb images.
    public static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";


    /**
     * Build a URI for the TMDb server.
     * @param context   Context to use
     * @param baseUrl   Base URL to use
     * @param endPoint  API endpoint
     * @param params    Query parameters to append to URL.
     * @return The URI to use to query the server.
     */
    public static Uri buildUri(Context context, String baseUrl, String endPoint, HashMap<String, String> params) {

        String apiKey = Utils.getManifestMetaData(context, "TMDB_API_KEY");

        Uri.Builder builder = Uri.parse(baseUrl + endPoint).buildUpon();
        builder.appendQueryParameter(API_KEY_PARAM, apiKey);
        if (params != null) {
            for (String key : params.keySet()) {
                String value = params.get(key);
                builder.appendQueryParameter(key, value);
            }
        }

        Uri uri = builder.build();

        Log.v(TAG, "Built Uri " + uri.toString());

        return uri;
    }

    /**
     * Build a URL for the TMDb server.
     * @param context   Context to use
     * @param baseUrl   Base URL to use
     * @param endPoint  API endpoint
     * @param params    Query parameters to append to URL.
     * @return The URL to use to query the server.
     */
    public static URL buildUrl(Context context, String baseUrl, String endPoint, HashMap<String, String> params) {
        Uri uri = buildUri(context, baseUrl, endPoint, params);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build a movie Uri for the TMDb server.
     * @param context   Context to use
     * @param endPoint  API endpoint
     * @param language  A ISO 639-1 value to display translated data for the fields that support it.
     * @param page      Which page to query.
     * @param region    A ISO 3166-1 code to filter release dates. Must be uppercase.
     * @return The URL to use to query the server.
     */
    public static Uri buildMovieUri(Context context, String endPoint, String language, int page, String region) {

        HashMap<String, String> params = new HashMap<String, String>();

        if (Utils.stringHasContent(language)) {
            params.put(LANGUAGE_PARAM, language);
        }
        if (page > 0) {
            params.put(PAGE_PARAM, Integer.toString(page));
        }
        if (Utils.stringHasContent(region)) {
            params.put(REGION_PARAM, region);
        }
        return buildUri(context, TMDB_BASE_URL, endPoint, params);
    }

    /**
     * Build a movie URL for the TMDb server.
     * @param context   Context to use
     * @param endPoint  API endpoint
     * @param language  A ISO 639-1 value to display translated data for the fields that support it.
     * @param page      Which page to query.
     * @param region    A ISO 3166-1 code to filter release dates. Must be uppercase.
     * @return The URL to use to query the server.
     */
    public static URL buildMovieUrl(Context context, String endPoint, String language, int page, String region) {
        Uri uri = buildMovieUri(context, endPoint, language, page, region);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build a movie Uri for the TMDb server, supplying only required attributes
     * @param context   Context to use
     * @param endPoint  API endpoint
     * @return The URL to use to query the server.
     */
    public static Uri buildMovieUri(Context context, String endPoint) {
        return buildMovieUri(context, endPoint, null, 0, null);
    }

    /**
     * Build a movieURL for the TMDb server, supplying only required attributes
     * @param context   Context to use
     * @param endPoint  API endpoint
     * @return The URL to use to query the server.
     */
    public static URL buildMovieUrl(Context context, String endPoint) {
        Uri uri = buildMovieUri(context, endPoint);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build the Get Top Rated Uri for the TMDb server.
     * @param context   Context to use
     * @param language  A ISO 639-1 value to display translated data for the fields that support it.
     * @param page      Which page to query.
     * @param region    A ISO 3166-1 code to filter release dates. Must be uppercase.
     * @return The URL to use to query the server.
     */
    public static Uri buildGetTopRatedUri(Context context, String language, int page, String region) {
        return buildMovieUri(context, GET_TOP_RATED, language, page, region);
    }

    /**
     * Build the Get Top Rated URL for the TMDb server.
     * @param context   Context to use
     * @param language  A ISO 639-1 value to display translated data for the fields that support it.
     * @param page      Which page to query.
     * @param region    A ISO 3166-1 code to filter release dates. Must be uppercase.
     * @return The URL to use to query the server.
     */
    public static URL buildGetTopRatedUrl(Context context, String language, int page, String region) {
        Uri uri = buildGetTopRatedUri(context, language, page, region);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build the Get Top Rated URI for the TMDb server, supplying only required attributes
     * @param context   Context to use
     * @return The Uri to use to query the server.
     */
    public static Uri buildGetTopRatedUri(Context context) {
        return buildGetTopRatedUri(context, null, 0, null);
    }

    /**
     * Build the Get Top Rated URL for the TMDb server, supplying only required attributes
     * @param context   Context to use
     * @return The URL to use to query the server.
     */
    public static URL buildGetTopRatedUrl(Context context) {
        Uri uri = buildGetTopRatedUri(context);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build the Get Popular URI for the TMDb server.
     * @param context   Context to use
     * @param language  A ISO 639-1 value to display translated data for the fields that support it.
     * @param page      Which page to query.
     * @param region    A ISO 3166-1 code to filter release dates. Must be uppercase.
     * @return The URI to use to query the server.
     */
    public static Uri buildGetPopularUri(Context context, String language, int page, String region) {
        return buildMovieUri(context, GET_POPULAR, language, page, region);
    }

    /**
     * Build the Get Popular URL for the TMDb server.
     * @param context   Context to use
     * @param language  A ISO 639-1 value to display translated data for the fields that support it.
     * @param page      Which page to query.
     * @param region    A ISO 3166-1 code to filter release dates. Must be uppercase.
     * @return The URL to use to query the server.
     */
    public static URL buildGetPopularUrl(Context context, String language, int page, String region) {
        Uri uri = buildGetPopularUri(context, language, page, region);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build the Get Popular URI for the TMDb server, supplying only required attributes
     * @param context   Context to use
     * @return The URI to use to query the server.
     */
    public static Uri buildGetPopularUri(Context context) {
        return buildGetPopularUri(context, null, 0, null);
    }

    /**
     * Build the Get Popular URL for the TMDb server, supplying only required attributes
     * @param context   Context to use
     * @return The URL to use to query the server.
     */
    public static URL buildGetPopularUrl(Context context) {
        Uri uri = buildGetPopularUri(context);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build the get movie list URL for the TMDb server
     * @param context   Context to use
     * @param list      The list to request
     * @return The URL to use to query the server.
     */
    public static URL buildGetMovieListUrl(Context context, String list) {
        URL url;
        if (list == null) {
            list = PreferenceControl.getMovieListPreference(context);
        }
        String popularList = context.getString(R.string.pref_movie_list_popular);
        String topRatedList = context.getString(R.string.pref_movie_list_top_rated);

        if (list.equals(popularList)) {
            url = buildGetPopularUrl(context);
        } else if (list.equals(topRatedList)) {
            url = buildGetTopRatedUrl(context);
        } else {
            url = buildGetMovieListUrl(context, null);
        }
        return url;
    }

    /**
     * Build the get movie list URL for the TMDb server, based on user preference
     * @param context   Context to use
     * @return The URL to use to query the server.
     */
    public static URL buildGetMovieListUrl(Context context) {
        return buildGetMovieListUrl(context, null);
    }

    /**
     * Build the Configuration URI for the TMDb server.
     * @param context   Context to use
     * @return The URI to use to query the server.
     */
    public static Uri buildGetConfigurationUri(Context context) {
        return buildUri(context, TMDB_BASE_URL, GET_CONFIGURATION, null);
    }

    /**
     * Build the Configuration URL for the TMDb server.
     * @param context   Context to use
     * @return The URL to use to query the server.
     */
    public static URL buildGetConfigurationUrl(Context context) {
        Uri uri = buildGetConfigurationUri(context);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Convert a raw size to the size path portion of an image url
     * @param size  Raw size
     * @return  Size path
     */
    public static String sizePath(String size) {
        // Note: the size element of a TMDb image url is 'w<size>'
        return "w" + size;
    }

    /**
     * Convert a raw size to the size path portion of an image url
     * @param size  Raw size
     * @return  Size path
     */
    public static String sizePath(int size) {
        return sizePath(String.valueOf(size));
    }

    /**
     * Build a movie get image URI for the TMDb server.
     * @param context   Context to use
     * @param size      Image size to request
     * @param path      Path to image size to request
     * @return The URI to use to query the server, or <code>null</code> if not able build
     */
    public static @Nullable Uri buildGetImageUri(Context context, String size, String path) {
        Uri uri;
        if (TextUtils.isEmpty(size) || TextUtils.isEmpty(path)) {
            uri = null;
        } else {
            String endPoint = NetworkUtils.joinUrlPaths(sizePath(size), path);
            uri = buildUri(context, IMAGE_BASE_URL, endPoint, null);
        }
        return uri;
    }

    /**
     * Build a movie get image URL for the TMDb server.
     * @param context   Context to use
     * @param size      Image size to request
     * @param path      Path to image size to request
     * @return The URL to use to query the server, or <code>null</code> if not able build
     */
    public static @Nullable URL buildGetImageUrl(Context context, String size, String path) {
        Uri uri = buildGetImageUri(context, sizePath(size), path);
        return NetworkUtils.convertUriToUrl(uri);
    }

    /**
     * Build a movie get details URI for the TMDb server.
     * @param context   Context to use
     * @param id        Id of movie to request details for
     * @return The URI to use to query the server.
     */
    public static Uri buildGetDetailsUri(Context context, int id) {
        String endPoint = NetworkUtils.joinUrlPaths(GET_DETAILS, String.valueOf(id));
        return buildUri(context, TMDB_BASE_URL, endPoint, null);
    }

    /**
     * Build a movie get details URL for the TMDb server.
     * @param context   Context to use
     * @param id        Id of movie to request details for
     * @return The URL to use to query the server.
     */
    public static URL buildGetDetailsUrl(Context context, int id) {
        Uri uri = buildGetDetailsUri(context, id);
        return NetworkUtils.convertUriToUrl(uri);
    }




}
