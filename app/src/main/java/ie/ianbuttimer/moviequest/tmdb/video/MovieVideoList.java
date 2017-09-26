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

package ie.ianbuttimer.moviequest.tmdb.video;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import ie.ianbuttimer.moviequest.tmdb.AbstractList;

/**
 * Class representing a TMDb server response to a video list request
 * This is the information returned from the following API endpoints:
 * <ul>
 *     <li>/movie/{movie_id}/videos</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class MovieVideoList extends AbstractList<Video> implements Parcelable {


    /** Name of movie id value in TMDb server response */
    public static final String MOVIE_ID = "id";

    private Integer movieId;

    /**
     * Default constructor
     */
    public MovieVideoList() {
        super(new Video[] {});
        movieId = 0;
    }

    @Override
    protected void setResult(int index, JSONObject json) {
        Video result = Video.getInstance(json);
        setResult(index, result);
    }

    /**
     * Create a MovieVideoList object from a JSON string
     * @param jsonString  The JSON string to read
     * @return  A MovieVideoList object
     */
    public static MovieVideoList getListFromJsonString(String jsonString) {
        MovieVideoList response = (MovieVideoList) getListFromJsonString(new MovieVideoList(), jsonString);
        return adjustNumbers(response);
    }

    @Override
    protected void readExtraFields(JSONObject json) {
        if (json != null) {
            if (json.has(MOVIE_ID)) {
                try {
                    setMovieId(json.getInt(MOVIE_ID));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Create a MovieVideoList object from a Bundle
     * @param bundle    The bundle read
     * @return  A MovieVideoList object
     */
    public static MovieVideoList getListFromBundle(Bundle bundle) {
        return (MovieVideoList) getListFromBundle(new MovieVideoList(), bundle);
    }

    @Override
    protected void readExtraFields(Bundle bundle) {
        setMovieId(bundle.getInt(MOVIE_ID, 0));
    }

    /**
     * Create a MovieVideoList object from JSON data
     * @param jsonData  JSON data object
     * @return  new MovieVideoList object or null if no data
     */
    public static MovieVideoList getInstance(JSONObject jsonData) {
        MovieVideoList response = getListFromJsonString(jsonData.toString());
        return adjustNumbers(response);
    }

    /**
     * Adjust range number to compensate from missing info in server response
     * @param response  Object to adjust
     */
    protected static MovieVideoList adjustNumbers(MovieVideoList response) {
        /* NOTE: a video list response differs slightly from other list responses in that it doesn't have
            the page/total_pages/total_results fields.
         */
        response.setPageNumber(1);
        response.setTotalPages(1);
        response.setTotalResults(response.getResults().length);
        return response;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(movieId);
    }

    public static final Creator<MovieVideoList> CREATOR
            = new Creator<MovieVideoList>() {
        public MovieVideoList createFromParcel(Parcel in) {
            return new MovieVideoList(in);
        }

        public MovieVideoList[] newArray(int size) {
            return new MovieVideoList[size];
        }
    };

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    private MovieVideoList(Parcel in) {
        this();
        readFromParcel(in, this, Video.class.getClassLoader(), Video[].class);
        movieId = in.readInt();
    }

}
