package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class ArtistRepositoryTest implements TestData {

    @Autowired
    private ArtistRepository artistRepository;

    @BeforeEach
    public void beforeEach() {
        artistRepository.deleteAll();
    }

    @Test
    public void givenNothing_whenSaveArtist_thenFindListWithOneElementAndFindArtistByIdAndCheckValues() {
        Artist artist = new Artist(FIRST_NAME, LAST_NAME, BAND_NAME, ARTIST_STAGE_NAME);

        artistRepository.save(artist);

        Optional<Artist> foundArtist = artistRepository.findById(artist.getId());

        assertAll(
            () -> assertEquals(1, artistRepository.findAll().size()),
            () -> assertTrue(foundArtist.isPresent()),
            () -> assertEquals(foundArtist.get().getFirstName(), FIRST_NAME),
            () -> assertEquals(foundArtist.get().getLastName(), LAST_NAME),
            () -> assertEquals(foundArtist.get().getBandName(), BAND_NAME),
            () -> assertEquals(foundArtist.get().getStageName(), ARTIST_STAGE_NAME)
        );
    }
}
