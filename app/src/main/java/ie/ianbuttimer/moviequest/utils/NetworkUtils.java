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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * This is a modified copy of the file of the same name provided in the
 * Android Developer Nanodegree Program from Udacity
 * (https://www.udacity.com/course/android-developer-nanodegree-by-google--nd801)
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Check if an internet connection is available
     * @param context   The current context
     * @return  true if internet connection is available
     */
    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return ((activeNetwork != null) && (activeNetwork.isConnectedOrConnecting()));
    }

    /**
     * Convert a URI to a URL.
     * @param uri   URI to convert
     * @return The URL.
     */
    public static URL convertUriToUrl(Uri uri) {

        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URL " + url);

        return url;
    }

    /**
     * Join to parts of a URL path
     * @param part1     First part of path
     * @param part2     Second part of path
     * @return  Joined URL path
     */
    public static String joinUrlPaths(String part1, String part2) {
        String joined;
        boolean p1Ends = part1.endsWith("/");
        boolean p2Starts = part2.startsWith("/");
        if (p1Ends && p2Starts) {
            joined = part1 + part2.substring(1);
        } else if (!p1Ends && !p2Starts) {
            joined = part1 + "/" + part2;
        } else {
            joined = part1 + part2;
        }
        return joined;
    }

}
