package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {

    /**
     * Find all stage entries ordered by name (ascending).
     *
     * @return ordered list of all stage entries
     */
    List<Stage> findAllByOrderByNameAsc();

    /**
     * Find all stage entries for a given location ordered by stage id (ascending).
     *
     * @param locationId the id of the location to find stages for
     *
     * @return ordered list of stage entries
     */
    @Query("SELECT DISTINCT s FROM stage s LEFT JOIN s.location l WHERE l.id = ?1 ORDER BY s.id")
    List<Stage> findAllByLocationIdOrderById(Long locationId);
}
