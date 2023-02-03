package at.ac.tuwien.sepm.groupphase.backend.repository;

import at.ac.tuwien.sepm.groupphase.backend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all message entries ordered by published at date (descending).
     *
     * @return ordered list of all message entries
     */
    List<Message> findAllByOrderByPublishedAtDesc();

    /**
     * Find all unread message entries ordered by published at date (descending).
     *
     * @param userId The id of the user
     * @return ordered list of all message entries that have not been read by the user
     */
    @Query(value = "SELECT * FROM message m WHERE m.id NOT IN (SELECT nr.message_id FROM news_read nr WHERE nr.user_id = :userId) ORDER BY m.published_at DESC", nativeQuery = true)
    List<Message> findNotReadByUserOrderByPublishedAtDesc(@Param("userId") Long userId);

}
