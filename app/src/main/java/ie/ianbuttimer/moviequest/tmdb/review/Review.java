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

import android.os.Parcel;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import ie.ianbuttimer.moviequest.tmdb.TMDbObject;
import ie.ianbuttimer.moviequest.utils.Utils;

/**
 * Class representing a movie review contents as utilised by the TMDb API.
 * This is the information returned from the following API endpoints:
 * <ul>
 *     <li>/review/{review_id}</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class Review extends BaseReview {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    private String iso_639_1;
    private Integer media_id;
    private String media_title;
    private String media_type;

    protected static final int FIRST_REVIEW_MEMBER = LAST_BASE_REVIEW_MEMBER + 1;
    protected static final int LANGUAGE = FIRST_REVIEW_MEMBER;
    protected static final int MEDIA_ID = FIRST_REVIEW_MEMBER + 1;
    protected static final int MEDIA_TITLE = FIRST_REVIEW_MEMBER + 2;
    protected static final int MEDIA_TYPE = FIRST_REVIEW_MEMBER + 3;
    protected static final int LAST_REVIEW_MEMBER = MEDIA_TYPE;

    private static final int[] sAllFields;  // list of all fields

    private static final String[] FIELD_NAMES;

    static {
        FIELD_NAMES = Arrays.copyOf(BaseReview.getFieldNamesArray(), LAST_REVIEW_MEMBER + 1);
        FIELD_NAMES[LANGUAGE] = "iso_639_1";
        FIELD_NAMES[MEDIA_ID] = "media_id";
        FIELD_NAMES[MEDIA_TITLE] = "media_title";
        FIELD_NAMES[MEDIA_TYPE] = "media_type";

        jsonMemberMap = generateMemberMap(null);

        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_REVIEW_MEMBER);
    }

    @Override
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
    }

    /**
     * Generate the member map representing this object
     */
    protected static HashMap<String, MemberEntry> generateMemberMap(int[] exclude) {
        HashMap<String, MemberEntry> memberMap = new HashMap<>();
        int[] sortedExclude = Utils.getSortedArray(exclude);

        for (int i = FIRST_REVIEW_MEMBER; i <= LAST_REVIEW_MEMBER ; i++) {
            if (Arrays.binarySearch(sortedExclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case LANGUAGE:
                        memberMap.put(key, stringTemplate.copy("setLanguage", "getLanguage", FIELD_NAMES[i]));
                        break;
                    case MEDIA_ID:
                        memberMap.put(key, intTemplate.copy("setMediaId", "getMediaId", FIELD_NAMES[i]));
                        break;
                    case MEDIA_TITLE:
                        memberMap.put(key, stringTemplate.copy("setMediaTitle", "getMediaTitle", FIELD_NAMES[i]));
                        break;
                    case MEDIA_TYPE:
                        memberMap.put(key, stringTemplate.copy("setMediaType", "getMediaType", FIELD_NAMES[i]));
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
    public Review() {
        super();
        init();
    }

    private void init() {
        iso_639_1 = "";
        media_id = 0;
        media_title = "";
        media_type = "";
    }

    /**
     * Placeholder Constructor
     */
    public Review(String id) {
        super(id);
        init();
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
     * Create a BaseReview object from JSON data
     * @param jsonData  JSON data object
     * @return  new BaseReview object or null if no data
     */
    public static Review getInstance(JSONObject jsonData) {
        return getInstance(jsonData, new Review());
    }

    /**
     * Create a BaseReview object from JSON data
     * @param jsonData  JSON data object
     * @param movie     Object to save data into
     * @return updated object
     */
    static Review getInstance(JSONObject jsonData, Review movie) {
        return getInstance(jsonMemberMap, jsonData, movie);
    }

    public String getLanguage() {
        return iso_639_1;
    }

    public void setLanguage(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public Integer getMediaId() {
        return media_id;
    }

    public void setMediaId(Integer media_id) {
        this.media_id = media_id;
    }

    public String getMediaTitle() {
        return media_title;
    }

    public void setMediaTitle(String media_title) {
        this.media_title = media_title;
    }

    public String getMediaType() {
        return media_type;
    }

    public void setMediaType(String media_type) {
        this.media_type = media_type;
    }

    @Override
    public boolean isEmpty() {
        return equals(new Review());
    }

    @Override
    public <T extends TMDbObject> void copy(T from, int[] fields) {
        copy(from, this, fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Review review = (Review) o;

        if (iso_639_1 != null ? !iso_639_1.equals(review.iso_639_1) : review.iso_639_1 != null)
            return false;
        if (media_id != null ? !media_id.equals(review.media_id) : review.media_id != null)
            return false;
        if (media_title != null ? !media_title.equals(review.media_title) : review.media_title != null)
            return false;
        return media_type != null ? media_type.equals(review.media_type) : review.media_type == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (iso_639_1 != null ? iso_639_1.hashCode() : 0);
        result = 31 * result + (media_id != null ? media_id.hashCode() : 0);
        result = 31 * result + (media_title != null ? media_title.hashCode() : 0);
        result = 31 * result + (media_type != null ? media_type.hashCode() : 0);
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR
            = new Creator<Review>() {
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        public Review[] newArray(int size) {
            return new Review[size];
        }
    };


    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeString(iso_639_1);
        parcel.writeInt(media_id);
        parcel.writeString(media_title);
        parcel.writeString(media_type);
    }

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, Review obj) {
        super.readFromParcel(in, obj);
        obj.iso_639_1 = in.readString();
        obj.media_id = in.readInt();
        obj.media_title = in.readString();
        obj.media_type = in.readString();
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected Review(Parcel in) {
        this();
        readFromParcel(in, this);
    }

}
