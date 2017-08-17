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

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ADULT;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.FIRST_MEMBER;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.LAST_MOVIE_INFO_MEMBER;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.RELEASE_DATE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.VIDEO;
import static org.junit.Assert.fail;

/**
 * Class for MovieInfo test object
 */
public class TestMovieInfoInstance extends TestObjectInstance {

    /** The MovieInfo object as returned by the Get Popular/Top Rated lists is a subset of the
     * Get Details object returned by the TMDb server
     *
         https://api.themoviedb.org/3/movie/211672?api_key=07b4203e40866fe38f9f338b9fc9a8da&language=en-US
         {
         "adult": false,
         "backdrop_path": "/uX7LXnsC7bZJZjn048UCOwkPXWJ.jpg",
         "belongs_to_collection": {
         "id": 86066,
         "name": "Despicable Me Collection",
         "poster_path": "/xIXhIlZDRmSSfNbpN7kBCm5hg39.jpg",
         "backdrop_path": "/15IZl405E664QDVxpFJBl7TtLmw.jpg"
         },
         "budget": 74000000,
         "genres": [
         {
         "id": 10751,
         "name": "Family"
         },
         {
         "id": 16,
         "name": "Animation"
         },
         {
         "id": 12,
         "name": "Adventure"
         },
         {
         "id": 35,
         "name": "Comedy"
         }
         ],
         "homepage": "http://www.minionsmovie.com/",
         "id": 211672,
         "imdb_id": "tt2293640",
         "original_language": "en",
         "original_title": "Minions",
         "overview": "Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.",
         "popularity": 196.218355,
         "poster_path": "/q0R4crx2SehcEEQEkYObktdeFy.jpg",
         "production_companies": [
         {
         "name": "Universal Pictures",
         "id": 33
         },
         {
         "name": "Illumination Entertainment",
         "id": 6704
         }
         ],
         "production_countries": [
         {
         "iso_3166_1": "US",
         "name": "United States of America"
         }
         ],
         "release_date": "2015-06-17",
         "revenue": 1156730962,
         "runtime": 91,
         "spoken_languages": [
         {
         "iso_639_1": "en",
         "name": "English"
         }
         ],
         "status": "Released",
         "tagline": "Before Gru, they had a history of bad bosses",
         "title": "Minions",
         "video": false,
         "vote_average": 6.4,
         "vote_count": 4086
         }
     */


    public static final int vote_count = 3849;
    public static final int id = 211672;
    public static final boolean video = false;
    public static final double vote_average = 6.4d;
    public static final String title = "Minions";
    public static final double popularity = 157.093026;
    public static final String poster_path = "/q0R4crx2SehcEEQEkYObktdeFy.jpg";
    public static final String original_language = "en";
    public static final String original_title = "Minions";
    public static final int[] genre_ids = new int[] {10751,16,12,35};
    public static final String backdrop_path = "/uX7LXnsC7bZJZjn048UCOwkPXWJ.jpg";
    public static final boolean adult = false;
    public static final String overview = "Minions Stuart, Kevin and Bob are recruited by Scarlet Overkill, a super-villain who, alongside her inventor husband Herb, hatches a plot to take over the world.";
    public static final String release_date = "2015-06-17";

    private static final SimpleDateFormat tmdbDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // don't have static access to MovieInfo.getPropertyName() or MovieInfo.getFieldName()
    private MovieInfo dummyMovieInfo = new MovieInfo();


    /**
     * Set the properties of the specified object for test
     * @param movie     Object to setup
     * @return
     */
    public MovieInfo setupObject(MovieInfo movie) {
        if (movie == null) {
            movie = new MovieInfo();
        }
        movie.setVoteCount(vote_count);
        movie.setId(id);
        movie.setVideo(video);
        movie.setVoteAverage(vote_average);
        movie.setTitle(title);
        movie.setPopularity(popularity);
        movie.setPosterPath(poster_path);
        movie.setOriginalLanguage(original_language);
        movie.setOriginalTitle(original_title);
        movie.setGenreIds(genre_ids);
        movie.setBackdropPath(backdrop_path);
        movie.setAdult(adult);
        movie.setOverview(overview);
        movie.setReleaseDate(release_date);
        return movie;
    }

