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

import android.os.Bundle;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Class representing a TMDb server response to a movie list request
 */
public class MovieListResponse {

    /** The number of movie result per list response. This mirrors what the TMDb server currently returns */
    public static final int RESULTS_PER_LIST = 20;

    private int totalResults = 0;   // total number of results
    private int totalPages = 0;     // total number of pages
    private int pageNumber = 0;     // current page
    private int resultsPerPage;
    private MovieInfoModel[] movies;
    private boolean nonResponse;    // non response flag
    

    // properties of movies json objects from TMDb server
    /** Name of page value in TMDb server response */
    public static final String MOVIE_PAGE = "page";
    /** Name of results array value in TMDb server response */
    public static final String MOVIE_RESULTS = "results";
    /** Name of total results count value in TMDb server response */
    public static final String MOVIE_TOTAL_RESULTS = "total_results";
    /** Name of total pages count value in TMDb server response */
    public static final String MOVIE_TOTAL_PAGES = "total_pages";

    public static final String RESULTS_PER_PAGES = "results_per_page";


    /**
     * Default constructor
     */
    public MovieListResponse() {
        setNumbers(0, 0, 0);
        movies = new MovieInfoModel[] {};
        nonResponse = true;   // doesn't represent a valid response
    }

    /**
     * Create a MovieListResponse object from a JSON object
     * @param json  The JSON object to read
     * @return  A MovieListResponse object
     */
    public static MovieListResponse getMovieListFromJson(JSONObject json) {
        MovieListResponse response = new MovieListResponse();

        if (json != null) {
            if (json.has(MOVIE_TOTAL_RESULTS) && json.has(MOVIE_TOTAL_PAGES) && json.has(MOVIE_PAGE)) {
                try {
                    response.setNumbers(json.getInt(MOVIE_TOTAL_RESULTS),
                            json.getInt(MOVIE_TOTAL_PAGES), json.getInt(MOVIE_PAGE));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (json.has(MOVIE_RESULTS)) {
                try {
                    JSONArray array = json.getJSONArray(MOVIE_RESULTS);
                    int length = array.length();

                    response.setMovieLength(length);

                    for (int i = 0; i < length; i++) {
                        setResult(response, i, array.getJSONObject(i));
                    }
                    response.nonResponse = false;   // represents a valid response
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    /**
     * Set the result for the specified index
     * @param response  MovieListResponse object to set result in
     * @param index     Index of result to set
     * @param json      JSONObject to construct result from
     */
    private static void setResult(MovieListResponse response, int index, JSONObject json) {
        MovieInfoModel movie = MovieInfoModel.getInstance(json);
        movie.setIndex(response.getRangeStart() + index);
        response.setMovie(index, movie);
    }

    /**
     * Create a MovieListResponse object from a JSON string
     * @param jsonString  The JSON string to read
     * @return  A MovieListResponse object
     */
    public static MovieListResponse getMovieListFromJsonString(String jsonString) {
        MovieListResponse response = new MovieListResponse();
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                JSONObject json = new JSONObject(jsonString);
                response = getMovieListFromJson(json);
                response.nonResponse = false;   // represents a valid response
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Create a MovieListResponse object from a Bundle
     * @param bundle    The bundle read
     * @return  A MovieListResponse object
     */
    public static MovieListResponse getMovieListFromBundle(Bundle bundle) {
        MovieListResponse response = new MovieListResponse();
        if (bundle != null) {
            try {
                response.setPageNumber(bundle.getInt(MOVIE_PAGE, 1));   // server is 1-based
                response.setTotalResults(bundle.getInt(MOVIE_TOTAL_RESULTS, 0));
                response.setTotalPages(bundle.getInt(MOVIE_TOTAL_PAGES, 0));

                String[] jsonStringArray;
                if (bundle.containsKey(MOVIE_RESULTS)) {
                    jsonStringArray = bundle.getStringArray(MOVIE_RESULTS);
                } else {
                    jsonStringArray = new String[] {};
                }
                int length = jsonStringArray.length;

                response.setMovieLength(length);

                for (int i = 0; i < length; i++) {
                    setResult(response, i, new JSONObject(jsonStringArray[i]));
                }
                response.nonResponse = false;   // represents a valid response
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    private void setNumbers(int totalResults, int totalPages, int pageNumber) {
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        if (totalPages != 0) {
            resultsPerPage = totalResults / totalPages;
        } else {
            resultsPerPage = 0;
        }
    }

    private void setMovie(int index, MovieInfoModel movie) {
        if (index < movies.length) {
            movies[index] = movie;
        }
    }

    private void setMovieLength(int length) {
        if (length >= 0) {
            movies = Arrays.copyOf(movies, length);
        }
    }

    public int getTotalResults() {
        return totalResults;
    }

    @SuppressWarnings("unused")
    public void setTotalResults(int totalResults) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    @SuppressWarnings("unused")
    public int getTotalPages() {
        return totalPages;
    }

    @SuppressWarnings("unused")
    public void setTotalPages(int totalPages) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    @SuppressWarnings("unused")
    public int getPageNumber() {
        return pageNumber;
    }

    @SuppressWarnings("unused")
    public void setPageNumber(int pageNumber) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    @SuppressWarnings("unused")
    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public MovieInfoModel[] getMovies() {
        return movies;
    }

    public int getMovieCount() {
        int count = 0;
        if (movies != null) {
            count = movies.length;
        }
        return count;
    }

    @SuppressWarnings("unused")
    public void setMovies(MovieInfoModel[] movies) {
        this.movies = movies;
    }

    public boolean rangeIsValid() {
        return ((totalResults > 0) && (totalPages > 0) && (pageNumber > 0));
    }

    public int getRangeStart() {
        return (((pageNumber - 1) * resultsPerPage) + 1);
    }

    public int getRangeEnd() {
        int end = getRangeStart() + resultsPerPage - 1;
        if (end > totalResults) {
            end = totalResults;
        }
        return end;
    }

    /**
     * Check if this object represents a valid response
     * @return  <code>true</code> if represents a valid response
     */
    public boolean isNonResponse() {
        return nonResponse;
    }
}
