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

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class representing genre objects as provided by the TMDb movie details API
 */

public class Genre extends IdName implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    static {
        jsonMemberMap = generateMemberMap();
    }

    @Override
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
    }

    /**
     * Default constructor
     */
    public Genre() {
        super();
    }

    /**
     * Constructor
     */
    public Genre(Integer id, String name) {
        super(id, name);
    }

    @Override
    public boolean isEmpty() {
        return equals(new Genre());
    }

    /**
     * Create an Genre object from JSON data
     * @param jsonData  JSON data object
     * @return  new Genre object or null if no data
     */
    public static Genre getInstance(JSONObject jsonData) {
        return (Genre)TMDbObject.getInstance(jsonMemberMap, jsonData, Genre.class, new Genre());
    }

    // just provide the creator and parcel constructor as other parcelable ,ethods are in super class

    public static final Parcelable.Creator<Genre> CREATOR
            = new Parcelable.Creator<Genre>() {
        public Genre createFromParcel(Parcel in) {
            return new Genre(in);
        }

        public Genre[] newArray(int size) {
            return new Genre[size];
        }
    };

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected Genre(Parcel in) {
        super(in);
    }
}
