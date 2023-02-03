package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import at.ac.tuwien.sepm.groupphase.backend.entity.Event;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ArtistRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.EventRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.ActSectorMappingRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.SectorMapRepository;
import at.ac.tuwien.sepm.groupphase.backend.type.EventType;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static net.datafaker.internal.helper.WordUtils.capitalize;

@Component
public class ActAndEventDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ActRepository actRepository;
    private final ArtistRepository artistRepository;
    private final EventRepository eventRepository;
    private final StageRepository stageRepository;
    private final LocationRepository locationRepository;
    private final ActSectorMappingRepository actSectorMappingRepository;
    private final SectorMapRepository sectorMapRepository;
    private final Faker faker = new Faker(new Random(1));

    public ActAndEventDataGenerator(ActRepository actRepository,
                                    ArtistRepository artistRepository,
                                    EventRepository eventRepository,
                                    StageRepository stageRepository,
                                    LocationRepository locationRepository,
                                    ActSectorMappingRepository actSectorMappingRepository1,
                                    SectorMapRepository sectorMapRepository) {
        this.actRepository = actRepository;
        this.artistRepository = artistRepository;
        this.eventRepository = eventRepository;
        this.stageRepository = stageRepository;
        this.locationRepository = locationRepository;
        this.actSectorMappingRepository = actSectorMappingRepository1;
        this.sectorMapRepository = sectorMapRepository;
    }

    public void generate() {
        if (!eventRepository.findAll().isEmpty()) {
            LOGGER.info("Events are already generated");
            return;
        }
        if (stageRepository.findAll().isEmpty()) {
            System.err.println("No stages stored! Acts require stages");
            return;
        }
        LOGGER.info("Generating Events and Acts");
        generateEventsAndActs();
    }

    @Transactional
    void generateEventsAndActs() {
        LOGGER.debug("generateEventsAndActs()");
        List<Event> events = new ArrayList<>();
        List<Act> acts = new ArrayList<>();

        List<Location> locations = locationRepository.findAll();
        List<Artist> artists = artistRepository.findAll();
        List<EventType> eventTypes = Arrays.stream(EventType.values()).toList();
        int numberOfLocations = locations.size();
        int numberOfArtists = artists.size();

        for (int i = 0; i < 1000; i++) {
            Event event = new Event();
            Location location = locations.get(faker.number().positive() % numberOfLocations);
            List<Stage> stages = stageRepository.findAllByLocationIdOrderById(location.getId());
            EventType eventType = eventTypes.get(i % eventTypes.size());

            if (eventType == EventType.MOVIE) {
                event.setName(faker.mood().tone() + " " + faker.appliance().equipment());
            } else if (eventType == EventType.SOCCER || eventType == EventType.BASKETBALL || eventType == EventType.SPORT) {
                event.setName("Derby at the " + location.getVenueName());
            } else if (eventType == EventType.BALL) {
                event.setName("Here at the " + location.getVenueName());
            } else if (eventType == EventType.THEATRE) {
                event.setName("Story of " + faker.ancient().hero());
            } else if (eventType == EventType.EXHIBITION) {
                event.setName("Showing " + faker.photography().genre());
            } else if (eventType == EventType.CULTURE) {
                event.setName("Understanding " + faker.ancient().hero());
            } else {
                event.setName("Live at the " + location.getVenueName());
            }

            List<Artist> featuredArtists = artists.stream().unordered().limit(faker.number().positive() % 5).toList();
            event.setDescription(capitalize(eventType.name().toLowerCase()) + " event at the " + location.getVenueName() + ".");
            event.setType(eventType);
            event.setDuration(faker.number().positive() % 180);
            event.setLocation(location);
            event.setFeaturedArtists(featuredArtists);

            events.add(event);

            for (int j = 0; j < faker.number().positive() % 3 + 1; j++) {
                Act act = new Act();
                act.setStart(LocalDateTime.now().minusDays(30).plusDays(faker.number().positive() % 3 + (i % 200)));
                act.setNrTicketsReserved(0);
                act.setNrTicketsSold(0);
                act.setStage(stages.get(faker.number().positive() % stages.size()));

                act.setEvent(event);
                acts.add(act);
            }

        }

        eventRepository.saveAll(events);
        actRepository.saveAll(acts);
    }

    private void generateEvent1() {
        Event event = new Event();
        event.setName("Event #1");
        event.setDescription("First ever event on the Ticketline webpage!");
        event.setType(EventType.CONCERT);
        event.setDuration(120);
        event.setLocation(locationRepository.findAll().get(0));
        event.setFeaturedArtists(artistRepository.findAll());
        event = eventRepository.save(event);

        Stage stageAct = stageRepository.findAll().get(0);
        System.out.println();
        System.out.println("Act Sector Mapping Repository:");
        System.out.println(actSectorMappingRepository.findAll());
        System.out.println();

        Act act = generateAct(
            LocalDateTime.now().plusDays(20),
            0,
            0,
            stageAct,
            null,
            eventRepository.findAll().get(eventRepository.findAll().size() - 1)
        );

        for (long i = 11; i <= 16; i++) {
            ActSectorMapping actSectorMapping = new ActSectorMapping();
            actSectorMapping.setPrice(2000);
            actSectorMapping.setAct(act);
            actSectorMapping.setSectorMap(sectorMapRepository.findById(i).get());
            actSectorMappingRepository.save(actSectorMapping);
        }
    }

    private Act generateAct(LocalDateTime start,
                            Integer nrTicketsReserved,
                            Integer nrTicketsSold,
                            Stage stage,
                            List<ActSectorMapping> sectorMaps,
                            Event event) {
        Act act = new Act();
        act.setStart(start);
        act.setNrTicketsReserved(nrTicketsReserved);
        act.setNrTicketsSold(nrTicketsSold);
        act.setStage(stage);
        act.setSectorMaps(sectorMaps);
        act.setEvent(event);
        return actRepository.save(act);
    }
}
