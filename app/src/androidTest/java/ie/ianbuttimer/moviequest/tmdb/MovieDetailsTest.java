package ie.ianbuttimer.moviequest.tmdb;

import android.os.Parcel;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Ian on 16/08/2017.
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
        checkObject(createdFromParcel, movieDetails);
    }

    public void checkObject(MovieDetails createdFromParcel, MovieDetails original) {
        // Verify that the received data is correct.
        super.checkObject(createdFromParcel, original);

        assertEquals(makeAssertMessage("Budget"),
                createdFromParcel.getBudget(), original.getBudget());
        assertArrayEquals(makeAssertMessage("Genres"),
                createdFromParcel.getGenres(), original.getGenres());
        assertEquals(makeAssertMessage("Homepage"),
                createdFromParcel.getHomepage(), original.getHomepage());
        assertEquals(makeAssertMessage("ImdbId"),
                createdFromParcel.getImdbId(), original.getImdbId());
        assertArrayEquals(makeAssertMessage("ProductionCompanies"),
                createdFromParcel.getProductionCompanies(), original.getProductionCompanies());
        assertArrayEquals(makeAssertMessage("ProductionCountries"),
                createdFromParcel.getProductionCountries(), original.getProductionCountries());
        assertEquals(makeAssertMessage("Revenue"),
                createdFromParcel.getRevenue(), original.getRevenue());
        assertEquals(makeAssertMessage("Runtime"),
                createdFromParcel.getRuntime(), original.getRuntime());
        assertArrayEquals(makeAssertMessage("SpokenLanguages"),
                createdFromParcel.getSpokenLanguages(), original.getSpokenLanguages());
        assertEquals(makeAssertMessage("Status"),
                createdFromParcel.getStatus(), original.getStatus());
        assertEquals(makeAssertMessage("Tagline"),
                createdFromParcel.getTagline(), original.getTagline());
        assertEquals(makeAssertMessage("Collection"),
                createdFromParcel.getCollection(), original.getCollection());
    }

}