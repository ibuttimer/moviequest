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

import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;

import ie.ianbuttimer.moviequest.R;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Class providing basic handling of okhttp3 responses
 */

public abstract class AsyncCallback implements ICallback {

    @Override
    public void onFailure(Call call, IOException e) {
        e.printStackTrace();
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        try {
            if (!response.isSuccessful()) {
                // unsuccessful
                throw new IOException("Unexpected code " + response);
            }
            // else result code is in [200..300], the request was successfully received, understood, and accepted.

            // process raw http response & handle
            onResponse(processResponse(response));
        }
        finally {
            //  must close the response body to prevent resource leaks
            if (response != null) {
                response.close();
            }
        }
    }

    /**
     * Asynchronously request the specified Url
     * @param url   Url to send
     */
    @Override
    public void request(URL url) {
        try {
            NetworkUtils.getHttpResponseStringAsync(url, this);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the resource id of the error message corresponding to an exception
     * @param call  Original request
     * @param e     Exception
     * @return resource id
     */
    public int getErrorId(Call call, IOException e) {
        int msgId = 0;
        if (e instanceof UnknownHostException) {
            msgId = R.string.cant_contact_server;
        }
        return msgId;
    }


}
