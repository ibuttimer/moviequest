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
import android.net.Uri;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Class representing movie info as used within the app
 *
 * Android unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoModelTest
 * Unit tests:
 *  ie.ianbuttimer.moviequest.data.MovieInfoModelGetInstanceTest
 */
public class MovieInfoModel extends MovieInfo implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    private int index;   // index of this object in a movies response
	private Uri posterUri;		// movie card image uri
	private Uri backdropUri;	// details backdrop image uri
	private Uri thumbnailUri;	// thumbnail image uri

    protected static final int FIRST_MOVIE_MODEL_MEMBER = LAST_MOVIE_INFO_MEMBER + 1;
    protected static final int INDEX = FIRST_MOVIE_MODEL_MEMBER;
    protected static final int POSTER_URI = FIRST_MOVIE_MODEL_MEMBER + 1;
    protected static final int BACKDROP_URI = FIRST_MOVIE_MODEL_MEMBER + 2;
    protected static final int THUMBNAIL_URI = FIRST_MOVIE_MODEL_MEMBER + 3;
    protected static final int LAST_MOVIE_MODEL_MEMBER = THUMBNAIL_URI;

    private static final String[] FIELD_NAMES;  // NOTE order must follow the order of the indices in super class & above!!!!

    static {
        FIELD_NAMES = Arrays.copyOf(MovieInfo.getFieldNamesArray(), LAST_MOVIE_MODEL_MEMBER + 1);
        FIELD_NAMES[INDEX] = "nonTMDb_index";    // field not returned from TMDb
        FIELD_NAMES[POSTER_URI] = "nonTMDb_posterUri"; // field not returned from TMDb
        FIELD_NAMES[BACKDROP_URI] = "nonTMDb_backdropUri"; // field not returned from TMDb
        FIELD_NAMES[THUMBNAIL_URI] = "nonTMDb_thumbnailUri";	// field not returned from TMDb

        // get members from super class
        jsonMemberMap = MovieInfo.generateMemberMap(null);
        // add local members
        jsonMemberMap.putAll(generateMemberMap(null));
    }

    static {
        jsonMemberMap = generateMemberMap(null);
        // append this object's
        jsonMemberMap.put(FIELD_NAMES[INDEX], intTemplate.copy("setIndex", "index"));
        jsonMemberMap.put(FIELD_NAMES[POSTER_URI], stringTemplate.copy("setPosterUri", "posterUri"));
        jsonMemberMap.put(FIELD_NAMES[BACKDROP_URI], stringTemplate.copy("setBackdropUri", "backdropUri"));
        jsonMemberMap.put(FIELD_NAMES[THUMBNAIL_URI], stringTemplate.copy("setThumbnailUri", "thumbnailUri"));
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

    /**
     * Default constructor
     */
    public MovieInfoModel() {
        super();
        index = 0;
		posterUri = null;
        backdropUri = null;
		thumbnailUri = null;
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
        return (MovieInfoModel)getInstance(jsonMemberMap, jsonData, MovieInfoModel.class, movieModel);
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
