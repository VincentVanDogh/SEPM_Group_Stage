package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    /**
     * Find an address with the given data.
     *
     * @param street the street name
     * @param city the city name
     * @param country the country name
     * @param postalCode the postal code
     * @return the address if found
     * */
    Address findFirstByStreetAndCityAndCountryAndPostalCode(String street, String city, String country, int postalCode);
}