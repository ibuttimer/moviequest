/*
 * Copyright (C) 2017  Ian Buttimer
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ie.ianbuttimer.moviequest.tmdb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Class representing a TMDb server response to a movie list request
 */
public class MovieListResponse {

    private int totalResults = 0;   // total number of results
    private int totalPages = 0;     // total number of pages
    private int pageNumber = 0;     // current page
    private int resultsPerPage;
    private MovieInfoModel[] movies;

    // properties of movies json objects from TMDb server
    public static final String MOVIE_PAGE = "page";
    public static final String MOVIE_RESULTS = "results";
    public static final String MOVIE_TOTAL_RESULTS = "total_results";
    public static final String MOVIE_TOTAL_PAGES = "total_pages";


    /**
     * Default constructor
     */
    public MovieListResponse() {
        setNumbers(0, 0, 0);
        movies = new MovieInfoModel[] {};
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
                        MovieInfoModel movie = MovieInfoModel.getInstance(array.getJSONObject(i));
                        movie.setIndex(response.getRangeStart() + i);
                        response.setMovie(i, movie);
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    /**
     * Create a MovieListResponse object from a JSON string
     * @param jsonString  The JSON string to read
     * @return  A MovieListResponse object
     */
    public static MovieListResponse getMovieListFromJsonString(String jsonString) {
        MovieListResponse response = new MovieListResponse();
        try {
            JSONObject json = new JSONObject(jsonString);
            response = getMovieListFromJson(json);
        }
        catch (JSONException e) {
            e.printStackTrace();
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

    public void setTotalResults(int totalResults) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public MovieInfoModel[] getMovies() {
        return movies;
    }

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

}
