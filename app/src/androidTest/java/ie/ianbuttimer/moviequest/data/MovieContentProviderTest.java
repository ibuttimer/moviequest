/*
* Copyright (C) 2016 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*
* Adapted from
*   https://github.com/udacity/ud851-Exercises/blob/student/Lesson09-ToDo-List/T09.03-Solution-UriMatcher/app/src/androidTest/java/com/example/android/todolist/data/TestTaskContentProvider.java
* with additional modifications
*/

package ie.ianbuttimer.moviequest.data;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import ie.ianbuttimer.moviequest.utils.UriUtils;

import ie.ianbuttimer.moviequest.data.MovieContract.MovieEntry;
import ie.ianbuttimer.moviequest.data.MovieContract.FavouriteEntry;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class MovieContentProviderTest {

    /* Context used to access various parts of the system */
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Because we annotate this method with the @Before annotation, this method will be called
     * before every single method with an @Test annotation. We want to start each test clean, so we
     * delete all entries in the tasks directory to do so.
     */
    @Before
    public void setUp() {
        /* Use MovieDbHelper to get access to a writable database */
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        for (String table : dbHelper.getTableNames()) {
            database.delete(table, null, null);
            /* need to reset the sequence number as otherwise when the table is recreated the
                auto inc number will continue from where it finished
             */
            database.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ= '0' WHERE NAME='" + table + "';");
        }
    }


    //================================================================================
    // Test ContentProvider Registration
    //================================================================================


    /**
     * This test checks to make sure that the content provider is registered correctly in the
     * AndroidManifest file. If it fails, you should check the AndroidManifest to see if you've
     * added a <provider/> tag and that you've properly specified the android:authorities attribute.
     */
    @Test
    public void testProviderRegistry() {

        /*
         * A ComponentName is an identifier for a specific application component, such as an
         * Activity, ContentProvider, BroadcastReceiver, or a Service.
         *
         * Two pieces of information are required to identify a component: the package (a String)
         * it exists in, and the class (a String) name inside of that package.
         *
         * We will use the ComponentName for our ContentProvider class to ask the system
         * information about the ContentProvider, specifically, the authority under which it is
         * registered.
         */
        String packageName = mContext.getPackageName();
        String taskProviderClassName = MovieContentProvider.class.getName();
        ComponentName componentName = new ComponentName(packageName, taskProviderClassName);

        try {

            /*
             * Get a reference to the package manager. The package manager allows us to access
             * information about packages installed on a particular device. In this case, we're
             * going to use it to get some information about our ContentProvider under test.
             */
            PackageManager pm = mContext.getPackageManager();

            /* The ProviderInfo will contain the authority, which is what we want to test */
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);
            String actualAuthority = providerInfo.authority;
            String expectedAuthority = packageName;

            /* Make sure that the registered authority matches the authority from the Contract */
            String incorrectAuthority =
                    "Error: MovieContentProvider registered with authority: " + actualAuthority +
                            " instead of expected authority: " + expectedAuthority;
            assertEquals(incorrectAuthority,
                    actualAuthority,
                    expectedAuthority);

        } catch (PackageManager.NameNotFoundException e) {
            String providerNotRegisteredAtAll =
                    "Error: MovieContentProvider not registered at " + mContext.getPackageName();
            /*
             * This exception is thrown if the ContentProvider hasn't been registered with the
             * manifest at all. If this is the case, you need to double check your
             * AndroidManifest file
             */
            fail(providerNotRegisteredAtAll);
        }
    }


    //================================================================================
    // Test UriMatcher
    //================================================================================


    /**
     * This function tests that the UriMatcher returns the correct integer value for
     * each of the Uri types that the ContentProvider can handle. Uncomment this when you are
     * ready to test your UriMatcher.
     */
    @Test
    public void testUriMatcher() {

        /* Create a URI matcher that the MovieContentProvider uses */
        UriMatcher testMatcher = MovieContentProvider.buildUriMatcher();

        /* Test that the code returned from our matcher matches the expected matcher int */
        String[] names = new String[] {
            "MOVIES",                   "MOVIE_WITH_ID",
            "FAVOURITES",               "FAVOURITE_WITH_ID",
        };
        Uri[] uris = new Uri[] {
            MovieEntry.CONTENT_URI,     UriUtils.getMovieWithIdUri(1),
            FavouriteEntry.CONTENT_URI, UriUtils.getFavouriteWithIdUri(1)
        };
        int[] codes = new int[] {
            MovieContentProvider.MOVIES,    MovieContentProvider.MOVIE_WITH_ID,
            MovieContentProvider.FAVOURITES, MovieContentProvider.FAVOURITE_WITH_ID
        };
        for (int i = 0; i < uris.length; i++) {
            int actualTasksMatchCode = testMatcher.match(uris[i]);
            int expectedTasksMatchCode = codes[i];
            assertEquals("Error: The " + names[i] + " URI was matched incorrectly.",
                    actualTasksMatchCode,
                    expectedTasksMatchCode);
        }
    }


    //================================================================================
    // Test Insert
    //================================================================================

    /**
     * Tests inserting a single row of data via a ContentResolver
     */
    @Test
    public void testInsert() {

        /* Create values to insert */
        int id = 100;   // using TMDb movie ids are primary key
        ContentValues testValues = MovieContentValues.builder()
                .setId(id)
                .setJson("When I grow up I want to be a json string")
                .build();

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MovieEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);


        Uri uri = contentResolver.insert(MovieEntry.CONTENT_URI, testValues);

        Uri expectedUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);

        String insertProviderFailed = "Unable to insert item through Provider ";
        assertNotNull(insertProviderFailed, uri);
        assertEquals(insertProviderFailed, expectedUri, uri);

        /*
         * If this fails, it's likely you didn't call notifyChange in your insert method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }

    //================================================================================
    // Test Query (for tasks directory)
    //================================================================================


    /**
     * Inserts data, then tests if a query for the tasks directory returns that data as a Cursor
     */
    @Test
    public void testQuery() {

        long movieRow = insertMovieRowDirect(100, "Query json string");
        long favRow = insertFavouriteRowDirect(200, true);

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Perform the ContentProvider query */
        Uri[] uris = new Uri[] {
            MovieEntry.CONTENT_URI,
            UriUtils.getMovieWithIdUri((int)movieRow),
            FavouriteEntry.CONTENT_URI,
            UriUtils.getFavouriteWithIdUri((int)favRow),
        };
        for (Uri uri : uris) {
            Cursor cursor = contentResolver.query(
                uri,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Sort order to return in Cursor */
                null);

            String queryFailed = "Query failed to return a valid Cursor for " + uri.toString();
            assertNotNull(queryFailed, cursor);
            assertTrue(queryFailed, cursor.getCount() == 1);    // should be 1 entry as table cleared before test

            /* We are done with the cursor, close it now. */
            cursor.close();
        }
    }

    /**
     * Insert a Movie row with the specified values directly into the database
     * @param id        Id
     * @param json      Json string
     * @return  id of new row
     */
    private long insertMovieRowDirect(int id, String json) {
        /* Create values to insert */
        ContentValues testValues = MovieContentValues.builder()
                .setId(id)
                .setJson(json)
                .build();
        return insertRowDirect(MovieEntry.TABLE_NAME, testValues);
    }

    /**
     * Insert a row with the specified values directly into the database
     * @param table         Table to insert into
     * @param testValues    vALUES TO INSERT
     * @return  id of new row
     */
    private long insertRowDirect(String table, ContentValues testValues) {
        /* Get access to a writable database */
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        /* Insert ContentValues into database and get a row ID back */
        long rowId = database.insert(
                /* Table to insert values into */
                table,
                null,
                /* Values to insert into table */
                testValues);

        String insertFailed = "Unable to insert directly into the database table " + table;
        assertTrue(insertFailed, rowId != -1);

        /* We are done with the database, close it now. */
        database.close();

        return rowId;
    }

    /**
     * Insert a row with the specified values directly into the database
     * @param id        Id
     * @param favourite Favourite flag
     * @return  id of new row
     */
    private long insertFavouriteRowDirect(int id, boolean favourite) {
        /* Create values to insert */
        ContentValues testValues = FavouritesContentValues.builder()
                .setId(id)
                .setFavourite(favourite)
                .build();
        return insertRowDirect(FavouriteEntry.TABLE_NAME, testValues);
    }

    //================================================================================
    // Test Delete (for a single item)
    //================================================================================


    /**
     * Tests deleting a single row of data via a ContentResolver
     */
    @Test
    public void testDelete() {

        long rowId = insertMovieRowDirect(100, "I better not be still here when you're done");
        insertMovieRowDirect(101, "I better not be either");

        testDelete(MovieEntry.CONTENT_URI, rowId);

        rowId = insertFavouriteRowDirect(200, true);
        insertFavouriteRowDirect(201, false);

        testDelete(FavouriteEntry.CONTENT_URI, rowId);
    }

    private void testDelete(Uri uri, long rowId) {

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                uri,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);

        /* The delete method deletes the previously inserted row */
        Uri uriToDelete = uri.buildUpon().appendPath(String.valueOf(rowId)).build();
        int deletedCount = contentResolver.delete(uriToDelete, null, null);

        String deleteFailed = "Unable to delete single item in the database with " + uriToDelete.toString();
        assertTrue(deleteFailed, deletedCount != 0);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /* The delete method with no selection deletes all rows */
        deletedCount = contentResolver.delete(uri, null, null);

        deleteFailed = "Unable to delete all items in the database with " + uri.toString();
        assertTrue(deleteFailed, deletedCount == 1);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }

    //================================================================================
    // Test Update
    //================================================================================


    /**
     * Tests updating a single row of data via a ContentResolver
     */
    @Test
    public void testUpdate() {
        /* Access writable database */
        String json1 = "I'm the first";
        long rowId1 = insertMovieRowDirect(100, json1);
        String json2 = "I'm the second";
        insertMovieRowDirect(101, json2);

        /* TestContentObserver allows us to test if notifyChange was called appropriately */
        TestUtilities.TestContentObserver taskObserver = TestUtilities.getTestContentObserver();

        ContentResolver contentResolver = mContext.getContentResolver();

        /* Register a content observer to be notified of changes to data at a given URI (tasks) */
        contentResolver.registerContentObserver(
                /* URI that we would like to observe changes to */
                MovieEntry.CONTENT_URI,
                /* Whether or not to notify us if descendants of this URI change */
                true,
                /* The observer to register (that will receive notifyChange callbacks) */
                taskObserver);

        /* The update method updates the previously inserted row */
        String update = json1 + " of 2";
        MovieContentValues.Builder movieBuilder = MovieContentValues.builder();
        ContentValues testValues = movieBuilder.setJson(update).build();

        Uri uriToUpdate = UriUtils.getMovieWithIdUri((int)rowId1);
        int updatedCount = contentResolver.update(uriToUpdate, testValues, null, null);

        String updateFailed = "Unable to update single item in the database: " + uriToUpdate.toString();
        assertTrue(updateFailed, updatedCount == 1);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();


        update = "We're all the same";
        testValues = movieBuilder.clear().setJson(update).build();

        updatedCount = contentResolver.update(MovieEntry.CONTENT_URI, testValues, null, null);

        updateFailed = "Unable to update all items in the database: updatedCount " + updatedCount;
        assertTrue(updateFailed, updatedCount == 2);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();



        Boolean favourite = true;

        rowId1 = insertFavouriteRowDirect(200, favourite);
        insertFavouriteRowDirect(201, favourite);

        FavouritesContentValues.Builder favBuilder = FavouritesContentValues.builder();
        testValues = favBuilder.setFavourite(!favourite).build();

        uriToUpdate = UriUtils.getFavouriteWithIdUri((int)rowId1);
        updatedCount = contentResolver.update(uriToUpdate, testValues, null, null);

        updateFailed = "Unable to update single item in the database: " + uriToUpdate.toString();
        assertTrue(updateFailed, updatedCount == 1);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();


        testValues = favBuilder.clear().setFavourite(!favourite).build();

        updatedCount = contentResolver.update(FavouriteEntry.CONTENT_URI, testValues, null, null);

        updateFailed = "Unable to update all items in the database: updatedCount " + updatedCount;
        assertTrue(updateFailed, updatedCount == 2);

        /*
         * If this fails, it's likely you didn't call notifyChange in your delete method from
         * your ContentProvider.
         */
        taskObserver.waitForNotificationOrFail();

        /*
         * waitForNotificationOrFail is synchronous, so after that call, we are done observing
         * changes to content and should therefore unregister this observer.
         */
        contentResolver.unregisterContentObserver(taskObserver);
    }


}
