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

package ie.ianbuttimer.moviequest.tmdb.video;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import ie.ianbuttimer.moviequest.tmdb.TMDbObject;
import ie.ianbuttimer.moviequest.utils.ITester;
import ie.ianbuttimer.moviequest.utils.Utils;

/**
 * Class representing a trailer video as utilised by the TMDb API
 * This is the information returned from the following API endpoints:
 * <ul>
 *     <li>as appended to a movie details response from /movie/{movie_id}</li>
 *     <li>/movie/{movie_id}/videos</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class Video extends TMDbObject implements Comparable {

    /** Trailer video type */
    public static final String TRAILER = "Trailer";
    /** Teaser video type */
    public static final String TEASER= "Teaser";
    /** Clip video type */
    public static final String CLIP = "Clip";
    /** Featurette video type */
    public static final String FEATURETTE = "Featurette";

    /** YouTube site */
    public static final String YOU_TUBE = "YouTube";

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names
    private static HashMap<String, MemberEntry> placeholderMemberMap;  // map of default value fields for a placeholder

    private String id;
    private String iso_639_1;
    private String iso_3166_1;
    private String key;
    private String name;
    private String site;
    private Integer size;
    private String type;

    protected static final int FIRST_MEMBER = 0;
    protected static final int ID = 0;
    protected static final int LANGUAGE = 1;
    protected static final int COUNTRY = 2;
    protected static final int KEY = 3;
    protected static final int NAME = 4;
    protected static final int SITE = 5;
    protected static final int SIZE = 6;
    protected static final int TYPE = 7;
    protected static final int LAST_VIDEO_MEMBER = TYPE;

    private static final int[] sAllFields;  // list of all fields

    private static final String[] FIELD_NAMES = new String[] {
        // NOTE must follow the order of the indices above!!!!
        "id",
        "iso_639_1",
        "iso_3166_1",
        "key",
        "name",
        "site",
        "size",
        "type"
    };

    static {
        jsonMemberMap = generateMemberMap(null);
        placeholderMemberMap = generateMemberMap(new int[] {
            ID
        });

        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_VIDEO_MEMBER);
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

        for (int i = FIRST_MEMBER; i <= LAST_VIDEO_MEMBER ; i++) {
            if (Arrays.binarySearch(sortedExclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case ID:
                        memberMap.put(key, stringTemplate.copy("setId", "getId", FIELD_NAMES[i]));
                        break;
                    case LANGUAGE:
                        memberMap.put(key, stringTemplate.copy("setLanguage", "getLanguage", FIELD_NAMES[i]));
                        break;
                    case COUNTRY:
                        memberMap.put(key, stringTemplate.copy("setCountry", "getCountry", FIELD_NAMES[i]));
                        break;
                    case KEY:
                        memberMap.put(key, stringTemplate.copy("setKey", "getKey", FIELD_NAMES[i]));
                        break;
                    case NAME:
                        memberMap.put(key, stringTemplate.copy("setName", "getName", FIELD_NAMES[i]));
                        break;
                    case SITE:
                        memberMap.put(key, stringTemplate.copy("setSite", "getSite", FIELD_NAMES[i]));
                        break;
                    case SIZE:
                        memberMap.put(key, intTemplate.copy("setSize", "getSize", FIELD_NAMES[i]));
                        break;
                    case TYPE:
                        memberMap.put(key, stringTemplate.copy("setType", "getType", FIELD_NAMES[i]));
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
    public Video() {
        id = "";
        iso_639_1 = "";
        iso_3166_1 = "";
        key = "";
        name = "";
        site = "";
        size = 0;
        type = "";
    }

    /**
     * Placeholder Constructor
     */
    public Video(String id) {
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
    public static Video getInstance(JSONObject jsonData) {
        return getInstance(jsonData, new Video());
    }

    /**
     * Create a BaseReview object from JSON data
     * @param jsonData  JSON data object
     * @param movie     Object to save data into
     * @return updated object
     */
    static Video getInstance(JSONObject jsonData, Video movie) {
        return getInstance(jsonMemberMap, jsonData, movie);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanguage() {
        return iso_639_1;
    }

    public void setLanguage(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public String getCountry() {
        return iso_3166_1;
    }

    public void setCountry(String iso_3166_1) {
        this.iso_3166_1 = iso_3166_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean isEmpty() {
        return equals(new Video());
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

        Video video = (Video) o;

        if (id != null ? !id.equals(video.id) : video.id != null) return false;
        if (iso_639_1 != null ? !iso_639_1.equals(video.iso_639_1) : video.iso_639_1 != null)
            return false;
        if (iso_3166_1 != null ? !iso_3166_1.equals(video.iso_3166_1) : video.iso_3166_1 != null)
            return false;
        if (key != null ? !key.equals(video.key) : video.key != null) return false;
        if (name != null ? !name.equals(video.name) : video.name != null) return false;
        if (site != null ? !site.equals(video.site) : video.site != null) return false;
        if (size != null ? !size.equals(video.size) : video.size != null) return false;
        return type != null ? type.equals(video.type) : video.type == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (iso_639_1 != null ? iso_639_1.hashCode() : 0);
        result = 31 * result + (iso_3166_1 != null ? iso_3166_1.hashCode() : 0);
        result = 31 * result + (key != null ? key.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (site != null ? site.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    public int describeContents() {
        return 0;
    }

    public static final Creator<Video> CREATOR
            = new Creator<Video>() {
        public Video createFromParcel(Parcel in) {
            return new Video(in);
        }

        public Video[] newArray(int size) {
            return new Video[size];
        }
    };


    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(iso_639_1);
        parcel.writeString(iso_3166_1);
        parcel.writeString(key);
        parcel.writeString(name);
        parcel.writeString(site);
        parcel.writeInt(size);
        parcel.writeString(type);
    }

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, Video obj) {
        obj.id = in.readString();
        obj.iso_639_1 = in.readString();
        obj.iso_3166_1 = in.readString();
        obj.key = in.readString();
        obj.name = in.readString();
        obj.site = in.readString();
        obj.size = in.readInt();
        obj.type = in.readString();
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected Video(Parcel in) {
        this();
        readFromParcel(in, this);
    }


    @Override
    public int compareTo(@NonNull Object o) {
        int result;
        if (getClass() != o.getClass()) {
            result = -1;    // this object before other class
        } else {
            Video video = (Video) o;
            result = getName().compareTo(video.getName());
        }
        return result;
    }

    /** The natural order comparator for this object */
    public static Comparator<Object> COMPARATOR = new Comparator<Object>() {
        @Override
        public int compare(Object o1, Object o2) {
            int result;
            if (o1 == null) {
                throw new NullPointerException("Object o1");
            } else if (o2 == null) {
                throw new NullPointerException("Object o2");
            } else if (o1 == o2) {
                result = 0;
            } else {
                boolean c1Ok = o1.getClass().equals(Video.class);
                boolean c2Ok = o2.getClass().equals(Video.class);
                if (c1Ok && !c2Ok) {
                    result = -1;     // correct class first
                } else if (!c1Ok && c2Ok) {
                    result = 1;     // correct class first
                } else if (c1Ok) {
                    Video v1 = (Video) o1;
                    Video v2 = (Video) o2;
                    result = v1.getName().compareTo(v2.getName());
                } else {
                    result = 0;     // don't care
                }
            }
            return result;
        }
    };

    /**
     * Is trailer tester
     */
    public static ITester<Video> IS_TRAILER = new ITester<Video>() {
        @Override
        public boolean test(Video obj) {
            boolean is = false;
            if (obj != null) {
                is = (TRAILER.compareToIgnoreCase(obj.getType()) == 0);
            }
            return is;
        }
    };

    /**
     * Is teaser tester
     */
    public static ITester<Video> IS_TEASER = new ITester<Video>() {
        @Override
        public boolean test(Video obj) {
            boolean is = false;
            if (obj != null) {
                is = (TEASER.compareToIgnoreCase(obj.getType()) == 0);
            }
            return is;
        }
    };

    /**
     * Is teaser tester
     */
    public static ITester<Video> IS_CLIP = new ITester<Video>() {
        @Override
        public boolean test(Video obj) {
            boolean is = false;
            if (obj != null) {
                is = (CLIP.compareToIgnoreCase(obj.getType()) == 0);
            }
            return is;
        }
    };

    /**
     * Is featurette tester
     */
    public static ITester<Video> IS_FEATURETTE = new ITester<Video>() {
        @Override
        public boolean test(Video obj) {
            boolean is = false;
            if (obj != null) {
                is = (FEATURETTE.compareToIgnoreCase(obj.getType()) == 0);
            }
            return is;
        }
    };

    /**
     * Get a list of intents which may be used to view the video
     * @return  Array of intents or <code>null</code> if the video site is unknown, or key is invalid
     */
    public Intent[] getViewIntents() {
        return getViewIntents(this);
    }

    /**
     * Get a list of intents which may be used to view the video
     * @param video Video object to view
     * @return  Array of intents or <code>null</code> if the video site is unknown, or key is invalid
     */
    public static Intent[] getViewIntents(Video video) {
        Intent[] intents = null;
        if ((video.site.compareToIgnoreCase(YOU_TUBE) == 0) && !TextUtils.isEmpty(video.key)) {
            intents = new Intent[] {
                new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.key)),    // view in youtube app
                new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video.key))    // view in browser
            };
        }
        return intents;
    }

}
