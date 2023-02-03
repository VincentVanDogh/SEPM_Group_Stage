package at.ac.tuwien.sepm.groupphase.backend.unittests;

import at.ac.tuwien.sepm.groupphase.backend.basetest.TestData;
import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import at.ac.tuwien.sepm.groupphase.backend.repository.AddressRepository;
import at.ac.tuwien.sepm.groupphase.backend.repository.LocationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
// This test slice annotation is used instead of @SpringBootTest to load only repository beans instead of
// the entire application context
@DataJpaTest
@ActiveProfiles("test")
public class LocationRepositoryTest implements TestData {

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    AddressRepository addressRepository;

    @BeforeEach
    public void beforeEach() {
        locationRepository.deleteAll();
        addressRepository.deleteAll();
    }

    private final Address address = new Address(STREET, CITY, COUNTRY, POSTAL_CODE);

    @Test
    public void givenNothing_whenSaveLocation_thenFindListWithOneElementAndFindLocationByIdAndCheckValues() {

        Address savedAddress = addressRepository.save(address);
        Location location = new Location(VENUE_NAME, savedAddress);

        locationRepository.save(location);

        Optional<Location> foundLocation = locationRepository.findById(location.getId());

        assertAll(
            () -> assertEquals(1, locationRepository.findAll().size()),
            () -> assertTrue(foundLocation.isPresent()),
            () -> assertEquals(foundLocation.get().getVenueName(), VENUE_NAME),
            () -> assertEquals(foundLocation.get().getAddress().getId(), savedAddress.getId()),
            () -> assertEquals(foundLocation.get().getAddress().getStreet(), STREET),
            () -> assertEquals(foundLocation.get().getAddress().getCity(), CITY),
            () -> assertEquals(foundLocation.get().getAddress().getCountry(), COUNTRY),
            () -> assertEquals(foundLocation.get().getAddress().getPostalCode(), POSTAL_CODE)
        );
    }

    @Test
    public void givenNothing_whenSearchByName_thenFindListWithOneElementAndFindLocationByIdAndCheckValues() {

        Address savedAddress = addressRepository.save(address);
        Location location = new Location(VENUE_NAME, savedAddress);

        locationRepository.save(location);

        Stream<Location> locations = locationRepository.findTop10ByVenueNameContainingIgnoreCaseOrderByVenueName("v").stream();

        assertAll(
            () -> assertEquals(1, locationRepository.findAll().size()),
            () -> assertEquals(locations.findFirst().get().getVenueName(), VENUE_NAME)
        );
    }
}
