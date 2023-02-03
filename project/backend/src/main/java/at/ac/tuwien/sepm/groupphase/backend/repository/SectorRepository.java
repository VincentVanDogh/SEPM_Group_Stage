package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Sector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface SectorRepository extends JpaRepository<Sector, Long> {

    /**
     * Find a sector for a given stage and seat number.
     *
     * @param stageId the id of the stage to find the sector for
     * @param seatNr the seat number which should be in the returned sector
     *
     * @return a sector in linked to the given stage containing the seat with the given number
     */
    @Query("""
        SELECT se FROM stage st LEFT JOIN st.stageTemplate ste LEFT JOIN ste.sectorMaps m LEFT JOIN m.sector se
        WHERE st.id = ?1 AND m.firstSeatNr <= ?2 AND m.firstSeatNr+se.numberOfSeats > ?2
        """)
    Sector findSectorForStageAndSeatNr(Long stageId, Integer seatNr);

    /**
     * Find the sector entry associated with a specified act at the given coordinates of a stage.
     *
     * @param actId the id of the act to the sector for
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the specific sector
     */
    @Query("""
        SELECT se FROM act a LEFT JOIN a.sectorMaps asm LEFT JOIN asm.sectorMap m LEFT JOIN m.sector se
        WHERE a.id = ?1 AND m.sectorX = ?2 AND m.sectorY = ?3
        """)
    Sector findSectorForActAndCoordinates(Long actId, Integer x, Integer y);

}
