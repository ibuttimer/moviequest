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

import android.net.Uri;

import org.junit.Before;
import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Unit test for UriURLPair utility class
 */
public class UriURLPairTest {

    private URL testUrl;
    private UriURLPair urlPair; // pair object with url value
    private Uri testUri;
    private UriURLPair uriPair; // pair object with uri value

    private static final String protocol = "http";
    private static final String port = "1080";
    private static final String host = "www.example.com";
    private static final String path = "docs/resource1.html";
    private static final String reference = "chapter1";

    @Before
    public void setUp() throws Exception {
        String str = protocol + "://" + host + ":" + port + "/" + path + "#" + reference;
        try {
            testUrl = new URL(str);
            urlPair = new UriURLPair(testUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            fail("Url creation error: " + e.getMessage());
        }
        try {
            testUri = Uri.parse(str);
            uriPair = new UriURLPair(testUri);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Uri creation error: " + e.getMessage());
        }
    }

    @Test
    public void getUri() throws Exception {
        Uri uri = urlPair.getUri();

        assertNotNull("No uri", uri);
        compare(testUrl, uri);
        assertEquals("Url", testUrl, urlPair.getUrl());
    }

    public void compare(URL url, Uri uri) throws Exception {
        assertEquals("Scheme", url.getProtocol(), uri.getScheme());
        assertEquals("Host", url.getHost(), uri.getHost());
        assertEquals("Port", url.getPort(), uri.getPort());
        assertEquals("Path", url.getPath(), uri.getPath());
        assertEquals("Fragment", url.getRef(), uri.getFragment());
        assertEquals("String", url.toString(), uri.toString());
    }

    @Test
    public void getUrl() throws Exception {
        URL url = uriPair.getUrl();

        assertNotNull("No url", url);
        compare(url, testUri);
        assertEquals("Uri", testUri, uriPair.getUri());
    }

}
