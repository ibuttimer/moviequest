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

import android.app.Activity;

import java.lang.ref.WeakReference;

import ie.ianbuttimer.moviequest.R;

/**
 * Created by Ian on 26/08/2017.
 */

public abstract class ResponseHandler<T> implements Runnable {

    private T response;
    private int errorId;
    private WeakReference<Activity> activity;   // weak ref so won't prevent activity being garbage collected

    /**
     * Constructor
     * @param response  Response object
     */
    public ResponseHandler(Activity activity, T response) {
        this(activity, response, 0);
    }

    /**
     * Constructor
     * @param response  Response object
     * @param errorId   Resource id of error message
     */
    public ResponseHandler(Activity activity, T response, int errorId) {
        this.activity = new WeakReference<Activity>(activity);
        this.response = response;
        if (response == null) {
            this.errorId = R.string.no_response;
        } else {
            this.errorId = errorId;
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
    public boolean hasDialog() {
        return (errorId > 0);
    }

    public T getResponse() {
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
    }

    public int getErrorId() {
        return errorId;
    }

    public void setErrorId(int errorId) {
        this.errorId = errorId;
    }
}