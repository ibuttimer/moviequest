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

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * This class represents the movie details provided by TMDb
 *
 * Android unit tests:
 *  ie.ianbuttimer.moviequest.tmdb.MovieDetailsTest
 * Unit tests:
 *  ie.ianbuttimer.moviequest.tmdb.TestMovieDetailInstance
 */

public class MovieDetails extends MovieInfo implements Parcelable {

    private static HashMap<String, MemberEntry> jsonMemberMap;  // map of JSON property names to class setter method & JSON getter method names

    private CollectionInfo collection;
    private Integer budget;
    private Genre[] genres;
    private String homepage;
    private String imdbId;
    private ProdCompany[] productionCompanies;
    private ProdCountry[] productionCountries;
    private Integer revenue;
    private Integer runtime;
    private Language[] spokenLanguages;
    private String status;
    private String tagline;

    protected static final int FIRST_MOVIE_DETAIL_MEMBER = LAST_MOVIE_INFO_MEMBER + 1;
    protected static final int BUDGET = FIRST_MOVIE_DETAIL_MEMBER;
    protected static final int GENRES = FIRST_MOVIE_DETAIL_MEMBER + 1;
    protected static final int HOMEPAGE = FIRST_MOVIE_DETAIL_MEMBER + 2;
    protected static final int IMDB_ID = FIRST_MOVIE_DETAIL_MEMBER + 3;
    protected static final int PRODUCTION_COMPANIES = FIRST_MOVIE_DETAIL_MEMBER + 4;
    protected static final int PRODUCTION_COUNTRIES = FIRST_MOVIE_DETAIL_MEMBER + 5;
    protected static final int REVENUE = FIRST_MOVIE_DETAIL_MEMBER + 6;
    protected static final int RUNTIME = FIRST_MOVIE_DETAIL_MEMBER + 7;
    protected static final int SPOKEN_LANGUAGES = FIRST_MOVIE_DETAIL_MEMBER + 8;
    protected static final int STATUS = FIRST_MOVIE_DETAIL_MEMBER + 9;
    protected static final int TAGLINE = FIRST_MOVIE_DETAIL_MEMBER + 10;
    protected static final int COLLECTION = FIRST_MOVIE_DETAIL_MEMBER + 11;
    protected static final int LAST_MOVIE_DETAIL_MEMBER = COLLECTION;

    private static final String[] FIELD_NAMES;  // NOTE order must follow the order of the indices in super class & above!!!!

