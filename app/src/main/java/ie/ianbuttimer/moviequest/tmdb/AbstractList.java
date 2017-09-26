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

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import ie.ianbuttimer.moviequest.data.IEmpty;
import ie.ianbuttimer.moviequest.utils.FilterList;
import ie.ianbuttimer.moviequest.utils.ITester;

import static ie.ianbuttimer.moviequest.utils.Utils.readBooleanFromParcel;
import static ie.ianbuttimer.moviequest.utils.Utils.writeBooleanToParcel;

/**
 * Class representing a TMDb server response to a list request
 */
@SuppressWarnings("unused")
public abstract class AbstractList<T extends TMDbObject> implements Parcelable, IEmpty {

    /** The number of movie result per list response. This mirrors what the TMDb server currently returns */
    public static final int RESULTS_PER_LIST = 20;

    protected int totalResults = 0;   // total number of results
    protected int totalPages = 0;     // total number of pages
    protected int pageNumber = 0;     // current page
    protected int resultsPerPage;
    private T[] results;
    protected boolean nonResponse;    // non response flag


    // properties of results json objects from TMDb server
    /** Name of page value in TMDb server response */
    public static final String LIST_PAGE = "page";
    /** Name of results array value in TMDb server response */
    public static final String LIST_RESULTS = "results";
    /** Name of total results count value in TMDb server response */
    public static final String LIST_TOTAL_RESULTS = "total_results";
    /** Name of total pages count value in TMDb server response */
    public static final String LIST_TOTAL_PAGES = "total_pages";

    public static final String RESULTS_PER_PAGE = "results_per_page";


    /**
     * Default constructor
     */
    public AbstractList(T[] results) {
        setNumbers(0, 0, 0);
        this.results = results;
        nonResponse = true;   // doesn't represent a valid response
    }

