/**
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
import android.text.TextUtils;
import android.util.DisplayMetrics;

import java.text.MessageFormat;

import ie.ianbuttimer.moviequest.R;





/**
 * This package contains miscellaneous utility functions
 */

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
        } catch (NullPointerException e) {
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
        boolean resolved = (intent.resolveActivity(context.getPackageManager()) != null);
        if (resolved) {
            context.startActivity(intent);
        }
        return resolved;
    }

    /**
     * Start an activity
     * @param activity      Parent activity
     * @param intent        Intent to start activity
     * @param requestCode   Reply request code
     * @return  true if intent was successfully resolved
     */
    public static boolean startActivityForResult(Activity activity, Intent intent, int requestCode) {
        boolean resolved = (intent.resolveActivity(activity.getPackageManager()) != null);
        if (resolved) {
            activity.startActivityForResult(intent, requestCode);
        }
        return resolved;
    }

    /**
     * Get the screen metrics.
     * @param activity  The current activity
     * @return  screen metrics
     */
    public static DisplayMetrics getScreenMetrics(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
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
     * Set the backdrop size preference for the app
     * @param context
     */
    public static void setBackdropPreference(Context context) {
        /* set the backdrop size depending on screen size
            its setup as a preference (but not currently shown in settings), so save as preference
         */
        String[] array = context.getResources().getStringArray(R.array.pref_backdrop_size_values);
        int sizeIndex = -1;
        if (isXLargeScreen(context)) {
            sizeIndex = 2;
        } else if (isLargeScreen(context)) {
            sizeIndex = 1;
        } else {
            sizeIndex = 0;
        }
        if ((sizeIndex >= 0) && (sizeIndex < array.length)) {
            PreferenceControl.setSharedStringPreference(context, R.string.pref_backdrop_size_key, array[sizeIndex]);
        }
    }


}
