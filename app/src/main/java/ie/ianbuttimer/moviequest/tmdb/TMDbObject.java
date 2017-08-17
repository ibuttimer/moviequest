/*
 * Copyright (C) 2017  Ian Buttimer
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ie.ianbuttimer.moviequest.tmdb;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Base class for TMDb-related objects
 */
public abstract class TMDbObject {

    protected static Class[] stringParameterTypes;      // single string parameter
    protected static Class[] intParameterTypes;         // single integer parameter
    protected static Class[] boolParameterTypes;        // single boolean parameter
    protected static Class[] dblParameterTypes;         // single double parameter
    protected static Class[] jsonObjectParameterTypes;  // single json object parameter
    protected static Class[] jsonArrayParameterTypes;   // single json array parameter

    protected static MemberEntry stringTemplate;    // string from json
    protected static MemberEntry intTemplate;       // int from json
    protected static MemberEntry boolTemplate;      // boolean from json
    protected static MemberEntry dblTemplate;       // double from json
    protected static MemberEntry jsonObjectTemplate;// object from json
    protected static MemberEntry jsonArrayTemplate; // array from json

    protected static SimpleDateFormat dateFormat;   // date format used by TMDb server

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        stringParameterTypes = new Class[] { String.class };
        intParameterTypes = new Class[] { Integer.class };
        boolParameterTypes = new Class[] { Boolean.class };
        dblParameterTypes = new Class[] { Double.class };
        jsonObjectParameterTypes = new Class[] { JSONObject.class };
        jsonArrayParameterTypes = new Class[] { JSONArray.class };

