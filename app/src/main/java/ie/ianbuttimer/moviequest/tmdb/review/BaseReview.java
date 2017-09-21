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

import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

import ie.ianbuttimer.moviequest.tmdb.TMDbObject;
import ie.ianbuttimer.moviequest.utils.TMDbNetworkUtils;
import ie.ianbuttimer.moviequest.utils.Utils;

/**
 * Class representing a basic review contents as utilised by the TMDb API.
 * This is the information returned from the following API endpoints:
 * <ul>
 *     <li>as appended to a movie details response from /movie/{movie_id}</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class BaseReview extends TMDbObject {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names
    private static HashMap<String, MemberEntry> placeholderMemberMap;  // map of default value fields for a placeholder

    private String id;
    private String author;
    private String content;
    private String url;

    protected static final int FIRST_MEMBER = 0;
    protected static final int ID = 0;
    protected static final int AUTHOR = 1;
    protected static final int CONTENT = 2;
    protected static final int URL = 3;
    protected static final int LAST_BASE_REVIEW_MEMBER = URL;

    private static final int[] sAllFields;  // list of all fields

    private static final String[] FIELD_NAMES;

    static {
        FIELD_NAMES = new String[LAST_BASE_REVIEW_MEMBER + 1];
        FIELD_NAMES[ID] = "id";
        FIELD_NAMES[AUTHOR] = "author";
        FIELD_NAMES[CONTENT] = "content";
        FIELD_NAMES[URL] = "url";

        jsonMemberMap = generateMemberMap(null);
        placeholderMemberMap = generateMemberMap(new int[] {
            ID
        });

        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_BASE_REVIEW_MEMBER);
    }

    static String[] getFieldNamesArray() {
        return FIELD_NAMES;
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

        for (int i = FIRST_MEMBER; i <= LAST_BASE_REVIEW_MEMBER ; i++) {
            if (Arrays.binarySearch(sortedExclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case ID:
                        memberMap.put(key, stringTemplate.copy("setId", "getId", FIELD_NAMES[i]));
                        break;
                    case AUTHOR:
                        memberMap.put(key, stringTemplate.copy("setAuthor", "getAuthor", FIELD_NAMES[i]));
                        break;
                    case CONTENT:
                        memberMap.put(key, stringTemplate.copy("setContent", "getContent", FIELD_NAMES[i]));
                        break;
                    case URL:
                        memberMap.put(key, stringTemplate.copy("setUrl", "getUrl", FIELD_NAMES[i]));
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
    public BaseReview() {
        id = "";
        author = "";
        content = "";
        url = "";
    }

    /**
     * Placeholder Constructor
     */
    public BaseReview(String id) {
        this();
        this.id = id;
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
    public static BaseReview getInstance(JSONObject jsonData) {
        return getInstance(jsonData, new BaseReview());
    }

    /**
     * Create a BaseReview object from JSON data
     * @param jsonData  JSON data object
     * @param movie     Object to save data into
     * @return updated object
     */
    static BaseReview getInstance(JSONObject jsonData, BaseReview movie) {
        return getInstance(jsonMemberMap, jsonData, movie);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean isEmpty() {
        return equals(new BaseReview());
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

        BaseReview review = (BaseReview) o;

        if (id != null ? !id.equals(review.id) : review.id != null) return false;
        if (author != null ? !author.equals(review.author) : review.author != null) return false;
        if (content != null ? !content.equals(review.content) : review.content != null)
            return false;
        return url != null ? url.equals(review.url) : review.url == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<BaseReview> CREATOR
            = new Parcelable.Creator<BaseReview>() {
        public BaseReview createFromParcel(Parcel in) {
            return new BaseReview(in);
        }

        public BaseReview[] newArray(int size) {
            return new BaseReview[size];
        }
    };


    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(url);
    }

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, BaseReview obj) {
        obj.id = in.readString();
        obj.author = in.readString();
        obj.content = in.readString();
        obj.url = in.readString();
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected BaseReview(Parcel in) {
        this();
        readFromParcel(in, this);
    }

    /**
     * Get a list of intents which may be used to view the review
     * @param context   The current context
     * @return  Array of intents
     */
    public Intent[] getViewIntents(Context context) {
        return getViewIntents(context, this);
    }

    /**
     * Get a list of intents which may be used to view the review
     * @param context   The current context
     * @param review    Video object to view
     * @return  Array of intents
     */
    public static Intent[] getViewIntents(Context context, BaseReview review) {
        return new Intent[] {
                new Intent(Intent.ACTION_VIEW, TMDbNetworkUtils.buildGetReviewUri(context, review.getId()))    // view in browser
        };
    }


}
