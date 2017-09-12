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
 * Class representing an id/name pair as utilised by the TMDb API
 */
@SuppressWarnings("unused")
public abstract class IdName extends TMDbObject {

    private static HashMap<String, MemberEntry> placeholderMemberMap;  // map of default value fields for a placeholder

    private Integer id;
    private String name;

    protected static final int FIRST_MEMBER = 0;
    protected static final int ID = 0;
    protected static final int NAME = 1;
    protected static final int LAST_IDNAME_MEMBER = NAME;

    private static final int[] sAllFields;  // list of all fields

    private static final String[] FIELD_NAMES = new String[] {
        // NOTE must follow the order of the indices above!!!!
        "id",
        "name"
    };

    static {
        placeholderMemberMap = generateMemberMap(new int[] {
            ID
        });

        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_IDNAME_MEMBER);
    }

    /**
     * Generate the member map representing this object
     */
    protected static HashMap<String, MemberEntry> generateMemberMap(int[] exclude) {
        HashMap<String, MemberEntry> memberMap = new HashMap<>();
        int[] sortedExclude = Utils.getSortedArray(exclude);

        for (int i = FIRST_MEMBER; i <= LAST_IDNAME_MEMBER ; i++) {
            if (Arrays.binarySearch(sortedExclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case NAME:
                        memberMap.put(key, stringTemplate.copy("setName", "getName", "name"));
                        break;
                    case ID:
                        memberMap.put(key, intTemplate.copy("setId", "getId", "id"));
                        break;
                }
            }
        }
        return memberMap;
    }

    @Override
    public int[] getFieldIds() {
        return sAllFields;
    }

    /**
     * Default constructor
     */
    public IdName() {
        id = 0;
        name = "";
    }

    /**
     * Constructor
     */
    public IdName(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    protected String getFieldName(int index) {
        String name = "";
        if ((index >= FIRST_MEMBER) && (index < FIELD_NAMES.length)) {
            name = FIELD_NAMES[index];
        }
        return name;
    }

    @Override
    protected String[] getFieldNames() {
        return FIELD_NAMES;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isPlaceHolder() {
        return isDefault(placeholderMemberMap, this);
    }

    @Override
    public <T extends TMDbObject> void copy(T from, int[] fields) {
        copy(from, this, fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IdName idName = (IdName) o;

        if (id != null ? !id.equals(idName.id) : idName.id != null) return false;
        return name != null ? name.equals(idName.name) : idName.name == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(id);
        parcel.writeString(name);
    }

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, IdName obj) {
        obj.id = in.readInt();
        obj.name = in.readString();
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected IdName(Parcel in) {
        this();
        readFromParcel(in, this);
    }

}
