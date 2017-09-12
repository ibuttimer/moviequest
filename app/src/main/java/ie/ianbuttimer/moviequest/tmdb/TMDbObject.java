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
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

/**
 * Base class for TMDb-related objects
 */
public abstract class TMDbObject {

    private static Class[] stringParameterTypes;      // single string parameter
    private static Class[] intParameterTypes;         // single integer parameter
    private static Class[] boolParameterTypes;        // single boolean parameter
    private static Class[] dblParameterTypes;         // single double parameter
    private static Class[] jsonObjectParameterTypes;  // single json object parameter
    private static Class[] jsonArrayParameterTypes;   // single json array parameter

    static MemberEntry stringTemplate;    // string from json
    static MemberEntry intTemplate;       // int from json
    static MemberEntry boolTemplate;      // boolean from json
    static MemberEntry dblTemplate;       // double from json
    static MemberEntry jsonObjectTemplate;// object from json
    static MemberEntry jsonArrayTemplate; // array from json

    static SimpleDateFormat dateFormat;   // date format used by TMDb server

    static {
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        stringParameterTypes = new Class[] { String.class };
        intParameterTypes = new Class[] { Integer.class };
        boolParameterTypes = new Class[] { Boolean.class };
        dblParameterTypes = new Class[] { Double.class };
        jsonObjectParameterTypes = new Class[] { JSONObject.class };
        jsonArrayParameterTypes = new Class[] { JSONArray.class };

        /* setup map to support creating object from json using reflection */
        stringTemplate = new MemberEntry("", "", "", "getString", stringParameterTypes, "");
        intTemplate = new MemberEntry("", "", "", "getInt", intParameterTypes, 0);
        boolTemplate = new MemberEntry("", "", "", "getBoolean", boolParameterTypes, false);
        dblTemplate = new MemberEntry("", "", "", "getDouble", dblParameterTypes, 0.0d);
        jsonObjectTemplate = new MemberEntry("", "", "", "getJSONObject", jsonObjectParameterTypes, null);
        jsonArrayTemplate = new MemberEntry("", "", "", "getJSONArray", jsonArrayParameterTypes, null);
    }


    /**
     * Make a all field ids array
     * @param first     Id of first field
     * @param last      Id of last field
     * @return  field ids array
     */
    protected static int[] makeFieldIdsArray(int first, int last) {
        int [] allFields = new int[last - first+ 1];
        for (int i = first; i <= last; i++) {
            allFields[i] = i;
        }
        return allFields;
    }

    /**
     * Default constructor
     */
    public TMDbObject() {
        // noop
    }

    /**
     * Get the member map for the specific sub class
     * @return Member map
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
     * Get the array of field ids used by this object
     * @return  field ids
     */
    public abstract int[] getFieldIds();

    /**
     * Get the object property name associated with the specified index
     * @param index     Index of field
     * @return  property name or empty string if invalid index
     */
    protected String getPropertyName(int index) {
        String propertyName = "";
        String fieldName = getFieldName(index);
        if (!TextUtils.isEmpty(fieldName)) {
            HashMap<String, MemberEntry> memberMap = getMemberMap();
            MemberEntry entry = memberMap.get(fieldName);
            if (entry != null) {
                propertyName = entry.property;
            }
        }
        return propertyName;
    }

    /**
     * Generate a member map representing specified fields
     * @param fields    Array of field isa of members to include
     * @return member map
     */
    protected HashMap<String, MemberEntry> makeMemberMap(int[] fields) {
        HashMap<String, MemberEntry> memberMap = new HashMap<>();
        HashMap<String, MemberEntry> baseMap = getMemberMap();
        for (int field : fields) {
            String fieldName = getFieldName(field);
            if (!TextUtils.isEmpty(fieldName)) {
                MemberEntry member = baseMap.get(fieldName);
                if (member != null) {
                    memberMap.put(fieldName, member);
                }
            }
        }
        return memberMap;
    }

    /**
     * Class representing the methods to be used by reflection for a class variable
     */
    protected static class MemberEntry {
        String classSetter;     // name of setter method in MovieInfo
        String classGetter;     // name of getter method in MovieInfo
        String jsonGetter;      // name of getter method to extract from JSON object
        String property;        // name of the object's internal property
        Class[] parameterTypes; // array that identifies the method's formal parameter types
        Object dfltValue;       // default value

        public MemberEntry(String classSetter, String classGetter, String property, String jsonGetter, Class[] parameterTypes, Object dfltValue) {
            this.classSetter = classSetter;
            this.classGetter = classGetter;
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
        public MemberEntry copy(String classSetter, String classGetter, String property) {
            return new MemberEntry(classSetter, classGetter, property, this.jsonGetter, this.parameterTypes, this.dfltValue);
        }
    }

