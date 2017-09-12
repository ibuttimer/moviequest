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
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import ie.ianbuttimer.moviequest.utils.Utils;

/**
 * This class represents the movie details provided by TMDb as part of the popular & top rated movie lists
 *
 * Android unit tests:
 *
 * Unit tests:
 *
 */
@SuppressWarnings("unused")
public class CollectionInfo extends TMDbObject implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names
    private static HashMap<String, MemberEntry> placeholderMemberMap;  // map of default value fields for a placeholder

    private String posterPath;
    private String backdropPath;
    private String name;
    private Integer id;

    protected static final int FIRST_MEMBER = 0;
    protected static final int POSTER_PATH = 0;
    protected static final int BACKDROP_PATH = 1;
    protected static final int NAME = 2;
    protected static final int ID = 3;
    protected static final int LAST_COLLECTION_INFO_MEMBER  = ID;

    private static final int[] sAllFields;  // list of all fields

    private static final String[] FIELD_NAMES = new String[] {
        // NOTE must follow the order of the indices above!!!!
        "poster_path",
        "backdrop_path",
        "name",
        "id"
    };

    static {
        jsonMemberMap = generateMemberMap(null);
        placeholderMemberMap = generateMemberMap(new int[] {
                NAME, ID
        });

        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_COLLECTION_INFO_MEMBER);
    }

    @Override
    public int[] getFieldIds() {
        return sAllFields;
    }

    /**
     * Generate the member map representing this object
     * @param exclude   Array of ids of members to exclude
     */
    protected static HashMap<String, MemberEntry> generateMemberMap(int[] exclude) {
        HashMap<String, MemberEntry> memberMap = new HashMap<>();
        int[] sortedExclude = Utils.getSortedArray(exclude);

        for (int i = FIRST_MEMBER; i <= LAST_COLLECTION_INFO_MEMBER ; i++) {
            if (Arrays.binarySearch(sortedExclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case POSTER_PATH:
                        memberMap.put(key, stringTemplate.copy("setPosterPath", "getPosterPath", "posterPath"));
                        break;
                    case BACKDROP_PATH:
                        memberMap.put(key, stringTemplate.copy("setBackdropPath", "getBackdropPath", "backdropPath"));
                        break;
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
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
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

    /**
     * Default constructor
     */
    public CollectionInfo() {
        posterPath = "";
        backdropPath = "";
        name = "";
        id = 0;
    }

    /**
     * Constructor
     */
    public CollectionInfo(String posterPath, String backdropPath, String name, Integer id) {
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.name = name;
        this.id = id;
    }

    /**
     * Create a CollectionInfo object from JSON data
     * @param jsonData  JSON data object
     * @return  new CollectionInfo object or null if no data
     */
    public static CollectionInfo getInstance(JSONObject jsonData) {
        return getInstance(jsonData, new CollectionInfo());
    }

    /**
     * Create a CollectionInfo object from JSON data
     * @param jsonData  JSON data object
     * @param movie     Object to save data into
     * @return updated object
     */
    static CollectionInfo getInstance(JSONObject jsonData, CollectionInfo movie) {
        return getInstance(jsonMemberMap, jsonData, movie);
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isEmpty() {
        return equals(new CollectionInfo());
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

        CollectionInfo that = (CollectionInfo) o;

        if (posterPath != null ? !posterPath.equals(that.posterPath) : that.posterPath != null)
            return false;
        if (backdropPath != null ? !backdropPath.equals(that.backdropPath) : that.backdropPath != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        int result = posterPath != null ? posterPath.hashCode() : 0;
        result = 31 * result + (backdropPath != null ? backdropPath.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(posterPath);
        parcel.writeString(backdropPath);
        parcel.writeString(name);
        parcel.writeInt(id);
    }

    public static final Creator<CollectionInfo> CREATOR
            = new Creator<CollectionInfo>() {
        public CollectionInfo createFromParcel(Parcel in) {
            return new CollectionInfo(in);
        }

        public CollectionInfo[] newArray(int size) {
            return new CollectionInfo[size];
        }
    };

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    public void readFromParcel(Parcel in, CollectionInfo obj) {
        obj.posterPath = in.readString();
        obj.backdropPath = in.readString();
        obj.name = in.readString();
        obj.id = in.readInt();
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    private CollectionInfo(Parcel in) {
        this();
        readFromParcel(in, this);
    }
}
