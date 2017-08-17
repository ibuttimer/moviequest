/*
 * Copyright (C) 2017 Ian Buttimer
 *
 * Licensed under the GNU General Public License v3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ie.ianbuttimer.moviequest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import ie.ianbuttimer.moviequest.utils.Utils;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // set version string
        TextView tv = (TextView)findViewById(R.id.tv_version_aboutA);
        tv.setText(Utils.getVersionString(getApplicationContext()));

        // make links work
        int[] tvIds = new int[] {
                R.id.tv_licencelink_aboutA, R.id.tv_tmdb_aboutA, R.id.tv_logo_aboutA, R.id.tv_icons_aboutA };
        for (int id : tvIds) {
            tv = (TextView) findViewById(id);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }
}
