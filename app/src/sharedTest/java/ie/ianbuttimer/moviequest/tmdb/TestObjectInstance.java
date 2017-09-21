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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static ie.ianbuttimer.moviequest.utils.DbUtils.DB_RAW_BOOLEAN_FALSE;
import static ie.ianbuttimer.moviequest.utils.DbUtils.DB_RAW_BOOLEAN_TRUE;

/**
 * Base class for test object instance classes
 */
public abstract class TestObjectInstance {

    private static final SimpleDateFormat tmdbDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    /**
     * Convert a boolean field from its internal object representation (int) to boolean in the
     * specified JSON string
     * @param raw       JSON string
     * @param field     Field name
     * @return  updated JSON string
     */
    public String convertFieldToBoolean(String raw, String field) {
        String result = raw;
        int index = raw.indexOf(field);
        if (index >= 0) {
            int zeroIndex = raw.indexOf(DB_RAW_BOOLEAN_FALSE, index);
            int oneIndex = raw.indexOf(DB_RAW_BOOLEAN_TRUE, index);
            if ((zeroIndex > 0) && (oneIndex > 0)) {
                if (zeroIndex < oneIndex) {
                    oneIndex = -1;
                }
            }
            boolean value = (oneIndex > 0);
            index = (value ? oneIndex : zeroIndex);
            result = raw.substring(0, index) + String.valueOf(value);
            result += raw.substring(index + 1);
        }
        return result;
    }

    /**
     * Convert a date field from the GSON format to the TMDb format in the
     * specified JSON string
     * @param raw       JSON string
     * @param field     Field name
     * @return  updated JSON string
     */
    String convertDate(String raw, String field) {
        // GsonBuilder.setDateFormat didn't work for some reason, hence this function
        String result = raw;
        int index = raw.indexOf(field);
        if (index >= 0) {
            // example segment "releaseDate":"Jun 17, 2015 12:00:00 AM",
            int startIndex = raw.indexOf('\"', index) + 1;      // " at end of field name
            startIndex = raw.indexOf('\"', startIndex + 1) + 1; // first char of date
            int endIndex = raw.indexOf('\"', startIndex);       // " at end of date
            try {
                Date date = new SimpleDateFormat("MMM d, yyyy HH:mm:ss a", Locale.US).parse(raw.substring(startIndex, endIndex));
                result = raw.substring(0, startIndex) + tmdbDateFormat.format(date);
                result += raw.substring(endIndex);
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Convert all the necessary fields in the specified JSON string
     * @param jsonString    JSON string
     * @return  updated JSON string
     */
    public String convertJsonFields(String jsonString, int start, int end) {
        // replace the internal property names with those returned by the TMDb server
        for (int i = start; i <= end; i++) {
            String property = getPropertyName(i);
            String field = getFieldName(i);
            if (!property.equals("") && !property.equals(field)) {
                jsonString = jsonString.replace(property, field);
            }
        }
        return jsonString;
    }

    /**
     * Get the field name as returned by the TMDb server
     * @param index     Index of field
     * @return  field name or empty string if invalid index
     * @see TMDbObject#getFieldName(int)
     */
    public abstract String getFieldName(int index);

    /**
     * Get the object property name associated with the specified index
     * @param index     Index of field
     * @return  property name or empty string if invalid index
     * @see TMDbObject#getFieldName(int)
     */
    protected abstract String getPropertyName(int index);

    /**
     * Make a message to use for an assert
     * @param msg   Message text
     * @return
     */
    protected String makeAssertMessage(String msg) {
        return getClass().getSimpleName() + ": " + msg;
    }
}