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

import static ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry.COLUMN_FAVOURITE;
import static ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry.COLUMN_TITLE;

/**
 * Builder class for Favourites ContentValue objects
 */

public class FavouritesContentValues extends DbContentValues {

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
         * Set the favourites flag
         * @param favourite Flag value
         * @return  Builder to facilitate chaining
         */
        public Builder setFavourite(boolean favourite) {
            cv.put(COLUMN_FAVOURITE, favourite);
            return this;
        }

        /**
         * Set the title
         * @param title     Movie title
         * @return  Builder to facilitate chaining
         */
        public Builder setTitle(String title) {
            cv.put(COLUMN_TITLE, title);
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
