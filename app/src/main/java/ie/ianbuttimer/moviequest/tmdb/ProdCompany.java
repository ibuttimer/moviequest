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

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Class representing production company objects as provided by the TMDb movie details API
 */

public class ProdCompany extends IdName implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    static {
        jsonMemberMap = generateMemberMap(null);
    }

    @Override
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
    }

    /**
     * Default constructor
     */
    public ProdCompany() {
        super();
    }

    /**
     * Constructor
     */
    public ProdCompany(Integer id, String name) {
        super(id, name);
    }

    @Override
    public boolean isEmpty() {
        return equals(new ProdCompany());
    }

    /**
     * Create an ProdCompany object from JSON data
     * @param jsonData  JSON data object
     * @return  new ProdCompany object or null if no data
     */
    public static ProdCompany getInstance(JSONObject jsonData) {
        return TMDbObject.getInstance(jsonMemberMap, jsonData, new ProdCompany());
    }

    // just provide the creator and parcel constructor as other parcelable methods are in super class

    public static final Parcelable.Creator<ProdCompany> CREATOR
            = new Parcelable.Creator<ProdCompany>() {
        public ProdCompany createFromParcel(Parcel in) {
            return new ProdCompany(in);
        }

        public ProdCompany[] newArray(int size) {
            return new ProdCompany[size];
        }
    };

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected ProdCompany(Parcel in) {
        super(in);
    }
}