        /* setup map to support creating object from json using reflection */
        stringTemplate = new MemberEntry("", "", "getString", stringParameterTypes, "");
        intTemplate = new MemberEntry("", "", "getInt", intParameterTypes, 0);
        boolTemplate = new MemberEntry("", "", "getBoolean", boolParameterTypes, false);
        dblTemplate = new MemberEntry("", "", "getDouble", dblParameterTypes, 0.0d);
        jsonObjectTemplate = new MemberEntry("", "", "getJSONObject", jsonObjectParameterTypes, null);
        jsonArrayTemplate = new MemberEntry("", "", "getJSONArray", jsonArrayParameterTypes, null);
    }

    /**
     * Default constructor
     */
    public TMDbObject() {
        // noop
    }

    /**
     * Get the member map for the specific sub class
     * @return
     */
    protected abstract HashMap<String, MemberEntry> getMemberMap();

    /**
     * Get the field name as returned by the TMDb server
     * @param index     Index of field
     * @return  field name or empty string if invalid index
     */
    protected abstract String getFieldName(int index);

    /**
     * Get the array of field names as returned by the TMDb server
     * @return  field names
     */
    protected abstract String[] getFieldNames();

    /**
     * Get the object property name associated with the specified index
     * @param index     Index of field
     * @return  property name or empty string if invalid index
     */
    protected String getPropertyName(int index) {
        String propertyName = "";
        String fieldName = getFieldName(index);
        if (fieldName.length() > 0) {
            HashMap<String, MemberEntry> memberMap = getMemberMap();
            MemberEntry entry = memberMap.get(fieldName);
            if (entry != null) {
                propertyName = entry.property;
            }
        }
        return propertyName;
    }

        /**
         * Class representing the methods to be used by reflection for a class variable
         */
    protected static class MemberEntry {
        String classSetter;     // name of setter method in MovieInfo
        String jsonGetter;      // name of getter method to extract from JSON object
        String property;        // name of the object's internal property
        Class[] parameterTypes; // array that identifies the method's formal parameter types
        Object dfltValue;       // default value

        public MemberEntry(String classSetter, String property, String jsonGetter, Class[] parameterTypes, Object dfltValue) {
            this.classSetter = classSetter;
            this.property = property;
            this.jsonGetter = jsonGetter;
            this.parameterTypes = parameterTypes;
            this.dfltValue = dfltValue;
        }

        /**
         * Creates a copy of this object with a new <code>classSetter</code>
         * @param classSetter   Class setter to use
         * @return  new object
         */
        public MemberEntry copy(String classSetter, String property) {
            return new MemberEntry(classSetter, property, this.jsonGetter, this.parameterTypes, this.dfltValue);
        }
    }

    /**
     * Create a MovieInfo object from JSON
     * @param memberMap Map of JSON property names to class setter method & JSON getter method names
     * @param jsonData  JSON data object
     * @param objClass  Class of object to save data into
     * @param obj       Object to save data into
     * @return updated object
     */
    public static Object getInstance(HashMap<String, MemberEntry> memberMap, JSONObject jsonData, Class objClass, Object obj) {
        if ((jsonData != null) && (jsonData.length() > 0)) {
            try {
                for (String key : memberMap.keySet()) {
                    if (jsonData.has(key)) {
                        MemberEntry entry = memberMap.get(key);
                        Object data;
                        try {
                            // use reflection to create object
                            Method getMethod = JSONObject.class.getMethod(entry.jsonGetter, stringParameterTypes);  // get the get method
                            data = getMethod.invoke(jsonData, key);  // get the data
                        } catch (Exception e) {
                            data = jsonData.get(key);  // get the data using generic get
                            if (data.equals(JSONObject.NULL)) {
                                data = entry.dfltValue;
                            } else {
                                e.printStackTrace();
                            }
                        }
                        try {
                            // use reflection to set value
                            Method setMethod = objClass.getMethod(entry.classSetter, entry.parameterTypes); // get the set method
                            setMethod.invoke(obj, data);    // set the data
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();;
            }
        }
        return obj;
    }

    /**
     * Create a MovieInfo object from JSON data
     * @param memberMap     Map of JSON property names to class setter method & JSON getter method names
     * @param jsonString    JSON data string
     * @param objClass      Class of object to save data into
     * @param obj           Object to save data into
     * @return updated object
     */
    static Object getInstance(HashMap<String, MemberEntry> memberMap, String jsonString, Class objClass, Object obj) {
        try {
            JSONObject json = new JSONObject(jsonString);
            getInstance(memberMap, json, objClass, obj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Write an Integer object array to a Parcel
     * @param parcel    Parcel to write to
     * @param flags     Additional flags about how the object should be written.
     * @param array     Array to write
     */
    public void writeIntegerArrayToParcel(Parcel parcel, int flags, Integer[] array) {
        int len = array.length;
        int[] intArray = new int[len];
        for (int index = 0; index < len; index++) {
            intArray[index] = array[index].intValue();
        }
        parcel.writeInt(len);
        if (len > 0) {
            parcel.writeIntArray(intArray);
        }
    }

    /**
     * Read an int array from a Parcel
     * @param in    Parcel to read from
     * @return  Integer object array
     */
    public int[] readIntArrayFromParcel(Parcel in) {
        int len = in.readInt();
        int[] intArray = new int[len];
        if (len > 0) {
            in.readIntArray(intArray);
        }
        return intArray;
    }

    /**
     * Read an Integer object array from a Parcel
     * @param in    Parcel to read from
     * @return  Integer object array
     */
    public Integer[] readIntegerArrayFromParcel(Parcel in) {
        int[] intArray = readIntArrayFromParcel(in);
        int len = intArray.length;
        Integer[] array = new Integer[len];
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                array[i] = Integer.valueOf(intArray[i]);
            }
        }
        return array;
    }

    /**
     * Read an array from a Parcel
     * @param in            Parcel to read from
     * @param loader        Class loader to create array elements
     * @param arrayClass    Class of the copy to be returned
     * @return
     */
    public Object[] readArrayFromParcel(Parcel in, ClassLoader loader, Class arrayClass) {
        Object[] objArray = in.readArray(loader);
        return Arrays.copyOf(objArray, objArray.length, arrayClass);
    }

    /**
     * Check if the object is empty
     * @return  <code>true</code> if no meaningful data set
     */
    public abstract boolean isEmpty();

    /**
     * Check if the object is empty
     * @return  <code>true</code> if no meaningful data set
     */
    public static boolean isEmpty(TMDbObject obj) {
        return (obj == null) || obj.isEmpty();
    }
}
