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
package ie.ianbuttimer.moviequest.tmdb;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static ie.ianbuttimer.moviequest.tmdb.MovieInfoModel.INDEX;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Unit test for MovieInfoModel object
 */
public class MovieInfoModelGetInstanceTest extends MovieInfoGetInstanceTest {

    private MovieInfoModel movieModel;
    private TestMovieInfoModelInstance provider = new TestMovieInfoModelInstance();

    @Before
    public void createObject() {
        movieModel = provider.setupObject();
    }

    @Test
    public void evaluatesExpression() {
        JSONObject jsonObject = provider.getJsonObject(movieModel);
        MovieInfoModel model;

        model = MovieInfoModel.getInstance(jsonObject);

        checkObject(jsonObject, model);
    }

    public void checkObject(JSONObject jsonObject, MovieInfoModel movieModel) {
        super.checkObject(jsonObject, movieModel, MovieInfo.class.getName());
        try {
            assertEquals(makeAssertMessage("Index"),
                    (long)jsonObject.getInt(provider.getFieldName(INDEX)),
                    Long.valueOf(movieModel.getIndex()).longValue());
        }
        catch (JSONException e) {
            fail(makeAssertMessage("JSONObject property error: " + e.getMessage()));
        }
    }
}