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

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

/**
 * Application class
 */

public class MovieQuestApplication extends Application {

    private static final String TAG = MovieQuestApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        String mode;
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this);
            mode = "debug";
        } else {
            mode = "release";
        }
        Log.d(TAG, "Application launched in " + mode + " mode");
    }

}
