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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ADULT;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.BACKDROP_PATH;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.GENRE_IDS;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ID;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ORIGINAL_LANGUAGE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ORIGINAL_TITLE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.OVERVIEW;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.POPULARITY;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.POSTER_PATH;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.RELEASE_DATE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.TITLE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.VIDEO;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.VOTE_AVERAGE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.VOTE_COUNT;
import static org.junit.Assert.*;

/**
 * Unit test for MovieInfo object
 */
public class MovieInfoGetInstanceTest extends GetInstanceTest {

    private MovieInfo movieInfo;
    private TestMovieInfoInstance provider = new TestMovieInfoInstance();
    private SimpleDateFormat tmdbDateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Before
    public void createObject() {
        movieInfo = provider.setupObject(new MovieInfo());
    }

    @Test
    public void evaluatesExpression() {

        String testClass = MovieInfo.class.getName();
        JSONObject jsonObject = provider.getJsonObject(movieInfo);
        MovieInfo movie;

        if (jsonObject != null) {
            try {
                String posterPath = provider.getFieldName(POSTER_PATH);
                String backDropPath = provider.getFieldName(BACKDROP_PATH);

                movie = MovieInfo.getInstance(jsonObject);

                checkObject(jsonObject, movie, testClass);

                /* according to the TMDbb API docs, the following fields may be null:
                    - poster_path: string or null
                    - backdrop_path: string or null
                 */
                // remove fields
                jsonObject.put(posterPath, null);
                jsonObject.put(backDropPath, null);

                movie = MovieInfo.getInstance(jsonObject);

                checkObject(jsonObject, movie, testClass);

                // explicitly set fields to null
                jsonObject.put(posterPath, JSONObject.NULL);
                jsonObject.put(backDropPath, JSONObject.NULL);

                movie = MovieInfo.getInstance(jsonObject);

                checkObject(jsonObject, movie, testClass);

            } catch (JSONException e) {
                fail(makeAssertMessage("JSONObject creation error: " + e.getMessage()));
            }
        }
    }

    public void checkObject(JSONObject jsonObject, MovieInfo movie, String name) {
        try {
            assertEquals(makeAssertMessage("ReleaseDate"),
                    tmdbDateFormat.parse(jsonObject.getString(provider.getFieldName(RELEASE_DATE))),
                    movie.getReleaseDate());

            // string tests
            checkStringFields(jsonObject, new TestFieldInfo[] {
                    new TestFieldInfo("PosterPath", POSTER_PATH, movie.getPosterPath(), true),
                    new TestFieldInfo("BackdropPath", BACKDROP_PATH, movie.getBackdropPath(), true),
                    new TestFieldInfo("Overview", OVERVIEW, movie.getOverview()),
                    new TestFieldInfo("OriginalTitle", ORIGINAL_TITLE, movie.getOriginalTitle()),
                    new TestFieldInfo("OriginalLanguage", ORIGINAL_LANGUAGE, movie.getOriginalLanguage()),
                    new TestFieldInfo("Title", TITLE, movie.getTitle())
            }, provider);

            // boolean tests
            checkBooleanFields(jsonObject, new TestFieldInfo[] {
                    new TestFieldInfo("Adult", ADULT, movie.isAdult()),
                    new TestFieldInfo("Video", VIDEO, movie.isVideo())
            }, provider);

            // int tests
            checkIntFields(jsonObject, new TestFieldInfo[] {
                    new TestFieldInfo("Id", ID, movie.getId()),
                    new TestFieldInfo("VoteCount", VOTE_COUNT, movie.getVoteCount())
            }, provider);

            // double tests
            checkDoubleFields(jsonObject, new TestFieldInfo[] {
                    new TestFieldInfo("Popularity", POPULARITY, movie.getPopularity()),
                    new TestFieldInfo("VoteAverage", VOTE_AVERAGE, movie.getVoteAverage())
            }, provider);

            if (name.equals(MovieInfo.class.getName())) {
                // genre_ids is only part of the MovieInfo object
                JSONArray array = jsonObject.getJSONArray(provider.getFieldName(GENRE_IDS));
                int length = array.length();
                Integer[] jsonArray = new Integer[length];
                for (int i = 0; i < length; i++) {
                    jsonArray[i] = array.getInt(i);
                }
                assertArrayEquals(makeAssertMessage("GenreIds"), jsonArray, movie.getGenreIds());
            }
        }
        catch (JSONException e) {
            fail(makeAssertMessage("JSONObject property error: " + e.getMessage()));
        }
        catch (ParseException e) {
            fail(makeAssertMessage("JSONObject test error: " + e.getMessage()));
        }
    }

}