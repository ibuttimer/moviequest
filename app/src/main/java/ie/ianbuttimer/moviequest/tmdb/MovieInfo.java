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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import ie.ianbuttimer.moviequest.utils.Utils;

import static ie.ianbuttimer.moviequest.Constants.INVALID_DATE;
import static ie.ianbuttimer.moviequest.utils.Utils.readBooleanFromParcel;
import static ie.ianbuttimer.moviequest.utils.Utils.readIntegerArrayFromParcel;
import static ie.ianbuttimer.moviequest.utils.Utils.writeBooleanToParcel;
import static ie.ianbuttimer.moviequest.utils.Utils.writeIntegerArrayToParcel;

/**
 * This class represents the movie details provided by TMDb as part of the popular & top rated movie lists
 *
 * Android unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoTest
 * Unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoGetInstanceTest
 */
@SuppressWarnings("unused")
public class MovieInfo extends TMDbObject implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names
    protected static HashMap<String, MemberEntry> placeholderMemberMap;  // map of default value fields for a placeholder

    private String posterPath;
    private String backdropPath;
    private String overview;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private Integer id;
    private Boolean adult;
    private Boolean video;
    private Integer voteCount;
    private Date releaseDate;
    private Integer[] genreIds;
    private Double popularity;
    private Double voteAverage;

    protected static final int FIRST_MEMBER = 0;
    protected static final int POSTER_PATH = 0;
    protected static final int BACKDROP_PATH = 1;
    protected static final int OVERVIEW = 2;
    protected static final int RELEASE_DATE = 3;
    protected static final int ORIGINAL_TITLE = 4;
    protected static final int ORIGINAL_LANGUAGE = 5;
    protected static final int TITLE = 6;
    protected static final int ID = 7;
    protected static final int ADULT = 8;
    protected static final int VIDEO = 9;
    protected static final int VOTE_COUNT = 10;
    protected static final int POPULARITY = 11;
    protected static final int VOTE_AVERAGE = 12;
    protected static final int GENRE_IDS = 13;
    protected static final int LAST_MOVIE_INFO_MEMBER = GENRE_IDS;

    private static final int[] sAllFields;  // list of all fields

    private static final String[] FIELD_NAMES = new String[] {
        // NOTE must follow the order of the indices above!!!!
        "poster_path",
        "backdrop_path",
        "overview",
        "release_date",
        "original_title",
        "original_language",
        "title",
        "id",
        "adult",
        "video",
        "vote_count",
        "popularity",
        "vote_average",
        "genre_ids"
    };

    static {
        jsonMemberMap = generateMemberMap(null);
        placeholderMemberMap = generateMemberMap(new int[] {
            TITLE, ID
        });

        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_MOVIE_INFO_MEMBER);
    }

    /**
     * Generate the member map representing this object
     * @param exclude   Array of ids of members to exclude
     * @return member map
     */
    protected static HashMap<String, MemberEntry> generateMemberMap(int[] exclude) {
        HashMap<String, MemberEntry> memberMap = new HashMap<>();
        int[] sortedExclude = Utils.getSortedArray(exclude);

        for (int i = FIRST_MEMBER; i <= LAST_MOVIE_INFO_MEMBER; i++) {
            if (Arrays.binarySearch(sortedExclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case POSTER_PATH:
                        memberMap.put(key, stringTemplate.copy("setPosterPath", "getPosterPath", "posterPath"));
                        break;
                    case BACKDROP_PATH:
                        memberMap.put(key, stringTemplate.copy("setBackdropPath", "getBackdropPath", "backdropPath"));
                        break;
                    case OVERVIEW:
                        memberMap.put(key, stringTemplate.copy("setOverview", "getOverview", "overview"));
                        break;
                    case RELEASE_DATE:
                        memberMap.put(key, stringTemplate.copy("setReleaseDate", "getReleaseDateMemberValue", "releaseDate"));
                        break;
                    case ORIGINAL_TITLE:
                        memberMap.put(key, stringTemplate.copy("setOriginalTitle", "getOriginalTitle", "originalTitle"));
                        break;
                    case ORIGINAL_LANGUAGE:
                        memberMap.put(key, stringTemplate.copy("setOriginalLanguage", "getOriginalLanguage", "originalLanguage"));
                        break;
                    case TITLE:
                        memberMap.put(key, stringTemplate.copy("setTitle", "getTitle", "title"));
                        break;
                    case ID:
                        memberMap.put(key, intTemplate.copy("setId", "getId", "id"));
                        break;
                    case ADULT:
                        memberMap.put(key, boolTemplate.copy("setAdult", "isAdult", "adult"));
                        break;
                    case VIDEO:
                        memberMap.put(key, boolTemplate.copy("setVideo", "isVideo", "video"));
                        break;
                    case VOTE_COUNT:
                        memberMap.put(key, intTemplate.copy("setVoteCount", "getVoteCount", "voteCount"));
                        break;
                    case POPULARITY:
                        memberMap.put(key, dblTemplate.copy("setPopularity", "getPopularity", "popularity"));
                        break;
                    case VOTE_AVERAGE:
                        memberMap.put(key, dblTemplate.copy("setVoteAverage", "getVoteAverage", "voteAverage"));
                        break;
                    case GENRE_IDS:
                        memberMap.put(key, jsonArrayTemplate.copy("setGenreIdsFromJson", "getGenreIdsMemberValue", "genreIds"));
                        break;
                }
            }
        }
        return memberMap;
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

    static String[] getFieldNamesArray() {
        return FIELD_NAMES;
    }

    @Override
    public int[] getFieldIds() {
        return sAllFields;
    }

    @Override
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
    }

    /**
     * Default constructor
     */
    public MovieInfo() {
        posterPath = "";
        backdropPath = "";
        adult = false;
        overview = "";
        releaseDate = INVALID_DATE;
        genreIds = new Integer[] {};
        id = 0;
        originalTitle = "";
        originalLanguage = "";
        title = "";
        popularity = 0.0d;
        voteCount = 0;
        video = false;
        voteAverage = 0.0d;
    }

    /**
     * Constructor for a MovieInfo placeholder
     * @param id        Movie id
     * @param title     Movie title
     */
    public MovieInfo(Integer id, String title) {
        this();
        this.title = title;
        this.id = id;
    }

    /**
     * Create a MovieInfo object from JSON data
     * @param jsonData  JSON data object
     * @return  new MovieInfo object or null if no data
     */
    public static MovieInfo getInstance(JSONObject jsonData) {
        return getInstance(jsonData, new MovieInfo());
    }

    /**
     * Create a MovieInfo object from JSON data
     * @param jsonData  JSON data object
     * @param movie     Object to save data into
     * @return updated object
     */
    static MovieInfo getInstance(JSONObject jsonData, MovieInfo movie) {
        return getInstance(jsonMemberMap, jsonData, movie);
    }

    @Override
    public <T extends TMDbObject> void copy(T from, int[] fields) {
        copy(from, this, fields);
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

    public Boolean isAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    /**
     * Return the release date as provided by the server
     * @return  Date string in server format, or field default value if not valid
     */
    public String getReleaseDateMemberValue() {
        String date;
        if (INVALID_DATE.equals(releaseDate)) {
            date = (String)jsonMemberMap.get(FIELD_NAMES[RELEASE_DATE]).dfltValue;
        } else {
            date = dateFormat.format(releaseDate);
        }
        return date;
    }

    public void setReleaseDate(String releaseDate) {
        try {
            this.releaseDate = dateFormat.parse(releaseDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
            this.releaseDate = INVALID_DATE;
        }
    }

    public Integer[] getGenreIds() {
        return genreIds;
    }

    /**
     * Return the genre ids as provided by the server
     * @return  Genre ids in server format, or field default value if not valid
     */
    public JSONArray getGenreIdsMemberValue() {
        JSONArray array = (JSONArray)jsonMemberMap.get(FIELD_NAMES[GENRE_IDS]).dfltValue;
        if ((genreIds != null) && (genreIds.length > 0)) {
            try {
                array = new JSONArray(new Gson().toJson(genreIds));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public void setGenreIds(Integer[] genreIds) {
        this.genreIds = genreIds;
    }

    public void setGenreIds(int[] genreIds) {
        int len;
        if (genreIds == null) {
            len = 0;
        } else {
            len = genreIds.length;
        }
        Integer[] ids = new Integer[len];
        for (int i = 0; i < len; i++) {
            ids[i] = genreIds[i];
        }
        this.genreIds = ids;
    }

    public void setGenreIdsFromJson(JSONArray genreIds) {
        int len;
        if (genreIds == null) {
            len = 0;
        } else {
            len = genreIds.length();
        }
        Integer[] ids = new Integer[len];
        for (int i = 0; i < len; i++) {
            try {
                ids[i] = genreIds.getInt(i);
            }
            catch (JSONException e) {
                e.printStackTrace();
                ids[i] = 0;
            }
        }
        this.genreIds = ids;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean isVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    @Override
    public boolean isEmpty() {
        return equals(new MovieInfo());
    }

    @Override
    public boolean isPlaceHolder() {
        return isDefault(placeholderMemberMap, this);
    }

    /**
     * Create a MovieInfo object from JSON
     * @param from      Object to copy from
     * @param to        Object to copy to
     * @param fields    Array of field isa of members to include
     * @return <code>true</code> if all fields were copied, <code>false</code> otherwise
     */
    public static boolean copy(MovieInfo from, MovieInfo to, int[] fields) {
        HashMap<String, MemberEntry> memberMap;
        if (fields == null) {
            memberMap = jsonMemberMap;
        } else {
            memberMap = from.makeMemberMap(fields);
        }
        return copy(memberMap, from, to);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieInfo movieInfo = (MovieInfo) o;

        if (posterPath != null ? !posterPath.equals(movieInfo.posterPath) : movieInfo.posterPath != null)
            return false;
        if (backdropPath != null ? !backdropPath.equals(movieInfo.backdropPath) : movieInfo.backdropPath != null)
            return false;
        if (overview != null ? !overview.equals(movieInfo.overview) : movieInfo.overview != null)
            return false;
        if (originalTitle != null ? !originalTitle.equals(movieInfo.originalTitle) : movieInfo.originalTitle != null)
            return false;
        if (originalLanguage != null ? !originalLanguage.equals(movieInfo.originalLanguage) : movieInfo.originalLanguage != null)
            return false;
        if (title != null ? !title.equals(movieInfo.title) : movieInfo.title != null) return false;
        if (id != null ? !id.equals(movieInfo.id) : movieInfo.id != null) return false;
        if (adult != null ? !adult.equals(movieInfo.adult) : movieInfo.adult != null) return false;
        if (video != null ? !video.equals(movieInfo.video) : movieInfo.video != null) return false;
        if (voteCount != null ? !voteCount.equals(movieInfo.voteCount) : movieInfo.voteCount != null)
            return false;
        if (releaseDate != null ? !releaseDate.equals(movieInfo.releaseDate) : movieInfo.releaseDate != null)
            return false;
        if (!Arrays.equals(genreIds, movieInfo.genreIds)) return false;
        if (popularity != null ? !popularity.equals(movieInfo.popularity) : movieInfo.popularity != null)
            return false;
        return voteAverage != null ? voteAverage.equals(movieInfo.voteAverage) : movieInfo.voteAverage == null;
    }

    @Override
    public int hashCode() {
        int result = posterPath != null ? posterPath.hashCode() : 0;
        result = 31 * result + (backdropPath != null ? backdropPath.hashCode() : 0);
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        result = 31 * result + (originalTitle != null ? originalTitle.hashCode() : 0);
        result = 31 * result + (originalLanguage != null ? originalLanguage.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (adult != null ? adult.hashCode() : 0);
        result = 31 * result + (video != null ? video.hashCode() : 0);
        result = 31 * result + (voteCount != null ? voteCount.hashCode() : 0);
        result = 31 * result + (releaseDate != null ? releaseDate.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(genreIds);
        result = 31 * result + (popularity != null ? popularity.hashCode() : 0);
        result = 31 * result + (voteAverage != null ? voteAverage.hashCode() : 0);
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
        parcel.writeString(overview);
        parcel.writeString(originalTitle);
        parcel.writeString(originalLanguage);
        parcel.writeString(title);
        parcel.writeInt(id);
        writeBooleanToParcel(parcel, adult);
        writeBooleanToParcel(parcel, video);
        parcel.writeInt(voteCount);
        parcel.writeSerializable(releaseDate);
        parcel.writeDouble(popularity);
        parcel.writeDouble(voteAverage);
        writeIntegerArrayToParcel(parcel, genreIds);
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR
            = new Parcelable.Creator<MovieInfo>() {
        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, MovieInfo obj) {
        obj.posterPath = in.readString();
        obj.backdropPath = in.readString();
        obj.overview = in.readString();
        obj.originalTitle = in.readString();
        obj.originalLanguage = in.readString();
        obj.title = in.readString();
        obj.id = in.readInt();
        obj.adult = readBooleanFromParcel(in);
        obj.video = readBooleanFromParcel(in);
        obj.voteCount = in.readInt();
        obj.releaseDate = (Date) in.readSerializable();
        obj.popularity = in.readDouble();
        obj.voteAverage = in.readDouble();
        obj.genreIds = readIntegerArrayFromParcel(in);
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected MovieInfo(Parcel in) {
        this();
        readFromParcel(in, this);
    }
}