    static {
        FIELD_NAMES = Arrays.copyOf(MovieInfo.getFieldNamesArray(), LAST_MOVIE_DETAIL_MEMBER + 1);
        FIELD_NAMES[BUDGET] = "budget";
        FIELD_NAMES[GENRES] = "genres";
        FIELD_NAMES[HOMEPAGE] = "homepage";
        FIELD_NAMES[IMDB_ID] = "imdb_id";
        FIELD_NAMES[PRODUCTION_COMPANIES] = "production_companies";
        FIELD_NAMES[PRODUCTION_COUNTRIES] = "production_countries";
        FIELD_NAMES[REVENUE] = "revenue";
        FIELD_NAMES[RUNTIME] = "runtime";
        FIELD_NAMES[SPOKEN_LANGUAGES] = "spoken_languages";
        FIELD_NAMES[STATUS] = "status";
        FIELD_NAMES[TAGLINE] = "tagline";
        FIELD_NAMES[COLLECTION] = "belongs_to_collection";

            // get members from super class
        jsonMemberMap = MovieInfo.generateMemberMap(new int [] {
                GENRE_IDS   // exclude genre ids which isn't part of this object
        });
        // add local members
        jsonMemberMap.putAll(generateMemberMap(null));
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
        for (int i = FIRST_MOVIE_DETAIL_MEMBER; i <= LAST_MOVIE_DETAIL_MEMBER; i++) {
            if (Arrays.binarySearch(exclude, i) < 0) {
                String key = FIELD_NAMES[i];
                switch (i) {
                    case BUDGET:
                        memberMap.put(key, intTemplate.copy("setBudget", "budget"));
                        break;
                    case GENRES:
                        memberMap.put(key, jsonArrayTemplate.copy("setGenresFromJson", "genres"));
                        break;
                    case HOMEPAGE:
                        memberMap.put(key, stringTemplate.copy("setHomepage", "homepage"));
                        break;
                    case IMDB_ID:
                        memberMap.put(key, stringTemplate.copy("setImdbId", "imdbId"));
                        break;
                    case PRODUCTION_COMPANIES:
                        memberMap.put(key, jsonArrayTemplate.copy("setProductionCompaniesFromJson", "productionCompanies"));
                        break;
                    case PRODUCTION_COUNTRIES:
                        memberMap.put(key, jsonArrayTemplate.copy("setProductionCountriesFromJson", "productionCountries"));
                        break;
                    case REVENUE:
                        memberMap.put(key, intTemplate.copy("setRevenue", "revenue"));
                        break;
                    case RUNTIME:
                        memberMap.put(key, intTemplate.copy("setRuntime", "runtime"));
                        break;
                    case SPOKEN_LANGUAGES:
                        memberMap.put(key, jsonArrayTemplate.copy("setSpokenLanguagesFromJson", "spokenLanguages"));
                        break;
                    case STATUS:
                        memberMap.put(key, stringTemplate.copy("setStatus", "status"));
                        break;
                    case TAGLINE:
                        memberMap.put(key, stringTemplate.copy("setTagline", "tagline"));
                        break;
                    case COLLECTION:
                        memberMap.put(key, jsonObjectTemplate.copy("setCollectionFromJson", "collection"));
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
    public MovieDetails() {
        super();
        budget = 0;
        genres = new Genre[] {};
        homepage = "";
        imdbId = "";
        productionCompanies = new ProdCompany[] {};
        productionCountries = new ProdCountry[] {};
        revenue = 0;
        runtime = 0;
        spokenLanguages = new Language[] {};
        status = "";
        tagline = "";
        collection = new CollectionInfo();
    }

    /**
     * Create a MovieInfo object from JSON data
     * @param jsonData  JSON data object
     * @return  new MovieInfo object or null if no data
     */
    public static MovieDetails getInstance(JSONObject jsonData) {
        return getInstance(jsonData, new MovieDetails());
    }

    /**
     * Create a MovieInfo object from JSON data
     * @param jsonString    JSON data string
     * @return  new MovieInfo object or null if no data
     */
    public static MovieDetails getInstance(String jsonString) {
        return (MovieDetails)getInstance(jsonMemberMap, jsonString, MovieDetails.class, new MovieDetails());
    }

    /**
     * Create a MovieDetails object from JSON data
     * @param jsonData  JSON data object
     * @param movie     Object to save data into
     * @return updated object
     */
    static MovieDetails getInstance(JSONObject jsonData, MovieDetails movie) {
        return (MovieDetails)getInstance(jsonMemberMap, jsonData, MovieDetails.class, movie);
    }


    public Integer getBudget() {
        return budget;
    }

    public String getBudgetFormatted() {
        return formatAmount(budget);
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public Genre[] getGenres() {
        return genres;
    }

    private String[] getIdNames(IdName[] array) {
        int len = array.length;
        String[] names = new String[len];
        for (int i = 0; i < len; i++) {
            names[i] = array[i].getName();
        }
        return names;
    }

    public String[] getGenreNames() {
        return getIdNames(genres);
    }

    public void setGenres(Genre[] genres) {
        this.genres = genres;
    }

    public void setGenresFromJson(JSONArray genres) {
        int len = genres.length();
        Genre[] array = new Genre[len];
        for (int i = 0; i < len; i++) {
            try {
                array[i] = Genre.getInstance(genres.getJSONObject(i));
            }
            catch (JSONException e) {
                e.printStackTrace();
                array[i] = new Genre();
            }
        }
        this.genres = array;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public ProdCompany[] getProductionCompanies() {
        return productionCompanies;
    }

    public String[] getProductionCompaniesNames() {
        return getIdNames(productionCompanies);
    }

    public void setProductionCompanies(ProdCompany[] productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public void setProductionCompaniesFromJson(JSONArray productionCompanies) {
        int len = productionCompanies.length();
        ProdCompany[] array = new ProdCompany[len];
        for (int i = 0; i < len; i++) {
            try {
                array[i] = ProdCompany.getInstance(productionCompanies.getJSONObject(i));
            }
            catch (JSONException e) {
                e.printStackTrace();
                array[i] = new ProdCompany();
            }
        }
        this.productionCompanies = array;
    }

    public ProdCountry[] getProductionCountries() {
        return productionCountries;
    }

    public String[] getProductionCountriesNames() {
        return getIsoNames(productionCountries);
    }

    public void setProductionCountries(ProdCountry[] productionCountries) {
        this.productionCountries = productionCountries;
    }

    public void setProductionCountriesFromJson(JSONArray productionCountries) {
        int len = productionCountries.length();
        ProdCountry[] array = new ProdCountry[len];
        for (int i = 0; i < len; i++) {
            try {
                array[i] = ProdCountry.getInstance(productionCountries.getJSONObject(i));
            }
            catch (JSONException e) {
                e.printStackTrace();
                array[i] = new ProdCountry();
            }
        }
        this.productionCountries = array;
    }

    public Integer getRevenue() {
        return revenue;
    }

    public String getRevenueFormatted() {
        return formatAmount(revenue);
    }

    public void setRevenue(Integer revenue) {
        this.revenue = revenue;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }

    public Language[] getSpokenLanguages() {
        return spokenLanguages;
    }

    private String[] getIsoNames(IsoName[] array) {
        int len = array.length;
        String[] names = new String[len];
        for (int i = 0; i < len; i++) {
            names[i] = array[i].getName();
        }
        return names;
    }

    public String[] getSpokenLanguageNames() {
        return getIsoNames(spokenLanguages);
    }

    public String getOriginalLanguageName() {
        String code = getOriginalLanguage();
        String name = "";
        for (int i = 0; i < spokenLanguages.length; i++) {
            if (code.equals(spokenLanguages[i].getIso())) {
                name =  spokenLanguages[i].getName();
                break;
            }
        }
        return name;
    }

    public void setSpokenLanguages(Language[] spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public void setSpokenLanguagesFromJson(JSONArray spokenLanguages) {
        int len = spokenLanguages.length();
        Language[] array = new Language[len];
        for (int i = 0; i < len; i++) {
            try {
                array[i] = Language.getInstance(spokenLanguages.getJSONObject(i));
            }
            catch (JSONException e) {
                e.printStackTrace();
                array[i] = new Language();
            }
        }
        this.spokenLanguages = array;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public CollectionInfo getCollection() {
        return collection;
    }

    public void setCollection(CollectionInfo collection) {
        this.collection = collection;
    }

    public void setCollectionFromJson(JSONObject collection) {
        this.collection = CollectionInfo.getInstance(collection);
    }

    private String formatAmount(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
        formatter.setMinimumFractionDigits(0);
        return formatter.format(amount);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MovieDetails that = (MovieDetails) o;

        if (collection != null ? !collection.equals(that.collection) : that.collection != null)
            return false;
        if (budget != null ? !budget.equals(that.budget) : that.budget != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(genres, that.genres)) return false;
        if (homepage != null ? !homepage.equals(that.homepage) : that.homepage != null)
            return false;
        if (imdbId != null ? !imdbId.equals(that.imdbId) : that.imdbId != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(productionCompanies, that.productionCompanies)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(productionCountries, that.productionCountries)) return false;
        if (revenue != null ? !revenue.equals(that.revenue) : that.revenue != null) return false;
        if (runtime != null ? !runtime.equals(that.runtime) : that.runtime != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(spokenLanguages, that.spokenLanguages)) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        return tagline != null ? tagline.equals(that.tagline) : that.tagline == null;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (collection != null ? collection.hashCode() : 0);
        result = 31 * result + (budget != null ? budget.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(genres);
        result = 31 * result + (homepage != null ? homepage.hashCode() : 0);
        result = 31 * result + (imdbId != null ? imdbId.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(productionCompanies);
        result = 31 * result + Arrays.hashCode(productionCountries);
        result = 31 * result + (revenue != null ? revenue.hashCode() : 0);
        result = 31 * result + (runtime != null ? runtime.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(spokenLanguages);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (tagline != null ? tagline.hashCode() : 0);
        return result;
    }

    @Override
    public boolean isEmpty() {
        return equals(new MovieDetails());
    }


    @Override
    public int describeContents() {
        return super.describeContents();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
        parcel.writeInt(budget);
        parcel.writeArray(genres);
        parcel.writeString(homepage);
        parcel.writeString(imdbId);
        parcel.writeArray(productionCompanies);
        parcel.writeArray(productionCountries);
        parcel.writeInt(revenue);
        parcel.writeInt(runtime);
        parcel.writeArray(spokenLanguages);
        parcel.writeString(status);
        parcel.writeString(tagline);
        parcel.writeParcelable(collection, flags);
    }

    public static final Parcelable.Creator<MovieDetails> CREATOR
            = new Parcelable.Creator<MovieDetails>() {
        public MovieDetails createFromParcel(Parcel in) {
            return new MovieDetails(in);
        }

        public MovieDetails[] newArray(int size) {
            return new MovieDetails[size];
        }
    };

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    void readFromParcel(Parcel in, MovieDetails obj) {
        super.readFromParcel(in, obj);
        obj.budget = in.readInt();
        obj.genres = (Genre[]) readArrayFromParcel(in, Genre.class.getClassLoader(), Genre[].class);
        obj.homepage = in.readString();
        obj.imdbId = in.readString();
        obj.productionCompanies = (ProdCompany[]) readArrayFromParcel(in, ProdCompany.class.getClassLoader(), ProdCompany[].class);
        obj.productionCountries = (ProdCountry[]) readArrayFromParcel(in, ProdCountry.class.getClassLoader(), ProdCountry[].class);
        obj.revenue = in.readInt();
        obj.runtime = in.readInt();
        obj.spokenLanguages = (Language[]) readArrayFromParcel(in, Language.class.getClassLoader(), Language[].class);
        obj.status = in.readString();
        obj.tagline = in.readString();
        obj.collection = in.readParcelable(CollectionInfo.class.getClassLoader());
    }

    /**
     * Constructor from Parcel
     * @param in    Parcel to read
     */
    protected MovieDetails(Parcel in) {
        this();
        readFromParcel(in, this);
    }

}
