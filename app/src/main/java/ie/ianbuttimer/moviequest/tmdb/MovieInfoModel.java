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
import android.net.Uri;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static ie.ianbuttimer.moviequest.Constants.INVALID_DATE;
import static ie.ianbuttimer.moviequest.utils.Utils.readBooleanFromParcel;
import static ie.ianbuttimer.moviequest.utils.Utils.writeBooleanToParcel;

/**
 * Class representing movie info as used within the app
 *
 * Android unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoModelTest
 * Unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoModelGetInstanceTest
 *  NOTE: currently incomplete, and not strictly required
 */
@SuppressWarnings("unused")
public class MovieInfoModel extends MovieInfo implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    private int index;   // index of this object in a movies response
	private Uri posterUri;		// movie card image uri
	private Uri backdropUri;	// details backdrop image uri
	private Uri thumbnailUri;	// thumbnail image uri
    private MovieDetails details;   // movie details
    private Date cacheDate;     // date movie details were cached
    private boolean favourite;  // favourite flag

    protected static final int FIRST_MOVIE_MODEL_MEMBER = LAST_MOVIE_INFO_MEMBER + 1;
    protected static final int INDEX = FIRST_MOVIE_MODEL_MEMBER;
    protected static final int POSTER_URI = FIRST_MOVIE_MODEL_MEMBER + 1;
    protected static final int BACKDROP_URI = FIRST_MOVIE_MODEL_MEMBER + 2;
    protected static final int THUMBNAIL_URI = FIRST_MOVIE_MODEL_MEMBER + 3;
    protected static final int DETAILS = FIRST_MOVIE_MODEL_MEMBER + 4;
    protected static final int CACHE_DATE = FIRST_MOVIE_MODEL_MEMBER + 5;
    protected static final int FAVOURITE = FIRST_MOVIE_MODEL_MEMBER + 6;
    protected static final int LAST_MOVIE_MODEL_MEMBER = FAVOURITE;

    private static final int[] sAllFields;  // list of all fields

    private static final String[] FIELD_NAMES;  // NOTE order must follow the order of the indices in super class & above!!!!

    static {
        FIELD_NAMES = Arrays.copyOf(MovieInfo.getFieldNamesArray(), LAST_MOVIE_MODEL_MEMBER + 1);
        FIELD_NAMES[INDEX] = "nonTMDb_index";    // field not returned from TMDb
        FIELD_NAMES[POSTER_URI] = "nonTMDb_posterUri"; // field not returned from TMDb
        FIELD_NAMES[BACKDROP_URI] = "nonTMDb_backdropUri"; // field not returned from TMDb
        FIELD_NAMES[THUMBNAIL_URI] = "nonTMDb_thumbnailUri";	// field not returned from TMDb
        FIELD_NAMES[DETAILS] = "nonTMDb_Details";	// field not returned from TMDb
        FIELD_NAMES[CACHE_DATE] = "nonTMDb_cacheDate";	// field not returned from TMDb
        FIELD_NAMES[FAVOURITE] = "nonTMDb_favourite";	// field not returned from TMDb

        // get members from super class
        jsonMemberMap = MovieInfo.generateMemberMap(null);
        // append this object's
        jsonMemberMap.put(FIELD_NAMES[INDEX], intTemplate.copy("setIndex", "getIndex", "index"));
        jsonMemberMap.put(FIELD_NAMES[POSTER_URI], stringTemplate.copy("setPosterUri", "getPosterUri", "posterUri"));
        jsonMemberMap.put(FIELD_NAMES[BACKDROP_URI], stringTemplate.copy("setBackdropUri", "getBackdropUri", "backdropUri"));
        jsonMemberMap.put(FIELD_NAMES[THUMBNAIL_URI], stringTemplate.copy("setThumbnailUri", "getThumbnailUri", "thumbnailUri"));
        jsonMemberMap.put(FIELD_NAMES[DETAILS], stringTemplate.copy("setDetails", "getDetails", "details"));
        jsonMemberMap.put(FIELD_NAMES[CACHE_DATE], stringTemplate.copy("setCacheDate", "getCacheDate", "cacheDate"));
        jsonMemberMap.put(FIELD_NAMES[FAVOURITE], boolTemplate.copy("setFavourite", "isFavourite", "favourite"));

        sAllFields = makeFieldIdsArray(FIRST_MEMBER, LAST_MOVIE_MODEL_MEMBER);
    }

    @Override
    protected HashMap<String, MemberEntry> getMemberMap() {
        return jsonMemberMap;
    }

    @Override
    protected String[] getFieldNames() {
        return FIELD_NAMES;
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
    public int[] getFieldIds() {
        return sAllFields;
    }

    /**
     * Default constructor
     */
    public MovieInfoModel() {
        super();
        index = 0;
		posterUri = null;
        backdropUri = null;
		thumbnailUri = null;
        details = null;
        cacheDate = INVALID_DATE;
        favourite = false;
    }

    /**
     * Constructor for a MovieInfoModel placeholder
     * @param id        Movie id
     * @param title     Movie title
     */
    public MovieInfoModel(Integer id, String title) {
        this();
        setTitle(title);
        setId(id);
    }

    /**
     * Create a MovieInfoModel object from JSON data
     * @param jsonData  JSON data object
     * @return  new MovieInfoModel object or null if no data
     */
    public static MovieInfoModel getInstance(JSONObject jsonData) {
        return getInstance(jsonData, new MovieInfoModel());
    }

    /**
     * Create a MovieInfoModel object from JSON data
     * @param jsonData  JSON data object
     * @return  new MovieInfo object or null if no data
     */
    public static MovieInfoModel getInstance(JSONObject jsonData, MovieInfoModel movieModel) {
        return getInstance(jsonMemberMap, jsonData, movieModel);
    }

    @Override
    public <T extends TMDbObject> void copy(T from, int[] fields) {
        copy(from, this, fields);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Uri getPosterUri() {
        return posterUri;
    }

    public void setPosterUri(Uri posterUri) {
        this.posterUri = posterUri;
    }

    public Uri getBackdropUri() {
        return backdropUri;
    }

    public void setBackdropUri(Uri backdropUri) {
        this.backdropUri = backdropUri;
    }

    public Uri getThumbnailUri() {
        return thumbnailUri;
    }

    public void setThumbnailUri(Uri thumbnailUri) {
        this.thumbnailUri = thumbnailUri;
    }

    public MovieDetails getDetails() {
        return details;
    }

    public void setDetails(MovieDetails details) {
        this.details = details;
    }

    public Date getCacheDate() {
        return cacheDate;
    }

    public void setCacheDate(Date cacheDate) {
        this.cacheDate = cacheDate;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    @Override
    public boolean isPlaceHolder() {
        return isDefault(placeholderMemberMap, this);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(index);
        parcel.writeParcelable(posterUri, flags);
        parcel.writeParcelable(thumbnailUri, flags);
        parcel.writeParcelable(backdropUri, flags);
        parcel.writeParcelable(details, flags);
        parcel.writeSerializable(cacheDate);
        writeBooleanToParcel(parcel, favourite);
    }

    public static final Parcelable.Creator<MovieInfoModel> CREATOR
            = new Parcelable.Creator<MovieInfoModel>() {
        public MovieInfoModel createFromParcel(Parcel in) {
            return new MovieInfoModel(in);
        }

        public MovieInfoModel[] newArray(int size) {
            return new MovieInfoModel[size];
        }
    };

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, MovieInfoModel obj) {
        super.readFromParcel(in, obj);
        obj.index = in.readInt();
        obj.posterUri = in.readParcelable(Uri.class.getClassLoader());
        obj.thumbnailUri = in.readParcelable(Uri.class.getClassLoader());
        obj.backdropUri = in.readParcelable(Uri.class.getClassLoader());
        obj.details = in.readParcelable(MovieDetails.class.getClassLoader());
        obj.cacheDate = (Date) in.readSerializable();
        obj.favourite = readBooleanFromParcel(in);
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    private MovieInfoModel(Parcel in) {
        this();
        readFromParcel(in, this);
    }
}
