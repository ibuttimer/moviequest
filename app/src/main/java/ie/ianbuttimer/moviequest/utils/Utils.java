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
package ie.ianbuttimer.moviequest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcel;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;

import ie.ianbuttimer.moviequest.R;

import static android.text.format.DateUtils.DAY_IN_MILLIS;
import static android.text.format.DateUtils.HOUR_IN_MILLIS;
import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static android.text.format.DateUtils.WEEK_IN_MILLIS;
import static android.text.format.DateUtils.YEAR_IN_MILLIS;
import static ie.ianbuttimer.moviequest.Constants.INVALID_DATE;

/**
 * This class contains miscellaneous utility functions
 */
@SuppressWarnings("unused")
public class Utils {

    /**
     * Private constructor
     */
    private Utils() {
        // can't instantiate class
    }

    /**
     * Return a formatted version string for the app
     * @param context   Context to use
     * @return  Version string
     */
    public static String getVersionString(Context context) {
        String ver = "";
        try {
            ver = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return MessageFormat.format(context.getResources().getString(R.string.app_version), ver);
    }

    /**
     * Retrieve the value of a meta-data entry from the manifest
     * @param context   Context to use
     * @param key       Meta-data name
     * @return  meta-data string
     */
    protected static String getManifestMetaData(Context context, String key) {
        String metaData = null;
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            metaData = bundle.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaData;
    }

    /**
     * Check if the string has some content other than spaces or empty
     * @param str   String to check
     * @return true if the string is has content.
     */
    public static boolean stringHasContent(String str) {
        return (!TextUtils.isEmpty(str) && (TextUtils.getTrimmedLength(str) > 0));
    }

    /**
     * Start an activity
     * @param context   The current context
     * @param intent    Intent to start activity
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivity(Context context, Intent intent) {
        return startActivity(context, new Intent[] { intent });
    }

    /**
     * Start an activity
     * @param activity      Parent activity
     * @param intent        Intent to start activity
     * @param requestCode   Reply request code
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivityForResult(Activity activity, Intent intent, int requestCode) {
        return startActivityForResult(activity, new Intent[] { intent }, requestCode);
    }

    /**
     * Start an activity with fallback options. All intents will be attempted in ascending order until
     * one is successfully resolved, and that one is used.
     * @param context   The current context
     * @param intents       Intents to start activity
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivity(Context context, Intent[] intents) {
        boolean resolved = false;
        PackageManager manager = context.getPackageManager();
        for (int i = 0, ll = intents.length; (i < ll) && !resolved; i++) {
            resolved = (intents[i].resolveActivity(manager) != null);
            if (resolved) {
                context.startActivity(intents[i]);
            }
        }
        return resolved;
    }

    /**
     * Start an activity with fallback options. All intents will be attempted in ascending order until
     * one is successfully resolved, and that one is used.
     * @param activity      Parent activity
     * @param intents       Intents to start activity
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivityForResult(Activity activity, Intent[] intents, int requestCode) {
        boolean resolved = false;
        PackageManager manager = activity.getPackageManager();
        for (int i = 0, ll = intents.length; (i < ll) && !resolved; i++) {
            resolved = (intents[i].resolveActivity(manager) != null);
            if (resolved) {
                activity.startActivityForResult(intents[i], requestCode);
            }
        }
        return resolved;
    }

    /**
     * Get the screen metrics.
     * @param activity  The current activity
     * @return  screen metrics
     */
    public static DisplayMetrics getScreenMetrics(Activity activity) {
        return getScreenMetrics(activity.getWindowManager());
    }

    /**
     * Get the display metrics.
     * @param manager  The current window manager
     * @return  screen metrics
     */
    private static DisplayMetrics getScreenMetrics(WindowManager manager) {
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * Get the available screen size in pixels.
     * @param activity  The current activity
     * @return  screen size
     */
    public static Point getScreenSize(Activity activity) {
        DisplayMetrics metrics = getScreenMetrics(activity);
        return new Point(metrics.widthPixels, metrics.heightPixels);
    }

    /**
     * Get the available screen width in pixels.
     * @param activity  The current activity
     * @return  screen width
     */
    public static int getScreenWidth(Activity activity) {
        Point size = getScreenSize(activity);
        return size.x;
    }

    /**
     * Get the available screen height in pixels.
     * @param activity  The current activity
     * @return  screen height
     */
    public static int getScreenHeight(Activity activity) {
        Point size = getScreenSize(activity);
        return size.y;
    }

    /**
     * Get the available screen size in density-independent pixels.
     * @param activity  The current activity
     * @return  screen size
     */
    public static Point getScreenDp(Activity activity) {
        DisplayMetrics metrics = getScreenMetrics(activity);
        float dpWidth = metrics.widthPixels / metrics.density;
        float dpHeight = metrics.heightPixels / metrics.density;
        return new Point(Float.valueOf(dpWidth).intValue(), Float.valueOf(dpHeight).intValue());
    }

    /**
     * Get the available screen width in density-independent pixels.
     * @param activity  The current activity
     * @return  screen width
     */
    public static int getScreenDpWidth(Activity activity) {
        Point size = getScreenDp(activity);
        return size.x;
    }

    /**
     * Get the available screen height in density-independent pixels.
     * @param activity  The current activity
     * @return  screen height
     */
    public static int getScreenDpHeight(Activity activity) {
        Point size = getScreenDp(activity);
        return size.y;
    }

    /**
     * Convert density-independent pixels to pixels
     * @param context   The current context
     * @param dp        Dp to convert
     * @return  pixel size
     */
    public static int convertDpToPixels(Context context, int dp) {
        DisplayMetrics metrics = getScreenMetrics((WindowManager)context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        float pixels = dp * metrics.density;
        return Float.valueOf(pixels).intValue();
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isSize(Context context, int size) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= size;
    }

    /**
     * Determine if the device has an extra-large screen, i.e. at least approximately 720x960 dp units
     * @param context   The current context
     * @return <code>true</code> if device has an extra-large screen, <code>false</code> otherwise
     */
    public static boolean isXLargeScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_XLARGE);
    }

    /**
     * Determine if the device has a large screen, i.e. at least approximately 480x640 dp units
     * @param context   The current context
     * @return <code>true</code> if device has a large screen, <code>false</code> otherwise
     */
    public static boolean isLargeScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_LARGE);
    }

    /**
     * Determine if the device has a normal screen, i.e. at least approximately 320x470 dp units
     * @param context   The current context
     * @return <code>true</code> if device has a normal screen, <code>false</code> otherwise
     */
    public static boolean isNormalScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_NORMAL);
    }

    /**
     * Determine if the device has a small screen, i.e. at least approximately 320x426 dp units
     * @param context   The current context
     * @return <code>true</code> if device has a small screen, <code>false</code> otherwise
     */
    public static boolean isSmallScreen(Context context) {
        return isSize(context, Configuration.SCREENLAYOUT_SIZE_SMALL);
    }

    /**
     * Determine if the device screen is in portrait orientation
     * @param context   The current context
     * @return <code>true</code> if screen is in portrait orientation, <code>false</code> otherwise
     */
    public static boolean isPotraitScreen(Context context) {
        return (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
    }

    /**
     * Set the backdrop size preference for the app
     * @param context   Current context
     */
    public static void setBackdropPreference(Context context) {
        /* set the backdrop size depending on screen size
            its setup as a preference (but not currently shown in settings), so save as preference
         */
        String[] array = context.getResources().getStringArray(R.array.pref_backdrop_size_values);
        int sizeIndex;
        if (isXLargeScreen(context)) {
            sizeIndex = 2;
        } else if (isLargeScreen(context)) {
            sizeIndex = 1;
        } else {
            sizeIndex = 0;
        }
        if (sizeIndex < array.length) {
            PreferenceControl.setSharedStringPreference(context, R.string.pref_backdrop_size_key, array[sizeIndex]);
        }
    }

    /**
     * Checks if a date is empty, i.e. null or equal to the app's 'invalid date'
     * @param date  Date to check
     * @return  <code>true</code> if empty, <code>false</code> otherwise
     */
    public static boolean isEmpty(Date date) {
        return ((date == null) || (date.equals(INVALID_DATE)));
    }

    /**
     * Returns a string describing 'time' as a time relative to 'now'.
     * @param context   Current context
     * @param time      the time to describe, in milliseconds
     * @param now       the current time in milliseconds
     * @return  elapsed time string
     */
    public static String getRelativeTimeSpanString(Context context, long time, long now) {
        String elapsed;
        long interval = now - time;
        if (interval < SECOND_IN_MILLIS) {
            interval = 0L;
        } else if (interval < MINUTE_IN_MILLIS) {
            interval = SECOND_IN_MILLIS;
        } else if (interval < HOUR_IN_MILLIS) {
            interval = MINUTE_IN_MILLIS;
        } else if (interval < DAY_IN_MILLIS) {
            interval = HOUR_IN_MILLIS;
        } else if (interval < WEEK_IN_MILLIS) {
            interval = DAY_IN_MILLIS;
        } else if (interval > YEAR_IN_MILLIS) {
            interval = YEAR_IN_MILLIS;
        } else {
            interval = WEEK_IN_MILLIS;
        }
        if (interval == 0L) {
            elapsed = context.getString(R.string.just_now);
        } else {
            elapsed = DateUtils.getRelativeTimeSpanString(time, now, interval).toString();
        }
        return elapsed;
    }

    /**
     * Returns a ascending numerical order sorted copy of an array
     * @param unsorted  Array to copy
     * @return  new sorted array, or empty array if <code>null</code> was passed
     */
    public static int[] getSortedArray(int[] unsorted) {
        int[] sorted;
        if (unsorted == null) {
            sorted = new int[] {};
        } else {
            sorted = Arrays.copyOf(unsorted, unsorted.length);
            Arrays.sort(sorted);
        }
        return sorted;
    }

    /**
     * Return an array representing the specified column of a multi-dimension array
     * @param array         Array to get column from
     * @param columnIndex   Index of column to get
     * @return  Column array
     */
    public static int[] getArrayColumn(int[][] array, int columnIndex) {
        int length = array.length;
        if (columnIndex < 0) {
            throw new ArrayIndexOutOfBoundsException("Invalid column index");
        }
        int[] column = new int[length];
        for (int i = 0; i < length; i++) {
            if (columnIndex >= array[i].length) {
                throw new ArrayIndexOutOfBoundsException("Invalid column index on row " + i);
            }
            column[i] = array[i][columnIndex];
        }
        return column;
    }

    /**
     * Return the index of a row from a multi-dimension array where the value at a particular column matches a value<br>
     * <b>NOTE:</b> The search column must be sorted in ascending order.
     * @param array         Array to get column from
     * @param columnIndex   Index of column to check
     * @param value         Value to find
     * @return  Index of row or <code>-1</code> if not found
     */
    public static int binarySearch(int[][] array, int columnIndex, int value) {
        int row = -1;
        if ((array != null) && (array.length > 0)) {
            int lo = 0;
            int hi = array.length - 1;
            while (lo <= hi) {
                int mid = (lo + hi) / 2;
                if (value < array[mid][columnIndex]) {
                    hi = mid - 1;
                } else if (value > array[mid][columnIndex]) {
                    lo = mid + 1;
                } else {
                    row = mid;
                    break;
                }
            }
        }
        return row;
    }

    /**
     * Write an Integer object array to a Parcel
     * @param parcel    Parcel to write to
     * @param array     Array to write
     */
    public static void writeIntegerArrayToParcel(Parcel parcel, Integer[] array) {
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
    public static int[] readIntArrayFromParcel(Parcel in) {
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
    public static Integer[] readIntegerArrayFromParcel(Parcel in) {
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
    public static Object[] readArrayFromParcel(Parcel in, ClassLoader loader, Class<? extends Object[]> arrayClass) {
        Object[] objArray = in.readArray(loader);
        return Arrays.copyOf(objArray, objArray.length, arrayClass);
    }

    /**
     * Write the representation of a boolean to a parcel
     * @param parcel    Parcel to write to
     * @param bool      Valur to write
     */
    public static void writeBooleanToParcel(Parcel parcel, Boolean bool) {
        parcel.writeInt(bool ? 1 : 0);
    }

    /**
     * Read a boolean from a Parcel
     * @param in            Parcel to read from
     * @return Boolean value
     */
    public static boolean readBooleanFromParcel(Parcel in) {
        return (in.readInt() == 1);
    }


}
