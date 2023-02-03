package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.ActSectorMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActSectorMappingRepository extends JpaRepository<ActSectorMapping, Long> {

    /**
     * Find all act sector mapping entries for a given act.
     *
     * @param actId the id of the act to find sector mappings for
     *
     * @return list of act sector mapping entries
     */
    @Query("SELECT m from act a LEFT JOIN a.sectorMaps m WHERE a.id = ?1")
    List<ActSectorMapping> loadActSectorMaps(Long actId);


    /**
     * DEPRECATED, USE METHOD IN TICKETSERVICE/REPO INSTEAD.
     * Find an act sector mapping entry containing the price of a given ticket.
     *
     * @param ticketId the id of the ticket to find the act sector mapping for
     *
     * @return act sector mapping entry containing the price of the ticket
     */
    @Query("""
SELECT asm1 FROM ticket t LEFT JOIN t.act a LEFT JOIN t.sectorMap sm LEFT JOIN sm.actSectorMaps asm1 LEFT JOIN a.sectorMaps asm2
WHERE t.id = ?1 AND asm1.id = asm2.id
        """)
    ActSectorMapping findPriceForTicket(Long ticketId);

}
