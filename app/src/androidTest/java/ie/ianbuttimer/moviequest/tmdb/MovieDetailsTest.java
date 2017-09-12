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

package ie.ianbuttimer.moviequest.tmdb;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test class for MovieDetails class
 */
public class MovieDetailsTest extends MovieInfoTest {

    private MovieDetails movieDetails;
    private TestMovieDetailInstance provider = new TestMovieDetailInstance();


    @Before
    public void createObject() {
        movieDetails = provider.setupObject();
    }

    @Override
    public void writeToParcel(Parcel parcel) {
        movieDetails.writeToParcel(parcel, movieDetails.describeContents());
    }

    @Test
    public void movieDetails_ParcelableWriteRead() {
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
        MovieDetails createdFromParcel = MovieDetails.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        checkObject("parcel", createdFromParcel, movieDetails);
    }

    public void checkObject(String test, MovieDetails createdFromParcel, MovieDetails original) {
        // Verify that the received data is correct.
        super.checkObject(test, createdFromParcel, original);

        assertEquals(makeAssertMessage(test + " Budget"),
                createdFromParcel.getBudget(), original.getBudget());
        assertArrayEquals(makeAssertMessage(test + " Genres"),
                createdFromParcel.getGenres(), original.getGenres());
        assertEquals(makeAssertMessage(test + " Homepage"),
                createdFromParcel.getHomepage(), original.getHomepage());
        assertEquals(makeAssertMessage(test + " ImdbId"),
                createdFromParcel.getImdbId(), original.getImdbId());
        assertArrayEquals(makeAssertMessage(test + " ProductionCompanies"),
                createdFromParcel.getProductionCompanies(), original.getProductionCompanies());
        assertArrayEquals(makeAssertMessage(test + " ProductionCountries"),
                createdFromParcel.getProductionCountries(), original.getProductionCountries());
        assertEquals(makeAssertMessage(test + " Revenue"),
                createdFromParcel.getRevenue(), original.getRevenue());
        assertEquals(makeAssertMessage(test + " Runtime"),
                createdFromParcel.getRuntime(), original.getRuntime());
        assertArrayEquals(makeAssertMessage(test + " SpokenLanguages"),
                createdFromParcel.getSpokenLanguages(), original.getSpokenLanguages());
        assertEquals(makeAssertMessage(test + " Status"),
                createdFromParcel.getStatus(), original.getStatus());
        assertEquals(makeAssertMessage(test + " Tagline"),
                createdFromParcel.getTagline(), original.getTagline());
        assertEquals(makeAssertMessage(test + " Collection"),
                createdFromParcel.getCollection(), original.getCollection());
    }

}