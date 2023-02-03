package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.SectorMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectorMapRepository extends JpaRepository<SectorMap, Long> {

    /**
     * Find the sector map entry associated with a specified act at the given coordinates of a stage.
     *
     * @param actId the id of the act to the sector map for
     * @param x the x coordinate
     * @param y the y coordinate
     *
     * @return the specific sector map
     */
    @Query("""
        SELECT m FROM act a LEFT JOIN a.sectorMaps asm LEFT JOIN asm.sectorMap m
        WHERE a.id = ?1 AND m.sectorX = ?2 AND m.sectorY = ?3
        """)
    SectorMap findSectorMapForActAndCoordinates(Long actId, Integer x, Integer y);

    /**
     * Find all sector map entries for a given stage.
     *
     * @param stageId the id of the stage to find sector maps for
     *
     * @return list of sector map entries
     */
    @Query("SELECT m FROM stage s LEFT JOIN s.stageTemplate st LEFT JOIN st.sectorMaps m WHERE s.id = ?1")
    List<SectorMap> loadSectorMaps(Long stageId);

}