    /**
     * Create a TMDbObject subclass object from JSON
     * @param memberMap Map of JSON property names to class MemberEntry objects
     * @param jsonData  JSON data object
     * @param obj       Object to save data into
     * @return updated object
     */
    public static <T extends TMDbObject> T getInstance(HashMap<String, MemberEntry> memberMap, JSONObject jsonData, T obj) {
        if ((jsonData != null) && (jsonData.length() > 0)) {
            Class objClass = obj.getClass();
            try {
                for (String key : memberMap.keySet()) {
                    if (jsonData.has(key)) {
                        MemberEntry entry = memberMap.get(key);
                        Object data;
                        try {
                            // use reflection to create object
                            Method getMethod = JSONObject.class.getMethod(entry.jsonGetter, stringParameterTypes);  // get the get method
                            data = getMethod.invoke(jsonData, key);  // get the data
                        }
                        catch (Exception e) {
                            data = jsonData.get(key);  // get the data using generic get
                            if (data.equals(JSONObject.NULL)) {
                                data = entry.dfltValue;
                            } else {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Method setMethod = objClass.getMethod(entry.classSetter, entry.parameterTypes); // get the set method
                            setMethod.invoke(obj, data);    // set the data
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    /**
     * Create a TMDbObject subclass object from JSON
     * @param memberMap     Map of JSON property names to class MemberEntry objects
     * @param jsonString    JSON data string
     * @param obj           Object to save data into
     * @return updated object
     */
    static <T extends TMDbObject> T getInstance(HashMap<String, MemberEntry> memberMap, String jsonString, T obj) {
        try {
            JSONObject json = new JSONObject(jsonString);
            getInstance(memberMap, json, obj);
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     * Check if the specified list of properties are their default values
     * @param memberMap Map of JSON property names to class MemberEntry objects
     * @param obj       Object to check
     * @return <code>true</code> if all specified MemberEntry are their default values, <code>false</code> otherwise
     */
    public static <T extends TMDbObject> boolean isDefault(HashMap<String, MemberEntry> memberMap, T obj) {
        int count = 0;
        Class objClass = obj.getClass();
        for (String key : memberMap.keySet()) {
            MemberEntry entry = memberMap.get(key);
            try {
                // use reflection to get value
                Method getMethod = objClass.getMethod(entry.classGetter); // get the get method
                Object value = getMethod.invoke(obj);
                if (value != null) {
                    if (value.equals(entry.dfltValue)) {
                        ++count;    // is default value
                    }
                } else if (entry.dfltValue == null) {
                    ++count;    // both null
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (count == memberMap.size());
    }

    /**
     * Copy from one TMDbObject subclass object to another
     * @param memberMap Map of JSON property names to class MemberEntry objects
     * @param from      Object to copy from
     * @param to        Object to copy to
     * @return <code>true</code> if all specified MemberEntry were copied, <code>false</code> otherwise
     */
    public static <T extends TMDbObject> boolean copy(HashMap<String, MemberEntry> memberMap, T from, T to) {
        int count = 0;
        Class fromClass = from.getClass();
        Class toClass = to.getClass();
        for (String key : memberMap.keySet()) {
            MemberEntry entry = memberMap.get(key);
            try {
                // use reflection to get value
                Method getMethod = fromClass.getMethod(entry.classGetter); // get the get method
                Object fromValue = getMethod.invoke(from);

                Method setMethod = toClass.getMethod(entry.classSetter, entry.parameterTypes); // get the set method
                setMethod.invoke(to, fromValue);    // set the value

                ++count;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (count == memberMap.size());
    }

    /**
     * Copy from one TMDbObject subclass object to another
     * @param from      Object to copy from
     * @param to        Object to copy to
     * @param fields    Array of field isa of members to include
     * @return <code>true</code> if all fields were copied, <code>false</code> otherwise
     */
    public static <T extends TMDbObject> boolean copy(T from, T to, int[] fields) {
        boolean result;
        if ((from == null) || (to == null)) {
            result = false;
        } else {
            HashMap<String, MemberEntry> memberMap;
            if (fields == null) {
                memberMap = to.getMemberMap();
            } else {
                memberMap = to.makeMemberMap(fields);
            }
            result = copy(memberMap, from, to);
        }
        return result;
    }

    /**
     * Copy from a TMDbObject subclass object to this object
     * @param from      Object to copy from
     * @param fields    Array of field ids of members to include. Passing <code>null</code> copies all fields.
     */
    public abstract <T extends TMDbObject> void copy(T from, int[] fields);

    /**
     * Copy from a TMDbObject subclass object to this object
     * @param from      Object to copy from
     */
    public <T extends TMDbObject> void copy(T from) {
        copy(from, this, null);
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
            intArray[index] = array[index];
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
                array[i] = intArray[i];
            }
        }
        return array;
    }

    /**
     * Read an array from a Parcel
     * @param in            Parcel to read from
     * @param loader        Class loader to create array elements
     * @param arrayClass    Class of the copy to be returned
     * @return Object array
     */
    public Object[] readArrayFromParcel(Parcel in, ClassLoader loader, Class arrayClass) {
        Object[] objArray = in.readArray(loader);
        return Arrays.copyOf(objArray, objArray.length, arrayClass);
    }

    /**
     * Write the representation of a boolean to a parcel
     * @param parcel    Parcel to write to
     * @param bool      Valur to write
     */
    public void writeBooleanToParcel(Parcel parcel, Boolean bool) {
        parcel.writeInt(bool ? 1 : 0);
    }

    /**
     * Read a boolean from a Parcel
     * @param in            Parcel to read from
     * @return Boolean value
     */
    public boolean readBooleanFromParcel(Parcel in) {
        return (in.readInt() == 1);
    }

    /**
     * Check if the object is empty
     * @return  <code>true</code> if no meaningful data set
     */
    public abstract boolean isEmpty();

    /**
     * Check if the object is a placeholder, i.e. the minimal amount of info has been set
     * @return  <code>true</code> if a placeholder
     */
    public abstract boolean isPlaceHolder();

    /**
     * Check if the object is empty
     * @return  <code>true</code> if no meaningful data set
     */
    public static boolean isEmpty(TMDbObject obj) {
        return (obj == null) || obj.isEmpty();
    }
}
