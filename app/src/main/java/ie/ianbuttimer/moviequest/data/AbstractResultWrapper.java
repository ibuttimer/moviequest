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

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.net.URL;

import ie.ianbuttimer.moviequest.utils.UriURLPair;

/**
 * Wrapper class for ICallback results
 */
@SuppressWarnings("unused")
public abstract class AbstractResultWrapper {

    public enum ResultType { STRING, STRING_ARRAY, INTEGER, CUSROR, URI, BUNDLE };

    private ICallback.ResponseHandler handler;  // type of handler required to process this object
    protected UriURLPair request;           // uri/url used to make request

    protected String stringResult;          // returned from url & uri call
    protected String[] stringArrayResult;   // returned from uri call
    protected int intResult;                // returned from update & delete
    protected Cursor cursorResult;          // returned from query
    protected Uri uriResult;                // returned from insert
    protected Bundle bundleResult;          // returned from uri call

    private ResultType resultType;


    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param urlRequest    Original request URL
     * @param stringResult  String response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull URL urlRequest, String stringResult) {
        this.handler = handler;
        this.request = new UriURLPair(urlRequest);
        this.stringResult = stringResult;
        this.resultType = ResultType.STRING;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param stringResult  String response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, String stringResult) {
        initUri(handler, uriRequest);
        this.stringResult = stringResult;
        this.resultType = ResultType.STRING;
    }

    /**
     * Constructor
     * @param handler               Type of handler required to process this object
     * @param uriRequest            Original request Uri
     * @param stringArrayResult     String array response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, String[] stringArrayResult) {
        initUri(handler, uriRequest);
        this.stringArrayResult = stringArrayResult;
        this.resultType = ResultType.STRING_ARRAY;
    }

    /**
     * Initialise the handler & uri
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     */
    protected void initUri(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest) {
        this.handler = handler;
        this.request = new UriURLPair(uriRequest);
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param intResult     Integer response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, int intResult) {
        initUri(handler, uriRequest);
        this.intResult = intResult;
        this.resultType = ResultType.INTEGER;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param cursorResult  Cursor response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Cursor cursorResult) {
        initUri(handler, uriRequest);
        this.cursorResult = cursorResult;
        this.resultType = ResultType.CUSROR;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param uriResult     Uri response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Uri uriResult) {
        initUri(handler, uriRequest);
        this.uriResult = uriResult;
        this.resultType = ResultType.URI;
    }

    /**
     * Constructor
     * @param handler       Type of handler required to process this object
     * @param uriRequest    Original request Uri
     * @param bundleResult  Bundle response
     */
    public AbstractResultWrapper(@NonNull ICallback.ResponseHandler handler, @NonNull Uri uriRequest, Bundle bundleResult) {
        initUri(handler, uriRequest);
        this.bundleResult = bundleResult;
        this.resultType = ResultType.BUNDLE;
    }

    public String getStringResult() {
        return stringResult;
    }

    public String[] getStringArrayResult() {
        return stringArrayResult;
    }

    public int getIntResult() {
        return intResult;
    }

    public Cursor getCursorResult() {
        return cursorResult;
    }

    public Uri getUriResult() {
        return uriResult;
    }

    public ICallback.ResponseHandler getHandler() {
        return handler;
    }

    public Uri getUriRequest() {
        return request.getUri();
    }

    public URL getUrlRequest() {
        return request.getUrl();
    }

    public ResultType getResultType() {
        return resultType;
    }

    public Bundle getBundleResult() {
        return bundleResult;
    }

    /**
     * Check if this object represents a result of the specified type
     * @param type  Type of result to check
     * @return  <code>true</code> if this object is a result of the specified type, <code>false</code> otherwise
     */
    public boolean isResultType(ResultType type) {
        return (resultType == type);
    }

}
