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
package ie.ianbuttimer.moviequest;

import android.support.annotation.IntDef;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Convenience class for storing constants used throughout the app
 */
@SuppressWarnings("unused")
public class Constants {

    /**
     * Default constructor
     */
    private Constants() {
        // class can't be instantiated
    }

    public static final String MOVIE_ID = "movieId";
    public static final String MOVIE_TITLE = "movieTitle";
    public static final String MOVIE_OBJ = "movieObj";
    public static final String MOVIE_DETAIL_OBJ = "movieDetailObj";
    public static final String MOVIE_LIST = "movieList";
    public static final String RESULT_RECEIVER = "resultReceiver";

    public static final Date INVALID_DATE;

    static {
        // Gregorian calendar was instituted October 15, 1582 in some countries, later in others
        Calendar cal = new GregorianCalendar(1582, Calendar.OCTOBER, 15, 0, 0, 0);
        INVALID_DATE = cal.getTime();
    }
}
