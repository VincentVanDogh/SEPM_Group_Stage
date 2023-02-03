package at.ac.tuwien.sepm.groupphase.backend.integrationtest;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.config.properties.SecurityProperties;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchArticleDto;
import at.ac.tuwien.sepm.groupphase.backend.endpoint.dto.MerchPageDto;
import at.ac.tuwien.sepm.groupphase.backend.entity.*;
import at.ac.tuwien.sepm.groupphase.backend.repository.*;
import at.ac.tuwien.sepm.groupphase.backend.security.JwtTokenizer;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MerchArticleEndpointTest implements TestData {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenizer jwtTokenizer;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private MerchArticleRepository merchArticleRepository;

    @BeforeAll
    @Transactional
    public void beforeAll() {
        merchArticleRepository.deleteAll();
        artistRepository.deleteAll();
        addressRepository.deleteAll();
        locationRepository.deleteAll();
        eventRepository.deleteAll();
        insertTestData();
    }

    @AfterAll
    @Transactional
    public void afterAll() {
        merchArticleRepository.deleteAll();
        eventRepository.deleteAll();
        artistRepository.deleteAll();
        addressRepository.deleteAll();
        locationRepository.deleteAll();
    }

    @Test
    @Transactional
    public void getAllMerchArticles() throws Exception {
        byte[] body1 = mockMvc
                .perform(MockMvcRequestBuilders
                        .get(MERCH_ARTICLE_BASE_URI + "/all/page/1")
                        .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        try {
            List<MerchPageDto> merchPages1 = objectMapper.readerFor(MerchPageDto.class).<MerchPageDto>readValues(body1).readAll();
            assertThat(merchPages1).isNotNull();
            assertThat(merchPages1.size()).isEqualTo(1);
            List<MerchArticleDto> merch1 = merchPages1.get(0).getMerchArticles();
            assertThat(merch1.size()).isEqualTo(6);
            assertThat(merch1)
                    .extracting(
                            MerchArticleDto::getId, MerchArticleDto::getName, MerchArticleDto::getArtistOrEventName,
                            MerchArticleDto::getImage, MerchArticleDto::getPrice, MerchArticleDto::getBonusPointPrice
                    )
                    .contains(
                            tuple(
                                    1L,
                                    "T-Shirt Size M",
                                    "David Guetta",
                                    "https://yatra8exe7uvportalprd.blob.core.windows.net/images/products/HighStDonated/Zoom/HD_101712815_01.jpg?v=1",
                                    2000L,
                                    2000L
                            ),
                            tuple(
                                    2L,
                                    "T-Shirt Size XXL",
                                    "David Guetta",
                                    "https://yatra8exe7uvportalprd.blob.core.windows.net/images/products/HighStDonated/Zoom/HD_101712815_01.jpg?v=1",
                                    4000L,
                                    4000L
                            ),
                            tuple(
                                    3L,
                                    "Socks",
                                    "Weeknd",
                                    "https://cdna.lystit.com/photos/dickssportinggoods/3ec3fe4b/nike-WhiteOmega-BlueBlack-Elite-Versatility-Crew-Basketball-Socks.jpeg",
                                    100L,
                                    100L
                            ),
                            tuple(
                                    4L,
                                    "Keychain",
                                    "Event #1",
                                    "https://www.beograund.com/images/detailed/2/a1220l.png",
                                    300L,
                                    300L
                            ),
                            tuple(
                                    5L,
                                    "Shoes",
                                    "Mumbai City v Odisha",
                                    "https://www.beograund.com/images/detailed/2/a1220l.png",
                                    100L,
                                    100L
                            ),
                            tuple(
                                    6L,
                                    "Football kit",
                                    "Mumbai City v Odisha",
                                    "https://img.planetafobal.com/2016/09/mumbay-city-fc-home-kit-puma-2016.jpg",
                                    900L,
                                    900L
                            )
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional
    public void filterMerchArticlesByTerm() throws Exception {
        byte[] body1 = mockMvc
                .perform(MockMvcRequestBuilders
                        .get(MERCH_ARTICLE_BASE_URI + "/all/page/1?term=keychain")
                        .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        try {
            List<MerchPageDto> merchPages1 = objectMapper.readerFor(MerchPageDto.class).<MerchPageDto>readValues(body1).readAll();
            assertThat(merchPages1).isNotNull();
            assertThat(merchPages1.size()).isEqualTo(1);
            List<MerchArticleDto> merch1 = merchPages1.get(0).getMerchArticles();
            assertThat(merch1.size()).isEqualTo(1);
            assertThat(merch1)
                    .extracting(
                            MerchArticleDto::getId, MerchArticleDto::getName, MerchArticleDto::getArtistOrEventName,
                            MerchArticleDto::getImage, MerchArticleDto::getPrice, MerchArticleDto::getBonusPointPrice
                    )
                    .contains(
                            tuple(
                                    4L,
                                    "Keychain",
                                    "Event #1",
                                    "https://www.beograund.com/images/detailed/2/a1220l.png",
                                    300L,
                                    300L
                            )
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Transactional
    public void filterMerchArticlesByMinPrice() throws Exception {
        byte[] body1 = mockMvc
                .perform(MockMvcRequestBuilders
                        .get(MERCH_ARTICLE_BASE_URI + "/all/page/1?minPrice=9&maxPrice=30")
                        .header(securityProperties.getAuthHeader(), jwtTokenizer.getAuthToken(ADMIN_USER, ADMIN_ROLES))
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsByteArray();

        try {
            List<MerchPageDto> merchPages1 = objectMapper.readerFor(MerchPageDto.class).<MerchPageDto>readValues(body1).readAll();
            assertThat(merchPages1).isNotNull();
            assertThat(merchPages1.size()).isEqualTo(1);
            List<MerchArticleDto> merch1 = merchPages1.get(0).getMerchArticles();
            assertThat(merch1.size()).isEqualTo(2);
            assertThat(merch1)
                    .extracting(
                            MerchArticleDto::getId, MerchArticleDto::getName, MerchArticleDto::getArtistOrEventName,
                            MerchArticleDto::getImage, MerchArticleDto::getPrice, MerchArticleDto::getBonusPointPrice
                    )
                    .contains(
                            tuple(
                                    1L,
                                    "T-Shirt Size M",
                                    "David Guetta",
                                    "https://yatra8exe7uvportalprd.blob.core.windows.net/images/products/HighStDonated/Zoom/HD_101712815_01.jpg?v=1",
                                    2000L,
                                    2000L
                            ),
                            tuple(
                                    6L,
                                    "Football kit",
                                    "Mumbai City v Odisha",
                                    "https://img.planetafobal.com/2016/09/mumbay-city-fc-home-kit-puma-2016.jpg",
                                    900L,
                                    900L
                            )
                    );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertTestData() {
        // Insert users
        userRepository.save(ApplicationUser.ApplicationUserBuilder.anApplicationUser()
                .withEmail("alfa@email.com")
                .withPassword(passwordEncoder.encode("12345678"))
                .withFirstName("Alfa")
                .withLastName("Alfonson")
                .withTimesWrongPwEntered(0)
                .isLockedOut(false)
                .isAdmin(false)
                .build());

        // Insert address and location and stage
        Address address = new Address();
        address.setCountry("Austria");
        address.setCity("Vienna");
        address.setPostalCode(1040);
        address.setStreet("Karlsplatz 13");

        Location location = new Location();
        location.setVenueName("TU Wien");

        address.setLocation(location);
        location.setAddress(address);

        addressRepository.save(address);
        locationRepository.save(location);

        // Insert artist
        Artist artist1 = createArtist("David", "Guetta", null, null);
        Artist artist2 = createArtist(null, null, null, "Weeknd");

        // Insert  event
        Event event1 = createEvent("Event #1", "First ever event on the Ticketline webpage!",
                EventType.CONCERT, 120, locationRepository.findAll().get(0), artistRepository.findAll());
        Event event2 = createEvent("Mumbai City v Odisha", "Indian Football Cup - Final",
                EventType.SOCCER, 90, locationRepository.findAll().get(0), List.of(artist2));

        // Insert merch articles with Artists
        createMerchArticleWithArtist("T-Shirt Size M", 2000L, 2000L, artist1,
                "https://yatra8exe7uvportalprd.blob.core.windows.net/images/products/HighStDonated/Zoom/HD_101712815_01.jpg?v=1");
        createMerchArticleWithArtist("T-Shirt Size XXL", 4000L, 4000L, artist1,
                "https://yatra8exe7uvportalprd.blob.core.windows.net/images/products/HighStDonated/Zoom/HD_101712815_01.jpg?v=1");
        createMerchArticleWithArtist("Socks", 100L, 100L, artist2,
                "https://cdna.lystit.com/photos/dickssportinggoods/3ec3fe4b/nike-WhiteOmega-BlueBlack-Elite-Versatility-Crew-Basketball-Socks.jpeg");

        // Insert merch articles with Events
        createMerchArticleWithEvent("Keychain", 300L, 300L, event1,
                "https://www.beograund.com/images/detailed/2/a1220l.png");
        createMerchArticleWithEvent("Shoes", 100L, 100L, event2,
                "https://www.beograund.com/images/detailed/2/a1220l.png");
        createMerchArticleWithEvent("Football kit", 900L, 900L, event2,
                "https://img.planetafobal.com/2016/09/mumbay-city-fc-home-kit-puma-2016.jpg");
    }

    private Artist createArtist(String firstName, String lastName, String bandName, String stageName) {
        Artist artist = new Artist();
        artist.setFirstName(firstName);
        artist.setLastName(lastName);
        artist.setBandName(bandName);
        artist.setStageName(stageName);
        return artistRepository.save(artist);
    }

    private Event createEvent(String name, String description, EventType type, int duration, Location location,
                              List<Artist> featuredArtists) {
        Event event = new Event();
        event.setName(name);
        event.setDescription(description);
        event.setType(type);
        event.setDuration(duration);
        event.setLocation(location);
        event.setFeaturedArtists(featuredArtists);
        return eventRepository.save(event);
    }

    private void createMerchArticleWithArtist(String name, Long price, Long pointsPrice, Artist artist, String image) {
        MerchArticle article = new MerchArticle();
        article.setName(name);
        article.setPrice(price);
        article.setBonusPointPrice(pointsPrice);
        article.setArtist(artist);
        article.setImage(image);
        merchArticleRepository.save(article);
    }

    private void createMerchArticleWithEvent(String name, Long price, Long pointsPrice, Event event, String image) {
        MerchArticle article = new MerchArticle();
        article.setName(name);
        article.setPrice(price);
        article.setBonusPointPrice(pointsPrice);
        article.setEvent(event);
        article.setImage(image);
        merchArticleRepository.save(article);
    }
}
