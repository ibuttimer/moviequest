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

package ie.ianbuttimer.moviequest.tmdb.review;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class representing a TMDb server response to a reviews list request
 * This is the information returned from the following API endpoints:
 * <ul>
 *     <li>/movie/{movie_id}/reviews</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class MovieReviewList extends AppendedReviewList implements Parcelable {

    /** Name of movie id value in TMDb server response */
    public static final String MOVIE_ID = "id";

    private Integer movieId;

    /**
     * Default constructor
     */
    public MovieReviewList() {
        super();
        movieId = 0;
    }

    /**
     * Create a MovieReviewList object from a JSON string
     * @param jsonString  The JSON string to read
     * @return  A MovieReviewList object
     */
    public static MovieReviewList getListFromJsonString(String jsonString) {
        return (MovieReviewList) getListFromJsonString(new MovieReviewList(), jsonString);
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
     * Create a MovieReviewList object from a Bundle
     * @param bundle    The bundle read
     * @return  A MovieReviewList object
     */
    public static MovieReviewList getListFromBundle(Bundle bundle) {
        return (MovieReviewList) getListFromBundle(new MovieReviewList(), bundle);
    }

    @Override
    protected void readExtraFields(Bundle bundle) {
        setMovieId(bundle.getInt(MOVIE_ID, 0));
    }

    /**
     * Create a MovieReviewList object from JSON data
     * @param jsonData  JSON data object
     * @return  new MovieReviewList object or null if no data
     */
    public static MovieReviewList getInstance(JSONObject jsonData) {
        return getListFromJsonString(jsonData.toString());
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

    public static final Creator<MovieReviewList> CREATOR
            = new Creator<MovieReviewList>() {
        public MovieReviewList createFromParcel(Parcel in) {
            return new MovieReviewList(in);
        }

        public MovieReviewList[] newArray(int size) {
            return new MovieReviewList[size];
        }
    };

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    private MovieReviewList(Parcel in) {
        this();
        readFromParcel(in, this, BaseReview.class.getClassLoader(), BaseReview[].class);
        movieId = in.readInt();
    }

}
