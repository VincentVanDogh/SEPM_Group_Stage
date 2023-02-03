package at.ac.tuwien.sepm.groupphase.backend.datagenerator;

import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.StageTemplateRepository;
import net.datafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static net.datafaker.internal.helper.WordUtils.capitalize;

@Component
public class AddressAndLocationDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final AddressRepository addressRepository;
    private final LocationRepository locationRepository;
    private final StageTemplateRepository stageTemplateRepository;
    private final StageRepository stageRepository;
    private final Faker localFaker = new Faker(new Locale("de", "at"), new Random(1));
    private final Faker faker = new Faker(new Random(1));


    public AddressAndLocationDataGenerator(AddressRepository addressRepository,
                                           LocationRepository locationRepository,
                                           StageTemplateRepository stageTemplateRepository,
                                           StageRepository stageRepository) {
        this.addressRepository = addressRepository;
        this.locationRepository = locationRepository;
        this.stageTemplateRepository = stageTemplateRepository;
        this.stageRepository = stageRepository;
    }

    public void generateAddressesAndLocationsAndStages() {
        if (addressRepository.findAll().size() > 0) {
            LOGGER.info("Addresses already generated");
        } else if (locationRepository.findAll().size() > 0) {
            LOGGER.info("Locations already generated");
        } else {
            LOGGER.info("Generating Adresses and Locations");
            generate25AddressesAndLocations();
            //generateAddressLocation1();
            //generateAddressLocation2();
            //generateAddressLocation3();
        }
    }

    @Transactional
    void generate25AddressesAndLocations() {
        LOGGER.debug("generate25AddressesAndLocations()");

        List<Address> addresses = new ArrayList<>();
        List<Location> locations = new ArrayList<>();
        List<Stage> stages = new ArrayList<>();

        List<StageTemplate> stageTemplates = stageTemplateRepository
            .findAll().stream().toList();

        for (int i = 0; i < 25; i++) {

            net.datafaker.providers.base.Address genAddress = localFaker.address();

            Address address = new Address();
            address.setCountry("Austria");
            address.setCity(genAddress.cityName());
            address.setPostalCode(Integer.parseInt(genAddress.postcode()));
            address.setStreet(genAddress.streetAddress());

            Location location = new Location();
            location.setVenueName(capitalize(faker.mood().emotion()) + " " + capitalize(faker.animal().name()));

            address.setLocation(location);
            location.setAddress(address);

            addresses.add(address);
            locations.add(location);

            int numberOfStages = faker.number().positive() % stageTemplates.size();
            if (numberOfStages == 0) {
                numberOfStages = 1;
            }

            for (int j = 0; j < numberOfStages; j++) {
                stages.add(
                    Stage.StageBuilder.aStage()
                        .withName("Stage" + j)
                        .withStageTemplate(stageTemplates.get(j))
                        .withLocation(location)
                        .build()
                );
            }
        }

        addressRepository.saveAll(addresses);
        locationRepository.saveAll(locations);
        stageRepository.saveAll(stages);
    }

    private void generateAddressLocation1() {
        LOGGER.debug("generating address and location 1");
        Address address = new Address();
        address.setCountry("Austria");
        address.setCity("Vienna");
        address.setPostalCode(1020);
        address.setStreet("Meiereistrasse 7");

        Location location = new Location();
        location.setVenueName("Ernst Happel Stadion");

        address.setLocation(location);
        location.setAddress(address);

        addressRepository.save(address);
        locationRepository.save(location);
    }

    private void generateAddressLocation2() {
        LOGGER.debug("generating address and location 2");
        Address address = new Address();
        address.setCountry("Germany");
        address.setCity("Merzig");
        address.setPostalCode(66663);
        address.setStreet("Saarwiesenring 1");

        Location location = new Location();
        location.setVenueName("Zeltpalast Merzig");

        address.setLocation(location);
        location.setAddress(address);

        addressRepository.save(address);
        locationRepository.save(location);
    }

    private void generateAddressLocation3() {
        LOGGER.debug("generating address and location 3");
        Address address = new Address();
        address.setCountry("Austria");
        address.setCity("Graz");
        address.setPostalCode(8041);
        address.setStreet("Zoisweg 15");

        Location location = new Location();
        location.setVenueName("Merkur Eisstadion");

        address.setLocation(location);
        location.setAddress(address);

        addressRepository.save(address);
        locationRepository.save(location);
    }
}
