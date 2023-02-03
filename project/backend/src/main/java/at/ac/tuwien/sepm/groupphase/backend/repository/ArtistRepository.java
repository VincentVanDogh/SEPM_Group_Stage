package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    /**
     * Finds first 10 artists by not case-sensitive first name, last name, band name or stage name.
     *
     * @param firstName first name of the artist
     * @param lastName last name of the artist
     * @param bandName band name of the artist
     * @param stageName stage name of the artist
     *
     * @return a collection of artists with corresponding names
     */
    List<Artist> findFirst10ByFirstNameContainsIgnoreCaseOrLastNameContainsIgnoreCaseOrBandNameContainsIgnoreCaseOrStageNameContainsIgnoreCase(
            String firstName,
            String lastName,
            String bandName,
            String stageName
    );
}
