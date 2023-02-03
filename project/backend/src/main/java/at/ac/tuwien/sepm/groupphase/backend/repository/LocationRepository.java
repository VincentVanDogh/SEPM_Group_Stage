package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * Find Top 10 stages by matching the name with a searchTerm.
     *
     * @param searchTerm the search input to match the venue name of the locations
     * @return ordered List of Top 10 matching location entries
     */
    List<Location> findTop10ByVenueNameContainingIgnoreCaseOrderByVenueName(String searchTerm);
}
