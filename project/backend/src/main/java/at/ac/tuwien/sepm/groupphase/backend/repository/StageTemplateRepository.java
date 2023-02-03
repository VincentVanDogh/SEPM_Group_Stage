package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.StageTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StageTemplateRepository extends JpaRepository<StageTemplate, Long> {

    /**
     * Find all stage entries ordered by name (ascending).
     *
     * @return ordered list of all stage entries
     */
    List<StageTemplate> findAllByOrderByNameAsc();

}
