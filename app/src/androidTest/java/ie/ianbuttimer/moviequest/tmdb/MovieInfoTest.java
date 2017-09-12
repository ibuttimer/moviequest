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
import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ADULT;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.BACKDROP_PATH;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.GENRE_IDS;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ID;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ORIGINAL_LANGUAGE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.ORIGINAL_TITLE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.OVERVIEW;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.POPULARITY;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.POSTER_PATH;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.RELEASE_DATE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.TITLE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.VIDEO;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.VOTE_AVERAGE;
import static ie.ianbuttimer.moviequest.tmdb.MovieInfo.VOTE_COUNT;
import static org.junit.Assert.*;

/**
 * Unit test for MovieInfo object
 */
@RunWith(AndroidJUnit4.class)
@SmallTest
public class MovieInfoTest extends ParcelTest {

    private MovieInfo movieInfo;
    private MovieInfo movieInfoPlaceholder;
    private TestMovieInfoInstance provider = new TestMovieInfoInstance();

    @Before
    public void createObject() {
        movieInfo = provider.setupObject(new MovieInfo());
        movieInfoPlaceholder = new MovieInfo(TestMovieInfoInstance.id, TestMovieInfoInstance.title);
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

    @Test
    public void movieInfo_Placeholder() {
        assertTrue(makeAssertMessage("isPlaceholder"), movieInfoPlaceholder.isPlaceHolder());
    }

    @Test
    public void movieInfo_Copy() {
        MovieInfo blankMovie = new MovieInfo();

        blankMovie.copy(movieInfo, null);

        checkObject("cop", blankMovie, movieInfo, null);
    }

    public void checkParcel(Parcel parcel) {
        // Read the data.
        MovieInfo createdFromParcel = MovieInfo.CREATOR.createFromParcel(parcel);

        // Verify that the received data is correct.
        checkObject("parcel", createdFromParcel, movieInfo);
    }

    public void checkObject(String test, MovieInfo createdFromParcel, MovieInfo original) {
        // Verify that the received data is correct.
        checkObject(test, createdFromParcel, original, null);
    }

    public void checkObject(String test, MovieInfo createdFromParcel, MovieInfo original, int[] fields) {
        // Verify that the received data is correct.
        if ((fields == null) || (fields.length == 0)) {
            fields = createdFromParcel.getFieldIds();
        }
        for (int field : fields) {
            switch (field) {
                case POSTER_PATH:
                    assertEquals(makeAssertMessage(test + " PosterPath"),
                            createdFromParcel.getPosterPath(), original.getPosterPath());
                    break;
                case BACKDROP_PATH:
                    assertEquals(makeAssertMessage(test + " BackdropPath"),
                            createdFromParcel.getBackdropPath(), original.getBackdropPath());
                    break;
                case OVERVIEW:
                    assertEquals(makeAssertMessage(test + " Overview"),
                            createdFromParcel.getOverview(), original.getOverview());
                    break;
                case RELEASE_DATE:
                    assertEquals(makeAssertMessage(test + " ReleaseDate"),
                            createdFromParcel.getReleaseDate(), original.getReleaseDate());
                    break;
                case ORIGINAL_TITLE:
                    assertEquals(makeAssertMessage(test + " OriginalTitle"),
                            createdFromParcel.getOriginalTitle(), original.getOriginalTitle());
                    break;
                case ORIGINAL_LANGUAGE:
                    assertEquals(makeAssertMessage(test + " OriginalLanguage"),
                            createdFromParcel.getOriginalLanguage(), original.getOriginalLanguage());
                    break;
                case TITLE:
                    assertEquals(makeAssertMessage(test + " Title"),
                            createdFromParcel.getTitle(), original.getTitle());
                    break;
                case ID:
                    assertEquals(makeAssertMessage(test + " Id"),
                            createdFromParcel.getId().longValue(), original.getId().longValue());
                    break;
                case ADULT:
                    assertEquals(makeAssertMessage(test + " isAdult"),
                            createdFromParcel.isAdult(), original.isAdult());
                    break;
                case VIDEO:
                    assertEquals(makeAssertMessage(test + " isVideo"),
                            createdFromParcel.isVideo(), original.isVideo());
                    break;
                case VOTE_COUNT:
                    assertEquals(makeAssertMessage(test + " VoteCount"),
                            createdFromParcel.getVoteCount().longValue(), original.getVoteCount().longValue());
                    break;
                case POPULARITY:
                    assertEquals(makeAssertMessage(test + " Popularity"),
                            createdFromParcel.getPopularity(), original.getPopularity(), 0.1d);
                    break;
                case VOTE_AVERAGE:
                    assertEquals(makeAssertMessage(test + " VoteAverage"),
                            createdFromParcel.getVoteAverage(), original.getVoteAverage(), 0.1d);
                    break;
                case GENRE_IDS:
                    assertArrayEquals(makeAssertMessage(test + " GenreIds"),
                            createdFromParcel.getGenreIds(), original.getGenreIds());
                    break;
            }
        }
    }

}