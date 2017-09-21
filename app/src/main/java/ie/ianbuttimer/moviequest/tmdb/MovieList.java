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
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Class representing a TMDb server response to a movie list request
 * This is the information returned from the /movie/popular & /movie/top_rated API endpoint.
 */

public class MovieList extends AbstractList<MovieInfoModel> implements Parcelable {

    /**
     * Default constructor
     */
    public MovieList() {
        super(new MovieInfoModel[] {});
    }

    @Override
    protected void setResult(int index, JSONObject json) {
        MovieInfoModel result = MovieInfoModel.getInstance(json);
        result.setIndex(getRangeStart() + index);
        setResult(index, result);
    }

    /**
     * Create a AbstractList object from a JSON string
     * @param jsonString  The JSON string to read
     * @return  A AbstractList object
     */
    public static MovieList getListFromJsonString(String jsonString) {
        return (MovieList) getListFromJsonString(new MovieList(), jsonString);
    }

    @Override
    protected void readExtraFields(JSONObject json) {
        // noop
    }

    /**
     * Create a AbstractList object from a Bundle
     * @param bundle    The bundle read
     * @return  A AbstractList object
     */
    public static MovieList getListFromBundle(Bundle bundle) {
        return (MovieList) getListFromBundle(new MovieList(), bundle);
    }

    @Override
    protected void readExtraFields(Bundle bundle) {
        // noop
    }

    public static final Parcelable.Creator<MovieList> CREATOR
            = new Parcelable.Creator<MovieList>() {
        public MovieList createFromParcel(Parcel in) {
            return new MovieList(in);
        }

        public MovieList[] newArray(int size) {
            return new MovieList[size];
        }
    };

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    private MovieList(Parcel in) {
        this();
        readFromParcel(in, this, MovieInfoModel.class.getClassLoader(), MovieInfoModel[].class);
    }

}
