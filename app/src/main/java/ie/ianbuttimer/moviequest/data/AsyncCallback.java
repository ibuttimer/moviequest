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

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

import ie.ianbuttimer.moviequest.R;
import ie.ianbuttimer.moviequest.utils.HttpException;
import ie.ianbuttimer.moviequest.utils.NetworkUtils;
import okhttp3.Call;
import okhttp3.Response;

import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;

/**
 * Class providing basic handling of both okhttp3 and content provider responses.<br>
 * The class <code>T</code> specifies the type of object to be processed by the application.<br>
 * <br>
 * Requests may be initiated in a number of ways:
 * <ul>
 * <li>{@link AsyncCallback#request(URL)} / {@link AsyncCallback#processUrlResponse(URL, Response)} / {@link AsyncCallback#onResponse(Object)}<br>
 *     Plain http request, where the server response is transformed into the appropriate object in the processUrlResponse() method,
 *     and the response handled in onResponse(). Error responses are handled in {@link AsyncCallback#onFailure(Call, IOException)}
 * </li>
 * <li>{@link ICallback#call(AppCompatActivity, int, Uri, String, String, Bundle)} / {@link AsyncCallback#processUriResponse(AbstractResultWrapper)} / {@link AsyncCallback#onResponse(Object)}<br>
 *     Plain http request via a http Uri. Utilises the call() method of a ContentProvider to provide the http response.
 * </li>
 * <li>{@link ICallback#insert(AppCompatActivity, int, Uri, ContentValues)} / {@link AsyncCallback#processInsertResponse(InsertResultWrapper)}<br>
 *     Utilises the insert() method of a ContentProvider to add data to a database.
 * </li>
 * <li>{@link ICallback#query(AppCompatActivity, int, Uri, String[], String, String[], String)} / {@link AsyncCallback#processQueryResponse(QueryResultWrapper)}<br>
 *     Utilises the query() method of a ContentProvider to read data from a database.
 * </li>
 * <li>{@link ICallback#update(AppCompatActivity, int, Uri, ContentValues, String, String[])} / {@link AsyncCallback#processUpdateResponse(UpdateResultWrapper)}<br>
 *     Utilises the update() method of a ContentProvider to update data in a database.
 * </li>
 * <li>{@link ICallback#delete(AppCompatActivity, int, Uri, String, String[])} / {@link AsyncCallback#processDeleteResponse(DeleteResultWrapper)}<br>
 *     Utilises the delete() method of a ContentProvider to delete data from a database.
 * </li>
 * </ul>
 */

public abstract class AsyncCallback<T> implements ICallback<T>, LoaderManager.LoaderCallbacks<AbstractResultWrapper> {

        //////// ICallback implementation ////////

