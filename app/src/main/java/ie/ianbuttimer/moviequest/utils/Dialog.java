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
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import ie.ianbuttimer.moviequest.R;

/**
 * Dialog related utility class
 */

public class Dialog {

    /**
     * Build an AlertDialog
     * @param activity          The current activity
     * @param titleResId        Resource id of title
     * @param msgResId          Resource id of message
     * @param positiveText      Resource id of positive button text
     * @param positiveListener  Positive button listener
     * @param negativeText      Resource id of negative button text
     * @param negativeListener  Negative button listener
     * @param neutralText       Resource id of neutral button text
     * @param nettralListener   Neutral button listener
     * @return  Alert dialog
     */
    public static AlertDialog buildAlert(Activity activity, int titleResId, int msgResId,
                                 int positiveText, DialogInterface.OnClickListener positiveListener,
                                 int negativeText, DialogInterface.OnClickListener negativeListener,
                                 int neutralText, DialogInterface.OnClickListener nettralListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        // Add the buttons
        if (positiveText != 0) {
            builder.setPositiveButton(positiveText, positiveListener);
        }
        if (negativeText != 0) {
            builder.setNegativeButton(negativeText, negativeListener);
        }
        if (neutralText != 0) {
            builder.setNeutralButton(neutralText, nettralListener);
        }
        builder.setTitle(titleResId);
        builder.setMessage(msgResId);
        // Create the AlertDialog
        return builder.create();
    }

    /**
     * Build an AlertDialog
     * @param activity          The current activity
     * @param titleResId        Resource id of title
     * @param msgResId          Resource id of message
     * @param positiveText      Resource id of positive button text
     * @param positiveListener  Positive button listener
     * @param negativeText      Resource id of negative button text
     * @param negativeListener  Negative button listener
     * @param neutralText       Resource id of neutral button text
     * @param nettralListener   Neutral button listener
     * @return  Alert dialog
     */
    public static void showAlert(Activity activity, int titleResId, int msgResId,
                                         int positiveText, DialogInterface.OnClickListener positiveListener,
                                         int negativeText, DialogInterface.OnClickListener negativeListener,
                                         int neutralText, DialogInterface.OnClickListener nettralListener) {
        AlertDialog dialod = buildAlert(activity, titleResId, msgResId,
                                        positiveText, positiveListener, negativeText, negativeListener,
                                        neutralText, nettralListener);
        dialod.show();
    }

    /**
     * Build an AlertDialog
     * @param activity          The current activity
     * @param titleResId        Resource id of title
     * @param msgResId          Resource id of message
     * @param positiveText      Resource id of positive button text
     * @param positiveListener  Positive button listener
     * @param negativeText      Resource id of negative button text
     * @param negativeListener  Negative button listener
     * @return  Alert dialog
     */
    public static void showAlert(Activity activity, int titleResId, int msgResId,
                                         int positiveText, DialogInterface.OnClickListener positiveListener,
                                         int negativeText, DialogInterface.OnClickListener negativeListener) {
        AlertDialog dialod = buildAlert(activity, titleResId, msgResId,
                                        positiveText, positiveListener, negativeText, negativeListener,
                                        0, null);
        dialod.show();
    }

    /**
     * Build an AlertDialog
     * @param activity          The current activity
     * @param titleResId        Resource id of title
     * @param msgResId          Resource id of message
     * @param positiveText      Resource id of positive button text
     * @param positiveListener  Positive button listener
     * @return  Alert dialog
     */
    public static void showAlert(Activity activity, int titleResId, int msgResId,
                                         int positiveText, DialogInterface.OnClickListener positiveListener) {
        AlertDialog dialod = buildAlert(activity, titleResId, msgResId,
                                        positiveText, positiveListener, 0, null, 0, null);
        dialod.show();
    }

    /**
     * Display a basic no response received dialog
     * @param activity          The current activity
     */
    public static void showNoResponseDialog(Activity activity) {
        showAlert(activity, android.R.string.dialog_alert_title, R.string.no_response,
                android.R.string.ok, null);
    }

    /**
     * Display a network unavailable dialog
     * @param activity          The current activity
     */
    public static void showNoNetworkDialog(Activity activity) {
        showAlert(activity, android.R.string.dialog_alert_title, R.string.network_na,
                android.R.string.ok, null);
    }

}
