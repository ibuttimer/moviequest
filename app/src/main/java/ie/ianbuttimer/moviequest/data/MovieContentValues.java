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

package ie.ianbuttimer.moviequest.data;

import android.text.TextUtils;

import java.util.Date;

import ie.ianbuttimer.moviequest.utils.DbUtils;

import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.COLUMN_JSON;
import static ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry.COLUMN_TIMESTAMP;

/**
 * Builder class for a Movie ContentValue objects
 */
@SuppressWarnings("unused")
public class MovieContentValues extends DbContentValues {

    public static class Builder extends DbContentValues.Builder {

        /**
         * Constructor
         */
        Builder() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Builder setId(int id) {
            super.setId(id);
            return this;
        }

        /**
         * Set the JSON string
         * @param json  Json string to set
         * @return  Builder to facilitate chaining
         */
        public Builder setJson(String json) {
            if (!TextUtils.isEmpty(json)) {
                cv.put(COLUMN_JSON, json);
            }
            return this;
        }

        /**
         * Set the timestamp to the current date & time
         * @return  Builder to facilitate chaining
         */
        public Builder setTimestamp() {
            return setTimestamp(new Date());
        }

        /**
         * Set the timestamp to the specified date & time
         * @param timestamp     Timestamp to set
         * @return  Builder to facilitate chaining
         */
        public Builder setTimestamp(Date timestamp) {
            cv.put(COLUMN_TIMESTAMP, DbUtils.getTimestamp(timestamp));
            return this;
        }

        @Override
        public Builder clear() {
            super.clear();
            return this;
        }
    }

    /**
     * Get a builder instance
     * @return  New builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

}