    /**
     * Called when the HTTP request could not be executed due to cancellation, a connectivity problem or timeout.
     * @param call  Original request that has been prepared for execution
     * @param e     Failure reason
     * @see <a href="https://square.github.io/okhttp/3.x/okhttp/okhttp3/Callback.html">okhttp3.Callback</a>
     */
    @Override
    public void onFailure(@Nullable Call call, @Nullable IOException e) {
        if (e != null) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the HTTP response was successfully returned by the remote server.
     * @param call      Original request that has been prepared for execution
     * @param response  HTTP response
     * @throws IOException if response was unsuccessful
     * @see <a href="https://square.github.io/okhttp/3.x/okhttp/okhttp3/Callback.html">okhttp3.Callback</a>
     */
    @Override
    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
        try {
            if (!response.isSuccessful()) {
                // unsuccessful
                throw new IOException("Unexpected code " + response);
            }
            // else result code is in [200..300], the request was successfully received, understood, and accepted.

            // process raw http response & handle
            onResponse(processUrlResponse(call.request().url().url(), response));
        }
        finally {
            //  must close the response body to prevent resource leaks
            response.close();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void request(@NonNull URL url) {
        try {
            /* request the url via okhttp3, providing this as the Callback, meaning the response is
             * handled in the onResponse() method */
            NetworkUtils.getHttpResponseStringAsync(url, this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void insert(@NonNull AppCompatActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putContentValues(contentValues)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_INSERT_LOADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void query(@NonNull AppCompatActivity activity, int loaderId, @NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putProjection(projection)
                .putSelection(selection)
                .putSelectionArgs(selectionArgs)
                .putSortOrder(sortOrder)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_QUERY_LOADER));
    }

    /**
     * Convenience wrapper for {@link ICallback#query(AppCompatActivity, int, Uri, String[], String, String[], String) )}
     * @param activity      Current activity
     * @param loaderId      Id of loader
     * @param uri           The URI for the newly inserted item.
     */
    public void query(@NonNull AppCompatActivity activity, int loaderId, @NonNull Uri uri) {
        query(activity, loaderId, uri, null, null, null, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(@NonNull AppCompatActivity activity, int loaderId, @NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putContentValues(contentValues)
                .putSelection(selection)
                .putSelectionArgs(selectionArgs)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_UPDATE_LOADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(@NonNull AppCompatActivity activity, int loaderId, @NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putSelection(selection)
                .putSelectionArgs(selectionArgs)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_DELETE_LOADER));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void call(@NonNull AppCompatActivity activity, int loaderId, @NonNull Uri uri, @NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
        Bundle args = AsyncCallbackBundle.builder()
                .putUri(uri)
                .putMethod(method)
                .putArg(arg)
                .putExtras(extras)
                .build();
        // setup the loader to use
        startLoader(activity, args, generateLoaderId(loaderId, CONTENT_PROVIDER_CALL_LOADER));
    }

    /**
     * Start a loader
     * @param activity  Current activity
     * @param args      Arguments bundle
     * @param loaderId  Id of loader to start
     */
    private void startLoader(@NonNull AppCompatActivity activity, @NonNull Bundle args, int loaderId) {
        LoaderManager manager = activity.getSupportLoaderManager();
        Loader loader = manager.getLoader(loaderId);
        if (loader == null) {
            // Initialize the loader
            manager.initLoader(loaderId, args, this);
        } else {
            manager.restartLoader(loaderId, args, this);
        }
    }

    //////// LoaderManager.LoaderCallbacks implementation ////////

    @Override
    public Loader<AbstractResultWrapper> onCreateLoader(int id, final Bundle args) {
        ContentProviderLoader loader;
        Context context = getContext();
        switch (getSubLoaderId(id)) {
            case CONTENT_PROVIDER_CALL_LOADER:
                // create an AsyncTaskLoader to handle the call to the content provider
                loader = new ContentProviderCallLoader(context, args);
                break;
            case CONTENT_PROVIDER_INSERT_LOADER:
            case CONTENT_PROVIDER_QUERY_LOADER:
            case CONTENT_PROVIDER_UPDATE_LOADER:
            case CONTENT_PROVIDER_DELETE_LOADER:
                loader = new ContentProviderCrudLoader(context, args);
                break;
            default:
                throw new RuntimeException("Loader not implemented: " + id);
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<AbstractResultWrapper> loader, AbstractResultWrapper data) {

        if (data != null) {
            ResponseHandler handler = data.getHandler();
            switch (handler) {
                case URL_PROVIDER_HANDLER:
                    if (data.isError()) {
                        Pair<Integer, String> pair = data.getErrorResult();
                        onFailure(pair.first, pair.second);
                    } else {
                        onResponse(processUriResponse(data));
                    }
                    break;
                case INSERT_HANDLER:
                    processInsertResponse((ICallback.InsertResultWrapper)data);
                    break;
                case QUERY_HANDLER:
                    processQueryResponse((ICallback.QueryResultWrapper)data);
                    break;
                case UPDATE_HANDLER:
                    processUpdateResponse((ICallback.UpdateResultWrapper)data);
                    break;
                case DELETE_HANDLER:
                    processDeleteResponse((ICallback.DeleteResultWrapper)data);
                    break;
                default:
                    /* a UrlResultWrapper i.e. URL_HANDLER will not arrive here as it is handled
                        directly by the onResponse/onFailure methods */
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // noop
    }


    @Override
    public void processInsertResponse(@Nullable InsertResultWrapper response) {
        // noop
    }

    @Override
    public void processQueryResponse(@Nullable QueryResultWrapper response) {
        // noop
    }

    @Override
    public void processUpdateResponse(@Nullable UpdateResultWrapper response) {
        // noop
    }

    @Override
    public void processDeleteResponse(@Nullable DeleteResultWrapper response) {
        // noop
    }


    //////// Additional methods ////////

    /**
     * Get the resource id of the error message corresponding to an exception
     * @param call  Original request
     * @param e     Exception
     * @return resource id
     */
    protected int getErrorId(Call call, IOException e) {
        int msgId = 0;
        if (e instanceof UnknownHostException) {
            msgId = R.string.cant_contact_server;
        } else if (e instanceof HttpException) {
            if (((HttpException) e).isUnauthorised()) {
                msgId = R.string.unauthorised_access;
            }
        }
        return msgId;
    }

    /**
     * Get the resource id of the error message corresponding to an error code
     * @param code  Error code
     * @return resource id
     */
    protected int getErrorId(int code) {
        int msgId;
        switch (code) {
            case HTTP_UNAUTHORIZED:
                msgId = R.string.unauthorised_access;
                break;
            default:
                msgId = 0;
                break;
        }
        return msgId;
    }

    /**
     * Get the current context
     * @return  Current context
     */
    public abstract Context getContext();

    /**
     * Generate a loader id for the LoaderManager
     * @param clientId  Id provided by client
     * @param subId     Sub id i.e. method indicator
     * @return  Loader id
     */
    public static int generateLoaderId(int clientId, int subId) {
        return ((clientId * LOADER_FACTOR) + subId);
    }

    /**
     * Get the sub id from a LoaderManager id
     * @param loaderId  LoaderManager id
     * @return  sub id
     */
    public static int getSubLoaderId(int loaderId) {
        return (loaderId % LOADER_FACTOR);
    }

}
