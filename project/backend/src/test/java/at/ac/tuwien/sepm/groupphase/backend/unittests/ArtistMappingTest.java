package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.ArtistDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.mapper.ArtistMapper;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
public class ArtistMappingTest implements TestData {

    private final Artist artist = new Artist(FIRST_NAME, LAST_NAME, BAND_NAME, ARTIST_STAGE_NAME);

    @Autowired
    private ArtistMapper artistMapper;

    @Test
    public void givenNothing_whenMapArtistDtoToArtist_thenArtistHasAllProperties() {
        ArtistDto artistDto = artistMapper.artistToArtistDto(artist);
        assertAll(
            () -> assertEquals(FIRST_NAME, artistDto.firstName()),
            () -> assertEquals(LAST_NAME, artistDto.lastName()),
            () -> assertEquals(BAND_NAME, artistDto.bandName()),
            () -> assertEquals(ARTIST_STAGE_NAME, artistDto.stageName())
        );
    }

}
