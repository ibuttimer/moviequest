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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * This class represents the movie details provided by TMDb as part of the popular & top rated movie lists
 *
 * Android unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoTest
 * Unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoGetInstanceTest
 */
public class MovieInfo extends TMDbObject implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    private String posterPath;
    private String backdropPath;
    private String overview;
    private String originalTitle;
    private String originalLanguage;
    private String title;
    private Integer id;
    private Integer adult;  // actually boolean values but storing as ints for parcelable convenience
    private Integer video;  // actually boolean values but storing as ints for parcelable convenience
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
    }

    /**
     * Generate the member map representing this object
     * @param exclude   Array of ids of members to exclude
     */
    protected static HashMap<String, MemberEntry> generateMemberMap(int[] exclude) {
        HashMap<String, MemberEntry> memberMap = new HashMap<String, MemberEntry>();
        if (exclude == null) {
            exclude = new int[] {};
        }
        for (int i = FIRST_MEMBER; i <= LAST_MOVIE_INFO_MEMBER; i++) {
            if (Arrays.binarySearch(exclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case POSTER_PATH:
                        memberMap.put(key, stringTemplate.copy("setPosterPath", "posterPath"));
                        break;
                    case BACKDROP_PATH:
                        memberMap.put(key, stringTemplate.copy("setBackdropPath", "backdropPath"));
                        break;
                    case OVERVIEW:
                        memberMap.put(key, stringTemplate.copy("setOverview", "overview"));
                        break;
                    case RELEASE_DATE:
                        memberMap.put(key, stringTemplate.copy("setReleaseDate", "releaseDate"));
                        break;
                    case ORIGINAL_TITLE:
                        memberMap.put(key, stringTemplate.copy("setOriginalTitle", "originalTitle"));
                        break;
                    case ORIGINAL_LANGUAGE:
                        memberMap.put(key, stringTemplate.copy("setOriginalLanguage", "originalLanguage"));
                        break;
                    case TITLE:
                        memberMap.put(key, stringTemplate.copy("setTitle", "title"));
                        break;
                    case ID:
                        memberMap.put(key, intTemplate.copy("setId", "id"));
                        break;
                    case ADULT:
                        memberMap.put(key, boolTemplate.copy("setAdult", "adult"));
                        break;
                    case VIDEO:
                        memberMap.put(key, boolTemplate.copy("setVideo", "video"));
                        break;
                    case VOTE_COUNT:
                        memberMap.put(key, intTemplate.copy("setVoteCount", "voteCount"));
                        break;
                    case POPULARITY:
                        memberMap.put(key, dblTemplate.copy("setPopularity", "popularity"));
                        break;
                    case VOTE_AVERAGE:
                        memberMap.put(key, dblTemplate.copy("setVoteAverage", "voteAverage"));
                        break;
                    case GENRE_IDS:
                        memberMap.put(key, jsonArrayTemplate.copy("setGenreIdsFromJson", "genreIds"));
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
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
    }

    /**
     * Default constructor
     */
    public MovieInfo() {
        posterPath = "";
        backdropPath = "";
        adult = 0;
        overview = "";
        releaseDate = new Date(0);
        genreIds = new Integer[] {};
        id = 0;
        originalTitle = "";
        originalLanguage = "";
        title = "";
        popularity = 0.0d;
        voteCount = 0;
        video = 0;
        voteAverage = 0.0d;
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
        return (MovieInfo)getInstance(jsonMemberMap, jsonData, MovieInfo.class, movie);
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
        return (adult == 1);
    }

    public void setAdult(Boolean adult) {
        this.adult = (adult ? 1 : 0);
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

    public void setReleaseDate(String releaseDate) {
        try {
            this.releaseDate = dateFormat.parse(releaseDate);
        }
        catch (ParseException e) {
            e.printStackTrace();
            this.releaseDate = new Date(0);
        }
    }

    public Integer[] getGenreIds() {
        return genreIds;
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
            ids[i] = Integer.valueOf(genreIds[i]);
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
        return (video == 1);
    }

    public void setVideo(Boolean video) {
        this.video = (video ? 1 : 0);
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
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
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
        parcel.writeInt(adult);
        parcel.writeInt(video);
        parcel.writeInt(voteCount);
        parcel.writeSerializable(releaseDate);
        parcel.writeDouble(popularity);
        parcel.writeDouble(voteAverage);
        writeIntegerArrayToParcel(parcel, flags, genreIds);
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
        obj.adult = in.readInt();
        obj.video = in.readInt();
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
