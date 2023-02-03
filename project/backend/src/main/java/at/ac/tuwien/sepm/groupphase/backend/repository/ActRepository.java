package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Act;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActRepository extends JpaRepository<Act, Long> {

    /**
     * Find all act entries for a given event.
     *
     * @param eventId the id of the event to find acts for
     *
     * @return list of act entries
     */
    @Query("SELECT a FROM act a LEFT JOIN a.event e WHERE e.id = ?1")
    List<Act> findActsByEventId(Long eventId);

    /**
     * Find act entries matching search parameters for a given event.
     *
     * @param eventId the id of the event to find acts for
     * @param dateFrom the earliest date
     * @param dateTo the latest date
     * @param priceFrom the minimum price
     * @param priceTo the maximum price
     *
     * @return list of act entries
     */
    @Query(value = "SELECT * FROM ACT a, ACT_SECTOR_MAPPING m WHERE event_id = ?1 AND m.act_id = a.id AND a.start BETWEEN ?2 AND ?3 AND m.price BETWEEN ?4 AND ?5", nativeQuery = true)
    List<Act> findActsByEventIdAndSearchParams(Long eventId, LocalDateTime dateFrom, LocalDateTime dateTo, Long priceFrom, Long priceTo);
}