    /**
     * Set the properties of the specified object for test
     * @return
     */
    public MovieInfo setupObject() {
        return setupObject(null);
    }

    /**
     * Convert a boolean field from its internal object representation (int) to boolean in the
     * specified JSON string
     * @param raw       JSON string
     * @param field     Field name
     * @return  updated JSON string
     */
    public String convertFieldToBoolean(String raw, String field) {
        String result = raw;
        int index = raw.indexOf(field);
        if (index >= 0) {
            int zeroIndex = raw.indexOf("0", index);
            int oneIndex = raw.indexOf("1", index);
            if ((zeroIndex > 0) && (oneIndex > 0)) {
                if (zeroIndex < oneIndex) {
                    oneIndex = -1;
                }
            }
            boolean value = (oneIndex > 0);
            index = (value ? oneIndex : zeroIndex);
            result = raw.substring(0, index) + String.valueOf(value);
            result += raw.substring(index + 1);
        }
        return result;
    }

    /**
     * Convert a date field from the GSON format to the TMDb format in the
     * specified JSON string
     * @param raw       JSON string
     * @param field     Field name
     * @return  updated JSON string
     */
    String convertDate(String raw, String field) {
        // GsonBuilder.setDateFormat didn't work for some reason, hence this function
        String result = raw;
        int index = raw.indexOf(field);
        if (index >= 0) {
            // example segment "releaseDate":"Jun 17, 2015 12:00:00 AM",
            int startIndex = raw.indexOf('\"', index) + 1;      // " at end of field name
            startIndex = raw.indexOf('\"', startIndex + 1) + 1; // first char of date
            int endIndex = raw.indexOf('\"', startIndex);       // " at end of date
            try {
                Date date = new SimpleDateFormat("MMM d, yyyy HH:mm:ss a").parse(raw.substring(startIndex, endIndex));
                result = raw.substring(0, startIndex) + tmdbDateFormat.format(date);
                result += raw.substring(endIndex);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Convert all the necessary fields in the specified JSON string
     * @param jsonString    JSON string
     * @return  updated JSON string
     */
    String convertMovieInfoFields(String jsonString) {
        // boolean's are stored as 0 or 1 in objects, so convert back
        jsonString = convertFieldToBoolean(jsonString, getFieldName(VIDEO));
        jsonString = convertFieldToBoolean(jsonString, getFieldName(ADULT));
        // replace the internal property names with those returned by the TMDb server
        jsonString = convertJsonFields(jsonString, FIRST_MEMBER, LAST_MOVIE_INFO_MEMBER);
        // convert dates
        jsonString = convertDate(jsonString, getFieldName(RELEASE_DATE));
        return jsonString;
    }

    /**
     * Return the JSON string representing the object
     * @param movieInfo     Object to stringify or if <code>null</code> a new object is created
     * @return  JSON string
     */
    public String getJsonString(MovieInfo movieInfo) {
        if (movieInfo == null) {
            movieInfo = setupObject(new MovieInfo());
        }

        Gson gson = new Gson();
        return convertMovieInfoFields(gson.toJson(movieInfo));
    }

    /**
     * Return the JSONObject representing the object
     * @param movieInfo     Object to JSONify or if <code>null</code> a new object is created
     * @return JSONObject or <code>null</code>
     */
    public JSONObject getJsonObject(MovieInfo movieInfo) {
        JSONObject jsonObject = null;
        String jsonString = getJsonString(movieInfo);
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
     * @see {@link ie.ianbuttimer.moviequest.tmdb.TMDbObject#getFieldName(int)}
     */
    @Override
    public String getFieldName(int index) {
        return dummyMovieInfo.getFieldName(index);
    }

    /**
     * Get the object property name associated with the specified index
     * @param index     Index of field
     * @return  property name or empty string if invalid index
     * @see {@link ie.ianbuttimer.moviequest.tmdb.TMDbObject#getFieldName(int)}
     */
    @Override
    protected String getPropertyName(int index) {
        return dummyMovieInfo.getPropertyName(index);
    }
}