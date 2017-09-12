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

import android.os.Parcel;

import java.util.Arrays;
import java.util.HashMap;

import ie.ianbuttimer.moviequest.utils.Utils;

/**
 * Class representing an iso designator/name pair as utilised by the TMDb API
 */
@SuppressWarnings("unused")
public abstract class IsoName extends TMDbObject {

    private String iso;
    private String name;

    protected static final int FIRST_MEMBER = 0;
    protected static final int ISO = 0;
    protected static final int NAME = 1;
    protected static final int LAST_ISONAME_MEMBER = NAME;

    private static final int[] sAllFields;  // list of all fields

    static {
        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_ISONAME_MEMBER);
    }

    @Override
    public int[] getFieldIds() {
        return sAllFields;
    }

    /**
     * Generate the member map representing this object
     * @param fieldNames    Array of fields names used by TMDb server
     */
    protected static HashMap<String, MemberEntry> generateMemberMap(String[] fieldNames, int[] exclude) {
        HashMap<String, MemberEntry> memberMap = new HashMap<>();
        int[] sortedExclude = Utils.getSortedArray(exclude);

        for (int i = FIRST_MEMBER; i <= LAST_ISONAME_MEMBER ; i++) {
            if (Arrays.binarySearch(sortedExclude, i) < 0) {
                String key = fieldNames[i];
                switch (i) {
                    case ISO:
                        memberMap.put(key, stringTemplate.copy("setIso", "getIso", "iso"));
                        break;
                    case NAME:
                        memberMap.put(key, stringTemplate.copy("setName", "getName", "name"));
                        break;
                }
            }
        }
        return memberMap;
    }

    @Override
    protected String getFieldName(int index) {
        String name = "";
        String[] fieldNames = getFieldNames();
        if ((index >= FIRST_MEMBER) && (index < fieldNames.length)) {
            name = fieldNames[index];
        }
        return name;
    }

    /**
     * Default constructor
     */
    public IsoName() {
        iso = "";
        name = "";
    }

    /**
     * Constructor
     */
    public IsoName(String iso, String name) {
        this.iso = iso;
        this.name = name;
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = iso;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <T extends TMDbObject> void copy(T from, int[] fields) {
        copy(from, this, fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IsoName isoName = (IsoName) o;

        if (iso != null ? !iso.equals(isoName.iso) : isoName.iso != null) return false;
        return name != null ? name.equals(isoName.name) : isoName.name == null;

    }

    @Override
    public int hashCode() {
        int result = iso != null ? iso.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(iso);
        parcel.writeString(name);
    }

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, IsoName obj) {
        obj.iso = in.readString();
        obj.name = in.readString();
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected IsoName(Parcel in) {
        this();
        readFromParcel(in, this);
    }

}
