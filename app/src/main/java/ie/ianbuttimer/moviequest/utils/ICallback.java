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

import java.net.URL;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * Extended okhttp Callback interface
 */

public interface ICallback extends Callback {

    /**
     * Process the response
     * @param result    Response received from the server
     */
    void onResponse(Object result);

    /**
     * Send a request using the specified Url
     * @param url   Url to send
     */
    void request(URL url);

    /**
     * Process the response received
     * @param response  Response from the server
     * @return  Response object
     */
    Object processResponse(Response response);

}
