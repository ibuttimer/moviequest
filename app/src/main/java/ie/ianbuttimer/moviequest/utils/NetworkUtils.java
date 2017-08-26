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
package ie.ianbuttimer.moviequest.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Network-related utility function class
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private final static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    /**
     * This method synchronously returns the entire result from a HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, or <code>null</code>
     * @throws IOException If the response was not successfully received, understood, and accepted.
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static String getHttpResponseStringSync(URL url) throws Exception {
        Request request = getHttpRequest(url);
        Call call = client.newCall(request);
        Response response = null;
        String body = null;

        try {
            response = call.execute();

            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            logHeaders(response);

            // response body can only be consumed once, and not on another thread
            body = response.body().string();
            if (body != null) {
                body = body.substring(0);
            }
        }
        finally {
            //  must close the response body to prevent resource leaks
            if (response != null) {
                response.close();
            }
        }
        return body;
    }

    /**
     * Generate a HTTP request.
     * @param url The URL to fetch the HTTP response from.
     * @return Http request
     */
    private static Request getHttpRequest(URL url) {
        return new Request.Builder()
                .url(url.toString())
                .build();
    }

    /**
     * This method synchronously returns the entire result from a HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, or <code>null</code>
     * @throws IOException If the response was not successfully received, understood, and accepted.
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static JSONObject getHttpResponseJsonSync(URL url) throws Exception {
        String body = getHttpResponseStringSync(url);
        JSONObject json = null;
        try {
            if (body != null) {
                json = new JSONObject(body);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Log http response headers
     * @param response  Http response
     */
    private static void logHeaders(Response response) {
        Headers responseHeaders = response.headers();
        for (int i = 0; i < responseHeaders.size(); i++) {
            Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
        }
    }

    /**
     * This method asynchronously returns the entire result from a HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @see <a href="https://github.com/square/okhttp/wiki/Recipes">okhttp Recipes</a>
     */
    public static void getHttpResponseStringAsync(URL url, ICallback callback) {
        Request request = getHttpRequest(url);
        client.newCall(request).enqueue(callback);
    }

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

    /**
     * Join to parts of a URL path
     * @param parts     Part of path
     * @return  Joined URL path
     */
    public static String joinUrlPaths(String[] parts) {
        String joined = parts[0];
        for (int i = 1, ll = parts.length; i < ll; i++) {
            joined = joinUrlPaths(joined, parts[i]);
        }
        return joined;
    }

}
