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

import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.FIRST_MOVIE_DETAIL_MEMBER;
import static ie.ianbuttimer.moviequest.tmdb.MovieDetails.LAST_MOVIE_DETAIL_MEMBER;
import static org.junit.Assert.fail;

/**
 * Class for MovieDetails test object
 */
public class TestMovieDetailInstance extends TestMovieInfoInstance {

    /** @see ie.ianbuttimer.moviequest.tmdb.TestMovieInfoInstance regarding test data */

    public static final int budget = 74000000;
    public static final Genre[] genres  = new Genre[] {
            new Genre( 10751, "Family"),
            new Genre( 16, "Animation"),
            new Genre( 12, "Adventure"),
            new Genre( 35, "Comedy")
    };
    public static final String homepage = "http://www.minionsmovie.com/";
    public static final String imdbId = "tt2293640";
    public static final ProdCompany[] productionCompanies = new ProdCompany[] {
            new ProdCompany(33, "Universal Pictures"),
            new ProdCompany(6704, "Illumination Entertainment")
    };
    public static final ProdCountry[] productionCountries = new ProdCountry[] {
            new ProdCountry("US", "United States of America")
    };
    public static final int revenue = 1156730962;
    public static final int runtime = 91;
    public static final Language[] spokenLanguages = new Language[] {
            new Language("en", "English")
    };
    public static final String status = "Released";
    public static final String tagline = "Before Gru, they had a history of bad bosses";
    private static CollectionInfo collection = new CollectionInfo("/xIXhIlZDRmSSfNbpN7kBCm5hg39.jpg",
            "/15IZl405E664QDVxpFJBl7TtLmw.jpg", "Despicable Me Collection", 86066);

    // don't have static access to MovieInfo.getPropertyName() or MovieInfo.getFieldName()
    private MovieDetails dummyMovieDetail = new MovieDetails();

    /**
     * Set the properties of the specified object for test
     * @param movie     Object to setup
     * @return
     */
    public MovieDetails setupObject(MovieDetails movie) {
        if (movie == null) {
            movie = new MovieDetails();
        }
        super.setupObject(movie);
        movie.setBudget(budget);
        movie.setGenres(genres);
        movie.setHomepage(homepage);
        movie.setImdbId(imdbId);
        movie.setProductionCompanies(productionCompanies);
        movie.setProductionCountries(productionCountries);
        movie.setRevenue(revenue);
        movie.setRuntime(runtime);
        movie.setSpokenLanguages(spokenLanguages);
        movie.setStatus(status);
        movie.setTagline(tagline);
        movie.setCollection(collection);
        return movie;
    }

    /**
     * Set the properties of the specified object for test
     * @return
     */
    public MovieDetails setupObject() {
        return setupObject(null);
    }

    /**
     * Convert all the necessary fields in the specified JSON string
     * @param jsonString    JSON string
     * @return  updated JSON string
     */
    String convertMovieDetailFields(String jsonString) {
        // replace the internal property names with those returned by the TMDb server
        return convertJsonFields(jsonString, FIRST_MOVIE_DETAIL_MEMBER, LAST_MOVIE_DETAIL_MEMBER);
    }

    /**
     * Return the JSON string representing the object
     * @param movieDetails     Object to stringify or if <code>null</code> a new object is created
     * @return  JSON string
     */
    public String getJsonString(MovieDetails movieDetails) {
        if (movieDetails == null ) {
            movieDetails = setupObject(new MovieDetails());
        }

        Gson gson = new Gson();
        String jsonString = convertMovieInfoFields(gson.toJson(movieDetails));
        return convertMovieDetailFields(jsonString);
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
            fail(makeAssertMessage("JSONObject creation error: " + e.getMessage()));
        }
        return jsonObject;
    }

    /**
     * Get the field name as returned by the TMDb server
     * @param index     Index of field
     * @return  field name or empty string if invalid index
     * @see ie.ianbuttimer.moviequest.tmdb.TMDbObject#getFieldName(int)
     */
    @Override
    public String getFieldName(int index) {
        return dummyMovieDetail.getFieldName(index);
    }

    /**
     * Get the object property name associated with the specified index
     * @param index     Index of field
     * @return  property name or empty string if invalid index
     * @see ie.ianbuttimer.moviequest.tmdb.TMDbObject#getFieldName(int)
     */
    @Override
    protected String getPropertyName(int index) {
        return dummyMovieDetail.getPropertyName(index);
    }
}