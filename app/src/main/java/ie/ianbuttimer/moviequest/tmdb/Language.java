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
 * Class representing language objects as provided by the TMDb movie details API
 */

public class Language extends IsoName implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    private static final String[] FIELD_NAMES = new String[] {
        // NOTE must follow the order of the indices in super class
        "iso_639_1",
        "name"
    };


    static {
        jsonMemberMap = generateMemberMap(FIELD_NAMES);
    }

    @Override
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
    }

    @Override
    protected String[] getFieldNames() {
        return FIELD_NAMES;
    }

    /**
     * Default constructor
     */
    public Language() {
        super();
    }

    /**
     * Constructor
     */
    public Language(String iso, String name) {
        super(iso, name);
    }

    @Override
    public boolean isEmpty() {
        return equals(new Language());
    }

    /**
     * Create an Language object from JSON data
     * @param jsonData  JSON data object
     * @return  new Language object or null if no data
     */
    public static Language getInstance(JSONObject jsonData) {
        return (Language)TMDbObject.getInstance(jsonMemberMap, jsonData, Language.class, new Language());
    }

    // just provide the creator and parcel constructor as other parcelable ,ethods are in super class

    public static final Parcelable.Creator<Language> CREATOR
            = new Parcelable.Creator<Language>() {
        public Language createFromParcel(Parcel in) {
            return new Language(in);
        }

        public Language[] newArray(int size) {
            return new Language[size];
        }
    };

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected Language(Parcel in) {
        super(in);
    }

}
