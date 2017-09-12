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
package ie.ianbuttimer.moviequest.tmdb;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.BUDGET;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.COLLECTION;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.HOMEPAGE;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.IMDB_ID;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.REVENUE;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.RUNTIME;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.STATUS;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.TAGLINE;

/**
 * Unit test for MovieInfoModel object
 */
public class MovieDetailGetInstanceTest extends MovieInfoGetInstanceTest implements GetInstanceTest.IGetInstance {

    private MovieDetails movieDetails;
    private TestMovieDetailInstance provider = new TestMovieDetailInstance();


    @Before
    public void createObject() {
        movieDetails = provider.setupObject(new MovieDetails());
    }

    @Test
    public void evaluatesExpression() {
        JSONObject jsonObject = provider.getJsonObject(movieDetails);
        MovieDetails movie = MovieDetails.getInstance(jsonObject);

        checkObject(jsonObject, movie);
    }

    public void checkObject(JSONObject jsonObject, MovieDetails movieDetails) {
        super.checkObject(jsonObject, movieDetails, MovieDetails.class.getName());
        // string tests
        checkStringFields(jsonObject, new TestFieldInfo[] {
                new TestFieldInfo("Homepage", HOMEPAGE, movieDetails.getHomepage()),
                new TestFieldInfo("ImdbId", IMDB_ID, movieDetails.getImdbId()),
                new TestFieldInfo("Status", STATUS, movieDetails.getStatus()),
                new TestFieldInfo("Tagline", TAGLINE, movieDetails.getTagline())
        }, provider);

        // int tests
        checkIntFields(jsonObject, new TestFieldInfo[] {
                new TestFieldInfo("Budget", BUDGET, movieDetails.getBudget()),
                new TestFieldInfo("Revenue", REVENUE, movieDetails.getRevenue()),
                new TestFieldInfo("Runtime", RUNTIME, movieDetails.getRuntime())
        }, provider);


        checkObjectFields(jsonObject, new TestFieldInfo[] {
                new TestFieldInfo("Collection", COLLECTION, movieDetails.getCollection()),
        }, provider, this);

        // TODO object array tests
    }

    @Override
    public Object getInstance(int index, JSONObject json) {
        Object obj = null;
        switch (index) {
            case COLLECTION:
                obj = CollectionInfo.getInstance(json);
                break;
        }
        return obj;
    }
}