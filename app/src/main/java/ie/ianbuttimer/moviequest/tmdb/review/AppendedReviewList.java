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

import org.json.JSONObject;

import ie.ianbuttimer.moviequest.tmdb.AbstractList;

/**
 * Class representing a TMDb server response to a reviews list request.
 * This is the information returned from the following API endpoints:
 * <ul>
 *     <li>as appended to a movie details response from /movie/{movie_id}</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class AppendedReviewList extends AbstractList<BaseReview> implements Parcelable {

    /**
     * Default constructor
     */
    public AppendedReviewList() {
        super(new BaseReview[] {});
    }

    @Override
    protected void setResult(int index, JSONObject json) {
        BaseReview result = BaseReview.getInstance(json);
        setResult(index, result);
    }

    /**
     * Create a AppendedReviewList object from a JSON string
     * @param jsonString  The JSON string to read
     * @return  A AppendedReviewList object
     */
    public static AppendedReviewList getListFromJsonString(String jsonString) {
        return (AppendedReviewList) getListFromJsonString(new AppendedReviewList(), jsonString);
    }

    @Override
    protected void readExtraFields(JSONObject json) {
        // noop
    }

    /**
     * Create a AppendedReviewList object from a Bundle
     * @param bundle    The bundle read
     * @return  A AppendedReviewList object
     */
    public static AppendedReviewList getListFromBundle(Bundle bundle) {
        return (AppendedReviewList) getListFromBundle(new AppendedReviewList(), bundle);
    }

    @Override
    protected void readExtraFields(Bundle bundle) {
        // noop
    }

    /**
     * Create a AppendedReviewList object from JSON data
     * @param jsonData  JSON data object
     * @return  new AppendedReviewList object or null if no data
     */
    public static AppendedReviewList getInstance(JSONObject jsonData) {
        return getListFromJsonString(jsonData.toString());
    }

    public static final Parcelable.Creator<AppendedReviewList> CREATOR
            = new Parcelable.Creator<AppendedReviewList>() {
        public AppendedReviewList createFromParcel(Parcel in) {
            return new AppendedReviewList(in);
        }

        public AppendedReviewList[] newArray(int size) {
            return new AppendedReviewList[size];
        }
    };

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    private AppendedReviewList(Parcel in) {
        this();
        readFromParcel(in, this, BaseReview.class.getClassLoader(), BaseReview[].class);
    }

}
