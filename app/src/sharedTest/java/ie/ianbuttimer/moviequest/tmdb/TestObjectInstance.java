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

/**
 * Base class for test object instance classes
 */
public abstract class TestObjectInstance {


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