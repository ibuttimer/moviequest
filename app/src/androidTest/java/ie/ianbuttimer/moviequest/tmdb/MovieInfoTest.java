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

import static org.junit.Assert.*;

/**
 * Unit test for MovieInfo object
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MovieInfoTest extends ParcelTest {

    private MovieInfo movieInfo;
    private TestMovieInfoInstance provider = new TestMovieInfoInstance();

    @Before
    public void createObject() {
        movieInfo = provider.setupObject(new MovieInfo());
    }

    public void writeToParcel(Parcel parcel) {
        movieInfo.writeToParcel(parcel, movieInfo.describeContents());
    }

    @Test
    public void movieInfo_ParcelableWriteRead() {
        // Write the data.
        Parcel parcel = Parcel.obtain();
        writeToParcel(parcel);

        // After you're done with writing, you need to reset the parcel for reading.
        parcel.setDataPosition(0);

        // Verify that the received data is correct.
        checkParcel(parcel);
    }

    public void checkParcel(Parcel parcel) {
        // Read the data.
        MovieInfo createdFromParcel = MovieInfo.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        checkObject(createdFromParcel, movieInfo);
    }

    public void checkObject(MovieInfo createdFromParcel, MovieInfo original) {
        // Verify that the received data is correct.
        assertEquals(makeAssertMessage("PosterPath"),
                createdFromParcel.getPosterPath(), original.getPosterPath());
        assertEquals(makeAssertMessage("BackdropPath"),
                createdFromParcel.getBackdropPath(), original.getBackdropPath());
        assertEquals(makeAssertMessage("Overview"),
                createdFromParcel.getOverview(), original.getOverview());
        assertEquals(makeAssertMessage("OriginalTitle"),
                createdFromParcel.getOriginalTitle(), original.getOriginalTitle());
        assertEquals(makeAssertMessage("OriginalLanguage"),
                createdFromParcel.getOriginalLanguage(), original.getOriginalLanguage());
        assertEquals(makeAssertMessage("Title"),
                createdFromParcel.getTitle(), original.getTitle());
        assertEquals(makeAssertMessage("ReleaseDate"),
                createdFromParcel.getReleaseDate(), original.getReleaseDate());

        assertEquals(makeAssertMessage("isAdult"),
                createdFromParcel.isAdult(), original.isAdult());
        assertEquals(makeAssertMessage("isVideo"),
                createdFromParcel.isVideo(), original.isVideo());

        assertEquals(makeAssertMessage("Id"),
                createdFromParcel.getId().longValue(), original.getId().longValue());
        assertEquals(makeAssertMessage("VoteCount"),
                createdFromParcel.getVoteCount().longValue(), original.getVoteCount().longValue());

        assertEquals(makeAssertMessage("Popularity"),
                createdFromParcel.getPopularity(), original.getPopularity(), 0.1d);
        assertEquals(makeAssertMessage("VoteAverage"),
                createdFromParcel.getVoteAverage(), original.getVoteAverage(), 0.1d);

        assertArrayEquals(makeAssertMessage("GenreIds"),
                createdFromParcel.getGenreIds(), original.getGenreIds());
    }

}