    /**
     * Create a AbstractList object from a JSON object
     * @param response  AbstractList object to save list to
     * @param json      The JSON object to read
     * @return  The AbstractList object
     */
    public static <T extends TMDbObject> AbstractList<T> getListFromJson(AbstractList<T> response, JSONObject json) {
        if (json != null) {
            if (json.has(LIST_TOTAL_RESULTS) && json.has(LIST_TOTAL_PAGES) && json.has(LIST_PAGE)) {
                try {
                    response.setNumbers(json.getInt(LIST_TOTAL_RESULTS),
                            json.getInt(LIST_TOTAL_PAGES), json.getInt(LIST_PAGE));
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (json.has(LIST_RESULTS)) {
                try {
                    JSONArray array = json.getJSONArray(LIST_RESULTS);
                    int length = array.length();

                    response.setListLength(length);

                    for (int i = 0; i < length; i++) {
                        response.setResult(i, array.getJSONObject(i));
                    }
                    response.nonResponse = false;   // represents a valid response
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // read fields particular to the response object
            response.readExtraFields(json);
        }
        return response;
    }

    /**
     * Read extra field for subclasses
     * @param json      JSONObject to construct result from
     */
    protected abstract void readExtraFields(JSONObject json);

    /**
     * Set the result for the specified index
     * @param index     Index of result to set
     * @param json      JSONObject to construct result from
     */
    protected abstract void setResult(int index, JSONObject json);

    /**
     * Create a AbstractList object from a JSON string
     * @param response  AbstractList object to save list to
     * @param jsonString  The JSON string to read
     * @return  A AbstractList object
     */
    public static <T extends TMDbObject> AbstractList<T> getListFromJsonString(AbstractList<T> response, String jsonString) {
        try {
            if (!TextUtils.isEmpty(jsonString)) {
                JSONObject json = new JSONObject(jsonString);
                getListFromJson(response, json);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    /**
     * Create a AbstractList object from a Bundle
     * @param response  AbstractList object to save list to
     * @param bundle    The bundle read
     * @return  A AbstractList object
     */
    public static <T extends TMDbObject> AbstractList<T> getListFromBundle(AbstractList<T> response, Bundle bundle) {
        if (bundle != null) {
            try {
                response.setPageNumber(bundle.getInt(LIST_PAGE, 1));   // server is 1-based
                response.setTotalResults(bundle.getInt(LIST_TOTAL_RESULTS, 0));
                response.setTotalPages(bundle.getInt(LIST_TOTAL_PAGES, 0));

                String[] jsonStringArray = null;
                if (bundle.containsKey(LIST_RESULTS)) {
                    jsonStringArray = bundle.getStringArray(LIST_RESULTS);
                }
                if (jsonStringArray == null) {
                    jsonStringArray = new String[] {};
                }
                int length = jsonStringArray.length;

                response.setListLength(length);

                for (int i = 0; i < length; i++) {
                    response.setResult(i, new JSONObject(jsonStringArray[i]));
                }

                // read fields particular to the response object
                response.readExtraFields(bundle);

                response.nonResponse = false;   // represents a valid response
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    /**
     * Read extra field for subclasses
     * @param bundle    The bundle read
     */
    protected abstract void readExtraFields(Bundle bundle);

    private void setNumbers(int totalResults, int totalPages, int pageNumber) {
        this.totalResults = totalResults;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        if (totalPages != 0) {
            resultsPerPage = totalResults / totalPages;
        } else {
            resultsPerPage = 0;
        }
    }

    protected void setResult(int index, T result) {
        if (index < results.length) {
            results[index] = result;
        }
    }

    protected void setListLength(int length) {
        if (length >= 0) {
            results = Arrays.copyOf(results, length);
        }
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        setNumbers(totalResults, totalPages, pageNumber);
    }

    public int getResultsPerPage() {
        return resultsPerPage;
    }

    public T[] getResults() {
        return results;
    }

    /**
     * Get a subset of the results
     * @param tester    Tester to select results to return
     * @return  Results subsete
     */
    public T[] getResults(ITester<T> tester) {
        return new FilterList<T>(results, tester).filter();
    }

    public int getResultCount() {
        int count = 0;
        if (results != null) {
            count = results.length;
        }
        return count;
    }

    public void setResults(T[] results) {
        this.results = results;
    }

    /**
     * Check if the range represented by this object is valid
     * @return <code>true</code>     if range is valid
     */
    public boolean rangeIsValid() {
        return ((totalResults > 0) && (totalPages > 0) && (pageNumber > 0));
    }

    /**
     * Get the start number of the range represented by this object
     * @return  Start of range or <code>-1</code> if range is invalid
     */
    public int getRangeStart() {
        int start;
        if (rangeIsValid()) {
            start = (((pageNumber - 1) * resultsPerPage) + 1);
        } else {
            start = -1;
        }
        return start;
    }

    /**
     * Get the end number of the range represented by this object
     * @return  End of range or <code>-1</code> if range is invalid
     */
    public int getRangeEnd() {
        int end = getRangeStart();
        if (end > 0) {
            end += resultsPerPage - 1;
            if (end > totalResults) {
                end = totalResults;
            }
        }
        return end;
    }

    @Override
    public boolean isEmpty() {
        return nonResponse;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(totalResults);
        parcel.writeInt(totalPages);
        parcel.writeInt(pageNumber);
        parcel.writeInt(resultsPerPage);
        parcel.writeParcelableArray(results, flags);
        writeBooleanToParcel(parcel, nonResponse);
    }

    /**
     * Populate the specified object from the specified Parcel
     * @param in    Parcel to read
     * @param obj   Object to populate
     */
    protected void readFromParcel(Parcel in, AbstractList obj, ClassLoader loader, Class<? extends TMDbObject[]> arrayClass) {
        obj.totalResults = in.readInt();
        obj.totalPages = in.readInt();
        obj.pageNumber = in.readInt();
        obj.resultsPerPage = in.readInt();

        Object[] objArray = in.readArray(loader);
        obj.results = Arrays.copyOf(objArray, objArray.length, arrayClass);

        obj.nonResponse = readBooleanFromParcel(in);
    }
}
