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
package ie.ianbuttimer.moviequest.tmdb;

import android.os.Parcel;
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for MovieInfoModelTest object
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MovieInfoModelTest extends MovieInfoTest {

    private MovieInfoModel movieModel;
    private TestMovieInfoModelInstance provider = new TestMovieInfoModelInstance();


    @Before
    public void createObject() {
//        movieModel = setupObject(new MovieInfoModel());
        movieModel = provider.setupObject();
    }

    @Override
    public void writeToParcel(Parcel parcel) {
        movieModel.writeToParcel(parcel, movieModel.describeContents());
    }

    @Test
    public void movieInfoModel_ParcelableWriteRead() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel);

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Verify that the received data is correct.
        checkParcel(parcel);
    }

    @Override
    public void checkParcel(Parcel parcel) {
        // Read the data.
        MovieInfoModel createdFromParcel = MovieInfoModel.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        checkObject(createdFromParcel, movieModel);
    }

    public void checkObject(MovieInfoModel createdFromParcel, MovieInfoModel original) {
        // Verify that the received data is correct.
        super.checkObject(createdFromParcel, original);

        assertEquals(makeAssertMessage("Index"),
                createdFromParcel.getIndex(), original.getIndex());
    }

}