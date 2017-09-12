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

import android.app.Activity;

import java.lang.ref.WeakReference;
import java.util.Date;

import ie.ianbuttimer.moviequest.R;

import static ie.ianbuttimer.moviequest.Constants.INVALID_DATE;

/**
 * Abstract class to handle the response for movie information<br>
 * The class <code>T</code> specifies the type of object to be processed by the application.<br>
 */
@SuppressWarnings("unused")
public abstract class ResponseHandler<T> implements Runnable {

    private T response;
    private int errorId;
    private Date cacheDate;
    private WeakReference<Activity> activity;   // weak ref so won't prevent activity being garbage collected

    /**
     * Constructor
     * @param activity  The current activity
     * @param response  Response object
     */
    @SuppressWarnings("unused")
    public ResponseHandler(Activity activity, T response) {
        this(activity, response, 0, null);
    }

    /**
     * Constructor
     * @param activity  The current activity
     * @param response  Response object
     * @param errorId   Resource id of error message
     */
    public ResponseHandler(Activity activity, T response, int errorId) {
        this(activity, response, errorId, null);
    }

    /**
     * Constructor
     * @param activity  The current activity
     * @param response  Response object
     * @param errorId   Resource id of error message
     * @param cacheDate Date response was cached
     */
    public ResponseHandler(Activity activity, T response, int errorId, Date cacheDate) {
        this.activity = new WeakReference<>(activity);
        this.response = response;
        if (response == null) {
            this.errorId = R.string.no_response;
        } else {
            this.errorId = errorId;
        }
        if (cacheDate == null) {
            this.cacheDate = INVALID_DATE;
        } else {
            this.cacheDate = cacheDate;
        }
    }

    @Override
    public void run() {
        if (hasDialog()) {
            Dialog.showAlertDialog(activity.get(), errorId);
        }
    }

    /**
     * Check if this object has a dialog to display
     * @return  <code>true</code> if has dialog, <code>false</code> otherwise
     */
    protected boolean hasDialog() {
        return (errorId > 0);
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    protected int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }

    public Date getCacheDate() {
        return cacheDate;
    }

    public void setCacheDate(Date cacheDate) {
        this.cacheDate = cacheDate;
    }
}