package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//@Profile("generateData")
@Component
public class ArtistGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;
    private final ActRepository actRepository;
    private final Faker faker = new Faker(new Random(1));

    public ArtistGenerator(ArtistRepository artistRepository, EventRepository eventRepository, ActRepository actRepository) {
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
        this.actRepository = actRepository;
    }

    public void generateArtists() {
        if (artistRepository.findAll().size() > 0) {
            LOGGER.info("Artists already generated");
        } else {
            LOGGER.info("Generating Artists");
            generateUpTo150Artists();
        }
    }

    private void generateUpTo150Artists() {
        LOGGER.debug("generateUpTo150Artists()");
        List<Artist> artists = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            Artist artist = new Artist();
            artist.setBandName(faker.rockBand().name());
            if (artists.isEmpty()) {
                artists.add(artist);
            } else {
                if (!artists.contains(artist)) {
                    artists.add(artist);
                }
            }

        }

        for (int i = 0; i < 50; i++) {
            Artist artist = new Artist();
            artist.setStageName(faker.football().teams());
            if (artists.isEmpty()) {
                artists.add(artist);
            } else {
                if (!artists.contains(artist)) {
                    artists.add(artist);
                }
            }
        }

        for (int i = 0; i < 50; i++) {
            Artist artist = new Artist();
            artist.setFirstName(faker.name().firstName());
            artist.setLastName(faker.name().lastName());
            if (artists.isEmpty()) {
                artists.add(artist);
            } else {
                if (!artists.contains(artist)) {
                    artists.add(artist);
                }
            }
        }

        artistRepository.saveAll(artists);
    }

    private void generateArtist1() {
        LOGGER.debug("generating artist #1");
        Artist artist = new Artist();
        artist.setFirstName("David");
        artist.setLastName("Guetta");
        artistRepository.save(artist);
    }

    private void generateArtist2() {
        LOGGER.debug("generating artist #2");
        Artist artist = new Artist();
        artist.setFirstName("Leonardo");
        artist.setLastName("DiCaprio");
        artistRepository.save(artist);
    }

    private void generateArtist3() {
        LOGGER.debug("generating artist #3");
        Artist artist = new Artist();
        artist.setFirstName("Abel");
        artist.setLastName("Tesfaye");
        artist.setStageName("The Weeknd");
        artistRepository.save(artist);
    }

    private void generateArtist4() {
        LOGGER.debug("generating artist #4");
        Artist artist = new Artist();
        artist.setFirstName("Kayode");
        artist.setLastName("Ewumi");
        artist.setStageName("Roll Safe");
        artistRepository.save(artist);
    }

    private void generateArtist5() {
        LOGGER.debug("generating artist #5");
        Artist artist = new Artist();
        artist.setFirstName("Samuel");
        artist.setLastName("Beam");
        artist.setBandName("Iron & Wine");
        artistRepository.save(artist);
    }
}
