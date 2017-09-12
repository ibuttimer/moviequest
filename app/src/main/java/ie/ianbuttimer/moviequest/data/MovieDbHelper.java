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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry;
import ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry;

/**
 * Movie database helper class
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "movieDb.db";

    // The database version
    private static final int VERSION = 1;

    /**
     * Constructor
     * @param context   The current context
     */
    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                /* use TMDb movie as the primary key */
                MovieEntry._ID              + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_JSON      + " STRING NOT NULL, " +
                MovieEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ");";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

        final String SQL_CREATE_FAVOURITES_TABLE =
            "CREATE TABLE " + FavouriteEntry.TABLE_NAME + " (" +
                /* use TMDb movie as the primary key */
                FavouriteEntry._ID              + " INTEGER PRIMARY KEY, " +
                FavouriteEntry.COLUMN_FAVOURITE + " BOOLEAN DEFAULT 0," +
                FavouriteEntry.COLUMN_TITLE     + " STRING" +
            ");";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // recreate db
        for (String table : getTableNames()) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + table + ";");
        }
        onCreate(sqLiteDatabase);
    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // recreate db
        onUpgrade(sqLiteDatabase, oldVersion, newVersion);
    }


    /**
     * Get a list of database table names
     * @return  Array of tables
     */
    public String[] getTableNames() {
        return new String []{
                MovieEntry.TABLE_NAME, FavouriteEntry.TABLE_NAME
        };
    }


}
