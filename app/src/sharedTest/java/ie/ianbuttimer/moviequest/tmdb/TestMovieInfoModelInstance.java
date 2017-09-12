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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import static org.junit.Assert.fail;

/**
 * Class for MovieInfoModel test object
 * NOTE: not really required as TMDb server does not send this object type
 */
@SuppressWarnings("unused")
public class TestMovieInfoModelInstance extends TestMovieInfoInstance {

    /** @see TestMovieInfoInstance regarding test data */

    public static final int index = 10;

    // don't have static access to MovieInfo.getPropertyName() or MovieInfo.getFieldName()
    private MovieInfoModel dummyMovieInfoModel = new MovieInfoModel();

    /**
     * Set the properties of the specified object for test
     * @param movie     Object to setup
     * @return  initialised object
     */
    public MovieInfoModel setupObject(MovieInfoModel movie) {
        if (movie == null) {
            movie = new MovieInfoModel();
        }
        super.setupObject(movie);
        movie.setIndex(index);
        return movie;
    }

    /**
     * Set the properties of the specified object for test
     * @return  initialised object
     */
    public MovieInfoModel setupObject() {
        return setupObject(null);
    }

    /**
     * Return the JSON string representing the object
     * @param movieModel     Object to stringify or if <code>null</code> a new object is created
     * @return  JSON string
     */
    public String getJsonString(MovieInfoModel movieModel) {
        if (movieModel == null ) {
            movieModel = setupObject(new MovieInfoModel());
        }

        Gson gson = new Gson();
        return convertMovieInfoFields(gson.toJson(movieModel));
    }

    /**
     * Return the JSONObject representing the object
     * @param movieDetails     Object to JSONify or if <code>null</code> a new object is created
     * @return JSONObject or <code>null</code>
     */
    public JSONObject getJsonObject(MovieDetails movieDetails) {
        JSONObject jsonObject = null;
        String jsonString = getJsonString(movieDetails);
        try {
            jsonObject = new JSONObject(jsonString);
        }
        catch (JSONException e) {
            fail("JSONObject creation error: " + e.getMessage());
        }
        return jsonObject;
    }

    /**
     * Get the field name as returned by the TMDb server
     * @param index     Index of field
     * @return  field name or empty string if invalid index
     * @see TMDbObject#getFieldName(int)
     */
    @Override
    public String getFieldName(int index) {
        return dummyMovieInfoModel.getFieldName(index);
    }

    /**
     * Get the object property name associated with the specified index
     * @param index     Index of field
     * @return  property name or empty string if invalid index
     * @see TMDbObject#getFieldName(int)
     */
    @Override
    protected String getPropertyName(int index) {
        return dummyMovieInfoModel.getPropertyName(index);
    }
}